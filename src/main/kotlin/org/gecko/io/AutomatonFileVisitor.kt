package org.gecko.io

import gecko.parser.SystemDefBaseVisitor
import gecko.parser.SystemDefParser.*
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.VBox
import org.gecko.exceptions.ModelException
import org.gecko.model.*
import org.gecko.view.ResourceHandler
import java.util.*

/**
 * Used for building a [GeckoModel] from a sys file. This class is a visitor for the ANTLR4 generated parser for
 * the sys file format. The entire [GeckoModel] can be built by calling [.visitModel] and passing it the
 * [SystemDefParser.ModelContext] of a sys file.
 */
class AutomatonFileVisitor : SystemDefBaseVisitor<Unit>() {

    var model: GeckoModel


    val warnings: MutableSet<String>

    var currentSystem: System
    var nextSystemName: String? = null
    var scout: AutomatonFileScout? = null
    var elementsCreated = 0u

    init {
        this.model = GeckoModel()
        this.warnings = TreeSet()
        currentSystem = model.root
    }

    override fun visitModel(ctx: ModelContext) {
        val scout = AutomatonFileScout(ctx)
        this.scout = scout

        if (hasCyclicChildSystems(ctx)) {
            throw RuntimeException("Cyclic child systems found")
        }
        val rootName: String?
        if (scout.rootChildren.size == 1) {
            rootName = scout.rootChildren.iterator().next().ident().text
        } else if (scout.rootChildren.size > 1) {
            val rootNames = scout.rootChildren.stream().map { ctx1: SystemContext -> ctx1.ident().text }
                .toList()
            rootName = makeUserChooseSystem(rootNames)
            if (rootName == null) {
                throw RuntimeException("No root system chosen")
            }
        } else {
            throw RuntimeException("No root system found")
        }
        scout.getSystem(rootName!!)!!.accept(this)
        val newRoot = model.root.children.first()
        newRoot.parent = null
        model = GeckoModel(newRoot)
        model.modelFactory.elementId = elementsCreated + 1u
        if (ctx.globalCode != null) {
            model.globalCode = cleanCode(ctx.globalCode.text)
        }
        if (ctx.defines() != null) {
            model.globalDefines = ctx.defines().text
        }
    }

    override fun visitSystem(ctx: SystemContext) {
        val system = buildSystem(ctx)
        currentSystem = system
        for (io in ctx.io()) {
            io.accept(this)
        }
        for (child in scout!!.getChildSystemInfos(ctx)) {
            if (scout!!.getSystem(child.type) == null) {
                throw RuntimeException(String.format("System %s not found", child.type))
            }
            val childCtx = scout!!.getSystem(child.type)
            nextSystemName = child.name
            childCtx!!.accept(this)
        }
        for (connection in ctx.connection()) {
            connection.accept(this)
        }
        if (ctx.use_contracts().isNotEmpty()) {
            if (ctx.use_contracts().size > 1 || ctx.use_contracts().first().use_contract().size > 1) {
                throw RuntimeException("Multiple automata in one system are not supported")
            }
            ctx.use_contracts().first().use_contract().first().accept(this)
        }
        currentSystem = system.parent!!
    }

    override fun visitUse_contract(ctx: Use_contractContext) {
        val automata = scout!!.getAutomaton(ctx.ident().text)
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
        if (currentSystem.variables.stream().noneMatch { variable: Variable -> variable.name == toReplaceWith }) {
            throw RuntimeException(
                String.format("Variable %s not found not found in system %s", toReplace, currentSystem.name)
            )
        }
        applySubstitution(currentSystem, toReplace, toReplaceWith)
    }

    override fun visitAutomata(ctx: AutomataContext) {
        if (ctx.history().isNotEmpty()) {
            warnings.add(String.format("Automaton %s has history, which is ignored", ctx.ident().text))
        }
        if (ctx.use_contracts().isNotEmpty()) {
            warnings.add(String.format("Automaton %s has use_contracts, which are be ignored", ctx.ident().text))
        }
        if (ctx.transition().isEmpty()) {
            warnings.add(String.format("Automaton %s has no transitions", ctx.ident().text))
        }
        for (transition in ctx.transition()) {
            transition.accept(this)
        }
        val startStateCandidates = currentSystem.automaton
            .states
            .filter { state: State -> state.name!!.matches(START_STATE_REGEX.toRegex()) }
        if (startStateCandidates.size > 1) {
            throw RuntimeException(String.format("Multiple start states found in automaton %s", ctx.ident().text))
        } else if (startStateCandidates.size == 1) {
            try {
                currentSystem.automaton.startState = startStateCandidates.first()
            } catch (e: ModelException) {
                throw RuntimeException(e.message)
            }
        } else {
            try {
                //this should always work because if we have a transition, we have a state
                currentSystem.automaton.startState = currentSystem.automaton.states.iterator().next()
            } catch (e: ModelException) {
                throw RuntimeException(e.message)
            }
        }
    }

    override fun visitTransition(ctx: TransitionContext) {
        if (ctx.vvguard() != null) {
            warnings.add(String.format("Transition %s has a vvguard, which is ignored", ctx.text))
        }
        val startName = ctx.from.text
        val endName = ctx.to.text
        var start = currentSystem.automaton.getStateByName(startName)
        if (start == null) {
            try {
                start = model.modelFactory.createState(currentSystem.automaton)
                elementsCreated++
                start.name = startName
            } catch (e: ModelException) {
                throw RuntimeException(e.message)
            }
        }
        var end = currentSystem.automaton.getStateByName(endName)
        if (end == null) {
            try {
                end = model.modelFactory.createState(currentSystem.automaton)
                elementsCreated++
                end.name = endName
            } catch (e: ModelException) {
                throw RuntimeException(e.message)
            }
        }
        val contract = if (ctx.contr != null) {
            buildContract(start, scout!!.getContract(ctx.contr.text))
        } else {
            buildContract(start, ctx.pre.text, ctx.post.text)
        }
        val edge: Edge
        try {
            edge = model.modelFactory.createEdge(currentSystem.automaton, start, end)
            elementsCreated++
        } catch (e: ModelException) {
            throw RuntimeException(e.message)
        }
        edge.contract = contract
    }

    override fun visitIo(ctx: IoContext) {
        val visibility = when (ctx.type.type) {
            INPUT -> Visibility.INPUT
            OUTPUT -> Visibility.OUTPUT
            STATE -> Visibility.STATE
            else -> throw IllegalStateException("Unexpected variable visibility: " + ctx.type.type)
        }
        for (variable in ctx.variable()) {
            if (!builtinTypes.contains(variable.t.text)) {
                if (visibility == Visibility.STATE) {
                    if (scout!!.getSystem(variable.t.text) != null) {
                        return
                    } else {
                        throw RuntimeException("State type must be a system or builtin type")
                    }
                } else {
                    throw RuntimeException("Input and Output type must be a builtin type")
                }
            }
            for (ident in variable.n) {
                if (currentSystem.getVariableByName(ident.Ident().text) != null
                    || scout!!.getSystem(ident.text) != null
                ) {
                    continue
                }
                try {
                    val v: Variable = model.modelFactory.createVariable(currentSystem)
                    elementsCreated++
                    v.name = (ident.Ident().text)
                    v.type = (variable.t.text)
                    v.visibility = (visibility)
                    if (variable.init != null) {
                        v.value = variable.init.text
                    }
                } catch (e: ModelException) {
                    throw RuntimeException(e.message)
                }
            }
        }
    }

    override fun visitConnection(ctx: ConnectionContext) {
        if (ctx.from.inst == null || ctx.to.stream().anyMatch { ident: IoportContext -> ident.inst == null }) {
            throw RuntimeException("Invalid System in connection")
        }
        val startSystem = parseSystemReference(ctx.from.inst.text)
        val start = startSystem.getVariableByName(ctx.from.port.text)
            ?: throw RuntimeException(String.format("Could not find variable %s", ctx.from.port.text))
        val end: MutableSet<Variable> = HashSet()
        for (ident in ctx.to) {
            val endSystem = parseSystemReference(ident.inst.text)
            val endVar = endSystem.getVariableByName(ident.port.text)
                ?: throw RuntimeException(String.format("Could not find variable %s", ident.port.text))
            end.add(endVar)
        }
        try {
            for (variable in end) {
                model.modelFactory.createSystemConnection(currentSystem, start, variable)
                elementsCreated++
            }
        } catch (e: ModelException) {
            throw RuntimeException(e.message)
        }
    }

    fun buildSystem(ctx: SystemContext): System {
        val system: System
        try {
            system = model.modelFactory.createSystem(currentSystem)
            elementsCreated++
        } catch (e: ModelException) {
            throw RuntimeException(e.message)
        }
        if (nextSystemName != null) {
            try {
                system.name = (nextSystemName)
            } catch (e: ModelException) {
                throw RuntimeException(e.message)
            }
            nextSystemName = null
        } else {
            try {
                system.name = ctx.ident().Ident().text
            } catch (e: ModelException) {
                throw RuntimeException(e.message)
            }
        }
        if (ctx.reaction() != null) {
            try {
                system.code = cleanCode(ctx.reaction().text)
            } catch (e: ModelException) {
                throw RuntimeException(e.message)
            }
        }
        return system
    }

    fun buildContract(state: State, contract: PrepostContext?): Contract {
        val c = buildContract(state, contract!!.pre.text, contract.post.text)
        try {
            c.name = contract.name.text
        } catch (e: ModelException) {
            throw RuntimeException(e.message)
        }
        return c
    }

    fun buildContract(state: State, pre: String, post: String): Contract {
        val newContract: Contract
        val preCondition: Condition
        val postCondition: Condition
        try {
            newContract = model.modelFactory.createContract(state)
            elementsCreated++
            preCondition = model.modelFactory.createCondition(pre)
            elementsCreated++
            postCondition = model.modelFactory.createCondition(post)
            elementsCreated++
            newContract.preCondition = preCondition
            newContract.postCondition = postCondition
        } catch (e: ModelException) {
            throw RuntimeException(e.message)
        }
        return newContract
    }

    fun applySubstitution(currentSystem: System, toReplace: String, toReplaceWith: String) {
        val automaton = currentSystem.automaton
        for (state in automaton.states) {
            for (contract in state.contracts) {
                applySubstitution(contract.preCondition, toReplace, toReplaceWith)
                applySubstitution(contract.postCondition, toReplace, toReplaceWith)
            }
        }
    }

    fun applySubstitution(condition: Condition, toReplace: String, toReplaceWith: String) {
        var con = condition.condition
        //replace normal occurrences (var -> newVar)
        con = con.replace("\\b$toReplace\\b".toRegex(), toReplaceWith)
        //replace history occurrences (h_var_\d -> h_newVar_\d)
        con = con.replace(("\\bh_" + toReplace + "_(\\d+)\\b").toRegex(), "h_" + toReplaceWith + "_$1")
        try {
            condition.condition = con
        } catch (e: ModelException) {
            throw RuntimeException("Failed to apply substitution")
        }
    }

    fun cleanCode(code: String): String {
        return code.substring(CODE_BEGIN.length, code.length - CODE_BEGIN.length)
    }

    fun parseSystemReference(name: String): System {
        val system: System?
        if (name == SELF_REFERENCE_TOKEN) {
            system = currentSystem
        } else {
            system = currentSystem.getChildByName(name)
            if (system == null) {
                error(String.format("Could not find system %s", name))
            }
        }
        return system
    }

    fun hasCyclicChildSystems(ctx: ModelContext): Boolean {
        for (system in ctx.system()) {
            val parents: MutableList<SystemContext?> = ArrayList()
            var currentParents = scout!!.getParents(system)
            while (currentParents != null && currentParents.isNotEmpty()) {
                parents.addAll(currentParents)
                currentParents = currentParents
                    .map { ctx -> scout!!.getParents(ctx) }
                    .flatten()
                if (parents.contains(system)) {
                    return true
                }
            }
        }
        return false
    }

    fun makeUserChooseSystem(systemNames: List<String?>): String? {
        val comboBox = ComboBox<String?>()
        comboBox.items.addAll(systemNames)
        comboBox.promptText = "Choose a system"

        val vBox = VBox(USER_SYSTEM_CHOICE_VBOX_SPACING.toDouble())
        vBox.padding = Insets(USER_SYSTEM_CHOICE_VBOX_PADDING.toDouble())
        vBox.children.add(comboBox)

        val alert = Alert(Alert.AlertType.WARNING)
        alert.title = ResourceHandler.Companion.title
        alert.headerText = ResourceHandler.Companion.multiple_top_level_header
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

    companion object {
        const val START_STATE_REGEX = "[a-z].*"
        const val USER_SYSTEM_CHOICE_VBOX_SPACING = 10
        const val USER_SYSTEM_CHOICE_VBOX_PADDING = 20
        const val SELF_REFERENCE_TOKEN: String = "self"
        const val CODE_BEGIN: String = "{="
        const val CODE_END: String = "=}"
    }
}
