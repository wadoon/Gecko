package org.gecko.view.inspector.element.button

import javafx.event.EventHandler
import javafx.scene.control.Tooltip
import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.PositionableViewModelElement

/**
 * Represents a type of [AbstractInspectorButton] used for focusing on a [PositionableViewModelElement].
 */
class InspectorFocusButton(actionManager: ActionManager, element: PositionableViewModelElement?) :
    AbstractInspectorButton() {
    init {
        styleClass.add(ICON_STYLE_NAME)
        tooltip = Tooltip(ResourceHandler.inspector_focus_element)
        onAction = EventHandler {
            actionManager.run(actionManager.actionFactory.createFocusPositionableViewModelElementAction(element!!))
        }
    }

    companion object {
        const val ICON_STYLE_NAME = "inspector-focus-element-button"
    }
}
