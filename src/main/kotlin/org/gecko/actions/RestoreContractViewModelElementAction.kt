package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.Contract
import org.gecko.viewmodel.Edge
import org.gecko.viewmodel.StateViewModel

/**
 * A concrete representation of an [Action] that restores a deleted [Contract] in a given
 * parent-[StateViewModel].
 */
class RestoreContractViewModelElementAction internal constructor(
    val parent: StateViewModel,
    val Contract: Contract?,
    val edgesWithContract: Set<Edge>?
) : Action() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        for (edge in edgesWithContract!!) {
            edge.contract = Contract
        }
        parent.addContract(Contract!!)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createDeleteContractViewModelAction(parent, Contract)
    }
}
