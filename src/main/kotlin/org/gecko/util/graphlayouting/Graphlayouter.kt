package org.gecko.util.graphlayouting

import javafx.geometry.Point2D
import org.eclipse.elk.alg.layered.options.LayeredOptions
import org.eclipse.elk.core.IGraphLayoutEngine
import org.eclipse.elk.core.RecursiveGraphLayoutEngine
import org.eclipse.elk.core.options.CoreOptions
import org.eclipse.elk.core.util.BasicProgressMonitor
import org.eclipse.elk.graph.ElkNode
import org.gecko.viewmodel.BlockViewModelElement
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.StateViewModel
import org.gecko.viewmodel.SystemViewModel

/**
 * The Graphlayouter is the core of the graphlayouting package. It is responsible for creating the ELK graph from the
 * view model and applying the layout to the view model. Currently, the layoutalgorithms are hardcoded to use the
 * layered algorithm for the system graph and the force algorithm for the automaton graph.
 */
class Graphlayouter(val viewModel: GeckoViewModel) {
    internal val elkGraphCreator = ELKGraphCreator(viewModel)

    fun layout() {
        val root = viewModel.root
        layout(root)
    }

    fun layout(systemViewModel: SystemViewModel) {
        val systemGraph = elkGraphCreator.createSystemElkGraph(systemViewModel)
        layoutGraph(systemGraph, LayoutAlgorithms.LAYERED)
        applySystemLayoutToViewModel(systemGraph, systemViewModel)
        val automatonGraph = elkGraphCreator.createAutomatonElkGraph(systemViewModel)
        layoutGraph(automatonGraph, LayoutAlgorithms.FORCE)
        applyAutomatonLayoutToViewModel(automatonGraph, systemViewModel)
        for (system in systemViewModel.subSystems) {
            layout(system)
        }
    }

    internal fun layoutGraph(root: ElkNode?, layoutAlgorithm: LayoutAlgorithms) {
        if (root!!.children.isEmpty()) {
            return
        }
        root.setProperty(CoreOptions.ALGORITHM, layoutAlgorithm.clazz.packageName)
        val spacing = root.children.first().width / 3
        root.setProperty(CoreOptions.SPACING_NODE_NODE, spacing)
        if (layoutAlgorithm == LayoutAlgorithms.LAYERED) {
            root.setProperty(LayeredOptions.SPACING_NODE_NODE_BETWEEN_LAYERS, spacing)
        }
        val engine: IGraphLayoutEngine = RecursiveGraphLayoutEngine()
        engine.layout(root, BasicProgressMonitor())
    }

    fun applySystemLayoutToViewModel(root: ElkNode?, viewModel: SystemViewModel) {
        val children: MutableList<BlockViewModelElement> = ArrayList(getChildSystemViewModels(viewModel))
        children.addAll(viewModel.ports)
        for (child in children) {
            applyLayoutToNode(root, child)
        }
    }

    fun applyAutomatonLayoutToViewModel(root: ElkNode?, viewModel: SystemViewModel) {
        for (child in getStates(viewModel)) {
            applyLayoutToNode(root, child)
        }
    }

    fun applyLayoutToNode(root: ElkNode?, viewModel: BlockViewModelElement) {
        val node = findNodeById(root, viewModel.hashCode())!!
        viewModel.position = Point2D(node.x, node.y)
        viewModel.size = Point2D(node.width, node.height)
    }

    fun findNodeById(root: ElkNode?, id: Int): ElkNode? =
        root!!.children.firstOrNull { it.identifier == id.toString() }

    fun getChildSystemViewModels(systemViewModel: SystemViewModel): List<SystemViewModel> =
        systemViewModel.subSystems

    fun getStates(systemViewModel: SystemViewModel): List<StateViewModel> = systemViewModel
        .automaton
        .states
}
