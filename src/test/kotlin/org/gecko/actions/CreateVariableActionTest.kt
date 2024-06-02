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
        Assertions.assertEquals(1, parent!!.target.variables.size)
    }

    @get:Test
    val undoAction: Unit
        get() {
            Assertions.assertEquals(1, parent!!.target.variables.size)
            actionManager!!.undo()
            Assertions.assertEquals(0, parent!!.target.variables.size)
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
            parent = viewModelFactory.createSystemViewModelFrom(geckoViewModel.geckoModel.root)
        }
    }
}
