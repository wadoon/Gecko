package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.Contract
import org.gecko.viewmodel.Edge

/**
 * A concrete representation of an [Action] that changes the contract of an [Edge], which it holds a
 * reference to. Additionally, holds the old and new [Contract]s of the edge for undo/redo purposes.
 */
class ChangeContractEdgeViewModelAction(val Edge: Edge, val newContract: Contract?) : Action() {
    var oldContract: Contract? = null

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        oldContract = Edge.contract
        Edge.contract = newContract
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createChangeContractEdge(Edge, oldContract)
    }
}
