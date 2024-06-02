package org.gecko.view.inspector.element.button

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Tooltip
import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.PortViewModel

/**
 * Represents a type of [AbstractInspectorButton] used for removing a [PortViewModel].
 */
class InspectorRemoveVariableButton(actionManager: ActionManager, portViewModel: PortViewModel?) :
    AbstractInspectorButton() {
    init {
        styleClass.add(ICON_STYLE_NAME)
        tooltip = Tooltip(ResourceHandler.inspector_remove_variable)
        onAction = EventHandler { _: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createDeletePositionableViewModelElementAction(portViewModel!!)
            )
        }
    }

    companion object {
        const val ICON_STYLE_NAME = "inspector-remove-button"
    }
}
