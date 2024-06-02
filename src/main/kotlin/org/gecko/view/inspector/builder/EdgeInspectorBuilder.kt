package org.gecko.view.inspector.builder

import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.element.InspectorSeparator
import org.gecko.view.inspector.element.combobox.InspectorContractComboBox
import org.gecko.view.inspector.element.container.InspectorEdgeStateLabel
import org.gecko.view.inspector.element.container.InspectorKindPicker
import org.gecko.view.inspector.element.container.InspectorPriorityLabel
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.viewmodel.EdgeViewModel

/**
 * Represents a type of [AbstractInspectorBuilder] of an [Inspector][org.gecko.view.inspector.Inspector] for
 * an [EdgeViewModel]. Adds to the list of
 * [InspectorElement][org.gecko.view.inspector.element.InspectorElement]s, which are added to a built
 * [Inspector][org.gecko.view.inspector.Inspector], the following: an [InspectorKindPicker], two
 * [InspectorEdgeStateLabel]s for the source- and target-states, an [InspectorPriorityLabel] and an
 * [InspectorContractComboBox].
 */
class EdgeInspectorBuilder(actionManager: ActionManager, viewModel: EdgeViewModel) :
    AbstractInspectorBuilder<EdgeViewModel?>(actionManager, viewModel) {
    init {
        // Kind
        addInspectorElement(InspectorKindPicker(actionManager, viewModel))
        addInspectorElement(InspectorSeparator())

        // Connected states
        addInspectorElement(
            InspectorEdgeStateLabel(
                actionManager, viewModel.source,
                ResourceHandler.Companion.source
            )
        )
        addInspectorElement(
            InspectorEdgeStateLabel(
                actionManager, viewModel.destination,
                ResourceHandler.Companion.target
            )
        )
        addInspectorElement(InspectorSeparator())

        // Priority
        addInspectorElement(InspectorPriorityLabel(actionManager, viewModel))
        addInspectorElement(InspectorSeparator())

        // Contracts
        addInspectorElement(InspectorLabel(ResourceHandler.Companion.contract_plural))
        addInspectorElement(InspectorContractComboBox(actionManager, viewModel))
    }
}
