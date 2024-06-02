package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.model.GeckoModel
import org.gecko.viewmodel.*

/**
 * A concrete representation of an [Action] that changes the name of a [Renamable].  Additionally, holds the
 * old and new [name][String]s of the element.
 */
class RenameViewModelElementAction internal constructor(
    val geckoModel: GeckoModel,
    val renamable: Renamable,
    val newName: String
) : Action() {
    val oldName: String = renamable.name

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        if (!geckoModel.isNameUnique(newName)) {
            return false
        }
        renamable.name = newName
        val abstractViewModelElement = renamable as AbstractViewModelElement<*>
        abstractViewModelElement.updateTarget()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createRenameViewModelElementAction(this.renamable, this.oldName)
    }
}
