package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.ContractViewModel

/**
 * A concrete representation of an [Action] that changes the precondition of a [ContractViewModel], which it
 * holds a reference to. Additionally, holds the old and new [precondition][String]s of the contract for undo/redo
 * purposes.
 */
class ChangePreconditionViewModelElementAction internal constructor(
    val contractViewModel: ContractViewModel,
    val newPrecondition: String
) : Action() {
    val oldPrecondition: String = contractViewModel.preCondition.value

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        if (newPrecondition.isEmpty()) {
            return false
        }
        contractViewModel.preCondition.value = newPrecondition
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createChangePreconditionViewModelElementAction(contractViewModel, oldPrecondition)
    }
}
