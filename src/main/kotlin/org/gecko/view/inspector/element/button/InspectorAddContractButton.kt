package org.gecko.view.inspector.element.button

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Tooltip
import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.State

/**
 * Represents a type of [AbstractInspectorButton] used for adding a
 * [ContractViewModel][org.gecko.viewmodel.Contract] to a given [State].
 */
class InspectorAddContractButton(actionManager: ActionManager, state: State) :
    AbstractInspectorButton() {
    init {
        styleClass.add(STYLE)
        text = ResourceHandler.Companion.inspector_add_contract
        tooltip = Tooltip(ResourceHandler.Companion.inspector_add_contract)
        prefWidth = WIDTH.toDouble()
        onAction = EventHandler { event: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createCreateContractViewModelElementAction(state)
            )
        }
    }

    companion object {
        const val STYLE = "inspector-add-button"
        const val WIDTH = 70
    }
}
