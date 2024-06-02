package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.ContractViewModel

/**
 * A concrete representation of an [Action] that changes the postcondition of a [ContractViewModel], which
 * it holds a reference to. Additionally, holds the old and new [postcondition][String]s of the contract for
 * undo/redo purposes.
 */
class ChangePostconditionViewModelElementAction internal constructor(
    val contractViewModel: ContractViewModel,
    val newPostcondition: String
) : Action() {
    val oldPostcondition: String = contractViewModel.postcondition

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        if (newPostcondition.isEmpty()) {
            return false
        }
        contractViewModel.postcondition = (newPostcondition)
        contractViewModel.updateTarget()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createChangePostconditionViewModelElementAction(contractViewModel, oldPostcondition)
    }
}
