package org.gecko.actions

import org.gecko.exceptions.ModelException


import org.gecko.util.TestHelper
import org.gecko.viewmodel.PortViewModel
import org.gecko.viewmodel.SystemViewModel
import org.gecko.viewmodel.Visibility
import org.junit.jupiter.api.*

internal class CreateSystemConnectionViewModelElementActionTest {
    @Test
    fun run() {
        val createSystemConnectionAction: Action =
            actionFactory!!.createCreateSystemConnectionViewModelElementAction(
                port1!!, port2!!
            )
        actionManager!!.run(createSystemConnectionAction)
        Assertions.assertEquals(0, parent!!.target.connections.size)

        port1!!.visibility = (Visibility.OUTPUT)
        Assertions.assertDoesNotThrow { port1!!.updateTarget() }
        port2!!.visibility = (Visibility.INPUT)
        Assertions.assertDoesNotThrow { port2!!.updateTarget() }

        actionManager!!.run(createSystemConnectionAction)
        Assertions.assertEquals(1, parent!!.target.connections.size)
    }

    @get:Test
    val undoAction: Unit
        get() {
            Assertions.assertEquals(1, parent!!.target.connections.size)
            actionManager!!.undo()
            Assertions.assertEquals(0, parent!!.target.connections.size)
        }

    companion object {
        private var actionManager: ActionManager? = null
        private var actionFactory: ActionFactory? = null
        private var parent: SystemViewModel? = null
        private var port1: PortViewModel? = null
        private var port2: PortViewModel? = null

        @BeforeAll
        @Throws(ModelException::class)
        fun setUp() {
            val geckoViewModel = TestHelper.createGeckoViewModel()
            actionManager = ActionManager(
                geckoViewModel!!
            )
            actionFactory = ActionFactory(
                geckoViewModel
            )
            val viewModelFactory = geckoViewModel.viewModelFactory
            parent = viewModelFactory.createSystemViewModelFrom(
                geckoViewModel.geckoModel.root
            )
            val system1 = viewModelFactory.createSystemViewModelIn(
                parent!!
            )
            val system2 = viewModelFactory.createSystemViewModelIn(
                parent!!
            )
            port1 = viewModelFactory.createPortViewModelIn(system1)
            port2 = viewModelFactory.createPortViewModelIn(system2)
        }
    }
}