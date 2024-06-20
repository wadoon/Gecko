package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.EdgeViewModel
import org.gecko.viewmodel.Kind

/**
 * Represents a type of [Action] that changes the [Kind] of an [EdgeViewModel], which it holds a
 * references to. Additionally, holds the old and new [Kind]s of the edge for undo/redo purposes.
 */
class ChangeKindEdgeViewModelAction(val edgeViewModel: EdgeViewModel, val kind: Kind?) : Action() {
    var oldKind: Kind? = null

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        oldKind = edgeViewModel.kind
        edgeViewModel.kind = kind!!
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createChangeKindAction(edgeViewModel, oldKind)
    }
}
