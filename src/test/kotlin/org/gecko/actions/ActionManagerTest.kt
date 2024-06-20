package org.gecko.actions

import javafx.geometry.Point2D
import org.gecko.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ActionManagerTest {
    val geckoViewModel = TestHelper.createGeckoViewModel()
    private var actionManager = ActionManager(geckoViewModel)
    private var actionFactory = ActionFactory(geckoViewModel)
    private var systemViewModel = geckoViewModel.root

    init {
        geckoViewModel.switchEditor(systemViewModel, true)
    }

    @Test
    fun testRedo() {
        val createStateAction = actionFactory.createCreateStateViewModelElementAction(Point2D(100.0, 100.0))
        actionManager.run(createStateAction)

        Assertions.assertEquals(1, systemViewModel.automaton.states.size)
        actionManager.undo()
        Assertions.assertEquals(0, systemViewModel.automaton.states.size)
        actionManager.undo()
        actionManager.redo()
        Assertions.assertEquals(1, systemViewModel.automaton.states.size)
        actionManager.redo()
    }
}
