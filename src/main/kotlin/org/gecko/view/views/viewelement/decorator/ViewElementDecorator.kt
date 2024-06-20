package org.gecko.view.views.viewelement.decorator

import javafx.collections.ObservableList
import javafx.geometry.Point2D
import org.gecko.view.views.viewelement.ViewElement
import org.gecko.viewmodel.PositionableViewModelElement

/**
 * An abstract representation of a [ViewElement] implementation following the decorator pattern and targeting a
 * decorated [ViewElement], which can be selected and encapsulates a [PositionableViewModelElement].
 */
abstract class ViewElementDecorator(val decoratorTarget: ViewElement<*>) :
    ViewElement<PositionableViewModelElement?> {
    override var isSelected = false

    override val zPriority: Int
        get() {
            if (isSelected) {
                return decoratorTarget.zPriority + 1
            }
            return decoratorTarget.zPriority
        }

    override val target
        get() = decoratorTarget.target

    override val edgePoints: ObservableList<Point2D>
        get() = decoratorTarget.edgePoints

    override fun setEdgePoint(index: Int, point: Point2D): Boolean {
        return decoratorTarget.setEdgePoint(index, point)
    }
}
