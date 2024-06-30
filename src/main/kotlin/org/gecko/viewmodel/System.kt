package org.gecko.viewmodel

import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Point2D
import org.gecko.actions.ActionManager
import org.gecko.view.GeckoView
import org.gecko.view.contextmenu.SystemViewElementContextMenuBuilder
import org.gecko.view.contextmenu.ViewContextMenuBuilder
import org.gecko.view.inspector.builder.AbstractInspectorBuilder
import org.gecko.view.inspector.builder.SystemInspectorBuilder
import org.gecko.view.views.viewelement.SystemViewElement
import org.gecko.view.views.viewelement.decorator.SelectableViewElementDecorator
import org.gecko.view.views.viewelement.decorator.ViewElementDecorator
import tornadofx.getValue
import tornadofx.setValue

/**
 * Represents an abstraction of a [System] model element. A [System] is described by a code snippet
 * and a set of [Port]s. Contains methods for managing the afferent data and updating the
 * target-[System].
 */
data class System(
    val codeProperty: SimpleStringProperty = SimpleStringProperty(""),
    val portsProperty: ListProperty<Port> = SimpleListProperty(FXCollections.observableArrayList())
) : BlockElement(), Inspectable {
    val allElements: MutableList<PositionableElement>
        get() {
            val allElements =
                ArrayList<PositionableElement>(
                    subSystems.size + portsProperty.size + connections.size
                )
            allElements.addAll(subSystems)
            allElements.addAll(portsProperty)
            allElements.addAll(connections)
            return allElements
        }

    override val children: Sequence<Element>
        get() = TODO("Not yet implemented")

    override fun updateIssues() {
        issues.clear()
    }

    override fun view(actionManager: ActionManager, geckoView: GeckoView): ViewElementDecorator {
        val newSystemViewElement = SystemViewElement(this)
        val contextMenuBuilder: ViewContextMenuBuilder =
            SystemViewElementContextMenuBuilder(actionManager, this, geckoView)
        // setContextMenu(newSystemViewElement, contextMenuBuilder)
        return SelectableViewElementDecorator(newSystemViewElement)
    }

    val connectionsProperty = listProperty<SystemConnection>()
    val connections by connectionsProperty

    var parent: System? = null

    var code: String by codeProperty
    var ports: ObservableList<Port> by portsProperty
    val subSystemsProperty = listProperty<System>()
    var subSystems: ObservableList<System> by subSystemsProperty

    val automatonProperty = objectProperty(Automaton())
    var automaton: Automaton by automatonProperty

    override fun asJson() =
        super.asJson().apply {
            addProperty("code", code)
            add("connections", connections.asJsonArray())
            add("ports", ports.asJsonArray())
            add("subSystems", subSystems.asJsonArray())
            add("automaton", automaton.asJson())
        }

    init {
        size = DEFAULT_SYSTEM_SIZE
        name = AutoNaming.name("System_")
    }

    fun addPort(port: Port) {
        portsProperty.add(port)
        port.systemPositionProperty.bind(positionProperty)
    }

    fun removePort(port: Port) {
        portsProperty.remove(port)
        port.systemPositionProperty.unbind()
    }

    fun removeConnection(con: SystemConnection) = connectionsProperty.remove(con)

    fun addConnection(con: SystemConnection) = connections.add(con)

    fun getChildByName(name: String): System? = subSystems.firstOrNull { it.name == name }

    fun getChildSystemWithVariable(element: Port) =
        subSystems.firstOrNull { it.ports.contains(element) }

    fun getVariableByName(text: String?): Port? = ports.firstOrNull { it.name == text }

    fun createVariable(): Port = Port().also { addPort(it) }

    fun createSubSystem(): System =
        System().also {
            subSystems.add(it)
            it.parent = this
        }

    companion object {
        val DEFAULT_SYSTEM_SIZE = Point2D(300.0, 300.0)
    }

    override fun inspector(actionManager: ActionManager): AbstractInspectorBuilder<*> =
        SystemInspectorBuilder(actionManager, this)

    fun updateSystemParents() {
        for (child in subSystems) {
            child.parent = this
            child.updateSystemParents()
        }
    }
}
