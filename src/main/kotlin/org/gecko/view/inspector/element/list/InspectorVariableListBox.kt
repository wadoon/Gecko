package org.gecko.view.inspector.element.list

import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import org.gecko.actions.*
import org.gecko.model.*
import org.gecko.view.inspector.element.container.InspectorVariableField
import org.gecko.viewmodel.PortViewModel
import org.gecko.viewmodel.SystemViewModel
import java.util.function.Consumer

/**
 * A concrete representation of an [AbstractInspectorList] encapsulating an [InspectorVariableField].
 */
class InspectorVariableListBox(
    val actionManager: ActionManager,
    val viewModel: SystemViewModel,
    val visibility: Visibility
) : AbstractInspectorList<InspectorVariableField>() {
    init {
        minHeight = MIN_HEIGHT.toDouble()

        viewModel.portsProperty.addListener { change: ListChangeListener.Change<out PortViewModel> ->
            this.onPortsListChanged(change)
        }

        viewModel.ports.stream().filter { port: PortViewModel -> port.visibility == visibility }
            .forEach { port: PortViewModel -> this.addPortItem(port) }
        viewModel.ports.forEach(Consumer<PortViewModel> { port: PortViewModel ->
            port.visibilityProperty.addListener { observable: ObservableValue<out Visibility>, oldValue: Visibility?, newValue: Visibility? ->
                this.onVisibilityChanged(observable)
            }
        })
    }

    fun onPortsListChanged(change: ListChangeListener.Change<out PortViewModel>) {
        while (change.next()) {
            if (change.wasAdded()) {
                change.addedSubList.forEach { port: PortViewModel ->
                    port.visibilityProperty.addListener { observable, oldValue, newValue ->
                        this.onVisibilityChanged(observable)
                    }
                    if (port.visibility == visibility) {
                        addPortItem(port)
                    }
                }
            }
            if (change.wasRemoved()) {
                change.removed.forEach { port: PortViewModel ->
                    if (port.visibility == visibility) {
                        removePortItem(port)
                    }
                }
            }
        }
    }

    fun onVisibilityChanged(observable: ObservableValue<out Visibility>) {
        //Since the visibility property of a port changed we should always find that port
        val port = viewModel.ports.first { it.visibilityProperty === observable }
        if (port.visibility == visibility) {
            addPortItem(port)
        } else {
            removePortItem(port)
        }
    }

    fun addPortItem(port: PortViewModel) {
        val field = InspectorVariableField(actionManager, port)
        items.add(field)
    }

    fun removePortItem(port: PortViewModel) {
        items.removeIf { field -> field!!.viewModel == port }
    }

    companion object {
        const val MIN_HEIGHT = 60
    }
}
