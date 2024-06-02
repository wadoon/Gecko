package org.gecko.actions

import javafx.geometry.Point2D
import org.gecko.exceptions.ModelException

import org.gecko.util.TestHelper
import org.gecko.viewmodel.SystemViewModel
import org.junit.jupiter.api.*

class ActionManagerTest {
    private var actionManager: ActionManager? = null
    private var actionFactory: ActionFactory? = null
    private var systemViewModel: SystemViewModel? = null

    @BeforeEach
    @Throws(ModelException::class)
    fun setUp() {
        val geckoViewModel = TestHelper.createGeckoViewModel()
        actionManager = ActionManager(geckoViewModel!!)
        actionFactory = ActionFactory(geckoViewModel)
        val viewModelFactory = geckoViewModel.viewModelFactory
        systemViewModel = viewModelFactory.createSystemViewModelFrom(geckoViewModel.geckoModel.root)
        geckoViewModel.switchEditor(systemViewModel!!, true)
    }

    @Test
    fun testRedo() {
        val createStateAction: Action = actionFactory!!.createCreateStateViewModelElementAction(Point2D(100.0, 100.0))
        actionManager!!.run(createStateAction)
        Assertions.assertEquals(1, systemViewModel!!.target.automaton.states.size)
        actionManager!!.undo()
        Assertions.assertEquals(0, systemViewModel!!.target.automaton.states.size)
        actionManager!!.undo()
        actionManager!!.redo()
        Assertions.assertEquals(1, systemViewModel!!.target.automaton.states.size)
        actionManager!!.redo()
    }
}
