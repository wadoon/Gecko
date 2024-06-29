package org.gecko.actions

import org.gecko.util.TestHelper
import org.gecko.viewmodel.Contract
import org.gecko.viewmodel.Edge
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ChangeContractEdgeActionTest {
    val geckoViewModel = TestHelper.createGeckoViewModel()
    val actionManager: ActionManager = ActionManager(geckoViewModel)
    val actionFactory: ActionFactory = ActionFactory(geckoViewModel)
    val edge: Edge
    val Contract: Contract

    init {
        val viewModelFactory = geckoViewModel.viewModelFactory
        val rootSystemViewModel =
            geckoViewModel.root

        val stateViewModel1 = viewModelFactory.createState(rootSystemViewModel)
        val stateViewModel2 = viewModelFactory.createState(rootSystemViewModel)

        edge = viewModelFactory.createEdgeViewModelIn(rootSystemViewModel, stateViewModel1, stateViewModel2)
        Contract = viewModelFactory.createContractViewModelIn(stateViewModel1)
    }

    @Test
    fun run() {
        val changeContractAction = actionFactory.createChangeContractEdge(edge, Contract)
        actionManager.run(changeContractAction)
        Assertions.assertEquals(Contract, edge.contract)
        Assertions.assertEquals(Contract, edge.contract)
    }

    @Test
    fun undoAction() {
        val changeContractAction = actionFactory.createChangeContractEdge(edge, Contract)
        actionManager.run(changeContractAction)
        actionManager.undo()
        Assertions.assertNull(edge.contract)
    }
}
