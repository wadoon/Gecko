package org.gecko.viewmodel


import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.geometry.Point2D

import tornadofx.getValue
import tornadofx.setValue

/**
 * Represents an abstraction of a [SystemConnection] model element. A [SystemConnectionViewModel] is
 * described by a source- and a destination-[PortViewModel]. Contains methods for managing the afferent data and
 * updating the target-[SystemConnection].
 */


data class SystemConnectionViewModel(
    val sourceProperty: SimpleObjectProperty<PortViewModel?> = SimpleObjectProperty<PortViewModel?>(),
    val destinationProperty: SimpleObjectProperty<PortViewModel?> = SimpleObjectProperty<PortViewModel?>()
) : PositionableViewModelElement(), ConnectionViewModel {
    override val edgePoints = listProperty<Point2D>()

    var source: PortViewModel? by sourceProperty
    var destination: PortViewModel? by destinationProperty

    init {
        sizeProperty.value = Point2D.ZERO
        source?.addOutgoingConnection(this)
        destination?.addIncomingConnection(this)

        sourceProperty.onChange { old, new ->
            old?.removeOutgoingConnection(this)
            new?.addOutgoingConnection(this)
        }

        destinationProperty.onChange { old, new ->
            old?.removeIncomingConnection(this)
            new?.addIncomingConnection(this)
        }
    }

    override fun <S> accept(visitor: PositionableViewModelElementVisitor<S>): S {
        return visitor.visit(this)
    }

    override fun setEdgePoint(index: Int, newPosition: Point2D) {
        edgePoints[index] = newPosition
    }
}

/**
 * Checks if a connection between the given source and destination port is allowed.
 *
 * @param source            the new source port
 * @param destination       the new destination port
 * @param sourceSystem      the system that contains the source port
 * @param destinationSystem the system that contains the destination port
 * @param parentSystem      the system where the connection is created
 * @param systemConnection  the connection that is being edited, or null if a new connection is being created
 * @return true if the connection is allowed, false otherwise
 */
fun isConnectingAllowed(
    source: PortViewModel, destination: PortViewModel, sourceSystem: SystemViewModel?,
    destinationSystem: SystemViewModel?, parentSystem: SystemViewModel?,
    systemConnection: SystemConnectionViewModel?
): Boolean {
    if (sourceSystem == null || destinationSystem == null || parentSystem == null) {
        return false
    }
    if (destination.hasIncomingConnection && !(systemConnection != null && systemConnection.destination == destination)) {
        return false
    }
    if (sourceSystem == destinationSystem) {
        return false
    }

    return if (sourceSystem != parentSystem && destinationSystem != parentSystem) {
        source.visibility == Visibility.OUTPUT && destination.visibility == Visibility.INPUT
    } else if (sourceSystem == parentSystem) {
        source.visibility != Visibility.OUTPUT && destination.visibility != Visibility.OUTPUT
    } else {
        source.visibility != Visibility.INPUT && destination.visibility != Visibility.INPUT
    }
}
