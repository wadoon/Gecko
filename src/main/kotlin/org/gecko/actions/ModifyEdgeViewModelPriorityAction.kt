package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.EdgeViewModel

/**
 * A concrete representation of an [Action] that changes the priority of an [EdgeViewModel]. Holds the old
 * and new priorities of the edge.
 */
class ModifyEdgeViewModelPriorityAction internal constructor(
    val edgeViewModel: EdgeViewModel,
    val newPriority: Int
) : Action() {
    val oldPriority = edgeViewModel.priority

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        edgeViewModel.priority = newPriority
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createModifyEdgeViewModelPriorityAction(edgeViewModel, oldPriority)
    }
}
