package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.PortViewModel

/**
 * A concrete representation of an [Action] that changes the type of a [PortViewModel], which it holds a
 * reference to. Additionally, holds the old and new [type][String]s of the contract for undo/redo purposes.
 */
class ChangeTypePortViewModelElementAction internal constructor(
    val portViewModel: PortViewModel,
    val newType: String
) : Action() {
    val oldType: String = portViewModel.type

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        if (newType.isEmpty()) {
            return false
        }
        portViewModel.type = (newType)
        portViewModel.updateTarget()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createChangeTypePortViewModelElementAction(portViewModel, oldType)
    }
}
