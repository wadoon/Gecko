package org.gecko.view.inspector.element.combobox

import org.gecko.actions.Action
import org.gecko.actions.ActionManager
import org.gecko.viewmodel.Port
import org.gecko.viewmodel.Visibility

/**
 * Represents a type of [InspectorComboBox] encapsulating a [Visibility]. Holds a reference to a
 * current [Port] and the current [ActionManager].
 */
class InspectorVisibilityComboBox(val actionManager: ActionManager, val viewModel: Port) :
    InspectorComboBox<Visibility>(actionManager, Visibility.entries, viewModel.visibilityProperty) {
    override val action: Action
        get() = actionManager.actionFactory.changeVisibility(viewModel, value!!)
}
