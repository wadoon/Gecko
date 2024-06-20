package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.SystemConnectionViewModel
import org.gecko.viewmodel.SystemViewModel

/**
 * A concrete representation of an [Action] that restores a deleted [SystemConnectionViewModel] in a given
 * [SystemViewModel][org.gecko.viewmodel.SystemViewModel].
 */
class RestoreSystemConnectionViewModelElementAction internal constructor(
    val geckoViewModel: GeckoViewModel,
    val systemConnectionViewModel: SystemConnectionViewModel,
    val system: SystemViewModel
) : Action() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        system.addConnection(systemConnectionViewModel)
        systemConnectionViewModel.source?.outgoingConnections?.add(systemConnectionViewModel)
        systemConnectionViewModel.destination?.incomingConnections?.add(systemConnectionViewModel)
        geckoViewModel.addViewModelElement(systemConnectionViewModel)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return DeleteSystemConnectionViewModelElementAction(geckoViewModel, systemConnectionViewModel, system)
    }
}
