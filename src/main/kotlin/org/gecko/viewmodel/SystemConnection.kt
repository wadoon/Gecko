package org.gecko.viewmodel

import javafx.beans.property.ObjectProperty
import javafx.geometry.Point2D
import org.gecko.actions.ActionManager
import org.gecko.view.GeckoView
import org.gecko.view.contextmenu.SystemConnectionViewElementContextMenuBuilder
import org.gecko.view.views.viewelement.SystemConnectionViewElement
import org.gecko.view.views.viewelement.decorator.ConnectionElementScalerViewElementDecorator
import org.gecko.view.views.viewelement.decorator.ViewElementDecorator
import tornadofx.getValue
import tornadofx.setValue

/**
 * Represents an abstraction of a [SystemConnection] model element. A [SystemConnection] is
 * described by a source- and a destination-[Port]. Contains methods for managing the afferent data
 * and updating the target-[SystemConnection].
 */
data class SystemConnection(
    val sourceProperty: ObjectProperty<Port?> = nullableObjectProperty<Port>(),
    val destinationProperty: ObjectProperty<Port?> = nullableObjectProperty<Port>(),
) : PositionableElement(), ConnectionViewModel {
    override val edgePoints = listProperty<Point2D>()

    override val children: Sequence<Element>
        get() = sequenceOf()

    override fun updateIssues() {
        issues.clear()
        if (source == null)
            issues.report("", 1.0)
        if (destination == null)
            issues.report("", 1.0)
    }

    var source: Port? by sourceProperty
    var destination: Port? by destinationProperty

    override fun asJson() =
        super.asJson().apply {
            addProperty("source", source?.name)
            addProperty("destination", destination?.name)
        }

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

    override fun view(actionManager: ActionManager, geckoView: GeckoView): ViewElementDecorator {
        val newSystemConnectionViewElement = SystemConnectionViewElement(this)
        val contextMenuBuilder =
            SystemConnectionViewElementContextMenuBuilder(actionManager, this, geckoView)
        // setContextMenu(newSystemConnectionViewElement.pane, contextMenuBuilder)
        return ConnectionElementScalerViewElementDecorator(newSystemConnectionViewElement)
    }

    override fun setEdgePoint(index: Int, newPosition: Point2D) {
        edgePoints[index] = newPosition
    }
}

/**
 * Checks if a connection between the given source and destination port is allowed.
 *
 * @param source the new source port
 * @param destination the new destination port
 * @param sourceSystem the system that contains the source port
 * @param destinationSystem the system that contains the destination port
 * @param parentSystem the system where the connection is created
 * @param systemConnection the connection that is being edited, or null if a new connection is being
 *   created
 * @return true if the connection is allowed, false otherwise
 */
fun isConnectingAllowed(
    source: Port,
    destination: Port,
    sourceSystem: System?,
    destinationSystem: System?,
    parentSystem: System?,
    systemConnection: SystemConnection?
): Boolean {
    if (sourceSystem == null || destinationSystem == null || parentSystem == null) {
        return false
    }
    if (
        destination.hasIncomingConnection &&
        !(systemConnection != null && systemConnection.destination == destination)
    ) {
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
