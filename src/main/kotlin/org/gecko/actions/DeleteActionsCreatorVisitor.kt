package org.gecko.actions


import org.gecko.viewmodel.*

/**
 * Follows the visitor pattern, implementing the [PositionableViewModelElementVisitor] interface. Determines all
 * necessary delete-[Action]s for deleting all "lower level"-dependencies of a given parent-@link
 * SystemViewModel}.
 */
class DeleteActionsHelper(val geckoViewModel: GeckoViewModel, val parentSystemViewModel: SystemViewModel) {
    fun visit(system: SystemViewModel): List<AbstractPositionableViewModelElementAction> {
        val ports = system.ports.flatMap { visit(it) }
        val subs = system.subSystems.map { visit(it) }.flatten()
        val c = system.connections.flatMap { visit(it) }
        return ports + subs + c + visit(system.automaton) +
                listOf(DeleteSystemViewModelElementAction(geckoViewModel, system, parentSystemViewModel))
    }

    fun visit(regionViewModel: RegionViewModel): List<AbstractPositionableViewModelElementAction> =
        listOf(DeleteRegionViewModelElementAction(geckoViewModel, regionViewModel, parentSystemViewModel.automaton))

    fun visit(systemConnectionViewModel: SystemConnectionViewModel) = listOf(
        DeleteSystemConnectionViewModelElementAction(geckoViewModel, systemConnectionViewModel, parentSystemViewModel)
    )

    fun visit(edgeViewModel: EdgeViewModel) = listOf(
        DeleteEdgeViewModelElementAction(geckoViewModel, edgeViewModel, parentSystemViewModel.automaton)
    )

    fun visit(stateViewModel: StateViewModel) = parentSystemViewModel.automaton.edges
        .filter { it.source == stateViewModel || (it.destination == stateViewModel) }
        .map { visit(it) }
        .flatten() + listOf(
        DeleteStateViewModelElementAction(
            geckoViewModel,
            stateViewModel,
            parentSystemViewModel
        )
    )

    // parentSystem has to be the system that contains the port
    fun visit(portViewModel: PortViewModel): List<AbstractPositionableViewModelElementAction> {
        val actualParentSystemViewModel: SystemViewModel
        val containingSystemViewModel: SystemViewModel
        if (parentSystemViewModel.ports.contains(portViewModel)) {
            actualParentSystemViewModel = parentSystemViewModel.parent!!
            containingSystemViewModel = parentSystemViewModel
        } else {
            actualParentSystemViewModel = parentSystemViewModel
            containingSystemViewModel =
                parentSystemViewModel.subSystems.first { system -> system.ports.contains(portViewModel) }
        }

        if (actualParentSystemViewModel != null) {
            visitSystemConnections(actualParentSystemViewModel, portViewModel)
        }
        visitSystemConnections(containingSystemViewModel, portViewModel)

        return listOf(DeletePortViewModelElementAction(geckoViewModel, portViewModel, containingSystemViewModel))
    }

    fun visit(automatonViewModel: AutomatonViewModel): List<AbstractPositionableViewModelElementAction> {
        //TODO("Not yet implemented")
        return listOf()
    }

    fun visitSystemConnections(
        systemViewModel: SystemViewModel,
        portViewModel: PortViewModel
    ): List<DeleteSystemConnectionViewModelElementAction> {
        val systemConnections =
            systemViewModel.connections.filter { it.source == portViewModel || it.destination == portViewModel }
        return systemConnections.flatMap { visit(it) }
    }

    fun visit(element: PositionableViewModelElement) = when (element) {
        is AutomatonViewModel -> visit(element)
        is PortViewModel -> visit(element)
        is SystemViewModel -> visit(element)
        is RegionViewModel -> visit(element)
        is EdgeViewModel -> visit(element)
        is SystemConnectionViewModel -> visit(element)
        is StateViewModel -> visit(element)
        else -> error("Unkown element ${element.javaClass}")
    }
}
