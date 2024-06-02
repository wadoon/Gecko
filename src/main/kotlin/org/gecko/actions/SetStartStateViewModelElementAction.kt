package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.StateViewModel

/**
 * A concrete representation of an [Action] that sets a [StateViewModel] as start state in the current
 * [SystemViewModel]. Additionally, holds the previous start-[StateViewModel].
 */
class SetStartStateViewModelElementAction internal constructor(
    val geckoViewModel: GeckoViewModel,
    val stateViewModel: StateViewModel?
) : Action() {
    var previousStartState: StateViewModel? = null

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        val systemViewModel = geckoViewModel.currentEditor!!.currentSystem
        previousStartState = systemViewModel.startState
        systemViewModel.startState = stateViewModel
        systemViewModel.updateTarget()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createSetStartStateViewModelElementAction(previousStartState)
    }
}
