package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.ContractViewModel
import org.gecko.viewmodel.EdgeViewModel
import org.gecko.viewmodel.StateViewModel

/**
 * A concrete representation of an [Action] that restores a deleted [ContractViewModel] in a given
 * parent-[StateViewModel].
 */
class RestoreContractViewModelElementAction internal constructor(
    val parent: StateViewModel,
    val contractViewModel: ContractViewModel?,
    val edgesWithContract: Set<EdgeViewModel>?
) : Action() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        for (edge in edgesWithContract!!) {
            edge.contract = contractViewModel
            edge.updateTarget()
        }
        parent.addContract(contractViewModel!!)
        parent.updateTarget()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createDeleteContractViewModelAction(parent, contractViewModel)
    }
}
