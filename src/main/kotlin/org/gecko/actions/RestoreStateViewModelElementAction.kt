package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.Automaton
import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.StateViewModel
import org.gecko.viewmodel.System

/**
 * A concrete representation of an [Action] that restores a deleted [StateViewModel] in a given
 * [Automaton].
 */
class RestoreStateViewModelElementAction internal constructor(
    val gModel: GModel,
    val stateViewModel: StateViewModel,
    val System: System,
    val wasStartState: Boolean = false
) : Action() {
    val automaton: Automaton = System.automaton


    @Throws(GeckoException::class)
    override fun run(): Boolean {
        automaton.states.add(stateViewModel)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return DeleteStateViewModelElementAction(gModel, stateViewModel, System)
    }
}
