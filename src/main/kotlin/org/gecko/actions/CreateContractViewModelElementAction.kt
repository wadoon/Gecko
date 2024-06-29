package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.*

/**
 * A concrete representation of an [Action] that creates a [Contract] in a given
 * [StateViewModel] through the [ViewModelFactory] of the
 * [GeckoViewModel][org.gecko.viewmodel.GModel].
 */
class CreateContractViewModelElementAction internal constructor(
    val viewModelFactory: GModel,
    val stateViewModel: StateViewModel
) : Action() {
    val createdContract: Contract = Contract()

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        stateViewModel.addContract(createdContract)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createDeleteContractViewModelAction(stateViewModel, createdContract)
    }
}
