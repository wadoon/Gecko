package org.gecko.actions


import org.gecko.util.TestHelper
import org.gecko.viewmodel.PortViewModel
import org.gecko.viewmodel.Visibility
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ChangeVisibilityPortViewModelElementActionTest {
    val geckoViewModel = TestHelper.createGeckoViewModel()
    private var port: PortViewModel
    private var actionManager = ActionManager(geckoViewModel)
    private var actionFactory = ActionFactory(geckoViewModel)

    init {
        val viewModelFactory = geckoViewModel.viewModelFactory
        val rootSystemViewModel = geckoViewModel.root
        val systemViewModel1 = viewModelFactory.createSystemViewModelIn(rootSystemViewModel)
        port = viewModelFactory.createPortViewModelIn(systemViewModel1)
    }

    @Test
    fun run() {
        val changeKindAction = actionFactory.changeVisibility(port, Visibility.OUTPUT)
        actionManager.run(changeKindAction)
        Assertions.assertEquals(Visibility.OUTPUT, port.visibility)
    }

    @Test
    fun runSameVisibility() {
        val changeKindAction = actionFactory.changeVisibility(port, port.visibility)
        actionManager.run(changeKindAction)
        Assertions.assertEquals(Visibility.STATE, port.visibility)
    }

    @Test
    fun undoAction() {
        val changeVisibilityPortViewModelAction: Action =
            actionFactory.changeVisibility(port, Visibility.OUTPUT)
        val beforeChangeVisibility = port.visibility
        actionManager.run(changeVisibilityPortViewModelAction)
        actionManager.undo()
        Assertions.assertEquals(beforeChangeVisibility, port.visibility)
        Assertions.assertEquals(beforeChangeVisibility, port.visibility)
    }
}
