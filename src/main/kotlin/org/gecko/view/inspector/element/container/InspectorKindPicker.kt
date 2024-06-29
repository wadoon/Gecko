package org.gecko.view.inspector.element.container

import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.element.combobox.InspectorKindComboBox
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.viewmodel.Edge

/**
 * Represents a type of [LabeledInspectorElement]. Contains an [InspectorLabel] and an
 * [InspectorKindComboBox].
 */
class InspectorKindPicker(actionManager: ActionManager, viewModel: Edge) : LabeledInspectorElement(
    InspectorLabel(ResourceHandler.Companion.kind),
    InspectorKindComboBox(actionManager, viewModel)
)
