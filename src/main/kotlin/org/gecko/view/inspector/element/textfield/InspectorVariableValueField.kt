package org.gecko.view.inspector.element.textfield

import org.gecko.actions.Action
import org.gecko.actions.ActionManager
import org.gecko.viewmodel.PortViewModel

/**
 * A concrete representation of an [InspectorTextField] for a [PortViewModel], through which the value of
 * the variable can be changed.
 */
class InspectorVariableValueField(actionManager: ActionManager, val portViewModel: PortViewModel) :
    InspectorTextField(
        portViewModel.valueProperty, actionManager
    ) {
    override val action: Action
        get() = actionManager.actionFactory.createChangeVariableValuePortViewModelAction(portViewModel, text)

    override fun updateText() {
        parent.requestFocus()
        if (text != null && text == portViewModel.value) {
            return
        }
        actionManager.run(action)
        text = portViewModel.value
    }
}
