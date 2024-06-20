package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.AutomatonViewModel
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
    val wasStartState: Boolean = false
) : Action() {
    val automaton: AutomatonViewModel = systemViewModel.automaton


    @Throws(GeckoException::class)
    override fun run(): Boolean {
        automaton.states.add(stateViewModel)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return DeleteStateViewModelElementAction(geckoViewModel, stateViewModel, systemViewModel)
    }
}
