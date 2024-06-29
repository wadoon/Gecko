package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.Port

/**
 * A concrete representation of an [Action] that changes the type of a [Port], which it holds a
 * reference to. Additionally, holds the old and new [type][String]s of the contract for undo/redo purposes.
 */
class ChangeTypePortViewModelElementAction internal constructor(
    val Port: Port,
    val newType: String
) : Action() {
    val oldType: String = Port.type

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        if (newType.isEmpty()) {
            return false
        }
        Port.type = (newType)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createChangeTypePortViewModelElementAction(Port, oldType)
    }
}
