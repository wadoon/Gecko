package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.model.Automaton
import org.gecko.viewmodel.EdgeViewModel
import org.gecko.viewmodel.GeckoViewModel

/**
 * A concrete representation of an [Action] that removes the target of an [EdgeViewModel] from the given
 * [Automaton] and the afferent [EdgeViewModel] from the list of outgoing- and
 * ingoing-[EdgeViewModel]s of its source- and destination-[org.gecko.viewmodel.StateViewModel]s.
 */
class DeleteEdgeViewModelElementAction(
    val geckoViewModel: GeckoViewModel,
    val edgeViewModel: EdgeViewModel,
    val automaton: Automaton
) : AbstractPositionableViewModelElementAction() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        edgeViewModel.source.outgoingEdges.remove(edgeViewModel)
        edgeViewModel.destination.incomingEdges.remove(edgeViewModel)
        automaton.removeEdge(edgeViewModel.target)
        geckoViewModel.deleteViewModelElement(edgeViewModel)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return RestoreEdgeViewModelElementAction(geckoViewModel, edgeViewModel, automaton)
    }

    override val target
        get() = edgeViewModel
}
