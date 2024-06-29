package org.gecko.view.inspector.element.container

import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.view.inspector.element.textfield.InspectorPriorityField
import org.gecko.viewmodel.Edge

/**
 * Represents a type of [LabeledInspectorElement]. Contains an [InspectorLabel] and an
 * [InspectorPriorityField].
 */
class InspectorPriorityLabel(actionManager: ActionManager, viewModel: Edge) : LabeledInspectorElement(
    InspectorLabel(ResourceHandler.Companion.priority),
    InspectorPriorityField(actionManager, viewModel)
)
