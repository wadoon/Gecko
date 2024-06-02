package org.gecko.model

import kotlinx.serialization.Serializable

import org.gecko.exceptions.ModelException

/**
 * Represents an automaton in the domain model of a Gecko project. An [Automaton] is described by a set of
 * [State]s, a set of [Edge]s that connect the states, a set of [Region]s that contain multiple states
 * and a start-[State]. Contains methods for managing the afferent data.
 */
@Serializable
data class Automaton(
    var startState: State? = null,
    val regions: MutableSet<Region> = mutableSetOf(),
    val states: MutableSet<State> = mutableSetOf(),
    val edges: MutableSet<Edge> = mutableSetOf()
) {

    fun validate() {
        if (startState != null && !states.contains(startState)) {
            throw ModelException("State cannot be set as start-state.")
        }
    }

    fun getStateWithContract(contract: Contract?) = states.find { it.contracts.contains(contract) }

    fun addRegion(region: Region) {
        regions.add(region)
    }

    fun addRegions(regions: Set<Region>) {
        for (region in regions) {
            addRegion(region)
        }
    }

    fun removeRegion(region: Region) {
        regions.remove(region)
    }

    fun removeRegions(regions: Set<Region>) {
        for (region in regions) {
            removeRegion(region)
        }
    }

    fun addState(state: State) {
        states.add(state)
    }

    fun addStates(states: Set<State>) {
        for (state in states) {
            addState(state)
        }
    }

    @Throws(ModelException::class)
    fun removeState(state: State) {
        if (states.size == 1 && states.contains(state)) {
            startState = null
            states.remove(state)
            return
        }
        if (state == startState && states.size > 1) {
            throw ModelException("Cannot remove the start state of an automaton.")
        }
        states.remove(state)
    }

    @Throws(ModelException::class)
    fun removeStates(states: Set<State>) {
        for (state in states) {
            removeState(state)
        }
    }

    fun addEdge(edge: Edge) {
        edges.add(edge)
    }

    fun addEdges(edges: Set<Edge>) {
        for (edge in edges) {
            addEdge(edge)
        }
    }

    fun removeEdge(edge: Edge) {
        edges.remove(edge)
    }

    fun removeEdges(edges: Set<Edge>) {
        for (edge in edges) {
            removeEdge(edge)
        }
    }

    fun getStateByName(name: String): State? = states.firstOrNull { it.name == name }
    fun getOutgoingEdges(state: State) = edges.filter { edge -> edge.source == state }
    fun getRegionsWithState(state: State?) = regions.filter { it.states.contains(state) }

    val isEmpty: Boolean
        get() = states.isEmpty() && edges.isEmpty() && regions.isEmpty()

    val allElements: Set<Element>
        get() {
            val allElements: MutableSet<Element> = HashSet()
            allElements.addAll(regions)
            allElements.addAll(states)
            allElements.addAll(edges)
            return allElements
        }
}
