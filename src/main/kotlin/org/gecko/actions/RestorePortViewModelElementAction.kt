package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.Port
import org.gecko.viewmodel.System

/**
 * A concrete representation of an [Action] that restores a deleted [Port] in a given
 * [System].
 */
class RestorePortViewModelElementAction internal constructor(
    val gModel: GModel,
    val Port: Port,
    val system: System
) : Action() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        system.addPort(Port)
        gModel.addViewModelElement(Port)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return DeletePortViewModelElementAction(gModel, Port, system)
    }
}
