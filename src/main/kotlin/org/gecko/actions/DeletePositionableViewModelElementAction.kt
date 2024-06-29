package org.gecko.actions

import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.PositionableViewModelElement

/**
 * A concrete representation of an [Action] that deletes a set of [PositionableViewModelElement]s and their
 * dependencies from the [GeckoViewModel].
 */
class DeletePositionableViewModelElementAction(
    val geckoViewModel: GeckoViewModel,
    val elementsToDelete: Set<PositionableViewModelElement>
) : Action() {
    lateinit var deleteActionGroup: ActionGroup
    val deletedElements = mutableSetOf<PositionableViewModelElement>()

    constructor(geckoViewModel: GeckoViewModel, element: PositionableViewModelElement)
            : this(geckoViewModel, setOf(element))

    override fun run(): Boolean {
        val allDeleteActions = hashSetOf<AbstractPositionableViewModelElementAction>()
        for (element in elementsToDelete) {
            val visitor =
                DeleteActionsHelper(geckoViewModel, geckoViewModel.currentEditor!!.currentSystem)
                    .visit(element)
            allDeleteActions.addAll(visitor)
        }

        deletedElements.addAll(allDeleteActions.map { it.target }.toSet())
        geckoViewModel.currentEditor!!.selectionManager.deselect(deletedElements)
        deleteActionGroup = ActionGroup(allDeleteActions.toList())
        return deleteActionGroup.run()
    }

    override fun getUndoAction(actionFactory: ActionFactory) =
        RestorePositionableViewModelElementAction(geckoViewModel, deleteActionGroup, deletedElements)
}
