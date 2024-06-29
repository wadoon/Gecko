package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.Contract

/**
 * A concrete representation of an [Action] that changes the postcondition of a [Contract], which
 * it holds a reference to. Additionally, holds the old and new [postcondition][String]s of the contract for
 * undo/redo purposes.
 */
class ChangePostconditionViewModelElementAction internal constructor(
    val Contract: Contract,
    val newPostcondition: String
) : Action() {
    val oldPostcondition: String = Contract.postCondition.value

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        Contract.postCondition.value = newPostcondition
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createChangePostconditionViewModelElementAction(Contract, oldPostcondition)
    }
}
