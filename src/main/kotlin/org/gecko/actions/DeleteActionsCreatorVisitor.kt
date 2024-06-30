package org.gecko.actions


import org.gecko.viewmodel.*

/**
 * Follows the visitor pattern, implementing the [PositionableViewModelElementVisitor] interface. Determines all
 * necessary delete-[Action]s for deleting all "lower level"-dependencies of a given parent-@link
 * SystemViewModel}.
 */
class DeleteActionsHelper(val gModel: GModel, val parentSystem: System) {
    fun visit(system: System): List<AbstractPositionableViewModelElementAction> {
        val ports = system.ports.flatMap { visit(it) }
        val subs = system.subSystems.map { visit(it) }.flatten()
        val c = system.connections.flatMap { visit(it) }
        return ports + subs + c + visit(system.automaton) +
                listOf(DeleteSystemAction(gModel, system, parentSystem))
    }

    fun visit(Region: Region): List<AbstractPositionableViewModelElementAction> =
        listOf(DeleteRegionAction(gModel, Region, parentSystem.automaton))

    fun visit(systemConnectionViewModel: SystemConnection) = listOf(
        DeleteSystemConnectionViewModelElementAction(gModel, systemConnectionViewModel, parentSystem)
    )

    fun visit(Edge: Edge) = listOf(
        DeleteEdgeViewModelElementAction(gModel, Edge, parentSystem.automaton)
    )

    fun visit(state: State) = parentSystem.automaton.edges
        .filter { it.source == state || (it.destination == state) }
        .map { visit(it) }
        .flatten() + listOf(
        DeleteStateViewModelElementAction(
            gModel,
            state,
            parentSystem
        )
    )

    // parentSystem has to be the system that contains the port
    fun visit(Port: Port): List<AbstractPositionableViewModelElementAction> {
        val actualParentSystem: System?
        val containingSystem: System
        if (parentSystem.ports.contains(Port)) {
            actualParentSystem = parentSystem.parent
            containingSystem = parentSystem
        } else {
            actualParentSystem = parentSystem
            containingSystem =
                parentSystem.subSystems.first { system -> system.ports.contains(Port) }
        }

        if (actualParentSystem != null) {
            visitSystemConnections(actualParentSystem, Port)
        }
        visitSystemConnections(containingSystem, Port)

        return listOf(DeletePortViewModelElementAction(gModel, Port, containingSystem))
    }

    fun visit(Automaton: Automaton): List<AbstractPositionableViewModelElementAction> {
        //TODO("Not yet implemented")
        return listOf()
    }

    fun visitSystemConnections(
        System: System,
        Port: Port
    ): List<DeleteSystemConnectionViewModelElementAction> {
        val systemConnections =
            System.connections.filter { it.source == Port || it.destination == Port }
        return systemConnections.flatMap { visit(it) }
    }

    fun visit(element: PositionableElement) = when (element) {
        is Automaton -> visit(element)
        is Port -> visit(element)
        is System -> visit(element)
        is Region -> visit(element)
        is Edge -> visit(element)
        is SystemConnection -> visit(element)
        is State -> visit(element)
        else -> error("Unkown element ${element.javaClass}")
    }
}
