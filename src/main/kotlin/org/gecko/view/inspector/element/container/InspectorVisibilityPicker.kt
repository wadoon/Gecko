package org.gecko.view.inspector.element.container

import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.element.combobox.InspectorVisibilityComboBox
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.viewmodel.Port

/**
 * Represents a type of [LabeledInspectorElement]. Contains an [InspectorLabel] and an
 * [InspectorVisibilityComboBox].
 */
class InspectorVisibilityPicker(actionManager: ActionManager, viewModel: Port) :
    LabeledInspectorElement(
        InspectorLabel(ResourceHandler.Companion.visibility),
        InspectorVisibilityComboBox(actionManager, viewModel)
    )
