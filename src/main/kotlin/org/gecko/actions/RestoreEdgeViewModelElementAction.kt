package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.Automaton
import org.gecko.viewmodel.Edge
import org.gecko.viewmodel.GModel

/**
 * A concrete representation of an [Action] that restores a deleted [Edge] in a given [Automaton].
 */
class RestoreEdgeViewModelElementAction
internal constructor(val gModel: GModel, val Edge: Edge, val automaton: Automaton) : Action() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        automaton.addEdge(Edge)
        gModel.addViewModelElement(Edge)
        Edge.source.outgoingEdges.add(Edge)
        Edge.destination.incomingEdges.add(Edge)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return DeleteEdgeViewModelElementAction(gModel, Edge, automaton)
    }
}
