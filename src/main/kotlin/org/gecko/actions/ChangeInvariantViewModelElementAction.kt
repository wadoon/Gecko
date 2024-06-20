package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.RegionViewModel

/**
 * A concrete representation of an [Action] that changes the invariant of a [RegionViewModel], which it
 * holds a reference to. Additionally, holds the old and new [invariants][String]s of the region for undo/redo
 * purposes
 */
class ChangeInvariantViewModelElementAction internal constructor(
    val regionViewModel: RegionViewModel,
    val newInvariant: String
) : Action() {
    val oldInvariant: String = regionViewModel.invariant.value

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        if (newInvariant.isEmpty()) {
            return false
        }
        regionViewModel.invariant.value = newInvariant
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createChangeInvariantViewModelElementAction(regionViewModel, oldInvariant)
    }
}
