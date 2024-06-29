package org.gecko.view.inspector.element.combobox

import javafx.event.EventHandler
import javafx.scene.control.ComboBox
import org.gecko.actions.ActionManager
import org.gecko.view.inspector.element.InspectorElement
import org.gecko.viewmodel.Edge

class InspectorContractComboBox(actionManager: ActionManager, edge: Edge) : ComboBox<String>(),
    InspectorElement<ComboBox<String>> {
    init {
        prefWidth = PREF_WIDTH.toDouble()
        val source = edge.source
        items.setAll(source.contracts.map { it.name })
        source.contractsProperty.addListener { _, _, _ ->
            items.setAll(source.contracts.map { it.name })
        }
        value = if (edge.contract == null) null else edge.contract!!.name
        edge.contractProperty.addListener { _, _, newValue ->
            value = newValue?.name
        }

        onAction = EventHandler {
            if (value == null || (edge.contract != null && value == edge.contract!!.name)) {
                return@EventHandler
            }
            val newContract = source.contracts.find { it.name == value }
            actionManager.run(
                actionManager.actionFactory.createChangeContractEdge(edge, newContract)
            )
        }
    }

    override val control = this

    companion object {
        const val PREF_WIDTH = 300
    }
}
