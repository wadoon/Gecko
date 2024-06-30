package org.gecko.viewmodel

import javafx.beans.property.*
import javafx.geometry.Point2D
import org.gecko.actions.ActionManager
import org.gecko.view.GeckoView
import org.gecko.view.contextmenu.VariableBlockViewElementContextMenuBuilder
import org.gecko.view.inspector.builder.AbstractInspectorBuilder
import org.gecko.view.inspector.builder.VariableBlockInspectorBuilder
import org.gecko.view.views.viewelement.VariableBlockViewElement
import org.gecko.view.views.viewelement.decorator.SelectableViewElementDecorator
import org.gecko.view.views.viewelement.decorator.ViewElementDecorator
import tornadofx.getValue
import tornadofx.setValue

/**
 * Represents an abstraction of a [Variable] model element. A [Port] is described by a type and a
 * [Visibility]. Contains methods for managing the afferent data and updating the target-[Variable].
 */
data class Port(
    val visibilityProperty: Property<Visibility> = SimpleObjectProperty(Visibility.STATE),
    val typeProperty: StringProperty = SimpleStringProperty("int"),
    val valueProperty: StringProperty = SimpleStringProperty("")
) : BlockElement(), Inspectable {
    val systemPortPositionProperty = SimpleObjectProperty(Point2D.ZERO)
    val systemPortSizeProperty = SimpleObjectProperty(Point2D.ZERO)

    val incomingConnections = listProperty<SystemConnection>()
    val outgoingConnections = listProperty<SystemConnection>()

    val systemPositionProperty = SimpleObjectProperty(Point2D.ZERO)
    val systemPortOffsetProperty = SimpleObjectProperty(Point2D.ZERO)

    val hasIncomingConnection: Boolean
        get() = incomingConnections.isNotEmpty()

    var visibility: Visibility by visibilityProperty
    var type: String by typeProperty
    var value: String? by valueProperty

    init {
        sizeProperty.value = DEFAULT_PORT_SIZE
        name = AutoNaming.name("port_${visibility}_")
    }

    override val children: Sequence<Element>
        get() = sequenceOf()

    override fun updateIssues() {
        issues.clear()
        if (type !in builtinTypes) {
            issues.report("Not a built-in types used", 0.5)
        }
    }

    fun setSystemPortPosition(position: Point2D) {
        systemPortPositionProperty.value = position
    }

    fun setSystemPortSize(size: Point2D) {
        systemPortSizeProperty.value = size
    }

    fun addIncomingConnection(connection: SystemConnection) {
        incomingConnections.add(connection)
    }

    fun removeIncomingConnection(connection: SystemConnection) {
        incomingConnections.remove(connection)
    }

    fun addOutgoingConnection(connection: SystemConnection) {
        outgoingConnections.add(connection)
    }

    fun removeOutgoingConnection(connection: SystemConnection) {
        outgoingConnections.remove(connection)
    }


    override fun view(actionManager: ActionManager, geckoView: GeckoView): ViewElementDecorator {
        val newVariableBlockViewElement = VariableBlockViewElement(this)
        val contextMenuBuilder =
            VariableBlockViewElementContextMenuBuilder(actionManager, this, geckoView)
        // setContextMenu(newVariableBlockViewElement, contextMenuBuilder)
        return SelectableViewElementDecorator(newVariableBlockViewElement)
    }

    override fun inspector(actionManager: ActionManager): AbstractInspectorBuilder<*> =
        VariableBlockInspectorBuilder(actionManager, this)

    override fun asJson() =
        super.asJson().apply {
            addProperty("type", type)
            addProperty("visibility", visibility.name)
            addProperty("value", value)
        }

    companion object {
        val DEFAULT_PORT_SIZE = Point2D(100.0, 50.0)
    }
}
