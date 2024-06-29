package org.gecko.actions

import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.PositionableViewModelElement

/**
 * A concrete representation of an [Action] that deletes a set of [PositionableViewModelElement]s and their
 * dependencies from the [GModel].
 */
class DeletePositionableViewModelElementAction(
    val gModel: GModel,
    val elementsToDelete: Set<PositionableViewModelElement>
) : Action() {
    lateinit var deleteActionGroup: ActionGroup
    val deletedElements = mutableSetOf<PositionableViewModelElement>()

    constructor(gModel: GModel, element: PositionableViewModelElement)
            : this(gModel, setOf(element))

    override fun run(): Boolean {
        val allDeleteActions = hashSetOf<AbstractPositionableViewModelElementAction>()
        for (element in elementsToDelete) {
            val visitor =
                DeleteActionsHelper(gModel, gModel.currentEditor!!.currentSystem)
                    .visit(element)
            allDeleteActions.addAll(visitor)
        }

        deletedElements.addAll(allDeleteActions.map { it.target }.toSet())
        gModel.currentEditor!!.selectionManager.deselect(deletedElements)
        deleteActionGroup = ActionGroup(allDeleteActions.toList())
        return deleteActionGroup.run()
    }

    override fun getUndoAction(actionFactory: ActionFactory) =
        RestorePositionableViewModelElementAction(gModel, deleteActionGroup, deletedElements)
}
