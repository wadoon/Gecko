package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.*

/**
 * A concrete representation of an [Action] that creates an [SystemConnectionViewModel] in the
 * current-[System] with given source- and destination-[Port]s through the
 * [ViewModelFactory][org.gecko.viewmodel.ViewModelFactory] of the [GModel].
 */
class CreateSystemConnectionViewModelElementAction internal constructor(
    val gModel: GModel,
    val source: Port,
    val destination: Port
) : Action() {
    var createdSystemConnectionViewModel: SystemConnectionViewModel? = null

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        val currentParentSystem = gModel.currentEditor!!.currentSystem
        if (!isConnectingAllowed(
                source,
                destination,
                gModel.getSystemViewModelWithPort(source),
                gModel.getSystemViewModelWithPort(destination),
                currentParentSystem,
                null
            )
        ) {
            return false
        }

        createdSystemConnectionViewModel = gModel.viewModelFactory
            .createSystemConnectionViewModelIn(currentParentSystem, source, destination)

        val actionManager = gModel.actionManager
        actionManager.run(actionManager.actionFactory.createSelectAction(createdSystemConnectionViewModel!!, true))
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createDeleteAction(createdSystemConnectionViewModel!!)
    }
}
