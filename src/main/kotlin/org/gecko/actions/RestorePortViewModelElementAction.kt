package org.gecko.actions

import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.Port
import org.gecko.viewmodel.System

/** A concrete representation of an [Action] that restores a deleted [port] in a given [System]. */
data class RestorePortViewModelElementAction(
    val gModel: GModel,
    val port: Port,
    val system: System
) : Action() {
    override fun run(): Boolean {
        system.addPort(port)
        gModel.addViewModelElement(port)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory) =
        DeletePortViewModelElementAction(gModel, port, system)
}
