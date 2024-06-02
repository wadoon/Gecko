package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.PortViewModel
import org.gecko.viewmodel.SystemViewModel

/**
 * A concrete representation of an [Action] that restores a deleted [PortViewModel] in a given
 * [SystemViewModel].
 */
class RestorePortViewModelElementAction internal constructor(
    val geckoViewModel: GeckoViewModel,
    val portViewModel: PortViewModel,
    val system: SystemViewModel
) : Action() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        system.addPort(portViewModel)
        system.updateTarget()
        geckoViewModel.addViewModelElement(portViewModel)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return DeletePortViewModelElementAction(geckoViewModel, portViewModel, system)
    }
}
