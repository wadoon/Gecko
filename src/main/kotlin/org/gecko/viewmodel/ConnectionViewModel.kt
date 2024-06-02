package org.gecko.viewmodel

import javafx.collections.ObservableList
import javafx.geometry.Point2D

/**
 * Provides methods for managing the edge points of connection-type [PositionableViewModelElement]s that must be
 * implemented by concrete such elements.
 */
interface ConnectionViewModel {
    /**
     * Sets the position of the edge newPosition at the given index.
     *
     * @param index       the index of the edge newPosition
     * @param newPosition the new position of the edge newPosition
     */
    fun setEdgePoint(index: Int, newPosition: Point2D)

    val edgePoints: ObservableList<Point2D?>?
}
