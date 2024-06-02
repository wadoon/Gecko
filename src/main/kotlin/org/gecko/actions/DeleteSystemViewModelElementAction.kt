package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.model.System
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.PositionableViewModelElement
import org.gecko.viewmodel.SystemViewModel

/**
 * A concrete representation of an [Action] that removes a [SystemViewModel] from a given
 * [SystemViewModel].
 */
class DeleteSystemViewModelElementAction(
    val geckoViewModel: GeckoViewModel, val systemViewModel: SystemViewModel, val system: System
) : AbstractPositionableViewModelElementAction() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        system.removeChild(systemViewModel.target)
        geckoViewModel.deleteViewModelElement(systemViewModel)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return RestoreSystemViewModelElementAction(geckoViewModel, systemViewModel, system)
    }

    override val target: PositionableViewModelElement<*>
        get() = systemViewModel
}
