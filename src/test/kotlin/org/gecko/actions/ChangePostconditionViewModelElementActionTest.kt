package org.gecko.actions

import org.gecko.exceptions.ModelException

import org.gecko.util.TestHelper
import org.gecko.viewmodel.RegionViewModel
import org.gecko.viewmodel.StateViewModel
import org.junit.jupiter.api.*

internal class ChangePostconditionViewModelElementActionTest {
    private var region1: RegionViewModel? = null
    private var actionManager: ActionManager? = null
    private var actionFactory: ActionFactory? = null

    @BeforeEach
    @Throws(ModelException::class)
    fun setUp() {
        val geckoViewModel = TestHelper.createGeckoViewModel()
        actionManager = ActionManager(geckoViewModel!!)
        actionFactory = ActionFactory(geckoViewModel)
        val viewModelFactory = geckoViewModel.viewModelFactory
        val rootSystemViewModel =
            viewModelFactory.createSystemViewModelFrom(geckoViewModel.geckoModel.root)
        region1 = viewModelFactory.createRegionViewModelIn(rootSystemViewModel)
        var stateViewModel: StateViewModel? = null
        try {
            stateViewModel = viewModelFactory.createStateViewModelIn(rootSystemViewModel)
        } catch (e: Exception) {
            Assertions.fail<Any>()
        }
        viewModelFactory.createContractViewModelIn(stateViewModel!!)
        val preCondition = stateViewModel.contractsProperty.first().precondition
        val postCondition = stateViewModel.contractsProperty.first().postcondition
        region1!!.contract.precondition = (preCondition)
        region1!!.contract.postcondition = (postCondition)
        geckoViewModel.switchEditor(rootSystemViewModel, true)
    }

    @Test
    fun run() {
        val changePostconditionAction: Action =
            actionFactory!!.createChangePostconditionViewModelElementAction(region1!!.contract, "newPostcondition")
        actionManager!!.run(changePostconditionAction)
        Assertions.assertEquals("newPostcondition", region1!!.contract.postcondition)
        Assertions.assertEquals(
            "newPostcondition",
            region1!!.target.preAndPostCondition.postCondition.condition
        )
    }

    @get:Test
    val undoAction: Unit
        get() {
            val changePostconditionAction: Action =
                actionFactory!!.createChangePostconditionViewModelElementAction(region1!!.contract, "newPostcondition")
            val beforeChangePostcondition = region1!!.contract.postcondition
            actionManager!!.run(changePostconditionAction)
            actionManager!!.undo()
            Assertions.assertEquals(beforeChangePostcondition, region1!!.contract.postcondition)
            Assertions.assertEquals(
                beforeChangePostcondition,
                region1!!.target.preAndPostCondition.postCondition.condition
            )
        }
}