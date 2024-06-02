package org.gecko.actions

import org.gecko.exceptions.ModelException
import org.gecko.model.Automaton
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.RegionViewModel

/**
 * A concrete representation of an [Action] that restores a deleted [RegionViewModel] in a given
 * [Automaton].
 */
class RestoreRegionViewModelElementAction internal constructor(
    val geckoViewModel: GeckoViewModel,
    val regionViewModel: RegionViewModel,
    val automaton: Automaton
) : Action() {
    @Throws(ModelException::class)
    override fun run(): Boolean {
        automaton.addRegion(regionViewModel.target!!)
        geckoViewModel.addViewModelElement(regionViewModel)
        geckoViewModel.currentEditor!!.updateRegions()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return DeleteRegionViewModelElementAction(geckoViewModel, regionViewModel, automaton)
    }
}
