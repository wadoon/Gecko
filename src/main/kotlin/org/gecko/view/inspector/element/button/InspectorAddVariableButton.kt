package org.gecko.view.inspector.element.button

import javafx.event.EventHandler
import javafx.scene.control.Tooltip
import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.System
import org.gecko.viewmodel.Visibility

/**
 * Represents a type of [AbstractInspectorButton] used for adding a [PortViewModel] to a given
 * [System] with a given [Visibility].
 */
class InspectorAddVariableButton(
    actionManager: ActionManager,
    System: System,
    visibility: Visibility?
) : AbstractInspectorButton() {
    init {
        styleClass.add(STYLE)
        text = ResourceHandler.inspector_add_variable
        tooltip = Tooltip(ResourceHandler.inspector_add_variable)
        prefWidth = WIDTH.toDouble()
        onAction = EventHandler { _ ->
            actionManager.run(
                actionManager.actionFactory.createCreatePortViewModelElementAction(System)
            )
            // Newly added port is the last in the list.
            val addedPort = System.portsProperty.last()
            // This is not an action because it should not be undoable.
            addedPort.visibility = visibility!!
        }
    }

    companion object {
        const val STYLE = "inspector-add-button"
        const val WIDTH = 70
    }
}
