package org.gecko.actions

import javafx.geometry.Point2D
import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.EditorViewModel
import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.StateViewModel

/**
 * A concrete representation of an [Action] that creates a [StateViewModel] in the
 * current-[SystemViewModel] through the [ViewModelFactory][org.gecko.viewmodel.ViewModelFactory] of the
 * [GModel]. Additionally, holds the [position][Point2D] and the current [EditorViewModel] for
 * setting the correct position for the created state.
 */
class CreateStateViewModelElementAction internal constructor(
    val gModel: GModel,
    val editorViewModel: EditorViewModel,
    val position: Point2D
) : Action() {
    lateinit var createdStateViewModel: StateViewModel

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        val currentParentSystem = gModel.currentEditor!!.currentSystem
        createdStateViewModel = gModel.viewModelFactory.createState(currentParentSystem)
        createdStateViewModel.setPositionFromCenter(position)
        editorViewModel.updateRegions()
        val actionManager = gModel.actionManager
        actionManager.run(actionManager.actionFactory.createSelectAction(createdStateViewModel, true))
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createDeleteAction(createdStateViewModel)
    }
}
