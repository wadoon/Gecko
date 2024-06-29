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
 * Represents an abstraction of a [Variable] model element. A [PortViewModel] is described by a type and a
 * [Visibility]. Contains methods for managing the afferent data and updating the target-[Variable].
 */
data class PortViewModel(
    val visibilityProperty: Property<Visibility> = SimpleObjectProperty(Visibility.STATE),
    val typeProperty: StringProperty = SimpleStringProperty("int"),
    val valueProperty: StringProperty = SimpleStringProperty("")
) : BlockViewModelElement(), Inspectable {
    val systemPortPositionProperty = SimpleObjectProperty(Point2D.ZERO)
    val systemPortSizeProperty = SimpleObjectProperty(Point2D.ZERO)

    val incomingConnections = listProperty<SystemConnectionViewModel>()
    val outgoingConnections = listProperty<SystemConnectionViewModel>()

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

    fun setSystemPortPosition(position: Point2D) {
        systemPortPositionProperty.value = position
    }

    fun setSystemPortSize(size: Point2D) {
        systemPortSizeProperty.value = size
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

    override val children: Sequence<Element>
        get() = sequenceOf()

    override fun view(actionManager: ActionManager, geckoView: GeckoView): ViewElementDecorator {
        val newVariableBlockViewElement = VariableBlockViewElement(this)
        val contextMenuBuilder = VariableBlockViewElementContextMenuBuilder(actionManager, this, geckoView)
        //setContextMenu(newVariableBlockViewElement, contextMenuBuilder)
        return SelectableViewElementDecorator(newVariableBlockViewElement)
    }

    override fun inspector(actionManager: ActionManager): AbstractInspectorBuilder<*> =
        VariableBlockInspectorBuilder(actionManager, this)

    override fun asJson() = super.asJson().apply {
        addProperty("type", type)
        addProperty("visibility", visibility.name)
        addProperty("value", value)
    }

    companion object {
        val DEFAULT_PORT_SIZE = Point2D(100.0, 50.0)
    }

}
