package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.tools.*
import org.gecko.viewmodel.EditorViewModel

/**
 * A concrete representation of an [Action] that changes the active [org.gecko.tools.Tool] in the current
 * [EditorViewModel].
 */
class SelectToolAction(val editorViewModel: EditorViewModel, val tool: ToolType) : Action() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        editorViewModel.setCurrentTool(tool)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action? {
        return null
    }
}
