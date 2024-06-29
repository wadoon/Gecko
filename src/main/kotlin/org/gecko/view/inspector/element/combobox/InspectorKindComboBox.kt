package org.gecko.view.inspector.element.combobox

import org.gecko.actions.Action
import org.gecko.actions.ActionManager
import org.gecko.viewmodel.Edge
import org.gecko.viewmodel.Kind

/**
 * Represents a type of [InspectorComboBox] encapsulating a [Kind]. Holds a reference to an
 * [Edge] and the current [ActionManager].
 */
class InspectorKindComboBox(val actionManager: ActionManager, val viewModel: Edge) :
    InspectorComboBox<Kind>(actionManager, Kind.entries, viewModel.kindProperty) {
    override val action: Action
        get() = actionManager.actionFactory.createChangeKindAction(viewModel, value)
}
