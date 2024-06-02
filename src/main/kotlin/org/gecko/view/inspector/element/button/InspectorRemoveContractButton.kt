package org.gecko.view.inspector.element.button

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Tooltip
import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.ContractViewModel
import org.gecko.viewmodel.StateViewModel

/**
 * Represents a type of [AbstractInspectorButton] used for removing a [ContractViewModel] from a
 * [StateViewModel].
 */
class InspectorRemoveContractButton(
    actionManager: ActionManager,
    stateViewModel: StateViewModel?,
    contractViewModel: ContractViewModel?
) : AbstractInspectorButton() {
    init {
        styleClass.add(ICON_STYLE_NAME)
        tooltip = Tooltip(ResourceHandler.Companion.inspector_remove_contract)
        onAction = EventHandler { event: ActionEvent? ->
            parent.requestFocus()
            actionManager.run(
                actionManager.actionFactory
                    .createDeleteContractViewModelAction(stateViewModel!!, contractViewModel)
            )
        }
    }

    companion object {
        const val ICON_STYLE_NAME = "inspector-remove-button"
    }
}
