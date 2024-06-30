package org.gecko.view.inspector.element.container

import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.element.combobox.InspectorTypeComboBox
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.viewmodel.Port

/**
 * Represents a type of [LabeledInspectorElement]. Contains an [InspectorLabel] and an
 * [InspectorTypeComboBox].
 */
class InspectorTypeLabel(actionManager: ActionManager, viewModel: Port) :
    LabeledInspectorElement(
        InspectorLabel(ResourceHandler.Companion.type),
        InspectorTypeComboBox(actionManager, viewModel)
    )
