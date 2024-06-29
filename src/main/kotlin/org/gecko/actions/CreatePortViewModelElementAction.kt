package org.gecko.actions

import javafx.geometry.Point2D
import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.Port
import org.gecko.viewmodel.System

/**
 * A concrete representation of an [Action] that creates a [Port] in a given
 * [System] through the [ViewModelFactory][org.gecko.viewmodel.ViewModelFactory] of the
 * [GModel].
 */
class CreatePortViewModelElementAction internal constructor(
    val gModel: GModel,
    parentSystem: System
) : Action() {
    val systemViewModel = parentSystem
    lateinit var createdPort: Port

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        createdPort = gModel.viewModelFactory.createPort(systemViewModel)
        val offset = createdPort.size.y * (systemViewModel.ports.size - 1)
        createdPort.position = (Point2D(MARGIN.toDouble(), MARGIN + offset))
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory) =
        actionFactory.createDeleteAction(createdPort)

    companion object {
        const val MARGIN = 2
    }
}
