package org.gecko.view.inspector.element.button

import javafx.event.EventHandler
import javafx.scene.control.Tooltip
import org.gecko.actions.ActionManager
import org.gecko.model.Visibility
import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.SystemViewModel

/**
 * Represents a type of [AbstractInspectorButton] used for adding a [PortViewModel] to a given
 * [SystemViewModel] with a given [Visibility].
 */
class InspectorAddVariableButton(
    actionManager: ActionManager,
    systemViewModel: SystemViewModel,
    visibility: Visibility?
) : AbstractInspectorButton() {
    init {
        styleClass.add(STYLE)
        text = ResourceHandler.inspector_add_variable
        tooltip = Tooltip(ResourceHandler.inspector_add_variable)
        prefWidth = WIDTH.toDouble()
        onAction = EventHandler { event ->
            actionManager.run(actionManager.actionFactory.createCreatePortViewModelElementAction(systemViewModel))
            // Newly added port is the last in the list.
            val addedPort = systemViewModel.portsProperty.last()
            // This is not an action because it should not be undoable.
            addedPort.visibility = visibility!!
            try {
                addedPort.updateTarget()
            } catch (e: Exception) {
                throw RuntimeException("Failed while changing a port's visibility. This should never happen.")
            }
        }
    }

    companion object {
        const val STYLE = "inspector-add-button"
        const val WIDTH = 70
    }
}
