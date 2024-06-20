package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.*

/**
 * A concrete representation of an [Action] that removes a [RegionViewModel] from the [GeckoViewModel]
 * and its target-[org.gecko.model.Region] from the given [Automaton].
 */
class DeleteRegionViewModelElementAction internal constructor(
    val geckoViewModel: GeckoViewModel,
    val regionViewModel: RegionViewModel,
    val automaton: AutomatonViewModel
) : AbstractPositionableViewModelElementAction() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        automaton.removeRegion(regionViewModel)
        val states: List<StateViewModel> = ArrayList(regionViewModel.statesProperty)

        for (state in states) {
            regionViewModel.removeState(state)
        }

        geckoViewModel.deleteViewModelElement(regionViewModel)
        geckoViewModel.currentEditor!!.updateRegions()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return RestoreRegionViewModelElementAction(geckoViewModel, regionViewModel, automaton)
    }

    override val target: PositionableViewModelElement
        get() = regionViewModel
}
