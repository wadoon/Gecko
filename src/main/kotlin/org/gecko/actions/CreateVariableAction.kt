package org.gecko.actions

import javafx.geometry.Point2D
import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.Port

/**
 * A concrete representation of an [Action] that creates a [Port] in the
 * current-[SystemViewModel][org.gecko.viewmodel.System] through the
 * [ViewModelFactory][org.gecko.viewmodel.ViewModelFactory] of the [GModel]. Additionally, holds the
 * [position][Point2D] and the current [EditorViewModel] for setting the correct position for the created
 * port.
 */
class CreateVariableAction internal constructor(
    val gModel: GModel,
    val position: Point2D
) : Action() {
    private lateinit var createdPort: Port

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        createdPort = gModel.viewModelFactory
            .createPort(gModel.currentEditor!!.currentSystem)
        createdPort.setPositionFromCenter(position)
        val actionManager = gModel.actionManager
        actionManager.run(actionManager.actionFactory.createSelectAction(createdPort, true))
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createDeleteAction(createdPort)
    }
}
