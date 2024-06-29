package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.Contract

/**
 * A concrete representation of an [Action] that changes the precondition of a [Contract], which it
 * holds a reference to. Additionally, holds the old and new [precondition][String]s of the contract for undo/redo
 * purposes.
 */
data class ChangePreconditionViewModelElementAction(
    val Contract: Contract,
    val newPrecondition: String
) : Action() {
    val oldPrecondition: String = Contract.preCondition.value

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        Contract.preCondition.value = newPrecondition
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory) =
        actionFactory.createChangePreconditionViewModelElementAction(Contract, oldPrecondition)
}
