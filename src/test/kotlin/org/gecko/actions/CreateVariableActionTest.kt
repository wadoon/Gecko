package org.gecko.actions

import javafx.geometry.Point2D
import org.gecko.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CreateVariableActionTest {
    val geckoViewModel = TestHelper.createGeckoViewModel()
    val actionManager = ActionManager(geckoViewModel)
    val actionFactory = ActionFactory(geckoViewModel)
    val parent = geckoViewModel.root

    @Test
    fun run() {
        val createVariableAction: Action = actionFactory.createVariable(Point2D(0.0, 0.0))
        actionManager.run(createVariableAction)
        Assertions.assertEquals(1, parent.ports.size)
    }

    @Test
    fun undoAction() {
        Assertions.assertEquals(1, parent.ports.size)
        actionManager.undo()
        Assertions.assertEquals(0, parent.ports.size)
    }
}
