package org.gecko.viewmodel


import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.geometry.Point2D
import org.gecko.exceptions.ModelException
import org.gecko.model.*

/**
 * Represents an abstraction of a [SystemConnection] model element. A [SystemConnectionViewModel] is
 * described by a source- and a destination-[PortViewModel]. Contains methods for managing the afferent data and
 * updating the target-[SystemConnection].
 */


class SystemConnectionViewModel(
    id: Int,
    target: SystemConnection?,
    source: PortViewModel,
    destination: PortViewModel
) : PositionableViewModelElement<SystemConnection>(id, target!!), ConnectionViewModel {
    val sourceProperty = SimpleObjectProperty<PortViewModel>()
    val destinationProperty = SimpleObjectProperty<PortViewModel>()
    override val edgePoints = SimpleListProperty(FXCollections.observableArrayList<Point2D>())

    init {
        sourceProperty.set(source)
        destinationProperty.set(destination)
        sizeProperty.value = Point2D.ZERO
        source.addOutgoingConnection(this)
        destination.addIncomingConnection(this)
    }

    var source: PortViewModel
        get() = sourceProperty.value
        set(source) {
            sourceProperty.value.removeOutgoingConnection(this)
            sourceProperty.value = source
            source.addOutgoingConnection(this)
        }

    var destination: PortViewModel
        get() = destinationProperty.value
        set(destination) {
            destinationProperty.value.removeIncomingConnection(this)
            destinationProperty.value = destination
            destination.addIncomingConnection(this)
        }

    @Throws(ModelException::class)
    override fun updateTarget() {
        target!!.source = source.target
        target.destination = destination.target
    }

    override fun <S> accept(visitor: PositionableViewModelElementVisitor<S>): S {
        return visitor.visit(this)
    }

    override fun setEdgePoint(index: Int, newPosition: Point2D) {
        edgePoints[index] = newPosition
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is SystemConnectionViewModel) {
            return false
        }
        return id == o.id
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
    if (destination.target.hasIncomingConnection && !(systemConnection != null && systemConnection.destination == destination)) {
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
