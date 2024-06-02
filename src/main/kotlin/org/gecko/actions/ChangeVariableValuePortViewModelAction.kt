package org.gecko.actions

import org.gecko.exceptions.ModelException
import org.gecko.viewmodel.PortViewModel

class ChangeVariableValuePortViewModelAction internal constructor(
    val portViewModel: PortViewModel,
    newValue: String?
) : Action() {
    var newValue: String?
    val oldValue: String

    init {
        this.newValue = newValue
        this.oldValue = portViewModel.value!!
    }

    @Throws(ModelException::class)
    override fun run(): Boolean {
        if (newValue == null || newValue!!.isEmpty()) {
            newValue = null
        }
        portViewModel.value = newValue
        portViewModel.updateTarget()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createChangeVariableValuePortViewModelAction(portViewModel, oldValue)
    }
}
