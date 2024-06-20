package org.gecko.view.views.viewelement

import javafx.collections.ObservableList
import javafx.geometry.Point2D
import javafx.scene.Node
import org.gecko.viewmodel.PositionableViewModelElement

/**
 * Provides methods used in the visualization of an element in the view. Any such view element corresponds to a
 * view-model element, which is why the interface is generic, encapsulating a type of
 * [PositionableViewModelElement]. Concrete visitors must implement this interface to define specific behavior for
 * each view element.
 */
interface ViewElement<T : PositionableViewModelElement?> {
    /**
     * Draw the element and returns a javafx node representing the element.
     *
     * @return the javafx node
     */
    fun drawElement(): Node

    /**
     * Get the edge points of the element in world coordinates. The edge points represent the bound points of the
     * element.
     *
     *
     * In case of a connection, these points represent the start and end points of the connection.
     *
     *
     * In case of a region, these points represent the bound points of the region.
     *
     * @return the edge points
     */
    val edgePoints: ObservableList<Point2D>

    /**
     * Set the edge point at the given index.
     *
     * @param index the index
     * @param point new point
     * @return true if the edge point was set, false if the edge point could not be set due to size limitations
     */
    fun setEdgePoint(index: Int, point: Point2D): Boolean {
        return true
    }

    /**
     * Get the selected state of the element.
     *
     * @return the selected state
     */
    /**
     * Set the selected state of the element.
     *
     * @param selected the selected state
     */
    var isSelected: Boolean

    /**
     * Get the target view model of the element.
     *
     * @return the target view model
     */
    val target: T

    /**
     * Get the position of the element in world coordinates.
     *
     * @return the position
     */
    val position: Point2D

    /**
     * Get the z priority of the element. The z priority is used to determine the order of the elements in the view.
     *
     * @return the z priority
     */
    val zPriority: Int

    /**
     * Accept the visitor.
     *
     * @param visitor the visitor
     */
    fun accept(visitor: ViewElementVisitor)
}
