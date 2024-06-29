package org.gecko.actions

import org.gecko.exceptions.GeckoException

import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.PositionableViewModelElement
import org.gecko.viewmodel.SystemConnectionViewModel
import org.gecko.viewmodel.System

/**
 * A concrete representation of an [Action] that removes a [SystemConnectionViewModel] from a given
 * [org.gecko.viewmodel.System].
 */
class DeleteSystemConnectionViewModelElementAction internal constructor(
    val gModel: GModel,
    val systemConnectionViewModel: SystemConnectionViewModel,
    val system: System
) : AbstractPositionableViewModelElementAction() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        system.removeConnection(systemConnectionViewModel)
        systemConnectionViewModel.source?.removeOutgoingConnection(systemConnectionViewModel)
        systemConnectionViewModel.destination?.removeIncomingConnection(systemConnectionViewModel)
        gModel.deleteViewModelElement(systemConnectionViewModel)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return RestoreSystemConnectionViewModelElementAction(gModel, systemConnectionViewModel, system)
    }

    override val target: PositionableViewModelElement
        get() = systemConnectionViewModel
}

