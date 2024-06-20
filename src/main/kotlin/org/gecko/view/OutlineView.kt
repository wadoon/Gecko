package org.gecko.view

import javafx.scene.control.Label
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderPane
import org.gecko.viewmodel.*
import tornadofx.treeview

/**
 *
 * @author Alexander Weigl
 * @version 1 (20.06.24)
 */
class OutlineView(val viewModel: GeckoViewModel) {
    val root = BorderPane().apply {
        top = Label("Outline")
        center = treeview(SystemItem(viewModel.root)) {}
    }
}

class SystemItem(val sys: SystemViewModel) : TreeItem<String>("") {
    init {
        populate()
        sys.nameProperty.onChange { _, _ -> populate() }
        sys.automatonProperty.onChange { _, _ -> populate() }
        sys.subSystemsProperty.onListChange { _ -> populate() }
        sys.portsProperty.onListChange { _ -> populate() }
    }

    private fun populate() {
        value = "System: ${sys.name}"
        val ports = sys.ports
            .sorted(Comparator.comparing { it.name })
            .map { PortItem(it) }
            .toList()
        val systems = sys.subSystems
            .sorted(Comparator.comparing { it.name })
            .map { SystemItem(it) }
        val a = AutomataItem(sys.automaton)
        children.setAll(listOf(a) + ports + systems)
    }
}

class AutomataItem(val auto: AutomatonViewModel) : TreeItem<String>("") {
    init {
        populate()
        auto.nameProperty.onChange { _, _ -> populate() }
        auto.statesProperty.onChange { _, _ -> populate() }
        auto.regionsProperty.onChange { _, _ -> populate() }
    }

    private fun populate() {
        value = "Automaton: ${auto.name}"

        val states = auto.states
            .sorted(Comparator.comparing { it.name })
            .map {
                val a = TreeItem("State: " + it.name)
                a.children.setAll(
                    it.contracts.map { c -> TreeItem("C: " + c.name) }
                )
                a
            }

        val regions = auto.regions.sorted(Comparator.comparing { it.name })
            .map { TreeItem("R: " + it.name) }
        children.setAll(states + regions)
    }
}

class PortItem(val it: PortViewModel) : TreeItem<String>("") {
    init {
        populate()
        it.nameProperty.onChange { old, new -> populate() }
        it.typeProperty.onChange { old, new -> populate() }
        it.valueProperty.onChange { old, new -> populate() }
        it.visibilityProperty.onChange { old, new -> populate() }
    }

    private fun populate() {
        value = it.name + " : " + it.type + "(${it.visibility})"
    }
}
