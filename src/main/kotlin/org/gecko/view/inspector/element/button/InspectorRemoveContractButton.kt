package org.gecko.view.inspector.element.button

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Tooltip
import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.Contract
import org.gecko.viewmodel.State

/** Represents a type of [AbstractInspectorButton] used for removing a [Contract] from a [State]. */
class InspectorRemoveContractButton(
    actionManager: ActionManager,
    state: State?,
    Contract: Contract?
) : AbstractInspectorButton() {
    init {
        styleClass.add(ICON_STYLE_NAME)
        tooltip = Tooltip(ResourceHandler.Companion.inspector_remove_contract)
        onAction = EventHandler { event: ActionEvent? ->
            parent.requestFocus()
            actionManager.run(
                actionManager.actionFactory.createDeleteContractViewModelAction(state!!, Contract)
            )
        }
    }

    companion object {
        const val ICON_STYLE_NAME = "inspector-remove-button"
    }
}
