package org.gecko.actions

import org.gecko.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ChangePostconditionViewModelElementActionTest {
    val geckoViewModel = TestHelper.createGeckoViewModel()
    val viewModelFactory = geckoViewModel.viewModelFactory
    val rootSystemViewModel = geckoViewModel.root
    private var actionManager = ActionManager(geckoViewModel)
    private var actionFactory = ActionFactory(geckoViewModel)
    private var region1 = viewModelFactory.createRegion(rootSystemViewModel)

    init {
        val stateViewModel = viewModelFactory.createState(rootSystemViewModel)
        viewModelFactory.createContractViewModelIn(stateViewModel)
        val preCondition = stateViewModel.contractsProperty.first().preCondition
        val postCondition = stateViewModel.contractsProperty.first().postCondition
        region1.contract.preCondition = preCondition
        region1.contract.postCondition = postCondition
        geckoViewModel.switchEditor(rootSystemViewModel, true)
    }

    @Test
    fun run() {
        val changePostconditionAction =
            actionFactory.createChangePostconditionViewModelElementAction(region1.contract, "newPostcondition")
        actionManager.run(changePostconditionAction)
        Assertions.assertEquals("newPostcondition", region1.contract.postCondition.value)
    }

    @Test
    fun undoAction() {
        val changePostconditionAction = actionFactory.createChangePostconditionViewModelElementAction(
            region1.contract, "newPostcondition"
        )

        val beforeChangePostcondition = region1.contract.postCondition.value
        actionManager.run(changePostconditionAction)
        actionManager.undo()
        Assertions.assertEquals(beforeChangePostcondition, region1.contract.postCondition.value)
    }
}