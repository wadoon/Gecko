package org.gecko.actions

import org.gecko.exceptions.ModelException

import org.gecko.util.TestHelper
import org.gecko.viewmodel.ContractViewModel
import org.gecko.viewmodel.EdgeViewModel
import org.junit.jupiter.api.*

class ChangeContractEdgeViewModelActionTest {
    private var edge: EdgeViewModel? = null
    private var actionManager: ActionManager? = null
    private var actionFactory: ActionFactory? = null
    private var contractViewModel: ContractViewModel? = null

    @BeforeEach
    @Throws(ModelException::class)
    fun setUp() {
        val geckoViewModel = TestHelper.createGeckoViewModel()
        actionManager = ActionManager(geckoViewModel)
        actionFactory = ActionFactory(geckoViewModel)
        val viewModelFactory = geckoViewModel.viewModelFactory
        val rootSystemViewModel =
            viewModelFactory.createSystemViewModelFrom(geckoViewModel.geckoModel.root)

        val stateViewModel1 = viewModelFactory.createStateViewModelIn(rootSystemViewModel)
        val stateViewModel2 = viewModelFactory.createStateViewModelIn(rootSystemViewModel)

        edge = viewModelFactory.createEdgeViewModelIn(rootSystemViewModel, stateViewModel1, stateViewModel2)
        contractViewModel = viewModelFactory.createContractViewModelIn(stateViewModel1)
    }

    @Test
    fun run() {
        val changeContractAction = actionFactory!!.createChangeContractEdgeViewModelAction(edge!!, contractViewModel)
        actionManager!!.run(changeContractAction)
        Assertions.assertEquals(contractViewModel, edge!!.contract)
        Assertions.assertEquals(contractViewModel!!.target, edge!!.target.contract)
    }

    @Test
    fun undoAction() {
        val changeContractAction =
            actionFactory!!.createChangeContractEdgeViewModelAction(edge!!, contractViewModel)
        actionManager!!.run(changeContractAction)
        actionManager!!.undo()
        Assertions.assertNull(edge!!.contract)
    }
}
