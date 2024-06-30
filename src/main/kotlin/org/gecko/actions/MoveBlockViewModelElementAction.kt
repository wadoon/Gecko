package org.gecko.actions

import javafx.geometry.Point2D
import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.EditorViewModel
import org.gecko.viewmodel.PositionableElement

/**
 * A concrete representation of an [Action] that moves a set of {link PositionableViewModelElement}s with a given
 * [delta value][Point2D].
 */
class MoveBlockViewModelElementAction : Action {
    val editorViewModel: EditorViewModel
    var elementsToMove: Set<PositionableElement>?
    val delta: Point2D

    internal constructor(editorViewModel: EditorViewModel, delta: Point2D) {
        this.editorViewModel = editorViewModel
        this.elementsToMove = null
        this.delta = delta
    }

    internal constructor(
        editorViewModel: EditorViewModel,
        elementsToMove: Set<PositionableElement>?,
        delta: Point2D
    ) {
        this.editorViewModel = editorViewModel
        this.elementsToMove = elementsToMove
        this.delta = delta
    }

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        if (delta == Point2D.ZERO) {
            return false
        }
        if (elementsToMove == null) {
            elementsToMove = HashSet(editorViewModel.selectionManager.currentSelection)
        }
        for (element in elementsToMove!!) {
            element.position = (element.position.add(delta))
        }

        editorViewModel.updateRegions()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createMoveBlockViewModelElementAction(elementsToMove, delta.multiply(-1.0))
    }
}
