package org.gecko.actions

import org.gecko.exceptions.ModelException
import org.gecko.util.TestHelper
import org.gecko.viewmodel.Region
import org.junit.jupiter.api.*

internal class ChangePreconditionViewModelElementActionTest {
    val geckoViewModel = TestHelper.createGeckoViewModel()
    private var region1: Region
    private var actionManager: ActionManager = ActionManager(geckoViewModel)
    private var actionFactory: ActionFactory = ActionFactory(geckoViewModel)

    init {
        val viewModelFactory = geckoViewModel.viewModelFactory
        val rootSystemViewModel = geckoViewModel.root
        region1 = viewModelFactory.createRegion(rootSystemViewModel)
        val stateViewModel = viewModelFactory.createState(rootSystemViewModel)
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
        Assertions.assertEquals("newPrecondition", region1.contract.preCondition.value)
    }

    @Test
    fun undoAction() {
        val changePreconditionAction=
            actionFactory.createChangePreconditionViewModelElementAction(region1.contract, "newPrecondition")
        val beforeChangePrecondition = region1.contract.preCondition.value
        actionManager.run(changePreconditionAction)
        actionManager.undo()
        Assertions.assertEquals(beforeChangePrecondition, region1.contract.preCondition.value)
    }
}