package org.gecko.viewmodel

import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Point2D
import javafx.scene.paint.Color


import org.gecko.exceptions.ModelException
import org.gecko.model.*

/**
 * Represents an abstraction of a [Variable] model element. A [PortViewModel] is described by a type and a
 * [Visibility]. Contains methods for managing the afferent data and updating the target-[Variable].
 */
class PortViewModel(id: Int, target: Variable) : BlockViewModelElement<Variable>(id, target) {
    val visibilityProperty: Property<Visibility> = SimpleObjectProperty(target.visibility)
    val typeProperty: StringProperty = SimpleStringProperty(target.type)
    val valueProperty: StringProperty = SimpleStringProperty(target.value)

    val systemPortPositionProperty: Property<Point2D>
    val systemPortSizeProperty: Property<Point2D>

    val incomingConnections: ObservableList<SystemConnectionViewModel>
    val outgoingConnections: ObservableList<SystemConnectionViewModel>

    val systemPositionProperty: Property<Point2D>
    val systemPortOffsetProperty: Property<Point2D>

    init {
        sizeProperty.value = DEFAULT_PORT_SIZE
        this.systemPortPositionProperty = SimpleObjectProperty(Point2D.ZERO)
        this.systemPortSizeProperty = SimpleObjectProperty(Point2D.ZERO)
        this.systemPositionProperty = SimpleObjectProperty(Point2D.ZERO)
        this.systemPortOffsetProperty = SimpleObjectProperty(Point2D.ZERO)
        this.incomingConnections = FXCollections.observableArrayList()
        this.outgoingConnections = FXCollections.observableArrayList()
    }

    fun setSystemPortPosition(position: Point2D) {
        systemPortPositionProperty.value = position
    }

    fun setSystemPortSize(size: Point2D) {
        systemPortSizeProperty.value = size
    }

    var visibility: Visibility
        get() = visibilityProperty.value
        set(visibility) {
            visibilityProperty.value = visibility
        }

    var type: String
        get() = typeProperty.value
        set(type) {
            typeProperty.value = type
        }

    var value: String?
        get() = valueProperty.value
        set(value) {
            valueProperty.value = value
        }

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

    @Throws(ModelException::class)
    override fun updateTarget() {
        super.updateTarget()
        target.visibility = visibility
        target!!.type = type
        target.value = value!!
    }

    override fun <T> accept(visitor: PositionableViewModelElementVisitor<T>): T {
        return visitor.visit(this)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is PortViewModel) {
            return false
        }
        return id == o.id
    }

    companion object {
        val DEFAULT_PORT_SIZE = Point2D(100.0, 50.0)
        fun getBackgroundColor(visibility: Visibility): Color {
            return when (visibility) {
                Visibility.INPUT -> Color.LIGHTGREEN
                Visibility.OUTPUT -> Color.LIGHTGOLDENRODYELLOW
                Visibility.STATE -> Color.LIGHTSEAGREEN
            }
        }
    }
}
