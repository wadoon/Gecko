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
 * Represents an abstraction of a [System] model element. A [SystemViewModel] is described by a code snippet
 * and a set of [PortViewModel]s. Contains methods for managing the afferent data and updating the
 * target-[System].
 */
data class SystemViewModel(
    val codeProperty: SimpleStringProperty = SimpleStringProperty(""),
    val portsProperty: ListProperty<PortViewModel> = SimpleListProperty(FXCollections.observableArrayList())
) : BlockViewModelElement(), Inspectable {
    val allElements: MutableList<PositionableViewModelElement>
        get() {
            val allElements =
                ArrayList<PositionableViewModelElement>(subSystems.size + portsProperty.size + connections.size)
            allElements.addAll(subSystems)
            allElements.addAll(portsProperty)
            allElements.addAll(connections)
            return allElements
        }

    override val children: Sequence<Element>
        get() = TODO("Not yet implemented")

    override fun view(actionManager: ActionManager, geckoView: GeckoView): ViewElementDecorator {
        val newSystemViewElement = SystemViewElement(this)
        val contextMenuBuilder: ViewContextMenuBuilder =
            SystemViewElementContextMenuBuilder(actionManager, this, geckoView)
        //setContextMenu(newSystemViewElement, contextMenuBuilder)
        return SelectableViewElementDecorator(newSystemViewElement)
    }

    val connectionsProperty = listProperty<SystemConnectionViewModel>()
    val connections by connectionsProperty

    var parent: SystemViewModel? = null

    var code: String by codeProperty
    var ports: ObservableList<PortViewModel> by portsProperty
    val subSystemsProperty = listProperty<SystemViewModel>()
    var subSystems: ObservableList<SystemViewModel> by subSystemsProperty

    val automatonProperty = objectProperty(AutomatonViewModel())
    var automaton: AutomatonViewModel by automatonProperty


    init {
        size = DEFAULT_SYSTEM_SIZE
    }

    fun addPort(port: PortViewModel) {
        portsProperty.add(port)
        port.systemPositionProperty.bind(positionProperty)
    }

    fun removePort(port: PortViewModel) {
        portsProperty.remove(port)
        port.systemPositionProperty.unbind()
    }


    fun removeConnection(con: SystemConnectionViewModel) = connectionsProperty.remove(con)
    fun addConnection(con: SystemConnectionViewModel) = connections.add(con)

    fun getChildByName(name: String): SystemViewModel? =
        subSystems.firstOrNull { it.name == name }

    fun getChildSystemWithVariable(element: PortViewModel) =
        subSystems.firstOrNull { it.ports.contains(element) }

    fun getVariableByName(text: String?): PortViewModel? = ports.firstOrNull { it.name == text }
    fun createVariable(): PortViewModel = PortViewModel().also { addPort(it) }
    fun createSubSystem(): SystemViewModel = SystemViewModel().also { subSystems.add(it) }

    companion object {
        val DEFAULT_SYSTEM_SIZE = Point2D(300.0, 300.0)
    }

    override fun inspector(actionManager: ActionManager): AbstractInspectorBuilder<*> =
        SystemInspectorBuilder(actionManager, this)

    fun deleteAction() {

    }
}
