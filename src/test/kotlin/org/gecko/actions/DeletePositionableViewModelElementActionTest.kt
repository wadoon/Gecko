package org.gecko.actions


import org.gecko.util.TestHelper
import org.gecko.viewmodel.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DeletePositionableViewModelElementActionTest {
    private val elements: Set<PositionableViewModelElement>
    private val stateViewModel1: StateViewModel
    private val actionManager: ActionManager
    private val actionFactory: ActionFactory
    private val gModel: GModel
    private val rootSystem: System

    init {
        gModel = TestHelper.createGeckoViewModel()
        actionManager = ActionManager(gModel)
        actionFactory = ActionFactory(gModel)
        val viewModelFactory = gModel.viewModelFactory
        rootSystem = gModel.root

        stateViewModel1 = viewModelFactory.createState(rootSystem)
        val stateViewModel2 = viewModelFactory.createState(rootSystem)
        val edge =
            viewModelFactory.createEdgeViewModelIn(rootSystem, stateViewModel1, stateViewModel2)
        val contractViewModel = viewModelFactory.createContractViewModelIn(stateViewModel1)
        edge.contract = contractViewModel
        val regionViewModel = viewModelFactory.createRegion(rootSystem)
        val systemViewModel1 = viewModelFactory.createSystem(rootSystem)
        val systemViewModel2 = viewModelFactory.createSystem(rootSystem)
        val portViewModel1 = viewModelFactory.createPort(systemViewModel1)
        portViewModel1.visibility = (Visibility.OUTPUT)
        val portViewModel2 = viewModelFactory.createPort(systemViewModel2)
        portViewModel2.visibility = (Visibility.INPUT)
        val systemConnectionViewModel =
            viewModelFactory.createSystemConnectionViewModelIn(rootSystem, portViewModel1, portViewModel2)

        elements = java.util.Set.of(
            stateViewModel1, stateViewModel2, edge, regionViewModel, systemViewModel1, systemViewModel2,
            systemConnectionViewModel
        )

        gModel.switchEditor(rootSystem, true)
    }

    @Test
    fun run() {
        val deleteAction: Action = actionFactory.createDeleteAction(elements)
        actionManager.run(deleteAction)
        assertEquals(0, gModel.currentEditor!!.viewableElements.size)
        gModel.switchEditor(rootSystem, false)
        assertEquals(0, gModel.currentEditor!!.viewableElements.size)
    }

    @Test
    fun undoAction() {
        val deleteAction: Action = actionFactory.createDeleteAction(elements)
        actionManager.run(deleteAction)

        assertEquals(0, gModel.currentEditor!!.viewableElements.size)
        gModel.switchEditor(rootSystem, false)
        assertEquals(0, gModel.currentEditor!!.viewableElements.size)

        actionManager.undo()

        assertEquals(gModel.currentEditor!!.viewableElements.size, 3)
        gModel.switchEditor(rootSystem, false)
        assertEquals(gModel.currentEditor!!.viewableElements.size, 3)

        actionManager.redo()
        assertEquals(gModel.currentEditor!!.viewableElements.size, 0)
        gModel.switchEditor(rootSystem, false)
        assertEquals(gModel.currentEditor!!.viewableElements.size, 0)
    }
}
