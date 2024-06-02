package org.gecko.view.inspector.element.combobox

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.ComboBox
import org.gecko.actions.*
import org.gecko.view.inspector.element.InspectorElement
import org.gecko.viewmodel.EdgeViewModel

class InspectorContractComboBox(actionManager: ActionManager, viewModel: EdgeViewModel) : ComboBox<String>(),
    InspectorElement<ComboBox<String>> {
    init {
        prefWidth = PREF_WIDTH.toDouble()
        items.setAll(viewModel.source.contracts.map { it.name })
        viewModel.source.contractsProperty.addListener { _, _, _ ->
            items.setAll(viewModel.source.contracts.map { it.name })
        }
        value = if (viewModel.contract == null) null else viewModel.contract!!.name
        viewModel.contractProperty.addListener { _, _, newValue ->
            value = newValue?.name
        }

        onAction = EventHandler { event: ActionEvent? ->
            if (value == null || (viewModel.contract != null && value == viewModel.contract!!.name)) {
                return@EventHandler
            }
            val newContract = viewModel.source.contracts.find { it.name == value }
            actionManager.run(
                actionManager.actionFactory.createChangeContractEdgeViewModelAction(viewModel, newContract)
            )
        }
    }

    override val control = this

    companion object {
        const val PREF_WIDTH = 300
    }
}
