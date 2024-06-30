package org.gecko.viewmodel

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Point2D
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import tornadofx.getValue
import tornadofx.plus
import tornadofx.setValue
import tornadofx.times

/**
 * Represents an abstraction of a view model element that has a rectangular shape in a Gecko
 * project. A [BlockElement] has a name. Contains methods for moving and scaling the element.
 */
abstract class BlockElement : PositionableElement(), Renamable {
    final override val nameProperty = stringProperty("")
    final override var name: String by nameProperty

    override fun asJson() = super.asJson().apply { addProperty("name", name) }

    /**
     * Manipulates the position and size of the element, so that the two points are two diagonally
     * opposite corners of the [BlockElement].
     *
     * @param firstCornerPoint the first corner point
     * @param secondCornerPoint the second corner point that is diagonally opposite to the first
     *   corner point
     */
    fun manipulate(firstCornerPoint: Point2D, secondCornerPoint: Point2D): Boolean {
        val newStartPosition =
            Point2D(
                min(firstCornerPoint.x, secondCornerPoint.x),
                min(firstCornerPoint.y, secondCornerPoint.y)
            )
        val newEndPosition =
            Point2D(
                max(firstCornerPoint.x, secondCornerPoint.x),
                max(firstCornerPoint.y, secondCornerPoint.y)
            )

        if (
            abs(newEndPosition.x - newStartPosition.x) *
                abs(newEndPosition.y - newStartPosition.y) <= MIN_HEIGHT * MIN_WIDTH
        ) {
            return false
        }

        position = newStartPosition
        size = newEndPosition.subtract(newStartPosition)
        return true
    }

    fun setPositionFromCenter(center: Point2D) {
        position =
            Point2D(center.x - +sizeProperty.value.x / 2, center.y - +sizeProperty.value.y / 2)
    }

    override val center: Point2D
        get() = positionProperty.value + sizeProperty.value * 0.5

    companion object {
        const val MIN_WIDTH: Double = 100.0
        const val MIN_HEIGHT: Double = 100.0
    }
}

fun <K, V> Map<K, V?>.extends(vararg mapOf: Pair<K, V?>): Map<K, V?> =
    this.toMutableMap().also { it.putAll(mapOf) }
