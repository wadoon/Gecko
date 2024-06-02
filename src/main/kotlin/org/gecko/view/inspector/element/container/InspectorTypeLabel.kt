package org.gecko.view.inspector.element.container

import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.element.combobox.InspectorTypeComboBox
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.viewmodel.PortViewModel

/**
 * Represents a type of [LabeledInspectorElement]. Contains an [InspectorLabel] and an
 * [InspectorTypeComboBox].
 */
class InspectorTypeLabel(actionManager: ActionManager, viewModel: PortViewModel) : LabeledInspectorElement(
    InspectorLabel(ResourceHandler.Companion.type),
    InspectorTypeComboBox(actionManager, viewModel)
)
