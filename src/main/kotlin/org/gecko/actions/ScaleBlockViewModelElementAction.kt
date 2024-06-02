package org.gecko.actions

import javafx.geometry.Point2D
import org.gecko.exceptions.GeckoException
import org.gecko.view.views.viewelement.decorator.ElementScalerBlock
import org.gecko.viewmodel.BlockViewModelElement
import org.gecko.viewmodel.EditorViewModel

/**
 * A concrete representation of an [Action] that scales a  [BlockViewModelElement] by a
 * [delta value][Point2D] using an [ElementScalerBlock].
 */
class ScaleBlockViewModelElementAction : Action {
    val editorViewModel: EditorViewModel
    val element: BlockViewModelElement<*>
    val elementScalerBlock: ElementScalerBlock?
    var size: Point2D? = null
    var position: Point2D? = null
    var isPreviousScale = false

    internal constructor(
        editorViewModel: EditorViewModel, element: BlockViewModelElement<*>, elementScalerBlock: ElementScalerBlock?,
        position: Point2D?, size: Point2D?, isPreviousScale: Boolean
    ) {
        this.editorViewModel = editorViewModel
        this.element = element
        this.elementScalerBlock = elementScalerBlock
        this.position = position
        this.size = size
        this.isPreviousScale = isPreviousScale
    }

    internal constructor(
        editorViewModel: EditorViewModel,
        element: BlockViewModelElement<*>,
        elementScalerBlock: ElementScalerBlock?
    ) {
        this.editorViewModel = editorViewModel
        this.element = element
        this.elementScalerBlock = elementScalerBlock
    }

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        val positionCopy = element.position
        val sizeCopy = element.size
        if (!isPreviousScale) {
            element.size = size!!
            element.position = position!!
            size = sizeCopy
            position = positionCopy
        }

        editorViewModel.updateRegions()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createScaleBlockViewModelElementAction(element, elementScalerBlock, position, size, false)
    }
}
