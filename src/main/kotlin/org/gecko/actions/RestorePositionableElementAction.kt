package org.gecko.actions

import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.PositionableElement

/**
 * A concrete representation of an [Action] that restores a set of deleted [PositionableElement]s
 * in the current [EditorViewModel][org.gecko.viewmodel.EditorViewModel].
 */
class RestorePositionableElementAction(
    val gModel: GModel,
    val actionGroup: ActionGroup,
    deletedElements: Set<PositionableElement>
) : Action() {
    val deletedElements: Set<PositionableElement> = deletedElements.toSet()

    override fun run(): Boolean {
        val undoAction = actionGroup.getUndoAction(gModel.actionManager.actionFactory)
            ?: error("undo action not found")
        if (!undoAction.run()) {
            return false
        }
        val elementsToRestoreFromCurrentEditor = gModel.currentEditor
            .viewableElementsProperty.filter { deletedElements.contains(it) }.toSet()
        val actionManager = gModel.actionManager
        actionManager.run(
            actionManager.actionFactory.createSelectAction(elementsToRestoreFromCurrentEditor, true)
        )
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory) =
        actionFactory.createDeleteAction(deletedElements)
}
