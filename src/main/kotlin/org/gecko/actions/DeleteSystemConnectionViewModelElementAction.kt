package org.gecko.actions

import org.gecko.exceptions.GeckoException

import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.PositionableViewModelElement
import org.gecko.viewmodel.SystemConnectionViewModel
import org.gecko.viewmodel.SystemViewModel

/**
 * A concrete representation of an [Action] that removes a [SystemConnectionViewModel] from a given
 * [org.gecko.viewmodel.SystemViewModel].
 */
class DeleteSystemConnectionViewModelElementAction internal constructor(
    val geckoViewModel: GeckoViewModel,
    val systemConnectionViewModel: SystemConnectionViewModel,
    val system: SystemViewModel
) : AbstractPositionableViewModelElementAction() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        system.removeConnection(systemConnectionViewModel)
        systemConnectionViewModel.source?.removeOutgoingConnection(systemConnectionViewModel)
        systemConnectionViewModel.destination?.removeIncomingConnection(systemConnectionViewModel)
        geckoViewModel.deleteViewModelElement(systemConnectionViewModel)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return RestoreSystemConnectionViewModelElementAction(geckoViewModel, systemConnectionViewModel, system)
    }

    override val target: PositionableViewModelElement
        get() = systemConnectionViewModel
}

