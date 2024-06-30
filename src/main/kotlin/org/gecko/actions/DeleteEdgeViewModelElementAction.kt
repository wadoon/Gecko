package org.gecko.actions

import org.gecko.exceptions.GeckoException

import org.gecko.viewmodel.Automaton
import org.gecko.viewmodel.Edge
import org.gecko.viewmodel.GModel

/**
 * A concrete representation of an [Action] that removes the target of an [Edge] from the given
 * [Automaton] and the afferent [Edge] from the list of outgoing- and
 * ingoing-[Edge]s of its source- and destination-[org.gecko.viewmodel.State]s.
 */
class DeleteEdgeViewModelElementAction(
    val gModel: GModel,
    val Edge: Edge,
    val automaton: Automaton
) : AbstractPositionableViewModelElementAction() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        Edge.source.outgoingEdges.remove(Edge)
        Edge.destination.incomingEdges?.remove(Edge)
        automaton.removeEdge(Edge)
        gModel.deleteViewModelElement(Edge)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return RestoreEdgeViewModelElementAction(gModel, Edge, automaton)
    }

    override val target
        get() = Edge
}
