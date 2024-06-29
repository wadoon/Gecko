package org.gecko.actions


import org.gecko.util.TestHelper
import org.gecko.viewmodel.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DeletePositionableViewModelElementActionTest {
    private val elements: Set<PositionableViewModelElement>
    private val stateViewModel1: StateViewModel
    private val actionManager: ActionManager
    private val actionFactory: ActionFactory
    private val geckoViewModel: GeckoViewModel
    private val rootSystemViewModel: SystemViewModel

    init {
        geckoViewModel = TestHelper.createGeckoViewModel()
        actionManager = ActionManager(geckoViewModel)
        actionFactory = ActionFactory(geckoViewModel)
        val viewModelFactory = geckoViewModel.viewModelFactory
        rootSystemViewModel = geckoViewModel.root

        stateViewModel1 = viewModelFactory.createState(rootSystemViewModel)
        val stateViewModel2 = viewModelFactory.createState(rootSystemViewModel)
        val edge =
            viewModelFactory.createEdgeViewModelIn(rootSystemViewModel, stateViewModel1, stateViewModel2)
        val contractViewModel = viewModelFactory.createContractViewModelIn(stateViewModel1)
        edge.contract = contractViewModel
        val regionViewModel = viewModelFactory.createRegion(rootSystemViewModel)
        val systemViewModel1 = viewModelFactory.createSystem(rootSystemViewModel)
        val systemViewModel2 = viewModelFactory.createSystem(rootSystemViewModel)
        val portViewModel1 = viewModelFactory.createPort(systemViewModel1)
        portViewModel1.visibility = (Visibility.OUTPUT)
        val portViewModel2 = viewModelFactory.createPort(systemViewModel2)
        portViewModel2.visibility = (Visibility.INPUT)
        val systemConnectionViewModel =
            viewModelFactory.createSystemConnectionViewModelIn(rootSystemViewModel, portViewModel1, portViewModel2)

        elements = java.util.Set.of(
            stateViewModel1, stateViewModel2, edge, regionViewModel, systemViewModel1, systemViewModel2,
            systemConnectionViewModel
        )

        geckoViewModel.switchEditor(rootSystemViewModel, true)
    }

    @Test
    fun run() {
        val deleteAction: Action = actionFactory.createDeleteAction(elements)
        actionManager.run(deleteAction)
        Assertions.assertEquals(0, geckoViewModel.currentEditor!!.positionableViewModelElements.size)
        geckoViewModel.switchEditor(rootSystemViewModel, false)
        Assertions.assertEquals(0, geckoViewModel.currentEditor!!.positionableViewModelElements.size)
    }

    @Test
    fun undoAction() {
        val deleteAction: Action = actionFactory.createDeleteAction(elements)
        actionManager.run(deleteAction)

        Assertions.assertEquals(geckoViewModel.currentEditor!!.positionableViewModelElements.size, 0)
        geckoViewModel.switchEditor(rootSystemViewModel, false)
        Assertions.assertEquals(geckoViewModel.currentEditor!!.positionableViewModelElements.size, 0)

        actionManager.undo()

        Assertions.assertEquals(geckoViewModel.currentEditor!!.positionableViewModelElements.size, 3)
        geckoViewModel.switchEditor(rootSystemViewModel, false)
        Assertions.assertEquals(geckoViewModel.currentEditor!!.positionableViewModelElements.size, 3)

        actionManager.redo()
        Assertions.assertEquals(geckoViewModel.currentEditor!!.positionableViewModelElements.size, 0)
        geckoViewModel.switchEditor(rootSystemViewModel, false)
        Assertions.assertEquals(geckoViewModel.currentEditor!!.positionableViewModelElements.size, 0)
    }
}
