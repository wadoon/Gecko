package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.PositionableViewModelElement

/**
 * A concrete representation of an [Action] that restores a set of deleted [PositionableViewModelElement]s
 * in the current [EditorViewModel][org.gecko.viewmodel.EditorViewModel].
 */
class RestorePositionableViewModelElementAction internal constructor(
    val gModel: GModel,
    val actionGroup: ActionGroup?,
    deletedElements: Set<PositionableViewModelElement>?
) : Action() {
    val deletedElements: Set<PositionableViewModelElement> = HashSet(deletedElements)

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        val undoAction = actionGroup!!.getUndoAction(gModel.actionManager.actionFactory)
        if (!undoAction!!.run()) {
            return false
        }
        val elementsToRestoreFromCurrentEditor = gModel.currentEditor!!
            .viewableElements
            .filter { deletedElements.contains(it) }
            .toSet()
        val actionManager = gModel.actionManager
        actionManager.run(
            actionManager.actionFactory.createSelectAction(elementsToRestoreFromCurrentEditor, true)
        )
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory) =
        actionFactory.createDeleteAction(deletedElements)
}
