package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.SystemViewModel

class ChangeCodeSystemViewModelAction(val systemViewModel: SystemViewModel, val newCode: String) :
    Action() {
    val oldCode = systemViewModel.code

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        systemViewModel.code = newCode
        systemViewModel.updateTarget()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createChangeCodeSystemViewModelAction(systemViewModel, oldCode)
    }
}
