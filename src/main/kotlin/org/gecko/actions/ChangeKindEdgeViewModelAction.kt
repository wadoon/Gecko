package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.model.*
import org.gecko.viewmodel.EdgeViewModel

/**
 * Represents a type of [Action] that changes the [Kind] of an [EdgeViewModel], which it holds a
 * references to. Additionally, holds the old and new [Kind]s of the edge for undo/redo purposes.
 */
class ChangeKindEdgeViewModelAction(val edgeViewModel: EdgeViewModel, kind: Kind?) : Action() {
    val kind: Kind?
    var oldKind: Kind? = null

    init {
        this.kind = kind
    }

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        oldKind = edgeViewModel.kind
        edgeViewModel.kind = kind!!
        edgeViewModel.updateTarget()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createChangeKindAction(edgeViewModel, oldKind)
    }
}
