package org.gecko.actions

import org.gecko.util.TestHelper
import org.gecko.viewmodel.ContractViewModel
import org.gecko.viewmodel.EdgeViewModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ChangeContractEdgeViewModelActionTest {
    val geckoViewModel = TestHelper.createGeckoViewModel()
    val actionManager: ActionManager = ActionManager(geckoViewModel)
    val actionFactory: ActionFactory = ActionFactory(geckoViewModel)
    val edge: EdgeViewModel
    val contractViewModel: ContractViewModel

    init {
        val viewModelFactory = geckoViewModel.viewModelFactory
        val rootSystemViewModel =
            geckoViewModel.root

        val stateViewModel1 = viewModelFactory.createStateViewModelIn(rootSystemViewModel)
        val stateViewModel2 = viewModelFactory.createStateViewModelIn(rootSystemViewModel)

        edge = viewModelFactory.createEdgeViewModelIn(rootSystemViewModel, stateViewModel1, stateViewModel2)
        contractViewModel = viewModelFactory.createContractViewModelIn(stateViewModel1)
    }

    @Test
    fun run() {
        val changeContractAction = actionFactory.createChangeContractEdgeViewModelAction(edge, contractViewModel)
        actionManager.run(changeContractAction)
        Assertions.assertEquals(contractViewModel, edge.contract)
        Assertions.assertEquals(contractViewModel, edge.contract)
    }

    @Test
    fun undoAction() {
        val changeContractAction = actionFactory.createChangeContractEdgeViewModelAction(edge, contractViewModel)
        actionManager.run(changeContractAction)
        actionManager.undo()
        Assertions.assertNull(edge.contract)
    }
}
