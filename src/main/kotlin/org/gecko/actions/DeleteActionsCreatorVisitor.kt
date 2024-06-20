package org.gecko.actions


import org.gecko.viewmodel.*
import java.util.function.Consumer

/**
 * Follows the visitor pattern, implementing the [PositionableViewModelElementVisitor] interface. Determines all
 * necessary delete-[Action]s for deleting all "lower level"-dependencies of a given parent-@link
 * SystemViewModel}.
 */
class DeleteActionsCreatorVisitor
    (val geckoViewModel: GeckoViewModel, val parentSystemViewModel: SystemViewModel) :
    PositionableViewModelElementVisitor<Set<AbstractPositionableViewModelElementAction>> {
    val deleteActions: MutableSet<AbstractPositionableViewModelElementAction> = HashSet()

    override fun visit(systemViewModel: SystemViewModel): Set<AbstractPositionableViewModelElementAction> {
        val deleteActionsCreatorVisitor =
            DeleteActionsCreatorVisitor(geckoViewModel, systemViewModel)
        for (childSystem in systemViewModel.subSystems) {
            val childSystemViewModel = childSystem as SystemViewModel
            deleteActions.addAll(
                childSystemViewModel.accept(
                    deleteActionsCreatorVisitor
                )
            )
        }

        systemViewModel.ports.forEach(Consumer { portViewModel: PortViewModel ->
            deleteActions.addAll(
                portViewModel.accept(
                    this
                )
            )
        })

        val system = systemViewModel
        system.connections.forEach { it.accept(deleteActionsCreatorVisitor) }
        system.automaton.allElements.forEach { element -> element.accept(this) }

        deleteActions.add(
            DeleteSystemViewModelElementAction(geckoViewModel, systemViewModel, parentSystemViewModel)
        )

        return deleteActions
    }

    override fun visit(regionViewModel: RegionViewModel): Set<AbstractPositionableViewModelElementAction> {
        deleteActions.add(
            DeleteRegionViewModelElementAction(
                geckoViewModel, regionViewModel,
                parentSystemViewModel.automaton
            )
        )

        return deleteActions
    }

    override fun visit(
        systemConnectionViewModel: SystemConnectionViewModel
    ): Set<AbstractPositionableViewModelElementAction> {
        deleteActions.add(
            DeleteSystemConnectionViewModelElementAction(
                geckoViewModel, systemConnectionViewModel,
                parentSystemViewModel
            )
        )

        return deleteActions
    }

    override fun visit(edgeViewModel: EdgeViewModel): Set<AbstractPositionableViewModelElementAction> {
        deleteActions.add(
            DeleteEdgeViewModelElementAction(
                geckoViewModel, edgeViewModel,
                parentSystemViewModel.automaton
            )
        )

        return deleteActions
    }

    override fun visit(stateViewModel: StateViewModel): Set<AbstractPositionableViewModelElementAction> {
        val edges = parentSystemViewModel.automaton.edges
            .filter { edge -> edge.source == stateViewModel || (edge.destination == stateViewModel) }
            .toSet()
        edges.forEach { it.accept(this) }
        deleteActions.add(DeleteStateViewModelElementAction(geckoViewModel, stateViewModel, parentSystemViewModel))

        return deleteActions
    }

    // parentSystem has to be the system that contains the port
    override fun visit(portViewModel: PortViewModel): Set<AbstractPositionableViewModelElementAction> {
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
        deleteActions.add(
            DeletePortViewModelElementAction(geckoViewModel, portViewModel, containingSystemViewModel)
        )

        return deleteActions
    }

    override fun visit(automatonViewModel: AutomatonViewModel): Set<AbstractPositionableViewModelElementAction> {
        TODO("Not yet implemented")
    }

    fun visitSystemConnections(systemViewModel: SystemViewModel, portViewModel: PortViewModel) {
        val systemConnections = systemViewModel
            .connections
            .filter { systemConnection -> systemConnection.source == portViewModel || systemConnection.destination == portViewModel }
            .toSet()
        systemConnections.forEach {
            val deleteActionsCreatorVisitor = DeleteActionsCreatorVisitor(geckoViewModel, systemViewModel)
            deleteActions.addAll(
                it.accept(deleteActionsCreatorVisitor)
            )
        }
    }
}
