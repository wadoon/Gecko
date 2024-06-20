package org.gecko.actions

import javafx.geometry.Point2D
import org.gecko.exceptions.ModelException

import org.gecko.util.TestHelper
import org.gecko.viewmodel.SystemViewModel
import org.junit.jupiter.api.*

class CreateVariableActionTest {
    @Test
    fun run() {
        val createVariableAction: Action = actionFactory!!.createCreateVariableAction(Point2D(0.0, 0.0))
        actionManager!!.run(createVariableAction)
        Assertions.assertEquals(1, parent!!.ports.size)
    }

    @Test
    fun undoAction() {
            Assertions.assertEquals(1, parent!!.ports.size)
            actionManager!!.undo()
            Assertions.assertEquals(0, parent!!.ports.size)
        }

    companion object {
        private var actionManager: ActionManager? = null
        private var actionFactory: ActionFactory? = null
        private var parent: SystemViewModel? = null

        @BeforeAll
        @Throws(ModelException::class)
        fun setUp() {
            val geckoViewModel = TestHelper.createGeckoViewModel()
            actionManager = ActionManager(geckoViewModel!!)
            actionFactory = ActionFactory(geckoViewModel)
            val viewModelFactory = geckoViewModel.viewModelFactory
            parent = geckoViewModel.root
        }
    }
}
