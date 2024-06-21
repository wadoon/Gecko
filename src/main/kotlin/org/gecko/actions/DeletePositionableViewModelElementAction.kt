package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.PositionableViewModelElement
import org.gecko.viewmodel.SystemViewModel

/**
 * A concrete representation of an [Action] that deletes a set of [PositionableViewModelElement]s and their
 * dependencies from the [GeckoViewModel].
 */
class DeletePositionableViewModelElementAction : Action {
    val geckoViewModel: GeckoViewModel
    val elementsToDelete: Set<PositionableViewModelElement>
    var deleteActionGroup: ActionGroup? = null
    var deletedElements: Set<PositionableViewModelElement> = setOf()

    internal constructor(geckoViewModel: GeckoViewModel, element: PositionableViewModelElement) {
        this.geckoViewModel = geckoViewModel
        this.elementsToDelete = java.util.Set.of(element)
    }

    internal constructor(geckoViewModel: GeckoViewModel, elements: Set<PositionableViewModelElement>?) {
        this.geckoViewModel = geckoViewModel
        this.elementsToDelete = HashSet(elements)
    }

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        val allDeleteActions: MutableSet<AbstractPositionableViewModelElementAction> = HashSet()
        for (element in elementsToDelete) {
            val visitor = DeleteActionsHelper(geckoViewModel, geckoViewModel.currentEditor!!.currentSystem)
                            .visit(element)

            allDeleteActions.addAll(visitor)
        }

        deletedElements = allDeleteActions.map { it.target }.toSet()
        geckoViewModel.currentEditor!!.selectionManager.deselect(deletedElements)
        deleteActionGroup = ActionGroup(ArrayList<Action>(allDeleteActions))
        return deleteActionGroup!!.run()
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return RestorePositionableViewModelElementAction(geckoViewModel, deleteActionGroup, deletedElements)
    }
}
