package org.gecko.actions

import javafx.geometry.Point2D
import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.SystemViewModel

/**
 * A concrete representation of an [Action] that creates a [SystemViewModel] in the
 * current-[SystemViewModel] through the [ViewModelFactory][org.gecko.viewmodel.ViewModelFactory] of the
 * [GeckoViewModel]. Additionally, holds the [position][Point2D] and the current [EditorViewModel] for
 * setting the correct position for the created system.
 */
class CreateSystemViewModelElementAction internal constructor(
    val geckoViewModel: GeckoViewModel,
    val position: Point2D
) : Action() {
    lateinit var createdSystemViewModel: SystemViewModel

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        val currentParentSystem = geckoViewModel.currentEditor!!.currentSystem
        createdSystemViewModel = geckoViewModel.viewModelFactory.createSystem(currentParentSystem)
        createdSystemViewModel.setPositionFromCenter(position)
        val actionManager = geckoViewModel.actionManager
        actionManager.run(actionManager.actionFactory.createSelectAction(createdSystemViewModel!!, true))
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createDeleteAction(createdSystemViewModel!!)
    }
}