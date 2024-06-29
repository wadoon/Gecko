package org.gecko.actions


import javafx.geometry.Point2D
import javafx.scene.paint.Color
import org.gecko.exceptions.ModelException
import org.gecko.util.TestHelper
import org.gecko.viewmodel.Visibility
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

internal class CreateViewModelElementActionTest {
    private val gmodel = TestHelper.createGeckoViewModel()
    private val actionManager = ActionManager(gmodel)
    private val actionFactory = ActionFactory(gmodel)
    private val factory = gmodel.viewModelFactory
    private val rootSystem = gmodel.root

    init {
        gmodel.switchEditor(rootSystem, true)
    }

    @Test
    @Throws(ModelException::class)
    fun createContractAction() {
        val stateViewModel = factory.createState(rootSystem)
        val createContractAction: Action = actionFactory.createCreateContractViewModelElementAction(stateViewModel)
        actionManager.run(createContractAction)
        assertEquals(stateViewModel.contracts.size, 1)
    }

    @Test
    @Throws(ModelException::class)
    fun undoContractAction() {
        val stateViewModel = factory.createState(rootSystem)
        val createContractAction: Action = actionFactory.createCreateContractViewModelElementAction(stateViewModel)
        actionManager.run(createContractAction)
        actionManager.undo()
        assertEquals(stateViewModel.contracts.size, 0)
    }

    @Test
    fun createRegion() {
        val state = factory.createState(rootSystem)
        val act = actionFactory.createRegion(Point2D(100.0, 100.0), Point2D(200.0, 200.0), Color.RED)
        state.position = Point2D(150.0, 150.0)
        state.size = Point2D(50.0, 50.0)

        val currentEditor = gmodel.currentEditor!!

        assertEquals(0, currentEditor.getRegions(state).size)

        actionManager.run(act)
        assertEquals(1, currentEditor.getRegions(state).size)

        actionManager.undo()
        assertEquals(0, currentEditor.getRegions(state).size)
    }

    @Test
    fun createEdge() {
        val state1 = factory.createState(rootSystem)
        val state2 = factory.createState(rootSystem)
        val act = actionFactory.createCreateEdgeViewModelElementAction(state1, state2)

        assertEquals(state1.outgoingEdges.size, 0)
        assertEquals(state2.incomingEdges.size, 0)

        actionManager.run(act)
        assertEquals(state1.outgoingEdges.size, 1)
        assertEquals(state2.incomingEdges.size, 1)

        actionManager.undo()
        assertEquals(state1.outgoingEdges.size, 0)
        assertEquals(state2.incomingEdges.size, 0)
    }

    @Test
    fun createSystem() {
        gmodel.switchEditor(rootSystem, false)
        val act = actionFactory.createSystem(Point2D(100.0, 100.0))
        assertEquals(0, rootSystem.subSystems.size)

        actionManager.run(act)
        assertEquals(1, rootSystem.subSystems.size)

        actionManager.undo()
        assertEquals(0, rootSystem.subSystems.size)
    }

    @Test
    fun createPortViewModelElementAction() {
        val systemViewModel = factory.createSystem(rootSystem)
        val act = actionFactory.createCreatePortViewModelElementAction(systemViewModel)
        assertEquals(systemViewModel.ports.size, 0)

        actionManager.run(act)
        assertEquals(systemViewModel.ports.size, 1)

        actionManager.undo()
        assertEquals(systemViewModel.ports.size, 0)
    }

    @Test
    fun createSystemConnection() {
        val systemViewModel1 = factory.createSystem(rootSystem)
        val systemViewModel2 = factory.createSystem(rootSystem)
        val portViewModel1 = factory.createPort(systemViewModel1)
        portViewModel1.visibility = Visibility.OUTPUT
        val portViewModel2 = factory.createPort(systemViewModel2)
        portViewModel2.visibility = Visibility.INPUT
        val createSystemConnectionViewModelElementAction: Action =
            actionFactory.createCreateSystemConnection(portViewModel1, portViewModel2)

        assertEquals(rootSystem.connections.size, 0)

        actionManager.run(createSystemConnectionViewModelElementAction)
        assertEquals(rootSystem.connections.size, 1)

        actionManager.undo()
        assertEquals(rootSystem.connections.size, 0)
    }

    @Test
    fun createState() {
        val act = actionFactory.createState(Point2D(100.0, 100.0))
        assertEquals(0, rootSystem.automaton.states.size)

        actionManager.run(act)
        assertEquals(1, rootSystem.automaton.states.size)

        actionManager.undo()
        assertEquals(0, rootSystem.automaton.states.size)
    }
}