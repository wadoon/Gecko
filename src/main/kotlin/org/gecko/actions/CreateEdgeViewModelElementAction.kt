package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.Edge
import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.State

/**
 * A concrete representation of an [Action] that creates an [Edge] in the
 * current-[SystemViewModel] with given source- and destination-[State]s through the
 * [ViewModelFactory][org.gecko.viewmodel.ViewModelFactory] of the [GModel].
 */
class CreateEdgeViewModelElementAction internal constructor(
    val gModel: GModel,
    val source: State,
    val destination: State
) : Action() {
    lateinit var createdEdge: Edge

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        val currentParentSystem = gModel.currentEditor!!.currentSystem
        createdEdge =
            gModel.viewModelFactory.createEdgeViewModelIn(currentParentSystem, source, destination)
        val actionManager = gModel.actionManager
        actionManager.run(actionManager.actionFactory.createSelectAction(createdEdge, true))
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createDeleteAction(createdEdge)
    }
}
