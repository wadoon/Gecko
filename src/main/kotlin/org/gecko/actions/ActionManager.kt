package org.gecko.actions

import javafx.scene.control.Alert


import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GModel
import java.util.*

/**
 * Represents a manager for the actions of the active [Gecko][org.gecko.application.Gecko]. Holds an
 * [ActionFactory], a stack of currently undoable [Action]s and a stack of currently redoable
 * [Action]s, thus providing methods for running, undoing and redoing actions.
 */
class ActionManager(gModel: GModel) {

    val actionFactory = ActionFactory(gModel)
    val undoStack = ArrayDeque<Action>()
    val redoStack = ArrayDeque<Action>()


    //var copyVisitor: CopyPositionableViewModelElementVisitor? = null

    /**
     * Undoes the last action and makes it redoable.
     */
    fun undo() {
        if (undoStack.isEmpty()) {
            return
        }
        val action = undoStack.removeFirst()
        try {
            if (!action.run()) {
                return
            }
        } catch (e: GeckoException) {
            showExceptionAlert(e.message)
            return
        }
        action.getUndoAction(actionFactory)?.let { undoAction ->
            redoStack.addFirst(undoAction)
        }
    }

    /**
     * Redoes the last undone action and makes it undoable again.
     */
    fun redo() {
        if (redoStack.isEmpty()) {
            return
        }
        val action: Action = redoStack.removeFirst()
        try {
            if (!action.run()) {
                return
            }
        } catch (e: GeckoException) {
            showExceptionAlert(e.message)
            return
        }
        val undoAction = action.getUndoAction(actionFactory)
        if (undoAction != null) {
            undoStack.addFirst(undoAction)
        }
    }

    /**
     * Runs the given action and makes it undoable if applicable.
     *
     * @param action The action to run.
     */
    fun run(action: Action) {
        try {
            if (!action.run()) {
                return
            }
        } catch (e: GeckoException) {
            showExceptionAlert(e.message)
            return
        }
        val undoAction = action.getUndoAction(actionFactory)
        if (undoAction != null) {
            undoStack.addFirst(undoAction)
            if (undoStack.size > MAX_STACK_SIZE) {
                undoStack.removeLast()
            }
            redoStack.clear()
        }
    }

    fun showExceptionAlert(message: String?) {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.title = "Error"
        alert.headerText = "An error occurred"
        alert.contentText = message
        alert.showAndWait()
    }

    companion object {
        const val MAX_STACK_SIZE = 1000
    }
}
