package org.gecko.actions

import org.gecko.exceptions.ModelException

import org.gecko.util.TestHelper
import org.gecko.viewmodel.RegionViewModel
import org.junit.jupiter.api.*

internal class ChangeInvariantViewModelElementActionTest {
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
        geckoViewModel.switchEditor(rootSystemViewModel, true)
    }

    @Test
    fun run() {
        val changeInvariantAction: Action =
            actionFactory!!.createChangeInvariantViewModelElementAction(region1!!, "newInvariant")
        actionManager!!.run(changeInvariantAction)
        Assertions.assertEquals("newInvariant", region1!!.invariant)
        Assertions.assertEquals("newInvariant", region1!!.target.invariant.condition)
    }

    @get:Test
    val undoAction: Unit
        get() {
            val changeInvariantAction: Action =
                actionFactory!!.createChangeInvariantViewModelElementAction(region1!!, "newInvariant")
            val beforeChangeInvariant = region1!!.invariant
            actionManager!!.run(changeInvariantAction)
            actionManager!!.undo()
            Assertions.assertEquals(beforeChangeInvariant, region1!!.invariant)
            Assertions.assertEquals(beforeChangeInvariant, region1!!.target.invariant.condition)
        }
}