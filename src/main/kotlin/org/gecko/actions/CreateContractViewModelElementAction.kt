package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.*

/**
 * A concrete representation of an [Action] that creates a [ContractViewModel] in a given
 * [StateViewModel] through the [ViewModelFactory] of the
 * [GeckoViewModel][org.gecko.viewmodel.GeckoViewModel].
 */
class CreateContractViewModelElementAction internal constructor(
    val viewModelFactory: GeckoViewModel,
    val stateViewModel: StateViewModel
) : Action() {
    val createdContractViewModel: ContractViewModel = ContractViewModel()

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        stateViewModel.addContract(createdContractViewModel)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createDeleteContractViewModelAction(stateViewModel, createdContractViewModel)
    }
}
