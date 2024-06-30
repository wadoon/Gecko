package org.gecko.view.inspector.element.combobox

import org.gecko.actions.Action
import org.gecko.actions.ActionManager
import org.gecko.viewmodel.Port
import org.gecko.viewmodel.builtinTypes

/**
 * A concrete representation of an [InspectorComboBox] for a [Port], through which the type of the
 * port can be changed.
 */
class InspectorTypeComboBox(val actionManager: ActionManager, val viewModel: Port) :
    InspectorComboBox<String>(actionManager, builtinTypes, viewModel.typeProperty) {
    init {
        isEditable = true
    }

    override val action: Action
        get() =
            actionManager.actionFactory.createChangeTypePortViewModelElementAction(viewModel, value)
}
