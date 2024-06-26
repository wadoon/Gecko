package org.gecko.view.views.viewelement

import javafx.geometry.Point2D
import javafx.scene.layout.Pane
import org.gecko.viewmodel.PositionableElement
import org.gecko.viewmodel.listProperty

/**
 * An abstract representation of a [Pane] view element, that is an element with a rectangular shape
 * in a Gecko project. Contains a list of [edge point][Point2D]s.
 */
abstract class BlockViewElement(positionableElement: PositionableElement) : Pane() {
    val edgePoints = listProperty<Point2D>()

    init {
        // Initialize edge points for a rectangular shaped block
        for (i in 0..3) {
            edgePoints.add(Point2D.ZERO)
        }

        // Auto calculate new edge points on size and position changes
        positionableElement.sizeProperty.addListener { _, _, _ ->
            calculateEdgePoints(positionableElement)
        }
        positionableElement.positionProperty.addListener { _, _, _ ->
            calculateEdgePoints(positionableElement)
        }

        calculateEdgePoints(positionableElement)
    }

    fun calculateEdgePoints(target: PositionableElement) {
        val position = target.position
        val width = target.size.x
        val height = target.size.y
        edgePoints.setAll(
            position.add(Point2D.ZERO),
            position.add(Point2D(width, 0.0)),
            position.add(Point2D(width, height)),
            position.add(Point2D(0.0, height))
        )
    }
}

const val BACKGROUND_ROUNDING: Int = 15
