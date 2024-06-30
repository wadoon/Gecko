package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.Contract
import org.gecko.viewmodel.Edge
import org.gecko.viewmodel.State

class DeleteContractViewModelAction internal constructor(
    val parent: State,
    val Contract: Contract?
) : Action() {
    var edgesWithContract: Set<Edge> = setOf()

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        edgesWithContract = parent.outgoingEdges.filter { Contract == it.contract }.toSet()
        for (edge in edgesWithContract) {
            edge.contract = null
        }
        parent.removeContract(Contract!!)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createRestoreContractViewModelElementAction(parent, Contract, edgesWithContract)
    }
}
