package org.gecko.actions

import org.gecko.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ChangeCodeSystemElementActionTest {
    val geckoViewModel = TestHelper.createGeckoViewModel()
    val actionManager = ActionManager(geckoViewModel)
    val actionFactory = ActionFactory(geckoViewModel)
    val viewModelFactory = geckoViewModel.viewModelFactory
    val systemViewModel = geckoViewModel.root

    @Test
    fun run() {
        val changeCodeAction: Action = actionFactory.createChangeCodeSystemViewModelAction(
            systemViewModel, "newCode"
        )
        actionManager.run(changeCodeAction)
        Assertions.assertEquals("newCode", systemViewModel.code)

        val changeCodeAction2: Action = actionFactory.createChangeCodeSystemViewModelAction(
            systemViewModel, ""
        )
        actionManager.run(changeCodeAction2)
        Assertions.assertEquals("", systemViewModel.code)
    }

    @Test
    fun undoAction() {
        val changeCodeAction: Action = actionFactory.createChangeCodeSystemViewModelAction(
            systemViewModel, "newCode2"
        )
        val beforeChangeCode = systemViewModel.code
        actionManager.run(changeCodeAction)
        Assertions.assertEquals("newCode2", systemViewModel.code)
        actionManager.undo()
        Assertions.assertEquals(beforeChangeCode, systemViewModel.code)
    }
}