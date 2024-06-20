package org.gecko.actions

import org.gecko.exceptions.GeckoException

import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.PositionableViewModelElement
import org.gecko.viewmodel.SystemViewModel

/**
 * A concrete representation of an [Action] that removes a [SystemViewModel] from a given
 * [SystemViewModel].
 */
class DeleteSystemViewModelElementAction(
    val geckoViewModel: GeckoViewModel, val systemViewModel: SystemViewModel, parentSystemViewModel: SystemViewModel
) : AbstractPositionableViewModelElementAction() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        geckoViewModel.deleteViewModelElement(systemViewModel)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return RestoreSystemViewModelElementAction(geckoViewModel, systemViewModel)
    }

    override val target: PositionableViewModelElement
        get() = systemViewModel
}
