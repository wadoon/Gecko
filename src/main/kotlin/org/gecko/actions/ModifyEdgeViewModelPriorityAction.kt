package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.Edge

/**
 * A concrete representation of an [Action] that changes the priority of an [Edge]. Holds the old
 * and new priorities of the edge.
 */
class ModifyEdgeViewModelPriorityAction internal constructor(val Edge: Edge, val newPriority: Int) :
    Action() {
    val oldPriority = Edge.priority

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        Edge.priority = newPriority
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createModifyEdgeViewModelPriorityAction(Edge, oldPriority)
    }
}
