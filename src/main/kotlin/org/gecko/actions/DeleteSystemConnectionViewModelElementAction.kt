package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.PositionableElement
import org.gecko.viewmodel.System
import org.gecko.viewmodel.SystemConnection

/**
 * A concrete representation of an [Action] that removes a [SystemConnection] from a given
 * [org.gecko.viewmodel.System].
 */
class DeleteSystemConnectionViewModelElementAction
internal constructor(
    val gModel: GModel,
    val systemConnectionViewModel: SystemConnection,
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
        return RestoreSystemConnectionAction(gModel, systemConnectionViewModel, system)
    }

    override val target: PositionableElement
        get() = systemConnectionViewModel
}
