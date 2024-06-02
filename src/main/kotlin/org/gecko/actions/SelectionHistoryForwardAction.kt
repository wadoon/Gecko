package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.SelectionManager

/**
 * A concrete representation of an [Action] that navigates forward in the [SelectionManager].
 */
class SelectionHistoryForwardAction(val selectionManager: SelectionManager) : Action() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        selectionManager.goForward()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action? {
        return null
    }
}
