package org.gecko.actions

import org.gecko.exceptions.ModelException


import org.gecko.util.TestHelper
import org.gecko.viewmodel.PortViewModel
import org.gecko.viewmodel.Visibility
import org.junit.jupiter.api.*

class ChangeVisibilityPortViewModelElementActionTest {
    private var port: PortViewModel? = null
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

        val systemViewModel1 = viewModelFactory.createSystemViewModelIn(rootSystemViewModel)
        port = viewModelFactory.createPortViewModelIn(systemViewModel1)
    }

    @Test
    fun run() {
        val changeKindAction: Action =
            actionFactory!!.createChangeVisibilityPortViewModelAction(port!!, Visibility.OUTPUT)
        actionManager!!.run(changeKindAction)
        Assertions.assertEquals(Visibility.OUTPUT, port!!.visibility)
    }

    @Test
    fun runSameVisibility() {
        val changeKindAction: Action =
            actionFactory!!.createChangeVisibilityPortViewModelAction(port!!, port!!.visibility)
        actionManager!!.run(changeKindAction)
        Assertions.assertEquals(Visibility.INPUT, port!!.visibility)
    }

    @get:Test
    val undoAction: Unit
        get() {
            val changeVisibilityPortViewModelAction: Action =
                actionFactory!!.createChangeVisibilityPortViewModelAction(port!!, Visibility.OUTPUT)
            val beforeChangeVisibility = port!!.visibility
            actionManager!!.run(changeVisibilityPortViewModelAction)
            actionManager!!.undo()
            Assertions.assertEquals(beforeChangeVisibility, port!!.visibility)
            Assertions.assertEquals(beforeChangeVisibility, port!!.target.visibility)
        }
}
