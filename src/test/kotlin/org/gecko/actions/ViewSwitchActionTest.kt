package org.gecko.actions

import org.gecko.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ViewSwitchActionTest {
    val geckoViewModel = TestHelper.createGeckoViewModel()
    val actionManager = ActionManager(geckoViewModel)
    val actionFactory = ActionFactory(geckoViewModel)
    val viewModelFactory = geckoViewModel.viewModelFactory
    val rootSystemViewModel = geckoViewModel.root
    val systemViewModel = viewModelFactory.createSystem(rootSystemViewModel)

    @Test
    fun switchAutomatonView() {
        val switchViewAction: Action = actionFactory.createViewSwitchAction(systemViewModel, true)
        actionManager.run(switchViewAction)
        Assertions.assertEquals(geckoViewModel.currentEditor!!.currentSystem, systemViewModel)
        Assertions.assertTrue(geckoViewModel.currentEditor!!.isAutomatonEditor)
    }

    @Test
    fun switchSystemView() {
        val switchViewAction: Action = actionFactory.createViewSwitchAction(systemViewModel, false)
        actionManager.run(switchViewAction)
        Assertions.assertEquals(geckoViewModel.currentEditor!!.currentSystem, systemViewModel)
        Assertions.assertFalse(geckoViewModel.currentEditor!!.isAutomatonEditor)
    }

    @Test
    fun switchToInvalidView() {
        val switchViewAction: Action = actionFactory.createViewSwitchAction(systemViewModel, true)
        actionManager.run(switchViewAction)
        val switchToInvalidViewAction: Action = actionFactory.createViewSwitchAction(null, false)
        actionManager.run(switchToInvalidViewAction)
        Assertions.assertEquals(geckoViewModel.currentEditor!!.currentSystem, systemViewModel)
        Assertions.assertTrue(geckoViewModel.currentEditor!!.isAutomatonEditor)
    }

    @Test
    fun undoAction() {
        actionManager.run(actionFactory.createViewSwitchAction(rootSystemViewModel, true))
        val changeViewSwitchAction: Action = actionFactory.createViewSwitchAction(systemViewModel, true)
        actionManager.run(changeViewSwitchAction)
        actionManager.undo()
        Assertions.assertEquals(geckoViewModel.currentEditor!!.currentSystem, rootSystemViewModel)
    }
}
