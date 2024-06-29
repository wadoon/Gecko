package org.gecko.actions

import org.gecko.exceptions.ModelException
import org.gecko.viewmodel.Port

class ChangeVariableValuePortViewModelAction internal constructor(
    val Port: Port,
    newValue: String?
) : Action() {
    var newValue: String?
    val oldValue: String

    init {
        this.newValue = newValue
        this.oldValue = Port.value!!
    }

    @Throws(ModelException::class)
    override fun run(): Boolean {
        if (newValue == null || newValue!!.isEmpty()) {
            newValue = null
        }
        Port.value = newValue
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createChangeVariableValuePortViewModelAction(Port, oldValue)
    }
}
