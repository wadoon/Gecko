package org.gecko.view.views.shortcuts

import javafx.event.EventHandler
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyEvent
import org.gecko.actions.ActionFactory
import org.gecko.actions.ActionManager
import org.gecko.tools.ToolType
import org.gecko.view.views.EditorView
import org.gecko.viewmodel.EditorViewModel
import java.util.function.Consumer

/**
 * An abstract representation of a handler for shortcut events, implementing the [EventHandler] interface, which
 * encapsulates a [KeyEvent]. Holds a reference to the current [ActionManager], the [ActionFactory]
 * and the [EditorView], as well as a map of [KeyCodeCombination]-keys and [Runnable]-values, which
 * allow for actions to be run by using keyboard shortcuts.
 */
abstract class ShortcutHandler protected constructor(
    protected var actionManager: ActionManager,
    protected var editorView: EditorView
) : EventHandler<KeyEvent> {
    protected var shortcuts: HashMap<KeyCodeCombination?, () -> Unit> = HashMap()
    protected var actionFactory: ActionFactory = actionManager.actionFactory

    init {
        addSelectStandardToolShortcuts()
        addZoomShortcuts()
        addSelectionShortcuts()
        addDeleteShortcuts()
        addUndoRedoShortcuts()
    }

    override fun handle(event: KeyEvent) {
        if (event.eventType == KeyEvent.KEY_PRESSED) {
            val shortcutAction = getShortcutAction(event)
            if (shortcutAction != null) {
                shortcutAction()
                event.consume()
            }
        }
    }

    fun getShortcutAction(event: KeyEvent) =
        shortcuts.keys.find { shortcut: KeyCodeCombination? -> shortcut!!.match(event) }?.let { shortcuts[it] }

    fun addSelectStandardToolShortcuts() {
        val standardTools = listOf(ToolType.CURSOR, ToolType.MARQUEE_TOOL, ToolType.PAN, ToolType.ZOOM_TOOL)
        standardTools.forEach { tool ->
            shortcuts[tool.keyCodeCombination] = { actionManager.run(actionFactory.createSelectToolAction(tool)) }
        }
    }

    fun addZoomShortcuts() {
        shortcuts[Shortcuts.ZOOM_IN.get()] = {
            actionManager.run(actionFactory.createZoomCenterAction(EditorViewModel.defaultZoomStep))
        }
        shortcuts[Shortcuts.ZOOM_OUT.get()] = {
            actionManager.run(actionFactory.createZoomCenterAction(1 / EditorViewModel.defaultZoomStep))
        }
    }

    fun addSelectionShortcuts() {
        shortcuts[Shortcuts.SELECT_ALL.get()] = {
            actionManager.run(
                actionFactory.createSelectAction(editorView.viewModel.positionableViewModelElements, true)
            )
        }

        shortcuts[Shortcuts.DESELECT_ALL.get()] = {
            actionManager.run(actionFactory.createSelectAction(setOf(), true))
        }

        shortcuts[Shortcuts.SELECTION_FORWARD.get()] = {
            actionManager.run(actionFactory.createSelectionHistoryForwardAction())
        }

        shortcuts[Shortcuts.SELECTION_BACK.get()] = {
            actionManager.run(actionFactory.createSelectionHistoryBackAction())
        }
        shortcuts[Shortcuts.FOCUS_SELECTED_ELEMENT.get()] = {
            editorView.viewModel.moveToFocusedElement()
        }
    }

    fun addDeleteShortcuts() {
        shortcuts[Shortcuts.DELETE.get()] = {
            actionManager.run(
                actionFactory.createDeletePositionableViewModelElementAction(
                    editorView.viewModel.selectionManager.currentSelection
                )
            )
        }
    }

    fun addUndoRedoShortcuts() {
        shortcuts[Shortcuts.UNDO.get()] = { actionManager.undo() }

        shortcuts[Shortcuts.REDO.get()] = { actionManager.redo() }
    }
}
