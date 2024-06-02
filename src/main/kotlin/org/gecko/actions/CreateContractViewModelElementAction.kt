package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.ContractViewModel
import org.gecko.viewmodel.StateViewModel
import org.gecko.viewmodel.ViewModelFactory

/**
 * A concrete representation of an [Action] that creates a [ContractViewModel] in a given
 * [StateViewModel] through the [ViewModelFactory] of the
 * [GeckoViewModel][org.gecko.viewmodel.GeckoViewModel].
 */
class CreateContractViewModelElementAction internal constructor(
    val viewModelFactory: ViewModelFactory,
    val stateViewModel: StateViewModel
) : Action() {
    var createdContractViewModel: ContractViewModel? = null

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        createdContractViewModel = viewModelFactory.createContractViewModelIn(stateViewModel)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createDeleteContractViewModelAction(stateViewModel, createdContractViewModel)
    }
}
