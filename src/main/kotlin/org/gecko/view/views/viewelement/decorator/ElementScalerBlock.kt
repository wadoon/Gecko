package org.gecko.view.views.viewelement.decorator

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Point2D
import javafx.scene.shape.Rectangle
import tornadofx.minus

/**
 * Represents a type of [Rectangle] used for displaying the scaling of elements.
 */
class ElementScalerBlock(
    val index: Int,
    val decoratorTarget: ElementScalerViewElementDecorator,
    width: Double,
    height: Double
) : Rectangle() {
    var isDragging = false
    var listener: ChangeListener<Point2D>? = null

    init {
        setWidth(width)
        setHeight(height)

        refreshListeners()
    }

    /**
     * Refreshes scaler block listeners and updates its position.
     */
    fun refreshListeners() {
        /*if (listener != null) {
            decoratorgetEdgePoints().get(index).removeListener(listener);
        }*/
        updatePosition()

        val newListener =
            ChangeListener { observable: ObservableValue<out Point2D?>?, oldValue: Point2D?, newValue: Point2D? ->
                if (!isDragging) {
                    updatePosition()
                }
            }
        //decoratorgetEdgePoints().get(index).addListener(newListener);
        //listener = newListener;
    }

    /**
     * Updates the position of the scaler block to match the edge point.
     */
    fun updatePosition() {
        layoutX =
            decoratorTarget.edgePoints[index].x - decoratorTarget.position.x - (width / 2)
        layoutY =
            decoratorTarget.edgePoints[index].y - decoratorTarget.position.y - (height / 2)
    }

    /**
     * Sets the center position of the scaler in world coordinates block and updates the edge point.
     *
     * @param point The new center of the scaler block.
     */
    fun setCenter(point: Point2D): Boolean {
        var point = point
        if (decoratorTarget.setEdgePoint(index, point)) {
            point = point!! - decoratorTarget.position
            val center = point - Point2D(width / 2, height / 2)
            layoutX = center.x
            layoutY = center.y
            return true
        }
        return false
    }

    var layoutPosition: Point2D
        /**
         * Returns the position of the scaler block.
         *
         * @return The position of the scaler block.
         */
        get() = Point2D(layoutX, layoutY)
        /**
         * Sets the position of the scaler block and updates the edge point.
         *
         * @param point The new position of the scaler block.
         */
        set(point) {
            layoutX = point.x
            layoutY = point.y

            decoratorTarget.setEdgePoint(index, point.add(width / 2, height / 2))
        }

    val center: Point2D
        /**
         * Returns the center position of the scaler block.
         *
         * @return The center position of the scaler block.
         */
        get() = Point2D(layoutX + (width / 2), layoutY + (height / 2))
}
