package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.System

class ChangeCodeSystemViewModelAction(val System: System, val newCode: String) :
    Action() {
    val oldCode = System.code

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        System.code = newCode
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createChangeCodeSystemViewModelAction(System, oldCode)
    }
}
