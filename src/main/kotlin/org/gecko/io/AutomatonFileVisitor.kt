package org.gecko.io

import gecko.parser.SystemDefBaseVisitor
import gecko.parser.SystemDefParser.*
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.VBox
import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.*
import java.util.*

/**
 * Used for building a [GeckoViewModel] from a sys file. This class is a visitor for the ANTLR4 generated parser for
 * the sys file format. The entire [GeckoViewModel] can be built by calling [.visitModel] and passing it the
 * [SystemDefParser.ModelContext] of a sys file.
 */
class AutomatonFileVisitor : SystemDefBaseVisitor<Unit>() {
    var model: GeckoViewModel = GeckoViewModel()
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
        model = GeckoViewModel(newRoot)
        if (ctx.globalCode != null) {
            model.globalCode = cleanCode(ctx.globalCode.text)
        }

        if (ctx.defines() != null) {
            val constants = ctx.defines().variable().flatMap {
                it.n.map { name ->
                    Constant(name.text, it.t.text, it.init.text)
                }
            }
            model.globalDefines.setAll(constants)
        }
    }

    override fun visitSystem(ctx: SystemContext) {
        val system: SystemViewModel = currentSystem.createSubSystem()
        system.name = instanceName ?: ctx.ident().Ident().text
        if (ctx.reaction() != null) {
            system.code = cleanCode(ctx.reaction().text)
        }
        currentSystem = system

        for (io in ctx.io()) io.accept(this)

        scout.getChildSystemInfos(ctx)
            .forEach {
                val ctx = scout.getSystem(it.type) ?: error(String.format("System %s not found", it.type))
                instanceName = it.name
                ctx.accept(this)
            }

        ctx.connection().forEach { it.accept(this) }

        if (ctx.use_contracts().isNotEmpty()) {
            require(ctx.use_contracts().size == 1 && ctx.use_contracts().first().use_contract().size == 1) {
                "Multiple automata in one system are not supported"
            }
            ctx.use_contracts().first().use_contract().first().accept(this)
        }
        currentSystem = system.parent!!
    }

    override fun visitUse_contract(ctx: Use_contractContext) {
        val automata = scout.getAutomaton(ctx.ident().text)
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
        if (currentSystem.ports.stream().noneMatch { variable -> variable.name == toReplaceWith }) {
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

        for (transition in ctx.transition())
            transition.accept(this)

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
        val contract = if (ctx.contr != null) {
            buildContract(start, scout.getContract(ctx.contr.text))
        } else {
            buildContract(start, ctx.pre.text, ctx.post.text)
        }
        val edge: EdgeViewModel = currentSystem.automaton.createEdge(start, end)

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
                if (currentSystem.getVariableByName(ident.Ident().text) != null || scout.getSystem(ident.text) != null
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
        if (ctx.from.inst == null || ctx.to.stream().anyMatch { ident: IoportContext -> ident.inst == null }) {
            throw RuntimeException("Invalid System in connection")
        }
        val startSystem = parseSystemReference(ctx.from.inst.text)
        val start = startSystem.getVariableByName(ctx.from.port.text)
            ?: throw RuntimeException(String.format("Could not find variable %s", ctx.from.port.text))
        val end: MutableSet<PortViewModel> = HashSet()
        for (ident in ctx.to) {
            val endSystem = parseSystemReference(ident.inst.text)
            val endVar = endSystem.getVariableByName(ident.port.text)
                ?: throw RuntimeException(String.format("Could not find variable %s", ident.port.text))
            end.add(endVar)
        }
        for (variable in end) {
            model.viewModelFactory.createSystemConnectionViewModelIn(currentSystem, start, variable)

        }

    }

    private fun buildContract(state: StateViewModel, contract: PrepostContext?): ContractViewModel {
        val c = buildContract(state, contract!!.pre.text, contract.post.text)
        c.name = contract.name.text
        return c
    }

    private fun buildContract(state: StateViewModel, pre: String, post: String): ContractViewModel {
        val newContract = ContractViewModel()
        val preCondition = Condition(pre)
        val postCondition = Condition(post)
        newContract.preCondition = preCondition
        newContract.postCondition = postCondition
        return newContract
    }

    private fun applySubstitution(currentSystem: SystemViewModel, toReplace: String, toReplaceWith: String) {
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
        //replace normal occurrences (var -> newVar)
        con = con.replace("\\b$toReplace\\b".toRegex(), toReplaceWith)
        //replace history occurrences (h_var_\d -> h_newVar_\d)
        con = con.replace(("\\bh_" + toReplace + "_(\\d+)\\b").toRegex(), "h_" + toReplaceWith + "_$1")
        condition.value = con
    }

    private fun cleanCode(code: String): String {
        return code.substring(CODE_BEGIN.length, code.length - CODE_BEGIN.length)
    }

    private fun parseSystemReference(name: String): SystemViewModel {
        return if (name == SELF_REFERENCE_TOKEN) {
            currentSystem
        } else {
            currentSystem.getChildByName(name) ?: error(String.format("Could not find system %s", name))
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

    companion object {
        const val USER_SYSTEM_CHOICE_VBOX_SPACING = 10
        const val USER_SYSTEM_CHOICE_VBOX_PADDING = 20
        const val SELF_REFERENCE_TOKEN: String = "self"
        const val CODE_BEGIN: String = "{="
        const val CODE_END: String = "=}"
    }
}
