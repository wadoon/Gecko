package org.gecko.model

import javafx.util.Pair

import org.gecko.exceptions.ModelException

/**
 * Represents a factory for the model elements of a Gecko project. Provides a method for the creation of each element.
 */
class ModelFactory(val geckoModel: GeckoModel) {
    var elementId: UInt = 0u
    fun getDefaultName(id: UInt): String {
        var id = id
        var name = DEFAULT_NAME.format(id)
        while (!geckoModel.isNameUnique(name)) {
            id++
            name = DEFAULT_NAME.format(id)
        }
        return name
    }

    @get:Throws(ModelException::class)
    val defaultContract: Contract
        get() {
            val id = newElementId
            return Contract(id, getDefaultName(id), Condition(DEFAULT_CONDITION), Condition(DEFAULT_CONDITION))
        }

    val newElementId: UInt
        get() = elementId++

    @Throws(ModelException::class)
    fun createState(automaton: Automaton): State {
        val id = newElementId
        val state = State(id, getDefaultName(id))
        automaton.addState(state)
        return state
    }

    fun copyState(state: State): Pair<State, Map<Contract, Contract>> {
        val id = newElementId
        val contractToCopy: MutableMap<Contract, Contract> = HashMap()
        val copy: State
        try {
            copy = State(id, getDefaultName(id))
        } catch (e: ModelException) {
            throw RuntimeException("Failed to create a copy of the state", e)
        }
        for (contract in state.contracts) {
            val copiedContract = copyContract(contract)
            copy.addContract(copiedContract)
            contractToCopy[contract] = copiedContract
        }
        return Pair(copy, contractToCopy)
    }

    @Throws(ModelException::class)
    fun createEdge(automaton: Automaton, source: State, destination: State): Edge {
        if (automaton.states.isEmpty() || !automaton.states.contains(source) || !automaton.states
                .contains(destination)
        ) {
            throw RuntimeException(
                "Failed to create edge, because source and / or destination states "
                        + "are not part of the automaton."
            )
        }

        val id = newElementId
        val edge = Edge(id, source, destination, defaultContract, DEFAULT_KIND, DEFAULT_PRIORITY)
        automaton.addEdge(edge)
        return edge
    }

    fun copyEdge(edge: Edge): Edge {
        val id = newElementId
        try {
            return Edge(
                id, edge.source, edge.destination, edge.contract, edge.kind,
                edge.priority
            )
        } catch (e: ModelException) {
            throw RuntimeException("Failed to create a copy of an edge", e)
        }
    }

    @Throws(ModelException::class)
    fun createSystem(parentSystem: System): System {
        val id = newElementId
        val system = System(id, getDefaultName(id), DEFAULT_CODE, Automaton())
        parentSystem.addChild(system)
        system.parent = parentSystem
        return system
    }

    @JvmOverloads
    @Throws(ModelException::class)
    fun copySystem(system: System, originalToCopy: MutableMap<Element, Element> = HashMap())
            : Pair<System, Map<Element, Element>> {
        val id = newElementId
        val copy: System
        try {
            copy = System(id, getDefaultName(id), DEFAULT_CODE, Automaton())
        } catch (e: ModelException) {
            throw RuntimeException("Failed to create a copy of the system", e)
        }
        for (state in system.automaton.states) {
            val copyResult = copyState(state)
            val copiedState = copyResult.key
            originalToCopy.putAll(copyResult.value)
            copy.automaton.addState(copiedState)
            if (system.automaton.startState == state) {
                copy.automaton.startState = copiedState
            }
            originalToCopy[state] = copiedState
        }
        for (edge in system.automaton.edges) {
            val copiedSource = originalToCopy[edge.source] as State?
            val copiedDestination = originalToCopy[edge.destination] as State?
            val copiedEdge = createEdge(copy.automaton, copiedSource!!, copiedDestination!!)
            copiedEdge.contract = originalToCopy[edge.contract!!] as Contract
            copiedEdge.kind = edge.kind
        }
        for (region in system.automaton.regions) {
            val copiedRegion = copyRegion(region)
            copy.automaton.addRegion(copiedRegion)
            originalToCopy[region] = copiedRegion
        }
        for (variable in system.variables) {
            val copiedVariable = copyVariable(variable)
            originalToCopy[variable] = copiedVariable
            copy.addVariable(copiedVariable)
        }
        for (childSystem in system.children) {
            val copiedChildSystem = copySystem(childSystem, originalToCopy).key
            copy.addChild(copiedChildSystem)
            originalToCopy[childSystem] = copiedChildSystem
        }
        for (childSystem in copy.children) {
            childSystem.parent = copy
        }
        for (connection in system.connections) {
            val copiedConnection =
                copySystemConnection(
                    connection, originalToCopy[connection.source] as Variable?,
                    originalToCopy[connection.destination] as Variable?
                )
            copy.addConnection(copiedConnection)
            originalToCopy[connection] = copiedConnection
        }
        copy.code = system.code
        originalToCopy[system] = copy
        return Pair(copy, originalToCopy)
    }

    @Throws(ModelException::class)
    fun createRoot(): System {
        val id = newElementId
        return System(id, getDefaultName(id), DEFAULT_CODE, Automaton())
    }

    @Throws(ModelException::class)
    fun createVariable(system: System): Variable {
        val id = newElementId
        val variable = Variable(id, getDefaultName(id), DEFAULT_TYPE, visibility = DEFAULT_VISIBILITY)
        system.addVariable(variable)
        return variable
    }

    fun copyVariable(variable: Variable): Variable {
        val id = newElementId
        try {
            return Variable(id, getDefaultName(id), variable.type, visibility = variable.visibility)
        } catch (e: ModelException) {
            throw RuntimeException("Failed to create a copy of a variable", e)
        }
    }

    @Throws(ModelException::class)
    fun createSystemConnection(
        system: System, source: Variable, destination: Variable
    ): SystemConnection {
        val sourceParent = geckoModel.getSystemWithVariable(source)
        val destinationParent = geckoModel.getSystemWithVariable(destination)

        if (sourceParent == null || destinationParent == null) {
            throw RuntimeException(
                "Failed to create system connection, because source and / or destination "
                        + "variables are not part of the model."
            )
        }

        if (source == destination) {
            throw ModelException("A system connection cannot connect a variable to itself.")
        }

        if (sourceParent == system && sourceParent == destinationParent) {
            throw ModelException("Two variables on the same level cannot be connected.")
        }

        if (!isConnectingAllowed(system, sourceParent, destinationParent, source, destination)) {
            throw ModelException("Failed to connect the source to the destination variable.")
        }

        val id = newElementId
        val connection = SystemConnection(id, source, destination)
        system.addConnection(connection)
        return connection
    }

    fun isConnectingAllowed(
        system: System, sourceParent: System, destinationParent: System, source: Variable, destination: Variable
    ): Boolean {
        if (sourceParent != system && destinationParent != system) {
            if (sourceParent.parent != system || destinationParent.parent != system) {
                return false
            }
            return source.visibility == Visibility.OUTPUT && destination.visibility == Visibility.INPUT
        } else if (sourceParent == system) {
            return source.visibility != Visibility.OUTPUT && destination.visibility != Visibility.OUTPUT
        }
        return source.visibility != Visibility.INPUT && destination.visibility != Visibility.INPUT
    }

    fun copySystemConnection(connection: SystemConnection): SystemConnection {
        val id = newElementId
        try {
            return SystemConnection(id, connection.source, connection.destination)
        } catch (e: ModelException) {
            throw RuntimeException("Failed to create a copy of a system connection", e)
        }
    }

    fun copySystemConnection(
        connection: SystemConnection, copiedSource: Variable?, copiedDestination: Variable?
    ): SystemConnection {
        val result = copySystemConnection(connection)
        try {
            result.source = copiedSource!!
            result.destination = copiedDestination!!
        } catch (e: ModelException) {
            throw RuntimeException(e)
        }
        return result
    }

    @Throws(ModelException::class)
    fun createContract(state: State): Contract {
        val id = newElementId
        val contract =
            Contract(id, getDefaultName(id), Condition(DEFAULT_CONDITION), Condition(DEFAULT_CONDITION))
        state.addContract(contract)
        return contract
    }

    fun copyContract(contract: Contract): Contract {
        val id = newElementId
        try {
            return Contract(
                id, getDefaultName(id), Condition(contract.preCondition.condition),
                Condition(contract.postCondition.condition)
            )
        } catch (e: ModelException) {
            throw RuntimeException("Failed to create a copy of a contract", e)
        }
    }

    @Throws(ModelException::class)
    fun createRegion(automaton: Automaton): Region {
        val id = newElementId
        val region = Region(id, getDefaultName(id), Condition(DEFAULT_CONDITION), defaultContract)
        automaton.addRegion(region)
        return region
    }

    fun copyRegion(region: Region): Region {
        val id = newElementId
        try {
            return Region(
                id, getDefaultName(id), Condition(region.invariant.condition),
                copyContract(region.preAndPostCondition)
            )
        } catch (e: ModelException) {
            throw RuntimeException("Failed to create a copy of a region", e)
        }
    }

    @Throws(ModelException::class)
    fun createCondition(init: String): Condition {
        return Condition(init)
    }

    companion object {
        const val DEFAULT_NAME = "Element_%d"
        const val DEFAULT_TYPE = "int"
        val DEFAULT_CONDITION: String = Condition.trueCondition().condition
        val DEFAULT_KIND = Kind.HIT
        const val DEFAULT_PRIORITY = 0u
        val DEFAULT_CODE: String = ""
        val DEFAULT_VISIBILITY = Visibility.INPUT
    }
}
