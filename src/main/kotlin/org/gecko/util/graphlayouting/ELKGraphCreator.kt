package org.gecko.util.graphlayouting

import org.eclipse.elk.graph.ElkNode
import org.eclipse.elk.graph.util.ElkGraphUtil
import org.gecko.viewmodel.*

/**
 * The ELKGraphCreator is used to create ELK graphs from a view model. When creating an ELK graph, it applies the
 * current position and size of the view model elements to the ELK nodes. It also creates edges between the nodes based
 * on the connections in the view model.
 */
internal class ELKGraphCreator(val viewModel: GeckoViewModel) {
    fun createSystemElkGraph(system: SystemViewModel): ElkNode {
        val root = ElkGraphUtil.createGraph()
        val children: MutableList<BlockViewModelElement<*>> = ArrayList(getChildSystemViewModels(system))
        children.addAll(system.ports)
        for (child in children) {
            createElkNode(root, child)
        }
        for (connection in getConnectionViewModels(system)) {
            val start = findPortOrSystemNode(root, system, connection.source)
            val end = findPortOrSystemNode(root, system, connection.destination)
            ElkGraphUtil.createSimpleEdge(start, end)
        }
        return root
    }

    fun createAutomatonElkGraph(system: SystemViewModel): ElkNode {
        val root = ElkGraphUtil.createGraph()
        val children = getStates(system)
        for (child in children) {
            createElkNode(root, child)
        }
        for (edge in getAutomatonEdges(system)) {
            ElkGraphUtil.createSimpleEdge(findNode(root, edge.source), findNode(root, edge.destination))
        }
        return root
    }

    fun createElkNode(parent: ElkNode, block: BlockViewModelElement<*>) {
        val node = ElkGraphUtil.createNode(parent)
        node.x = block.position.x
        node.y = block.position.y
        node.width = block.size.x
        node.height = block.size.y
        node.identifier = block.id.toString()
    }

    fun getChildSystemViewModels(systemViewModel: SystemViewModel): List<SystemViewModel> {
        return systemViewModel.target!!.children
            .map { s -> viewModel.getViewModelElement(s) as SystemViewModel }
    }

    fun getAutomatonEdges(systemViewModel: SystemViewModel): List<EdgeViewModel> =
        systemViewModel.target!!.automaton!!.edges
            .map { viewModel.getViewModelElement(it!!) as EdgeViewModel }

    fun getConnectionViewModels(systemViewModel: SystemViewModel): List<SystemConnectionViewModel> =
        systemViewModel.target!!.connections
            .map { viewModel.getViewModelElement(it) as SystemConnectionViewModel }

    fun getStates(systemViewModel: SystemViewModel): List<StateViewModel> {
        return systemViewModel.target.automaton.states
            .map { s -> viewModel.getViewModelElement(s) as StateViewModel }
    }

    fun findNode(graph: ElkNode, element: BlockViewModelElement<*>): ElkNode {
        return graph.children
            .stream()
            .filter { e: ElkNode -> e.identifier == element.id.toString() }
            .findFirst()
            .orElse(null)
    }

    fun findPortOrSystemNode(graph: ElkNode, parentSystem: SystemViewModel, element: PortViewModel): ElkNode {
        if (parentSystem.ports.contains(element)) {
            return findNode(graph, element)
        } else {
            val sys = parentSystem.target.getChildSystemWithVariable(element.target)
            return findNode(graph, viewModel.getViewModelElement(sys!!) as SystemViewModel)
        }
    }
}
