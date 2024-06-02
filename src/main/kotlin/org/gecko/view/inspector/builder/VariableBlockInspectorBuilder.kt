package org.gecko.view.inspector.builder

import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.element.InspectorSeparator
import org.gecko.view.inspector.element.container.InspectorTypeLabel
import org.gecko.view.inspector.element.container.InspectorVisibilityPicker
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.view.inspector.element.textfield.InspectorVariableValueField
import org.gecko.viewmodel.PortViewModel

/**
 * Represents a type of [AbstractInspectorBuilder] of an [Inspector][org.gecko.view.inspector.Inspector] for
 * a [PortViewModel]. Adds to the list of
 * [InspectorElement][org.gecko.view.inspector.element.InspectorElement]s, which are added to a built
 * [Inspector][org.gecko.view.inspector.Inspector], the following: an [InspectorVisibilityPicker] and an
 * [InspectorTypeLabel].
 */
class VariableBlockInspectorBuilder(actionManager: ActionManager, viewModel: PortViewModel) :
    AbstractInspectorBuilder<PortViewModel?>(actionManager, viewModel) {
    init {
        // Visibility
        addInspectorElement(InspectorVisibilityPicker(actionManager, viewModel))
        addInspectorElement(InspectorSeparator())

        // Type
        addInspectorElement(InspectorTypeLabel(actionManager, viewModel))

        // Value
        addInspectorElement(InspectorLabel(ResourceHandler.Companion.variable_value))
        addInspectorElement(InspectorVariableValueField(actionManager, viewModel))
    }
}
