package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.*

/**
 * A concrete representation of an [Action] that changes the name of a [Renamable].  Additionally, holds the
 * old and new [name][String]s of the element.
 */
class RenameViewModelElementAction internal constructor(
    val renamable: Renamable, val newName: String
) : Action() {
    val oldName: String = renamable.name

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        renamable.name = newName
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createRenameViewModelElementAction(this.renamable, this.oldName)
    }
}
