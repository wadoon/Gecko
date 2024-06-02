package org.gecko.view.views.viewelement


import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.geometry.Point2D
import javafx.scene.layout.Pane
import org.gecko.model.Element
import org.gecko.viewmodel.PositionableViewModelElement

/**
 * An abstract representation of a [Pane] view element, that is an element with a rectangular shape in a Gecko
 * project. Contains a list of [edge point][Point2D]s.
 */
abstract class BlockViewElement(
    positionableViewModelElement: PositionableViewModelElement<out Element>
) : Pane() {

    val edgePoints = SimpleListProperty(FXCollections.observableArrayList<Point2D>())
    val isSelected = false

    init {
        // Initialize edge points for a rectangular shaped block
        for (i in 0..3) {
            edgePoints.add(Point2D.ZERO)
        }

        // Auto calculate new edge points on size and position changes
        positionableViewModelElement.sizeProperty.addListener { _, _, _ ->
            calculateEdgePoints(positionableViewModelElement)
        }
        positionableViewModelElement.positionProperty.addListener { _, _, _ ->
            calculateEdgePoints(positionableViewModelElement)
        }

        calculateEdgePoints(positionableViewModelElement)
    }

    fun calculateEdgePoints(target: PositionableViewModelElement<*>) {
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
