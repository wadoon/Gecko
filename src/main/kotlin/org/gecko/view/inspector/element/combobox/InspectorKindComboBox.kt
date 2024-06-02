package org.gecko.view.inspector.element.combobox

import org.gecko.actions.Action
import org.gecko.actions.ActionManager
import org.gecko.model.Kind
import org.gecko.viewmodel.EdgeViewModel
import java.util.*

/**
 * Represents a type of [InspectorComboBox] encapsulating a [Kind]. Holds a reference to an
 * [EdgeViewModel] and the current [ActionManager].
 */
class InspectorKindComboBox(val actionManager: ActionManager, val viewModel: EdgeViewModel) :
    InspectorComboBox<Kind?>(
        actionManager, Arrays.stream(Kind.entries.toTypedArray()).toList(), viewModel.kindProperty
    ) {
    override val action: Action
        get() = actionManager.actionFactory.createChangeKindAction(viewModel, value)
}
