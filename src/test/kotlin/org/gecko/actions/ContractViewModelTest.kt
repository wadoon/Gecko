package org.gecko.actions

import org.gecko.exceptions.ModelException

import org.gecko.util.TestHelper
import org.gecko.viewmodel.EdgeViewModel
import org.gecko.viewmodel.StateViewModel
import org.junit.jupiter.api.*

class ContractViewModelTest {
    private var stateViewModel: StateViewModel? = null
    private var edge: EdgeViewModel? = null
    private var actionManager: ActionManager? = null
    private var actionFactory: ActionFactory? = null

    @BeforeEach
    @Throws(ModelException::class)
    fun setUp() {
        val geckoViewModel = TestHelper.createGeckoViewModel()
        actionManager = ActionManager(geckoViewModel)
        actionFactory = ActionFactory(geckoViewModel)
        val viewModelFactory = geckoViewModel.viewModelFactory
        val rootSystemViewModel =
            viewModelFactory.createSystemViewModelFrom(geckoViewModel.geckoModel.root)

        stateViewModel = viewModelFactory.createStateViewModelIn(rootSystemViewModel)
        val stateViewModel2 = viewModelFactory.createStateViewModelIn(rootSystemViewModel)

        edge = viewModelFactory.createEdgeViewModelIn(rootSystemViewModel, stateViewModel!!, stateViewModel2)
    }

    @Test
    fun createNewContract() {
        val createContractAction: Action = actionFactory!!.createCreateContractViewModelElementAction(
            stateViewModel!!
        )
        actionManager!!.run(createContractAction)
        Assertions.assertEquals(1, stateViewModel!!.contracts.size)
        actionManager!!.undo()
        Assertions.assertEquals(0, stateViewModel!!.contracts.size)
        actionManager!!.redo()
        Assertions.assertEquals(1, stateViewModel!!.contracts.size)
    }

    @Test
    fun assignContractToEdge() {
        val createContractAction: Action = actionFactory!!.createCreateContractViewModelElementAction(
            stateViewModel!!
        )
        actionManager!!.run(createContractAction)
        Assertions.assertEquals(1, stateViewModel!!.contracts.size)
        val contractViewModel = stateViewModel!!.contracts.first()
        val assignContractAction = actionFactory!!.createChangeContractEdgeViewModelAction(edge!!, contractViewModel)
        actionManager!!.run(assignContractAction)
        Assertions.assertEquals(contractViewModel, edge!!.contract)
        Assertions.assertEquals(contractViewModel.target, edge!!.target.contract)
    }

    @Test
    fun deleteContract() {
        val createContractAction: Action = actionFactory!!.createCreateContractViewModelElementAction(
            stateViewModel!!
        )
        actionManager!!.run(createContractAction)
        Assertions.assertEquals(1, stateViewModel!!.contracts.size)
        val contractViewModel = stateViewModel!!.contracts.first()

        val assignContractAction = actionFactory!!.createChangeContractEdgeViewModelAction(edge!!, contractViewModel)
        actionManager!!.run(assignContractAction)
        Assertions.assertEquals(contractViewModel, edge!!.contract)

        val deleteContractAction: Action =
            actionFactory!!.createDeleteContractViewModelAction(stateViewModel!!, contractViewModel)
        actionManager!!.run(deleteContractAction)
        Assertions.assertEquals(0, stateViewModel!!.contracts.size)

        actionManager!!.run(deleteContractAction.getUndoAction(actionFactory!!))
        Assertions.assertEquals(1, stateViewModel!!.contracts.size)
        Assertions.assertEquals(edge!!.contract, contractViewModel)
    }
}
