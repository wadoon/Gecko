package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.Region

/**
 * A concrete representation of an [Action] that changes the invariant of a [Region], which it
 * holds a reference to. Additionally, holds the old and new [invariants][String]s of the region for undo/redo
 * purposes
 */
class ChangeInvariantViewModelElementAction internal constructor(
    val Region: Region,
    val newInvariant: String
) : Action() {
    val oldInvariant: String = Region.invariant.value

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        Region.invariant.value = newInvariant
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createChangeInvariantViewModelElementAction(Region, oldInvariant)
    }
}
