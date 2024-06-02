package org.gecko.model

import kotlinx.serialization.Serializable
import org.gecko.exceptions.MissingViewModelElementException
import org.gecko.exceptions.ModelException

/**
 * Represents a system in the domain model of a Gecko project. A [System] has a name, a parent-[System], a
 * set of children-[System]s and an [Automaton]. It is also described by a code snippet, a set of
 * [Variable]s and a set of [SystemConnection]s connecting the variables. Contains methods for managing the
 * afferent data.
 */
@Serializable
data class System(
    override val id: UInt = 0u,
    override var name: String? = null,
    var code: String = "",
    var automaton: Automaton = Automaton(),
    val children: MutableSet<System> = mutableSetOf(),
    val connections: MutableSet<SystemConnection> = mutableSetOf(),
    val variables: MutableSet<Variable> = mutableSetOf(),
) : Element(), Renamable {
    var parent: System? = null

    fun validate() {
        if (name!!.isEmpty()) {
            throw ModelException("System's name is invalid.")
        }
        if (code != null && code.isEmpty()) {
            throw ModelException("System's code is invalid.")
        }
    }

    fun addChild(child: System) {
        children.add(child)
    }

    fun addChildren(children: Set<System>) {
        for (child in children) {
            addChild(child)
        }
    }

    fun removeChild(child: System) {
        children.remove(child)
    }

    fun removeChildren(children: Set<System>) {
        for (child in children) {
            removeChild(child)
        }
    }

    fun addConnection(connection: SystemConnection) {
        connections.add(connection)
    }

    fun addConnections(connections: Set<SystemConnection>) {
        for (connection in connections) {
            addConnection(connection)
        }
    }

    fun removeConnection(connection: SystemConnection) {
        connection.destination.hasIncomingConnection = false
        connections.remove(connection)
    }

    fun removeConnections(connections: Set<SystemConnection>) {
        for (connection in connections) {
            removeConnection(connection)
        }
    }

    fun addVariable(variable: Variable) {
        variables.add(variable)
    }

    fun addVariables(variables: Set<Variable>) {
        for (variable in variables) {
            addVariable(variable)
        }
    }

    fun removeVariable(variable: Variable) {
        variables.remove(variable)
    }

    fun removeVariables(variables: Set<Variable>) {
        for (variable in variables) {
            removeVariable(variable)
        }
    }

    fun getChildByName(name: String): System? = children.find { child -> child.name == name }

    fun getVariableByName(name: String): Variable? = variables.find { variable -> variable.name == name }

    val allChildren: List<System>
        /**
         * Returns all children of this system, including the children of the children, and so on.
         *
         * @return a list of all children of this system
         */
        get() = children.flatMap { child -> listOf(child) + child.allChildren }

    val allElements: Set<Element>
        get() {
            val allElements = HashSet<Element>()
            allElements.addAll(children)
            allElements.addAll(variables)
            allElements.addAll(connections)
            return allElements
        }

    fun getChildSystemWithVariable(variable: Variable?) = children.find { variable in it.variables }

    @Throws(ModelException::class, MissingViewModelElementException::class)
    override fun accept(visitor: ElementVisitor) {
        visitor.visit(this)
    }
}
