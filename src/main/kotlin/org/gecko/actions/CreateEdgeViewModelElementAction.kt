package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.EdgeViewModel
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.StateViewModel

/**
 * A concrete representation of an [Action] that creates an [EdgeViewModel] in the
 * current-[SystemViewModel] with given source- and destination-[StateViewModel]s through the
 * [ViewModelFactory][org.gecko.viewmodel.ViewModelFactory] of the [GeckoViewModel].
 */
class CreateEdgeViewModelElementAction internal constructor(
    val geckoViewModel: GeckoViewModel,
    val source: StateViewModel,
    val destination: StateViewModel
) : Action() {
    lateinit var createdEdgeViewModel: EdgeViewModel

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        val currentParentSystem = geckoViewModel.currentEditor!!.currentSystem
        createdEdgeViewModel =
            geckoViewModel.viewModelFactory.createEdgeViewModelIn(currentParentSystem, source, destination)
        val actionManager = geckoViewModel.actionManager
        actionManager.run(actionManager.actionFactory.createSelectAction(createdEdgeViewModel, true))
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createDeletePositionableViewModelElementAction(createdEdgeViewModel)
    }
}
