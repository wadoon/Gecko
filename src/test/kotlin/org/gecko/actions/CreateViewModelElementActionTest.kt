package org.gecko.actions

import javafx.geometry.Point2D
import javafx.scene.paint.Color
import org.gecko.exceptions.ModelException


import org.gecko.util.TestHelper
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.SystemViewModel
import org.gecko.viewmodel.ViewModelFactory
import org.gecko.viewmodel.Visibility
import org.junit.jupiter.api.*

internal class CreateViewModelElementActionTest {
    @Test
    @Throws(ModelException::class)
    fun createContractAction() {
        val stateViewModel = viewModelFactory!!.createStateViewModelIn(
            rootSystemViewModel!!
        )
        val createContractAction: Action = actionFactory!!.createCreateContractViewModelElementAction(stateViewModel)
        actionManager!!.run(createContractAction)
        Assertions.assertEquals(stateViewModel.contracts.size, 1)
    }

    @Test
    @Throws(ModelException::class)
    fun undoContractAction() {
        val stateViewModel = viewModelFactory!!.createStateViewModelIn(
            rootSystemViewModel!!
        )
        val createContractAction: Action = actionFactory!!.createCreateContractViewModelElementAction(stateViewModel)
        actionManager!!.run(createContractAction)
        actionManager!!.undo()
        Assertions.assertEquals(stateViewModel.contracts.size, 0)
    }

    @Test
    @Throws(ModelException::class)
    fun createRegionViewModelElementAction() {
        val stateViewModel = viewModelFactory!!.createStateViewModelIn(
            rootSystemViewModel!!
        )
        val createRegionViewModelElementAction: Action =
            actionFactory!!.createCreateRegionViewModelElementAction(
                Point2D(100.0, 100.0), Point2D(200.0, 200.0),
                Color.RED
            )
        actionManager!!.run(createRegionViewModelElementAction)
        Assertions.assertEquals(geckoViewModel!!.currentEditor.getRegionViewModels(stateViewModel).size, 1)
    }

    @Test
    @Throws(ModelException::class)
    fun undoRegionViewModelElementAction() {
        val stateViewModel = viewModelFactory!!.createStateViewModelIn(
            rootSystemViewModel!!
        )
        val createRegionViewModelElementAction: Action =
            actionFactory!!.createCreateRegionViewModelElementAction(
                Point2D(100.0, 100.0), Point2D(200.0, 200.0),
                Color.RED
            )
        actionManager!!.run(createRegionViewModelElementAction)
        actionManager!!.undo()
        Assertions.assertEquals(geckoViewModel!!.currentEditor.getRegionViewModels(stateViewModel).size, 0)
    }

    @Test
    @Throws(ModelException::class)
    fun createEdgeViewModelElementAction() {
        val stateViewModel1 = viewModelFactory!!.createStateViewModelIn(
            rootSystemViewModel!!
        )
        val stateViewModel2 = viewModelFactory!!.createStateViewModelIn(
            rootSystemViewModel!!
        )
        val createEdgeViewModelElementAction: Action =
            actionFactory!!.createCreateEdgeViewModelElementAction(stateViewModel1, stateViewModel2)
        actionManager!!.run(createEdgeViewModelElementAction)
        Assertions.assertEquals(stateViewModel1.outgoingEdges.size, 1)
        Assertions.assertEquals(stateViewModel2.incomingEdges.size, 1)
    }

    @Test
    @Throws(ModelException::class)
    fun undoEdgeViewModelElementAction() {
        val stateViewModel1 = viewModelFactory!!.createStateViewModelIn(
            rootSystemViewModel!!
        )
        val stateViewModel2 = viewModelFactory!!.createStateViewModelIn(
            rootSystemViewModel!!
        )
        val createEdgeViewModelElementAction: Action =
            actionFactory!!.createCreateEdgeViewModelElementAction(stateViewModel1, stateViewModel2)
        actionManager!!.run(createEdgeViewModelElementAction)
        actionManager!!.undo()
        Assertions.assertEquals(stateViewModel1.outgoingEdges.size, 0)
        Assertions.assertEquals(stateViewModel2.incomingEdges.size, 0)
    }

    @Test
    @Throws(ModelException::class)
    fun createSystemViewModelElementAction() {
        geckoViewModel!!.switchEditor(rootSystemViewModel!!, false)
        val createSystemViewModelElementAction: Action =
            actionFactory!!.createCreateSystemViewModelElementAction(Point2D(100.0, 100.0))
        actionManager!!.run(createSystemViewModelElementAction)
        Assertions.assertEquals(rootSystemViewModel!!.target.children.size, 5)
    }

    @Test
    @Throws(ModelException::class)
    fun undoSystemViewModelElementAction() {
        geckoViewModel!!.switchEditor(rootSystemViewModel!!, false)
        val createSystemViewModelElementAction: Action =
            actionFactory!!.createCreateSystemViewModelElementAction(Point2D(100.0, 100.0))
        actionManager!!.run(createSystemViewModelElementAction)
        actionManager!!.undo()
        Assertions.assertEquals(rootSystemViewModel!!.target.children.size, 7)
    }

    @Test
    @Throws(ModelException::class)
    fun createPortViewModelElementAction() {
        val systemViewModel = viewModelFactory!!.createSystemViewModelIn(
            rootSystemViewModel!!
        )
        val createPortViewModelElementAction: Action =
            actionFactory!!.createCreatePortViewModelElementAction(systemViewModel)
        actionManager!!.run(createPortViewModelElementAction)
        Assertions.assertEquals(systemViewModel.ports.size, 1)
    }

    @Test
    @Throws(ModelException::class)
    fun undoPortViewModelElementAction() {
        val systemViewModel = viewModelFactory!!.createSystemViewModelIn(
            rootSystemViewModel!!
        )
        val createPortViewModelElementAction: Action =
            actionFactory!!.createCreatePortViewModelElementAction(systemViewModel)
        actionManager!!.run(createPortViewModelElementAction)
        actionManager!!.undo()
        Assertions.assertEquals(systemViewModel.ports.size, 0)
    }

    @Test
    @Throws(ModelException::class)
    fun createSystemConnectionViewModelElementAction() {
        val systemViewModel1 = viewModelFactory!!.createSystemViewModelIn(
            rootSystemViewModel!!
        )
        val systemViewModel2 = viewModelFactory!!.createSystemViewModelIn(
            rootSystemViewModel!!
        )
        val portViewModel1 = viewModelFactory!!.createPortViewModelIn(systemViewModel1)
        portViewModel1.visibility = (Visibility.OUTPUT)
        portViewModel1.updateTarget()
        val portViewModel2 = viewModelFactory!!.createPortViewModelIn(systemViewModel2)
        portViewModel2.visibility = (Visibility.INPUT)
        portViewModel2.updateTarget()
        val createSystemConnectionViewModelElementAction: Action =
            actionFactory!!.createCreateSystemConnectionViewModelElementAction(portViewModel1, portViewModel2)
        actionManager!!.run(createSystemConnectionViewModelElementAction)
        Assertions.assertEquals(rootSystemViewModel!!.target.connections.size, 1)
    }

    @Test
    @Throws(ModelException::class)
    fun undoSystemConnectionViewModelElementAction() {
        val systemViewModel1 = viewModelFactory!!.createSystemViewModelIn(
            rootSystemViewModel!!
        )
        val systemViewModel2 = viewModelFactory!!.createSystemViewModelIn(
            rootSystemViewModel!!
        )
        val portViewModel1 = viewModelFactory!!.createPortViewModelIn(systemViewModel1)
        portViewModel1.visibility = (Visibility.OUTPUT)
        portViewModel1.updateTarget()
        val portViewModel2 = viewModelFactory!!.createPortViewModelIn(systemViewModel2)
        portViewModel2.visibility = (Visibility.INPUT)
        portViewModel2.updateTarget()
        val createSystemConnectionViewModelElementAction: Action =
            actionFactory!!.createCreateSystemConnectionViewModelElementAction(portViewModel1, portViewModel2)
        actionManager!!.run(createSystemConnectionViewModelElementAction)
        actionManager!!.undo()
        Assertions.assertEquals(rootSystemViewModel!!.target.connections.size, 0)
    }

    @Test
    @Throws(ModelException::class)
    fun createStateViewModel() {
        val createStateViewModelElementAction: Action =
            actionFactory!!.createCreateStateViewModelElementAction(Point2D(100.0, 100.0))
        actionManager!!.run(createStateViewModelElementAction)

        Assertions.assertEquals(rootSystemViewModel!!.target.automaton.states.size, 7)
    }

    @Test
    @Throws(ModelException::class)
    fun undoStateViewModel() {
        val createStateViewModelElementAction: Action =
            actionFactory!!.createCreateStateViewModelElementAction(Point2D(100.0, 100.0))
        actionManager!!.run(createStateViewModelElementAction)
        actionManager!!.undo()

        Assertions.assertEquals(rootSystemViewModel!!.target.automaton.states.size, 2)
    }

    companion object {
        private var actionManager: ActionManager? = null
        private var actionFactory: ActionFactory? = null
        private var geckoViewModel: GeckoViewModel? = null
        private var viewModelFactory: ViewModelFactory? = null
        private var rootSystemViewModel: SystemViewModel? = null

        @BeforeAll
        @Throws(ModelException::class)
        fun setUp() {
            geckoViewModel = TestHelper.createGeckoViewModel()
            actionManager = ActionManager(
                geckoViewModel!!
            )
            actionFactory = ActionFactory(
                geckoViewModel!!
            )
            viewModelFactory = geckoViewModel!!.viewModelFactory
            rootSystemViewModel = viewModelFactory!!.createSystemViewModelFrom(
                geckoViewModel!!.geckoModel.root
            )
            geckoViewModel!!.switchEditor(rootSystemViewModel!!, true)
        }
    }
}