package org.gecko.actions

import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.System
import org.gecko.viewmodel.SystemConnection

/**
 * A concrete representation of an [Action] that restores a deleted [SystemConnection] in a given
 * [SystemViewModel][org.gecko.viewmodel.System].
 */
class RestoreSystemConnectionAction
internal constructor(
    val gModel: GModel,
    val systemConnectionViewModel: SystemConnection,
    val system: System
) : Action() {
    override fun run(): Boolean {
        system.addConnection(systemConnectionViewModel)
        systemConnectionViewModel.source?.outgoingConnections?.add(systemConnectionViewModel)
        systemConnectionViewModel.destination?.incomingConnections?.add(systemConnectionViewModel)
        gModel.addViewModelElement(systemConnectionViewModel)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return DeleteSystemConnectionViewModelElementAction(
            gModel,
            systemConnectionViewModel,
            system
        )
    }
}
