package org.gecko.view.inspector.element.combobox

import org.gecko.actions.*
import org.gecko.model.*
import org.gecko.viewmodel.PortViewModel

/**
 * A concrete representation of an [InspectorComboBox] for a [PortViewModel], through which the type of the
 * port can be changed.
 */
class InspectorTypeComboBox(val actionManager: ActionManager, val viewModel: PortViewModel) : InspectorComboBox<String>(
    actionManager, builtinTypes, viewModel.typeProperty
) {
    init {
        isEditable = true
    }

    override val action: Action
        get() = actionManager.actionFactory.createChangeTypePortViewModelElementAction(viewModel, value)
}
