package org.gecko.actions

import org.gecko.viewmodel.EditorViewModel
import org.gecko.viewmodel.SelectionManager

class DeselectAction internal constructor(editorViewModel: EditorViewModel) : Action() {
    val selectionManager: SelectionManager = editorViewModel.selectionManager

    override fun run(): Boolean {
        selectionManager.deselectAll()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action? {
        return null
    }
}
