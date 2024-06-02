package org.gecko.actions

import org.gecko.exceptions.ModelException

import org.gecko.util.TestHelper
import org.gecko.viewmodel.PortViewModel
import org.junit.jupiter.api.*

internal class ChangeTypePortViewModelElementActionTest {
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
        port = viewModelFactory.createPortViewModelIn(rootSystemViewModel)
    }

    @Test
    fun run() {
        val changeTypeAction: Action = actionFactory!!.createChangeTypePortViewModelElementAction(
            port!!, "newType"
        )
        actionManager!!.run(changeTypeAction)
        Assertions.assertEquals("newType", port!!.type)
        Assertions.assertEquals("newType", port!!.target.type)
    }

    @get:Test
    val undoAction: Unit
        get() {
            val changeTypeAction: Action = actionFactory!!.createChangeTypePortViewModelElementAction(
                port!!, "newType"
            )
            val beforeChangeType = port!!.type
            actionManager!!.run(changeTypeAction)
            actionManager!!.undo()
            Assertions.assertEquals(beforeChangeType, port!!.type)
            Assertions.assertEquals(beforeChangeType, port!!.target.type)
        }
}