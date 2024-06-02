package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.model.Automaton
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.StateViewModel
import org.gecko.viewmodel.SystemViewModel

/**
 * A concrete representation of an [Action] that restores a deleted [StateViewModel] in a given
 * [Automaton].
 */
class RestoreStateViewModelElementAction internal constructor(
    val geckoViewModel: GeckoViewModel,
    val stateViewModel: StateViewModel,
    val systemViewModel: SystemViewModel,
    val wasStartState: Boolean
) : Action() {
    val automaton: Automaton = systemViewModel.target!!.automaton!!


    @Throws(GeckoException::class)
    override fun run(): Boolean {
        automaton.addState(stateViewModel.target!!)
        geckoViewModel.addViewModelElement(stateViewModel)
        if (wasStartState) {
            systemViewModel.startState = stateViewModel
            systemViewModel.updateTarget()
        }
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return DeleteStateViewModelElementAction(geckoViewModel, stateViewModel, systemViewModel)
    }
}
