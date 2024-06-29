package org.gecko.actions

import javafx.geometry.Point2D
import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.System

/**
 * A concrete representation of an [Action] that creates a [System] in the
 * current-[System] through the [ViewModelFactory][org.gecko.viewmodel.ViewModelFactory] of the
 * [GModel]. Additionally, holds the [position][Point2D] and the current [EditorViewModel] for
 * setting the correct position for the created system.
 */
class CreateSystemViewModelElementAction internal constructor(
    val gModel: GModel,
    val position: Point2D
) : Action() {
    lateinit var createdSystem: System

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        val currentParentSystem = gModel.currentEditor!!.currentSystem
        createdSystem = gModel.viewModelFactory.createSystem(currentParentSystem)
        createdSystem.setPositionFromCenter(position)
        val actionManager = gModel.actionManager
        actionManager.run(actionManager.actionFactory.createSelectAction(createdSystem!!, true))
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createDeleteAction(createdSystem!!)
    }
}