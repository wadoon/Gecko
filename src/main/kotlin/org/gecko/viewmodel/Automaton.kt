package org.gecko.viewmodel

import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.geometry.Point2D
import org.gecko.actions.ActionManager
import org.gecko.exceptions.ModelException
import org.gecko.view.GeckoView
import org.gecko.view.views.EditorView
import org.gecko.view.views.viewelement.decorator.ViewElementDecorator
import tornadofx.getValue
import tornadofx.setValue

val DEFAULT_SYSTEM_SIZE = Point2D(300.0, 300.0)

/**
 * Represents an abstraction of a [System] model element. A [Automaton] is described by a code
 * snippet and a set of [Port]s. Contains methods for managing the afferent data and updating the
 * target-[System].
 */
data class Automaton(
    // val portsProperty: ListProperty<PortViewModel> =
    // SimpleListProperty(FXCollections.observableArrayList()),
    val statesProperty: ListProperty<State> =
        SimpleListProperty(FXCollections.observableArrayList()),
    val edgesProperty: ListProperty<Edge> = SimpleListProperty(FXCollections.observableArrayList()),
    val regionsProperty: ListProperty<Region> =
        SimpleListProperty(FXCollections.observableArrayList()),
) : BlockElement(), Openable {
    val edges by edgesProperty
    var states by statesProperty
    var regions by regionsProperty

    init {
        name = AutoNaming.name("Automaton_")
    }

    override fun asJson() =
        super.asJson().apply {
            add("states", states.asJsonArray())
            add("edges", edges.asJsonArray())
            add("regions", regions.asJsonArray())
        }

    val allElements: MutableList<PositionableElement>
        get() = (states + edges + regions).toMutableList()

    override val children: Sequence<Element>
        get() = states.asSequence() + edges.asSequence() + regions.asSequence()

    init {
        size = DEFAULT_SYSTEM_SIZE
    }

    override fun validate() {
        if (startState != null && !states.contains(startState)) {
            throw ModelException("State cannot be set as start-state.")
        }
    }



    /*fun addPort(port: PortViewModel) {
        portsProperty.add(port)
        port.systemPositionProperty.bind(positionProperty)
    }

    fun removePort(port: PortViewModel) {
        portsProperty.remove(port)
        port.systemPositionProperty.unbind()
    }*/

    fun removeEdge(target: Edge) = edges.remove(target)

    fun removeRegion(target: Region) = regions.remove(target)

    fun removeState(s: State) = states.remove(s)

    fun getOutgoingEdges(state: State) = edges.filter { it.source == state }

    fun getStateByName(startName: String?): State? = states.firstOrNull { it.name == startName }

    fun createEdge(start: State, end: State): Edge =
        Edge(start, end).also {
            edges.add(it)
            start.outgoingEdges.add(it)
            end.incomingEdges.add(it)
        }

    fun createState(): State = State().also { states.add(it) }

    fun getRegionsWithState(state: State): List<Region> =
        regions.filter { it.checkStateInRegion(state) }

    fun addRegion(target: Region) = regionsProperty.add(target)

    fun addEdge(edge: Edge) = edges.add(edge)

    override fun editor(actionManager: ActionManager, geckoView: GeckoView): EditorView {
        TODO("Not yet implemented")
    }

    override fun view(actionManager: ActionManager, geckoView: GeckoView): ViewElementDecorator {
        TODO()
    }
}
