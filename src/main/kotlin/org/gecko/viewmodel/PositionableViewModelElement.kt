package org.gecko.viewmodel

import javafx.beans.property.BooleanProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Point2D
import tornadofx.getValue
import tornadofx.setValue

/**
 * Represents an abstraction of a view model element that is graphically represented in a Gecko project. A
 * [PositionableViewModelElement] is described by a position- and a size-[Point2D]. Contains methods for
 * managing the different data.
 */
abstract class PositionableViewModelElement : Element(),Viewable {
    val positionProperty: Property<Point2D> = SimpleObjectProperty(Point2D.ZERO)
    val sizeProperty: Property<Point2D> = SimpleObjectProperty(DEFAULT_SIZE)
    val isCurrentlyModified: BooleanProperty = SimpleBooleanProperty(false)
    val observers: Set<PositionableViewModelElement> = HashSet()

    open var position: Point2D by positionProperty
    open var size: Point2D by sizeProperty

    open val center: Point2D
        get() = Point2D(
            positionProperty.value.x + sizeProperty.value.x / 2,
            positionProperty.value.y + sizeProperty.value.y / 2
        )
    /*set(point) {
        position = Point2D(
            point!!.x - sizeProperty.value.x / 2,
            point.y - sizeProperty.value.y / 2
        )
    }*/

    fun setCurrentlyModified(isCurrentlyModified: Boolean) {
        this.isCurrentlyModified.value = isCurrentlyModified
    }

    fun isCurrentlyModified(): Boolean {
        return isCurrentlyModified.value
    }

    companion object {
        val DEFAULT_SIZE = Point2D(200.0, 300.0)
    }
}
