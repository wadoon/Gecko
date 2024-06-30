package org.gecko.actions

import org.gecko.viewmodel.*

/**
 * A concrete representation of an [Action] that removes a [region] from the [GModel] and its
 * target-[org.gecko.model.Region] from the given [Automaton].
 */
class DeleteRegionAction(val gModel: GModel, val region: Region, val automaton: Automaton) :
    AbstractPositionableViewModelElementAction() {
    override fun run(): Boolean {
        automaton.removeRegion(region)
        val states = region.states.toMutableList()

        for (state in states) {
            region.states.remove(state)
        }

        gModel.deleteViewModelElement(region)
        gModel.currentEditor.updateRegions()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory) =
        RestoreRegionViewModelElementAction(gModel, region, automaton)

    override val target: PositionableElement
        get() = region
}
