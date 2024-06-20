package org.gecko.actions

import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.StateViewModel

/**
 * A concrete representation of an [Action] that sets a [StateViewModel] as start state in the current
 * [SystemViewModel]. Additionally, holds the previous start-[StateViewModel].
 */
data class SetStartStateViewModelElementAction(
    val geckoViewModel: GeckoViewModel, val stateViewModel: StateViewModel, val value: Boolean
) : Action() {
    override fun run(): Boolean {
        stateViewModel.isStartState = value
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory) =
        SetStartStateViewModelElementAction(geckoViewModel, stateViewModel, value)
}
