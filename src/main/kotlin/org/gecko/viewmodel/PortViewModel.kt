package org.gecko.viewmodel


import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import tornadofx.getValue
import tornadofx.setValue


/**
 * Represents an abstraction of a [Variable] model element. A [PortViewModel] is described by a type and a
 * [Visibility]. Contains methods for managing the afferent data and updating the target-[Variable].
 */
data class PortViewModel(
    val visibilityProperty: Property<Visibility> = SimpleObjectProperty(Visibility.STATE),
    val typeProperty: StringProperty = SimpleStringProperty("int"),
    val valueProperty: StringProperty = SimpleStringProperty("")
) : BlockViewModelElement() {
    val systemPortPositionProperty = SimpleObjectProperty(Point2D.ZERO)
    val systemPortSizeProperty = SimpleObjectProperty(Point2D.ZERO)

    val incomingConnections = listProperty<SystemConnectionViewModel>()
    val outgoingConnections = listProperty<SystemConnectionViewModel>()

    val systemPositionProperty = SimpleObjectProperty(Point2D.ZERO)
    val systemPortOffsetProperty = SimpleObjectProperty(Point2D.ZERO)

    val hasIncomingConnection: Boolean
        get() = incomingConnections.isNotEmpty()

    init {
        sizeProperty.value = DEFAULT_PORT_SIZE
    }

    fun setSystemPortPosition(position: Point2D) {
        systemPortPositionProperty.value = position
    }

    fun setSystemPortSize(size: Point2D) {
        systemPortSizeProperty.value = size
    }

    var visibility: Visibility by visibilityProperty
    var type: String by typeProperty
    var value: String? by valueProperty

    fun addIncomingConnection(connection: SystemConnectionViewModel) {
        incomingConnections.add(connection)
    }

    fun removeIncomingConnection(connection: SystemConnectionViewModel) {
        incomingConnections.remove(connection)
    }

    fun addOutgoingConnection(connection: SystemConnectionViewModel) {
        outgoingConnections.add(connection)
    }

    fun removeOutgoingConnection(connection: SystemConnectionViewModel) {
        outgoingConnections.remove(connection)
    }

    override fun <T> accept(visitor: PositionableViewModelElementVisitor<T>): T {
        return visitor.visit(this)
    }

    companion object {
        val DEFAULT_PORT_SIZE = Point2D(100.0, 50.0)
    }
}
