package org.gecko.actions


import org.gecko.util.TestHelper
import org.gecko.viewmodel.State
import org.gecko.viewmodel.Visibility
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DeletePositionableElementActionTest {

    @Test
    fun run() {
        val gModel = TestHelper.createGeckoViewModel()
        val actionManager = ActionManager(gModel)
        val actionFactory = ActionFactory(gModel)
        val rootSystem = gModel.root
        val f = gModel.viewModelFactory

        val state1: State = f.createState(rootSystem)
        val state2 = f.createState(rootSystem)
        val edge = f.createEdgeViewModelIn(rootSystem, state1, state2)
        val contractViewModel = f.createContractViewModelIn(state1)
        edge.contract = contractViewModel
        val region = f.createRegion(rootSystem)
        val sys1 = f.createSystem(rootSystem)
        val sys2 = f.createSystem(rootSystem)
        val portViewModel1 = f.createPort(sys1)
        portViewModel1.visibility = Visibility.OUTPUT
        val portViewModel2 = f.createPort(sys2)
        portViewModel2.visibility = Visibility.INPUT
        val con = f.createSystemConnectionViewModelIn(rootSystem, portViewModel1, portViewModel2)

        val elements = setOf(state1, state2, edge, region, sys1, sys2, con)

        gModel.switchEditor(rootSystem, true)
        val vEAuto = gModel.currentEditor.viewableElements

        gModel.switchEditor(rootSystem, false)
        val vESys = gModel.currentEditor.viewableElements

        val deleteAction = actionFactory.createDeleteAction(elements)

        assertEquals(4, vEAuto.size)
        assertEquals(3, vESys.size)
        actionManager.run(deleteAction)
        assertEquals(0, vEAuto.size)
        assertEquals(0, vESys.size)

        actionManager.undo()
        assertEquals(4, vEAuto.size)
        assertEquals(3, vESys.size)

        actionManager.redo()
        assertEquals(0, vEAuto.size)
        assertEquals(0, vESys.size)
    }
}
