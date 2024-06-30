package org.gecko.actions

import org.gecko.viewmodel.Automaton
import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.State
import org.gecko.viewmodel.System

/**
 * A concrete representation of an [Action] that restores a deleted [State] in a given [Automaton].
 */
class RestoreStateAction(val gModel: GModel, val state: State, val system: System) : Action() {
    val automaton: Automaton = system.automaton

    override fun run(): Boolean {
        automaton.states.add(state)
        gModel.currentEditor.updateRegions()
        gModel.updateEditors()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory) =
        DeleteStateViewModelElementAction(gModel, state, system)
}
