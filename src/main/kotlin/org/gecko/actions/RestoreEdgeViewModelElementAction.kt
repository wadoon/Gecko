package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.AutomatonViewModel
import org.gecko.viewmodel.EdgeViewModel
import org.gecko.viewmodel.GeckoViewModel

/**
 * A concrete representation of an [Action] that restores a deleted [EdgeViewModel] in a given
 * [Automaton].
 */
class RestoreEdgeViewModelElementAction internal constructor(
    val geckoViewModel: GeckoViewModel,
    val edgeViewModel: EdgeViewModel,
    val automaton: AutomatonViewModel
) : Action() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        automaton.addEdge(edgeViewModel)
        geckoViewModel.addViewModelElement(edgeViewModel)
        edgeViewModel.source!!.outgoingEdges.add(edgeViewModel)
        edgeViewModel.destination!!.incomingEdges.add(edgeViewModel)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return DeleteEdgeViewModelElementAction(geckoViewModel, edgeViewModel, automaton)
    }
}
