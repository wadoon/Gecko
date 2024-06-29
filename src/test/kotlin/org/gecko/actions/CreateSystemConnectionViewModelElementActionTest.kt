package org.gecko.actions


import org.gecko.util.TestHelper
import org.gecko.viewmodel.Port
import org.gecko.viewmodel.System
import org.gecko.viewmodel.Visibility
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class CreateSystemConnectionViewModelElementActionTest {
    private val geckoViewModel = TestHelper.createGeckoViewModel()
    private var actionManager: ActionManager = ActionManager(geckoViewModel)
    private var actionFactory: ActionFactory = ActionFactory(geckoViewModel)
    private var parent: System = geckoViewModel.root
    private val viewModelFactory = geckoViewModel.viewModelFactory
    private val system1 = viewModelFactory.createSystem(parent)
    private val system2 = viewModelFactory.createSystem(parent)
    private var port1: Port = viewModelFactory.createPort(system1)
    private var port2: Port = viewModelFactory.createPort(system2)

    @Test
    fun run() {
        val createSystemConnectionAction: Action =
            actionFactory.createCreateSystemConnection(
                port1, port2
            )
        actionManager.run(createSystemConnectionAction)
        Assertions.assertEquals(0, parent.connections.size)

        port1.visibility = (Visibility.OUTPUT)
        //Assertions.assertDoesNotThrow { port1!!.updateTarget() }
        port2.visibility = (Visibility.INPUT)
        //Assertions.assertDoesNotThrow { port2!!.updateTarget() }

        actionManager.run(createSystemConnectionAction)
        Assertions.assertEquals(1, parent.connections.size)

        actionManager.undo()
        Assertions.assertEquals(0, parent.connections.size)
    }

}
