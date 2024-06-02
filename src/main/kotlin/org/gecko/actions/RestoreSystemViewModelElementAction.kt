package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.model.System
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.SystemViewModel

/**
 * A concrete representation of an [Action] that restores a deleted [SystemViewModel] in a given
 * [SystemViewModel].
 */
class RestoreSystemViewModelElementAction internal constructor(
    val geckoViewModel: GeckoViewModel,
    val systemViewModel: SystemViewModel,
    val system: System
) : Action() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        system.addChild(systemViewModel.target!!)
        geckoViewModel.addViewModelElement(systemViewModel)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return DeleteSystemViewModelElementAction(geckoViewModel, systemViewModel, system)
    }
}
