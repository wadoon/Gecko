package org.gecko.actions


import org.gecko.util.TestHelper
import org.gecko.viewmodel.Edge
import org.gecko.viewmodel.Kind
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ChangeKindEdgeActionTest {
    private var edge: Edge? = null
    private var actionManager: ActionManager? = null
    private var actionFactory: ActionFactory? = null

    init {
        val geckoViewModel = TestHelper.createGeckoViewModel()
        actionManager = ActionManager(geckoViewModel)
        actionFactory = ActionFactory(geckoViewModel)
        val viewModelFactory = geckoViewModel.viewModelFactory
        val rootSystemViewModel =
            geckoViewModel.root

        val stateViewModel1 = viewModelFactory.createState(rootSystemViewModel)
        val stateViewModel2 = viewModelFactory.createState(rootSystemViewModel)

        edge = viewModelFactory.createEdgeViewModelIn(rootSystemViewModel, stateViewModel1, stateViewModel2)
    }

    @Test
    fun run() {
        val changeKindAction: Action = actionFactory!!.createChangeKindAction(edge!!, Kind.FAIL)
        actionManager!!.run(changeKindAction)
        Assertions.assertEquals(Kind.FAIL, edge!!.kind)
        Assertions.assertEquals(Kind.FAIL, edge!!.kind)
    }

    @Test
    fun undoAction() {
        val changeKindAction: Action = actionFactory!!.createChangeKindAction(edge!!, Kind.FAIL)
        val beforeChangeKind = edge!!.kind
        actionManager!!.run(changeKindAction)
        actionManager!!.undo()
        Assertions.assertEquals(beforeChangeKind, edge!!.kind)
        Assertions.assertEquals(beforeChangeKind, edge!!.kind)
    }
}
