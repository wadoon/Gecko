package org.gecko.actions

import org.gecko.model.Edge
import org.gecko.model.System
import org.gecko.model.SystemConnection
import org.gecko.viewmodel.*
import java.util.function.Consumer
import java.util.stream.Collectors

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
        for (childSystem in systemViewModel.target!!.children) {
            val childSystemViewModel = geckoViewModel.getViewModelElement(childSystem) as SystemViewModel
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

        val system = systemViewModel.target
        geckoViewModel.getViewModelElements(system.connections)
            .forEach(Consumer { systemConnectionViewModel: PositionableViewModelElement<*> ->
                systemConnectionViewModel.accept(
                    deleteActionsCreatorVisitor
                )
            })
        geckoViewModel.getViewModelElements(system.automaton.allElements)
            .forEach(Consumer { element: PositionableViewModelElement<*> ->
                element.accept(
                    this
                )
            })

        deleteActions.add(
            DeleteSystemViewModelElementAction(geckoViewModel, systemViewModel, parentSystemViewModel.target)
        )

        return deleteActions
    }

    override fun visit(regionViewModel: RegionViewModel): Set<AbstractPositionableViewModelElementAction> {
        deleteActions.add(
            DeleteRegionViewModelElementAction(
                geckoViewModel, regionViewModel,
                parentSystemViewModel.target.automaton
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
                parentSystemViewModel.target
            )
        )

        return deleteActions
    }

    override fun visit(edgeViewModel: EdgeViewModel): Set<AbstractPositionableViewModelElementAction> {
        deleteActions.add(
            DeleteEdgeViewModelElementAction(
                geckoViewModel, edgeViewModel,
                parentSystemViewModel.target.automaton
            )
        )

        return deleteActions
    }

    override fun visit(stateViewModel: StateViewModel): Set<AbstractPositionableViewModelElementAction> {
        val edges = parentSystemViewModel.target
            .automaton
            .edges
            .stream()
            .filter { edge: Edge ->
                edge.source == stateViewModel.target || (edge.destination
                        == stateViewModel.target)
            }
            .collect(Collectors.toSet())
        geckoViewModel.getViewModelElements(edges).forEach(Consumer { edgeViewModel: PositionableViewModelElement<*> ->
            edgeViewModel.accept(
                this
            )
        })
        deleteActions.add(DeleteStateViewModelElementAction(geckoViewModel, stateViewModel, parentSystemViewModel))

        return deleteActions
    }

    // parentSystem has to be the system that contains the port
    override fun visit(portViewModel: PortViewModel): Set<AbstractPositionableViewModelElementAction> {
        val actualParentSystemViewModel: SystemViewModel
        val containingSystemViewModel: SystemViewModel
        if (parentSystemViewModel.ports.contains(portViewModel)) {
            actualParentSystemViewModel =
                geckoViewModel.getViewModelElement(parentSystemViewModel.target!!.parent!!) as SystemViewModel
            containingSystemViewModel = parentSystemViewModel
        } else {
            actualParentSystemViewModel = parentSystemViewModel
            containingSystemViewModel = geckoViewModel.getViewModelElement(
                parentSystemViewModel.target!!.children
                    .first { system: System -> system.variables.contains(portViewModel.target) }) as SystemViewModel
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

    fun visitSystemConnections(systemViewModel: SystemViewModel, portViewModel: PortViewModel) {
        val systemConnections = systemViewModel.target!!
            .connections
            .filter { systemConnection: SystemConnection -> systemConnection.source == portViewModel.target || systemConnection.destination == portViewModel.target }
            .toSet()
        geckoViewModel.getViewModelElements(systemConnections).forEach {
            val deleteActionsCreatorVisitor = DeleteActionsCreatorVisitor(geckoViewModel, systemViewModel)
            deleteActions.addAll(
                it.accept(deleteActionsCreatorVisitor)
            )
        }
    }
}
