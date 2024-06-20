package org.gecko.actions

import org.gecko.exceptions.ModelException
import org.gecko.util.TestHelper
import org.gecko.viewmodel.RegionViewModel
import org.junit.jupiter.api.*

internal class ChangePreconditionViewModelElementActionTest {
    val geckoViewModel = TestHelper.createGeckoViewModel()
    private var region1: RegionViewModel
    private var actionManager: ActionManager = ActionManager(geckoViewModel)
    private var actionFactory: ActionFactory = ActionFactory(geckoViewModel)

    init {
        val viewModelFactory = geckoViewModel.viewModelFactory
        val rootSystemViewModel = geckoViewModel.root
        region1 = viewModelFactory.createRegionViewModelIn(rootSystemViewModel)
        val stateViewModel = viewModelFactory.createStateViewModelIn(rootSystemViewModel)
        viewModelFactory.createContractViewModelIn(stateViewModel)
        val preCondition = stateViewModel.contractsProperty.first().preCondition
        val postCondition = stateViewModel.contractsProperty.first().postCondition
        region1.contract.preCondition = preCondition
        region1.contract.postCondition = postCondition
        geckoViewModel.switchEditor(rootSystemViewModel, true)
    }

    @Test
    @Throws(ModelException::class)
    fun run() {
        val changePreconditionAction: Action =
            actionFactory.createChangePreconditionViewModelElementAction(region1.contract, "newPrecondition")
        actionManager.run(changePreconditionAction)
        Assertions.assertEquals("newPrecondition", region1.contract.preCondition)
        Assertions.assertEquals("newPrecondition", region1.contract.preCondition.value)
    }

    @Test
    fun undoAction() {
        val changePreconditionAction: Action =
            actionFactory.createChangePreconditionViewModelElementAction(region1.contract, "newPrecondition")
        val beforeChangePrecondition = region1.contract.preCondition
        actionManager.run(changePreconditionAction)
        actionManager.undo()
        Assertions.assertEquals(beforeChangePrecondition, region1.contract.preCondition)
        Assertions.assertEquals(beforeChangePrecondition, region1.contract.preCondition.value)
    }
}