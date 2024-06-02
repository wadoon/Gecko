package org.gecko.viewmodel

import javafx.beans.property.*
import javafx.geometry.Point2D
import org.gecko.exceptions.ModelException
import org.gecko.model.*
import org.gecko.model.Renamable
import tornadofx.getValue
import tornadofx.plus
import tornadofx.setValue
import tornadofx.times
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Represents an abstraction of a view model element that has a rectangular shape in a Gecko project. A
 * [BlockViewModelElement] has a name. Contains methods for moving and scaling the element.
 */

abstract class BlockViewModelElement<T>(id: Int, target: T) : PositionableViewModelElement<T>(id, target),
    org.gecko.viewmodel.Renamable where T : Renamable, T : Element {

    override val nameProperty: StringProperty = SimpleStringProperty()
    override var name by nameProperty

    /**
     * Manipulates the position and size of the element, so that the two points are two diagonally opposite corners of
     * the [BlockViewModelElement].
     *
     * @param firstCornerPoint  the first corner point
     * @param secondCornerPoint the second corner point that is diagonally opposite to the first corner point
     */
    fun manipulate(firstCornerPoint: Point2D, secondCornerPoint: Point2D): Boolean {
        val newStartPosition = Point2D(
            min(firstCornerPoint.x, secondCornerPoint.x),
            min(firstCornerPoint.y, secondCornerPoint.y)
        )
        val newEndPosition = Point2D(
            max(firstCornerPoint.x, secondCornerPoint.x),
            max(firstCornerPoint.y, secondCornerPoint.y)
        )

        if (abs(newEndPosition.x - newStartPosition.x) * abs(
                newEndPosition.y - newStartPosition.y
            ) <= MIN_HEIGHT * MIN_WIDTH
        ) {
            return false
        }

        position = newStartPosition
        size = newEndPosition.subtract(newStartPosition)
        return true
    }

    override var center: Point2D?
        get() = positionProperty.value + sizeProperty.value * 0.5
        set(value) {}

    @Throws(ModelException::class)
    override fun updateTarget() {
        target.name = name
    }

    companion object {
        const val MIN_WIDTH: Double = 100.0
        const val MIN_HEIGHT: Double = 100.0
    }
}
