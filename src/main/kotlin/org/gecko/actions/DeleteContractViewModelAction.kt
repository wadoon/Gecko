package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.ContractViewModel
import org.gecko.viewmodel.EdgeViewModel
import org.gecko.viewmodel.StateViewModel

class DeleteContractViewModelAction internal constructor(
    val parent: StateViewModel,
    val contractViewModel: ContractViewModel?
) : Action() {
    var edgesWithContract: Set<EdgeViewModel> = setOf()

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        edgesWithContract = parent.outgoingEdges.filter { contractViewModel == it.contract }.toSet()
        for (edge in edgesWithContract) {
            edge.contract = null
            edge.updateTarget()
        }
        parent.removeContract(contractViewModel!!)
        parent.updateTarget()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createRestoreContractViewModelElementAction(parent, contractViewModel, edgesWithContract)
    }
}
