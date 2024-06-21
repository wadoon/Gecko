package org.gecko.actions

import javafx.geometry.Point2D
import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.PortViewModel

/**
 * A concrete representation of an [Action] that creates a [PortViewModel] in the
 * current-[SystemViewModel][org.gecko.viewmodel.SystemViewModel] through the
 * [ViewModelFactory][org.gecko.viewmodel.ViewModelFactory] of the [GeckoViewModel]. Additionally, holds the
 * [position][Point2D] and the current [EditorViewModel] for setting the correct position for the created
 * port.
 */
class CreateVariableAction internal constructor(
    val geckoViewModel: GeckoViewModel,
    val position: Point2D?
) : Action() {
    private lateinit var createdPortViewModel: PortViewModel

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        createdPortViewModel = geckoViewModel.viewModelFactory
            .createPortViewModelIn(geckoViewModel.currentEditor!!.currentSystem)
        createdPortViewModel.center = (position!!)
        val actionManager = geckoViewModel.actionManager
        actionManager.run(actionManager.actionFactory.createSelectAction(createdPortViewModel, true))
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createDeletePositionableViewModelElementAction(createdPortViewModel)
    }
}
