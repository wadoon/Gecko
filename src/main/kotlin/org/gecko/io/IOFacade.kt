package org.gecko.io

import com.google.gson.JsonParser
import gecko.parser.SystemDefBaseVisitor
import gecko.parser.SystemDefLexer
import gecko.parser.SystemDefParser
import gecko.parser.SystemDefParser.*
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.DialogEvent
import javafx.scene.layout.VBox
import org.antlr.v4.runtime.*
import org.gecko.util.graphlayouting.Graphlayouter
import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.*
import java.io.File
import java.io.InputStreamReader
import java.util.*

/**
 *
 * @author Alexander Weigl
 * @version 1 (30.06.24)
 */
object IOFacade {
    /** Provides methods for the conversion of data from a JSON file into Gecko-specific data. */
    fun readModelJson(file: File): GModel {
        val wrapper = file.inputStream().use { JsonParser.parseReader(InputStreamReader(it)) }.asJsonObject
        val model = GModel()
        model.initFromMap(wrapper.get("model").asJsonObject)
        model.root.updateSystemParents()
        return model
    }

    /**
     * The AutomatonFileParser is used to import a project from a sys file. It is responsible for
     * parsing a sys file and creating a [GModel] from it. It uses the [AutomatonFileVisitor] the file
     * into a [GModel]. And then uses the [ViewModelElementCreator] to create the view model from the
     * model.
     */
    fun parse(file: File): Pair<GModel, MutableSet<String>> {
        val stream = CharStreams.fromPath(file.toPath())
        val parser = SystemDefParser(CommonTokenStream(SystemDefLexer(stream)))
        val listener = SyntaxErrorListener()
        parser.removeErrorListeners()
        parser.addErrorListener(listener)

        val visitor = AutomatonFileVisitor()
        val gvm = visitor.visitModel(parser.model()).let { visitor.model }

        Graphlayouter(gvm).layout()
        return gvm to visitor.warnings
    }
}

data class ParseException(val errorMessage: String) : RuntimeException(errorMessage)

class SyntaxErrorListener : BaseErrorListener() {
    override fun syntaxError(
        recognizer: Recognizer<*, *>?, offendingSymbol: Any, line: Int,
        charPositionInLine: Int, msg: String, e: RecognitionException
    ) = throw ParseException("$msg at line $line:$charPositionInLine")
}


/**
 * The AutomatonFileScout is responsible for scanning the parsed automaton file and extracting
 * information about the systems, automata, and contracts. It is used by the [AutomatonFileParser]
 * to give access to information about a sys file that would otherwise be hard to obtain while
 * visiting single elements.
 */
private class AutomatonFileScout(ctx: ModelContext) {
    val systems: MutableMap<String, SystemContext> = HashMap()
    val automata: MutableMap<String, AutomataContext> = HashMap()
    val contracts: MutableMap<String, PrepostContext> = HashMap()

    val foundChildren: MutableSet<SystemInfo> = HashSet()
    val rootChildrenIdents: MutableSet<String> = HashSet()

    val rootChildren: MutableSet<SystemContext> = HashSet()
    val parents = HashMap<SystemContext, MutableList<SystemContext>>()

    val scoutVisitor: ScoutVisitor

    init {
        this.scoutVisitor = ScoutVisitor()
        ctx.accept(scoutVisitor)
    }

    fun getSystem(name: String) = systems[name]

    fun getAutomaton(name: String) = automata[name]

    fun getContract(name: String) = contracts[name]

    fun getParents(ctx: SystemContext) = parents[ctx] ?: listOf()

    fun getChildSystemInfos(ctx: SystemContext) = scoutVisitor.getChildSystems(ctx)

    inner class ScoutVisitor : SystemDefBaseVisitor<Unit>() {
        override fun visitModel(ctx: ModelContext) {
            ctx.system().forEach { system: SystemContext -> system.accept(this) }
            ctx.system().forEach { systemContext: SystemContext ->
                this.registerParent(systemContext)
            }
            ctx.contract().forEach { contract: ContractContext -> contract.accept(this) }
            val defines = ctx.defines()
            defines?.variable()?.forEach { variable: VariableContext -> variable.accept(this) }
            foundChildren.map(SystemInfo::type).forEach { rootChildrenIdents.remove(it) }
            rootChildren.addAll(
                ctx.system().filter { rootChildrenIdents.contains(it.ident().Ident().text) }
            )
        }

        override fun visitSystem(ctx: SystemContext) {
            val sysName = ctx.ident().Ident().text
            systems[sysName] = ctx
            rootChildrenIdents.add(sysName)
            foundChildren.addAll(getChildSystems(ctx))
            parents[ctx] = ArrayList()
        }

        override fun visitContract(ctx: ContractContext) {
            automata[ctx.automata().ident().Ident().text] = ctx.automata()
            for (prepost in ctx.automata().prepost()) {
                contracts[prepost.ident().text] = prepost
            }
        }

        fun getChildSystems(ctx: SystemContext): List<SystemInfo> {
            val children: MutableList<SystemInfo> = ArrayList()
            ctx.io()
                .filter { io -> io.type.type == STATE }
                .forEach { io ->
                    children.addAll(
                        io.variable()
                            .filter { !builtinTypes.contains(it.t.text) }
                            .flatMap { it.n.map { n -> SystemInfo(n.text, it.t.text) } }
                    )
                }
            return children
        }

        fun registerParent(systemContext: SystemContext) {
            for ((_, type) in getChildSystems(systemContext)) {
                val childCtx = systems[type] ?: continue
                parents[childCtx]!!.add(systemContext)
            }
        }
    }
}


/**
 * Used for building a [GModel] from a sys file. This class is a visitor for the ANTLR4 generated
 * parser for the sys file format. The entire [GModel] can be built by calling [.visitModel] and
 * passing it the [SystemDefParser.ModelContext] of a sys file.
 */
class AutomatonFileVisitor : SystemDefBaseVisitor<Unit>() {
    var model: GModel = GModel()
    val warnings: MutableSet<String> = TreeSet()

    var currentSystem = model.root
    private lateinit var scout: AutomatonFileScout

    var instanceName: String? = null

    override fun visitModel(ctx: ModelContext) {
        val scout = AutomatonFileScout(ctx)
        this.scout = scout

        if (hasCyclicChildSystems(ctx)) {
            throw RuntimeException("Cyclic child systems found")
        }

        val rootName: String =
            if (scout.rootChildren.size == 1) {
                scout.rootChildren.first().ident().text
            } else if (scout.rootChildren.size > 1) {
                val rootNames = scout.rootChildren.map { it.ident().text }
                makeUserChooseSystem(rootNames)
            } else {
                throw RuntimeException("No root system found")
            }

        scout.getSystem(rootName)!!.accept(this)
        val newRoot = model.root.subSystems.first()
        newRoot.parent = null
        model = GModel(newRoot)
        if (ctx.globalCode != null) {
            model.globalCode = cleanCode(ctx.globalCode.text)
        }

        if (ctx.defines() != null) {
            val constants =
                ctx.defines().variable().flatMap {
                    it.n.map { name -> Constant(name.text, it.t.text, it.init.text) }
                }
            model.globalDefines.setAll(constants)
        }
    }

    override fun visitSystem(ctx: SystemContext) {
        val system: System = currentSystem.createSubSystem()
        system.name = instanceName ?: ctx.ident().Ident().text
        if (ctx.reaction() != null) {
            system.code = cleanCode(ctx.reaction().text)
        }
        currentSystem = system

        for (io in ctx.io()) io.accept(this)

        scout.getChildSystemInfos(ctx).forEach {
            val ctx = scout.getSystem(it.type) ?: error(String.format("System %s not found", it.type))
            instanceName = it.name
            ctx.accept(this)
        }

        ctx.connection().forEach { it.accept(this) }

        if (ctx.use_contracts().isNotEmpty()) {
            require(
                ctx.use_contracts().size == 1 &&
                        ctx.use_contracts().first().use_contract().size == 1
            ) {
                "Multiple automata in one system are not supported"
            }
            ctx.use_contracts().first().use_contract().first().accept(this)
        }
        currentSystem = system.parent!!
    }

    override fun visitUse_contract(ctx: Use_contractContext) {
        val automata =
            scout.getAutomaton(ctx.ident().text)
                ?: throw RuntimeException(String.format("Automaton %s not found", ctx.ident().text))
        automata.accept(this)
        for (subst in ctx.subst()) {
            subst.accept(this)
        }
    }

    override fun visitSubst(ctx: SubstContext) {
        val toReplace = ctx.local.text
        if (ctx.from.inst != null) {
            throw RuntimeException("Variables to substitute can only be from the same system")
        }
        val toReplaceWith = ctx.from.port.text
        if (currentSystem.ports.none { it.name == toReplaceWith }) {
            throw RuntimeException(
                String.format(
                    "Variable %s not found not found in system %s",
                    toReplace,
                    currentSystem.name
                )
            )
        }
        applySubstitution(currentSystem, toReplace, toReplaceWith)
    }

    override fun visitAutomata(ctx: AutomataContext) {
        if (ctx.history().isNotEmpty()) {
            warnings.add(
                String.format("Automaton %s has history, which is ignored", ctx.ident().text)
            )
        }

        if (ctx.use_contracts().isNotEmpty()) {
            warnings.add(
                String.format(
                    "Automaton %s has use_contracts, which are be ignored",
                    ctx.ident().text
                )
            )
        }

        if (ctx.transition().isEmpty()) {
            warnings.add(String.format("Automaton %s has no transitions", ctx.ident().text))
        }

        for (transition in ctx.transition()) transition.accept(this)

        currentSystem.automaton.states.forEach { it.isStartState = it.name[0].isLowerCase() }
    }

    override fun visitTransition(ctx: TransitionContext) {
        if (ctx.vvguard() != null) {
            warnings.add(String.format("Transition %s has a vvguard, which is ignored", ctx.text))
        }
        val startName = ctx.from.text
        val endName = ctx.to.text
        var start = currentSystem.automaton.getStateByName(startName)
        if (start == null) {
            start = currentSystem.automaton.createState()

            start.name = startName
        }
        var end = currentSystem.automaton.getStateByName(endName)
        if (end == null) {
            end = currentSystem.automaton.createState()

            end.name = endName
        }
        val contract =
            if (ctx.contr != null) {
                buildContract(start, scout.getContract(ctx.contr.text))
            } else {
                buildContract(start, ctx.pre.text, ctx.post.text)
            }
        val edge: Edge = currentSystem.automaton.createEdge(start, end)

        edge.contract = contract
    }

    override fun visitIo(ctx: IoContext) {
        val visibility =
            when (ctx.type.type) {
                INPUT -> Visibility.INPUT
                OUTPUT -> Visibility.OUTPUT
                STATE -> Visibility.STATE
                else ->
                    throw IllegalStateException("Unexpected variable visibility: " + ctx.type.type)
            }
        for (variable in ctx.variable()) {
            if (!builtinTypes.contains(variable.t.text)) {
                if (visibility == Visibility.STATE) {
                    if (scout.getSystem(variable.t.text) != null) {
                        return
                    } else {
                        throw RuntimeException("State type must be a system or builtin type")
                    }
                } else {
                    throw RuntimeException("Input and Output type must be a builtin type")
                }
            }
            for (ident in variable.n) {
                if (
                    currentSystem.getVariableByName(ident.Ident().text) != null ||
                    scout.getSystem(ident.text) != null
                ) {
                    continue
                }
                val v = currentSystem.createVariable()

                v.name = (ident.Ident().text)
                v.type = (variable.t.text)
                v.visibility = (visibility)
                if (variable.init != null) {
                    v.value = variable.init.text
                }
            }
        }
    }

    override fun visitConnection(ctx: ConnectionContext) {
        if (ctx.from.inst == null || ctx.to.any { it.inst == null }) {
            throw RuntimeException("Invalid System in connection")
        }
        val startSystem = parseSystemReference(ctx.from.inst.text)
        val start =
            startSystem.getVariableByName(ctx.from.port.text)
                ?: throw RuntimeException(
                    String.format("Could not find variable %s", ctx.from.port.text)
                )
        val end: MutableSet<Port> = HashSet()
        for (ident in ctx.to) {
            val endSystem = parseSystemReference(ident.inst.text)
            val endVar =
                endSystem.getVariableByName(ident.port.text)
                    ?: throw RuntimeException(
                        String.format("Could not find variable %s", ident.port.text)
                    )
            end.add(endVar)
        }
        for (variable in end) {
            model.viewModelFactory.createSystemConnectionViewModelIn(currentSystem, start, variable)
        }
    }

    private fun buildContract(state: State, contract: PrepostContext?): Contract {
        val c = buildContract(state, contract!!.pre.text, contract.post.text)
        c.name = contract.name.text
        return c
    }

    private fun buildContract(state: State, pre: String, post: String): Contract {
        val newContract = Contract()
        val preCondition = Condition(pre)
        val postCondition = Condition(post)
        newContract.preCondition = preCondition
        newContract.postCondition = postCondition
        return newContract
    }

    private fun applySubstitution(currentSystem: System, toReplace: String, toReplaceWith: String) {
        val automaton = currentSystem.automaton
        for (state in automaton.states) {
            for (contract in state.contracts) {
                applySubstitution(contract.preCondition, toReplace, toReplaceWith)
                applySubstitution(contract.postCondition, toReplace, toReplaceWith)
            }
        }
    }

    private fun applySubstitution(condition: Condition, toReplace: String, toReplaceWith: String) {
        var con = condition.value
        // replace normal occurrences (var -> newVar)
        con = con.replace("\\b$toReplace\\b".toRegex(), toReplaceWith)
        // replace history occurrences (h_var_\d -> h_newVar_\d)
        con =
            con.replace(
                ("\\bh_" + toReplace + "_(\\d+)\\b").toRegex(),
                "h_" + toReplaceWith + "_$1"
            )
        condition.value = con
    }

    private fun cleanCode(code: String): String {
        return code.substring(CODE_BEGIN.length, code.length - CODE_BEGIN.length)
    }

    private fun parseSystemReference(name: String): System {
        return if (name == SELF_REFERENCE_TOKEN) {
            currentSystem
        } else {
            currentSystem.getChildByName(name)
                ?: error(String.format("Could not find system %s", name))
        }
    }

    private fun hasCyclicChildSystems(ctx: ModelContext): Boolean {
        for (system in ctx.system()) {
            val parents: MutableList<SystemContext?> = ArrayList()
            var currentParents = scout.getParents(system)
            while (currentParents != null && currentParents.isNotEmpty()) {
                parents.addAll(currentParents)
                currentParents = currentParents.map { scout.getParents(it) }.flatten()
                if (parents.contains(system)) {
                    return true
                }
            }
        }
        return false
    }

    private fun makeUserChooseSystem(systemNames: List<String>): String {
        val comboBox = ComboBox<String>()
        comboBox.items.addAll(systemNames)
        comboBox.promptText = "Choose a system"

        val vBox = VBox(USER_SYSTEM_CHOICE_VBOX_SPACING.toDouble())
        vBox.padding = Insets(USER_SYSTEM_CHOICE_VBOX_PADDING.toDouble())
        vBox.children.add(comboBox)

        val alert = Alert(Alert.AlertType.WARNING)
        alert.title = ResourceHandler.title
        alert.headerText = ResourceHandler.multiple_top_level_header
        alert.dialogPane.content = vBox

        alert.onCloseRequest = EventHandler { event: DialogEvent ->
            val result = alert.result
            if (result == ButtonType.OK && comboBox.value == null) {
                event.consume() // Prevent dialog from closing
            }
        }

        alert.showAndWait()

        return comboBox.value
    }
}

const val USER_SYSTEM_CHOICE_VBOX_SPACING = 10
const val USER_SYSTEM_CHOICE_VBOX_PADDING = 20
const val SELF_REFERENCE_TOKEN: String = "self"
const val CODE_BEGIN: String = "{="
const val CODE_END: String = "=}"

@JvmRecord
data class SystemInfo(val name: String, val type: String)
