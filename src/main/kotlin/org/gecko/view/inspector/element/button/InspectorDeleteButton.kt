package org.gecko.view.inspector.element.button

import javafx.event.EventHandler
import javafx.scene.control.Tooltip
import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.views.shortcuts.Shortcuts
import org.gecko.viewmodel.PositionableViewModelElement

/**
 * Represents a type of [AbstractInspectorButton] used for deleting a [PositionableViewModelElement].
 */
class InspectorDeleteButton(actionManager: ActionManager, elementToRemove: PositionableViewModelElement?) :
    AbstractInspectorButton() {
    init {
        onAction = EventHandler { event ->
            actionManager.run(
                actionManager.actionFactory.createDeletePositionableViewModelElementAction(elementToRemove!!)
            )
        }
        text = ResourceHandler.delete
        val toolTip = "%s (%s)".format(
            ResourceHandler.delete,
            Shortcuts.DELETE.get().displayText
        )
        tooltip = Tooltip(toolTip)
        prefWidth = WIDTH.toDouble()
        styleClass.add(STYLE)
    }

    companion object {
        const val STYLE = "inspector-delete-button"
        const val WIDTH = 300
    }
}
