package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.PortViewModel
import org.gecko.viewmodel.SystemViewModel

/**
 * A concrete representation of an [Action] that removes a [PortViewModel] from the given
 * parent-[SystemViewModel].
 */
class DeletePortViewModelElementAction(
    val geckoViewModel: GeckoViewModel,
    val portViewModel: PortViewModel,
    val system: SystemViewModel
) : AbstractPositionableViewModelElementAction() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        system.removePort(portViewModel)
        geckoViewModel.deleteViewModelElement(portViewModel)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return RestorePortViewModelElementAction(geckoViewModel, portViewModel, system)
    }

    override val target
        get() = portViewModel
}
