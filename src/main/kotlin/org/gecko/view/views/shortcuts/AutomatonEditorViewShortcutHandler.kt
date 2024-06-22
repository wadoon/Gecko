package org.gecko.view.views.shortcuts

import org.gecko.actions.ActionManager
import org.gecko.tools.ToolType
import org.gecko.view.views.EditorView
import java.util.List
import java.util.function.Consumer

/**
 * A concrete representation of a [ShortcutHandler] that manages the shortcuts corresponding to the selection of
 * creator tools specific to an automaton editor view.
 */
class AutomatonEditorViewShortcutHandler(actionManager: ActionManager, editorView: EditorView) :
    ShortcutHandler(actionManager, editorView) {
    init {
        addCreatorShortcuts()
    }

    fun addCreatorShortcuts() {
        val creatorTools = List.of(ToolType.STATE_CREATOR, ToolType.EDGE_CREATOR, ToolType.REGION_CREATOR)
        creatorTools.forEach { tool: ToolType ->
            shortcuts[tool.keyCodeCombination] =
                { actionManager.run(actionFactory.createSelectToolAction(tool)) }
        }
    }
}
