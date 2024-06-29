package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.Edge
import org.gecko.viewmodel.Kind

/**
 * Represents a type of [Action] that changes the [Kind] of an [Edge], which it holds a
 * references to. Additionally, holds the old and new [Kind]s of the edge for undo/redo purposes.
 */
class ChangeKindEdgeViewModelAction(val Edge: Edge, val kind: Kind?) : Action() {
    var oldKind: Kind? = null

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        oldKind = Edge.kind
        Edge.kind = kind!!
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createChangeKindAction(Edge, oldKind)
    }
}
