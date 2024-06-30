package org.gecko.view.inspector.element.button

import javafx.event.EventHandler
import javafx.scene.control.Tooltip
import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.views.shortcuts.Shortcuts
import org.gecko.viewmodel.PositionableElement

/** Represents a type of [AbstractInspectorButton] used for deleting a [PositionableElement]. */
class InspectorDeleteButton(actionManager: ActionManager, elementToRemove: PositionableElement?) :
    AbstractInspectorButton() {
    init {
        onAction = EventHandler { event ->
            actionManager.run(actionManager.actionFactory.createDeleteAction(elementToRemove!!))
        }
        text = ResourceHandler.delete
        val toolTip = "%s (%s)".format(ResourceHandler.delete, Shortcuts.DELETE.get().displayText)
        tooltip = Tooltip(toolTip)
        prefWidth = WIDTH.toDouble()
        styleClass.add(STYLE)
    }

    companion object {
        const val STYLE = "inspector-delete-button"
        const val WIDTH = 300
    }
}
