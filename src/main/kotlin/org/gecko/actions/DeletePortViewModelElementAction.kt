package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.Port
import org.gecko.viewmodel.System

/**
 * A concrete representation of an [Action] that removes a [Port] from the given
 * parent-[System].
 */
class DeletePortViewModelElementAction(
    val gModel: GModel,
    val Port: Port,
    val system: System
) : AbstractPositionableViewModelElementAction() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        system.removePort(Port)
        gModel.deleteViewModelElement(Port)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return RestorePortViewModelElementAction(gModel, Port, system)
    }

    override val target
        get() = Port
}
