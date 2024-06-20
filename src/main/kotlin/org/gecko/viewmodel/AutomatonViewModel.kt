package org.gecko.viewmodel

import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.geometry.Point2D
import tornadofx.getValue
import tornadofx.setValue

/**
 * Represents an abstraction of a [System] model element. A [AutomatonViewModel] is described by a code snippet
 * and a set of [PortViewModel]s. Contains methods for managing the afferent data and updating the
 * target-[System].
 */
data class AutomatonViewModel(
    //val portsProperty: ListProperty<PortViewModel> = SimpleListProperty(FXCollections.observableArrayList()),
    val statesProperty: ListProperty<StateViewModel> = SimpleListProperty(FXCollections.observableArrayList()),
    val edgesProperty: ListProperty<EdgeViewModel> = SimpleListProperty(FXCollections.observableArrayList()),
    val regionsProperty: ListProperty<RegionViewModel> = SimpleListProperty(FXCollections.observableArrayList()),
) : BlockViewModelElement() {
    val allElements: MutableList<PositionableViewModelElement> = arrayListOf()//(TODO

    val edges by edgesProperty
    //var ports by portsProperty
    var states by statesProperty
    var regions by regionsProperty

    var startState: StateViewModel? = null
        /**
         * Sets the start state of the automaton of the system.
         *
         * @param value the new start state
         */
        set(value) {
            field?.let { it.isStartState = false }
            value?.isStartState = true
            field = value
        }

    init {
        size = DEFAULT_SYSTEM_SIZE
    }

    /*fun addPort(port: PortViewModel) {
        portsProperty.add(port)
        port.systemPositionProperty.bind(positionProperty)
    }

    fun removePort(port: PortViewModel) {
        portsProperty.remove(port)
        port.systemPositionProperty.unbind()
    }*/

    override fun <S> accept(visitor: PositionableViewModelElementVisitor<S>): S {
        return visitor.visit(this)
    }

    fun removeEdge(target: EdgeViewModel) = edges.remove(target)
    fun removeRegion(target: RegionViewModel) = regions.remove(target)
    fun removeState(s: StateViewModel) = states.remove(s)
    fun getOutgoingEdges(state: StateViewModel) = edges.filter { it.source == state }
    fun getStateByName(startName: String?): StateViewModel? = states.firstOrNull { it.name == startName }
    fun createEdge(start: StateViewModel, end: StateViewModel): EdgeViewModel =
        EdgeViewModel(start, end).also {
            edges.add(it)
            start.outgoingEdges.add(it)
            end.incomingEdges.add(it)
        }

    fun createState(): StateViewModel = StateViewModel().also { states.add(it) }
    fun getRegionsWithState(state: StateViewModel): List<RegionViewModel> = regions.filter { it.includes(state) }
    fun addRegion(target: RegionViewModel) = regionsProperty.add(target)
    fun addEdge(edge: EdgeViewModel) = edges.add(edge)

    companion object {
        val DEFAULT_SYSTEM_SIZE = Point2D(300.0, 300.0)
    }
}
