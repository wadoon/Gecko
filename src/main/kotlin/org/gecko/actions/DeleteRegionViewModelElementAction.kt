package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.*

/**
 * A concrete representation of an [Action] that removes a [Region] from the [GModel]
 * and its target-[org.gecko.model.Region] from the given [Automaton].
 */
class DeleteRegionViewModelElementAction internal constructor(
    val gModel: GModel,
    val Region: Region,
    val automaton: Automaton
) : AbstractPositionableViewModelElementAction() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        automaton.removeRegion(Region)
        val states: List<StateViewModel> = ArrayList(Region.statesProperty)

        for (state in states) {
            Region.removeState(state)
        }

        gModel.deleteViewModelElement(Region)
        gModel.currentEditor!!.updateRegions()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return RestoreRegionViewModelElementAction(gModel, Region, automaton)
    }

    override val target: PositionableViewModelElement
        get() = Region
}
