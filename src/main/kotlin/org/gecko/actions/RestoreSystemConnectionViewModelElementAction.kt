package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.SystemConnectionViewModel
import org.gecko.viewmodel.System

/**
 * A concrete representation of an [Action] that restores a deleted [SystemConnectionViewModel] in a given
 * [SystemViewModel][org.gecko.viewmodel.System].
 */
class RestoreSystemConnectionViewModelElementAction internal constructor(
    val gModel: GModel,
    val systemConnectionViewModel: SystemConnectionViewModel,
    val system: System
) : Action() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        system.addConnection(systemConnectionViewModel)
        systemConnectionViewModel.source?.outgoingConnections?.add(systemConnectionViewModel)
        systemConnectionViewModel.destination?.incomingConnections?.add(systemConnectionViewModel)
        gModel.addViewModelElement(systemConnectionViewModel)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return DeleteSystemConnectionViewModelElementAction(gModel, systemConnectionViewModel, system)
    }
}
