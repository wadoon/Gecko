package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.ContractViewModel
import org.gecko.viewmodel.EdgeViewModel

/**
 * A concrete representation of an [Action] that changes the contract of an [EdgeViewModel], which it holds
 * a reference to. Additionally, holds the old and new [ContractViewModel]s of the edge for undo/redo purposes.
 */
class ChangeContractEdgeViewModelAction(
    val edgeViewModel: EdgeViewModel,
    val newContract: ContractViewModel?
) : Action() {
    var oldContract: ContractViewModel? = null

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        oldContract = edgeViewModel.contract
        edgeViewModel.contract = newContract
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createChangeContractEdgeViewModelAction(edgeViewModel, oldContract)
    }
}
