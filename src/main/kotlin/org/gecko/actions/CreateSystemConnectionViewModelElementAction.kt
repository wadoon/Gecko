package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.*

/**
 * A concrete representation of an [Action] that creates an [SystemConnectionViewModel] in the
 * current-[SystemViewModel] with given source- and destination-[PortViewModel]s through the
 * [ViewModelFactory][org.gecko.viewmodel.ViewModelFactory] of the [GeckoViewModel].
 */
class CreateSystemConnectionViewModelElementAction internal constructor(
    val geckoViewModel: GeckoViewModel,
    val source: PortViewModel,
    val destination: PortViewModel
) : Action() {
    var createdSystemConnectionViewModel: SystemConnectionViewModel? = null

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        val currentParentSystem = geckoViewModel.currentEditor!!.currentSystem
        if (!isConnectingAllowed(
                source,
                destination,
                geckoViewModel.getSystemViewModelWithPort(source),
                geckoViewModel.getSystemViewModelWithPort(destination),
                currentParentSystem,
                null
            )
        ) {
            return false
        }

        createdSystemConnectionViewModel = geckoViewModel.viewModelFactory
            .createSystemConnectionViewModelIn(currentParentSystem, source, destination)

        val actionManager = geckoViewModel.actionManager
        actionManager.run(actionManager.actionFactory.createSelectAction(createdSystemConnectionViewModel!!, true))
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action? {
        return actionFactory.createDeletePositionableViewModelElementAction(createdSystemConnectionViewModel!!)
    }
}
