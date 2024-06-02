package org.gecko.view.inspector.element.textfield

import org.gecko.actions.Action
import org.gecko.actions.ActionManager
import org.gecko.viewmodel.ContractViewModel

/**
 * A concrete representation of an [InspectorAreaField] for a [ContractViewModel], through which the
 * postcondition of the contract can be changed.
 */
class InspectorPostconditionField(
    val actionManager: ActionManager,
    val contractViewModel: ContractViewModel
) : InspectorAreaField(
    actionManager, contractViewModel.postConditionProperty, false
) {
    override val action: Action
        get() = actionManager.actionFactory
            .createChangePostconditionViewModelElementAction(contractViewModel, text)
}
