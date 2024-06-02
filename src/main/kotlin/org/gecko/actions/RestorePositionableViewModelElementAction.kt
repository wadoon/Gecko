package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.PositionableViewModelElement

/**
 * A concrete representation of an [Action] that restores a set of deleted [PositionableViewModelElement]s
 * in the current [EditorViewModel][org.gecko.viewmodel.EditorViewModel].
 */
class RestorePositionableViewModelElementAction internal constructor(
    val geckoViewModel: GeckoViewModel,
    val actionGroup: ActionGroup?,
    deletedElements: Set<PositionableViewModelElement<*>>?
) : Action() {
    val deletedElements: Set<PositionableViewModelElement<*>> = HashSet(deletedElements)

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        val undoAction = actionGroup!!.getUndoAction(geckoViewModel.actionManager.actionFactory)
        if (!undoAction!!.run()) {
            return false
        }
        val elementsToRestoreFromCurrentEditor = geckoViewModel.currentEditor!!
            .containedPositionableViewModelElementsProperty
            .filter { deletedElements.contains(it) }
            .toSet()
        val actionManager = geckoViewModel.actionManager
        actionManager.run(
            actionManager.actionFactory.createSelectAction(elementsToRestoreFromCurrentEditor, true)
        )
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory) =
        actionFactory.createDeletePositionableViewModelElementAction(deletedElements)
}
