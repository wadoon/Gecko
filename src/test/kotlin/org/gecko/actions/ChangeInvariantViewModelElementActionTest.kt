package org.gecko.actions

import org.gecko.util.TestHelper
import org.gecko.viewmodel.RegionViewModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ChangeInvariantViewModelElementActionTest {
    val geckoViewModel = TestHelper.createGeckoViewModel()
    val actionManager: ActionManager = ActionManager(geckoViewModel)
    val actionFactory: ActionFactory = ActionFactory(geckoViewModel)
    val region1: RegionViewModel

    init {
        val viewModelFactory = geckoViewModel.viewModelFactory
        val rootSystemViewModel = geckoViewModel.root
        region1 = viewModelFactory.createRegion(rootSystemViewModel)
        geckoViewModel.switchEditor(rootSystemViewModel, true)
    }

    @Test
    fun run() {
        val changeInvariantAction: Action =
            actionFactory.createChangeInvariantViewModelElementAction(region1, "newInvariant")
        actionManager.run(changeInvariantAction)
        Assertions.assertEquals("newInvariant", region1.invariant.value)
    }

    @Test
    fun undoAction() {
        val changeInvariantAction =
            actionFactory.createChangeInvariantViewModelElementAction(region1, "newInvariant")
        val beforeChangeInvariant = region1.invariant.value
        actionManager.run(changeInvariantAction)
        actionManager.undo()
        Assertions.assertEquals(beforeChangeInvariant, region1.invariant.value)
    }
}