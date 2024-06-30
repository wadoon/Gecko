package org.gecko.actions

import org.gecko.util.TestHelper
import org.gecko.viewmodel.Edge
import org.gecko.viewmodel.State
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ContractTest {
    private var state: State? = null
    private var edge: Edge? = null
    private var actionManager: ActionManager? = null
    private var actionFactory: ActionFactory? = null

    init {
        val geckoViewModel = TestHelper.createGeckoViewModel()
        actionManager = ActionManager(geckoViewModel)
        actionFactory = ActionFactory(geckoViewModel)
        val viewModelFactory = geckoViewModel.viewModelFactory
        val rootSystemViewModel =
            geckoViewModel.root

        state = viewModelFactory.createState(rootSystemViewModel)
        val stateViewModel2 = viewModelFactory.createState(rootSystemViewModel)

        edge = viewModelFactory.createEdgeViewModelIn(rootSystemViewModel, state!!, stateViewModel2)
    }

    @Test
    fun createNewContract() {
        val createContractAction: Action = actionFactory!!.createCreateContractViewModelElementAction(
            state!!
        )
        actionManager!!.run(createContractAction)
        Assertions.assertEquals(1, state!!.contracts.size)
        actionManager!!.undo()
        Assertions.assertEquals(0, state!!.contracts.size)
        actionManager!!.redo()
        Assertions.assertEquals(1, state!!.contracts.size)
    }

    @Test
    fun assignContractToEdge() {
        val createContractAction: Action = actionFactory!!.createCreateContractViewModelElementAction(
            state!!
        )
        actionManager!!.run(createContractAction)
        Assertions.assertEquals(1, state!!.contracts.size)
        val contractViewModel = state!!.contracts.first()
        val assignContractAction = actionFactory!!.createChangeContractEdge(edge!!, contractViewModel)
        actionManager!!.run(assignContractAction)
        Assertions.assertEquals(contractViewModel, edge!!.contract)
        Assertions.assertEquals(contractViewModel, edge!!.contract)
    }

    @Test
    fun deleteContract() {
        val createContractAction: Action = actionFactory!!.createCreateContractViewModelElementAction(
            state!!
        )
        actionManager!!.run(createContractAction)
        Assertions.assertEquals(1, state!!.contracts.size)
        val contractViewModel = state!!.contracts.first()

        val assignContractAction = actionFactory!!.createChangeContractEdge(edge!!, contractViewModel)
        actionManager!!.run(assignContractAction)
        Assertions.assertEquals(contractViewModel, edge!!.contract)

        val deleteContractAction: Action =
            actionFactory!!.createDeleteContractViewModelAction(state!!, contractViewModel)
        actionManager!!.run(deleteContractAction)
        Assertions.assertEquals(0, state!!.contracts.size)

        actionManager!!.run(deleteContractAction.getUndoAction(actionFactory!!)!!)
        Assertions.assertEquals(1, state!!.contracts.size)
        Assertions.assertEquals(edge!!.contract, contractViewModel)
    }
}
