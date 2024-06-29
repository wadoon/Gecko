package org.gecko.view.views.shortcuts

import org.gecko.actions.ActionManager
import org.gecko.tools.ToolType
import org.gecko.view.views.EditorView
import org.gecko.viewmodel.System

/**
 * A concrete representation of a [ShortcutHandler] that manages the shortcuts corresponding to the navigation of
 * and the selection of creator tools specific to a system editor view.
 */
class SystemEditorViewShortcutHandler(actionManager: ActionManager, editorView: EditorView) :
    ShortcutHandler(actionManager, editorView) {
    init {
        addNavigateSystemShortcuts()
        addCreatorShortcuts()
    }

    fun addNavigateSystemShortcuts() {
        shortcuts[Shortcuts.OPEN_CHILD_SYSTEM_EDITOR.get()] = {
            val focusedElement = editorView.viewModel.focusedElement
            val System = focusedElement as System
            actionManager.run(actionFactory.createViewSwitchAction(System, false))
        }
    }

    fun addCreatorShortcuts() {
        val creatorTools = listOf(ToolType.SYSTEM_CREATOR, ToolType.CONNECTION_CREATOR, ToolType.VARIABLE_BLOCK_CREATOR)
        creatorTools.forEach { tool: ToolType ->
            shortcuts[tool.keyCodeCombination] = { actionManager.run(actionFactory.createSelectToolAction(tool)) }
        }
    }
}

