package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.SelectionManager

/**
 * A concrete representation of an [Action] that navigates back in the [SelectionManager].
 */
class SelectionHistoryBackAction internal constructor(val selectionManager: SelectionManager) : Action() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        selectionManager.goBack()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action? {
        return null
    }
}
