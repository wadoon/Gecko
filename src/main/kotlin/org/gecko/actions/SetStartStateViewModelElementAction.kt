package org.gecko.actions

import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.State

/**
 * A concrete representation of an [Action] that sets a [State] as start state in the current
 * [SystemViewModel]. Additionally, holds the previous start-[State].
 */
data class SetStartStateViewModelElementAction(
    val gModel: GModel,
    val state: State,
    val value: Boolean
) : Action() {
    override fun run(): Boolean {
        state.isStartState = value
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory) =
        SetStartStateViewModelElementAction(gModel, state, value)
}
