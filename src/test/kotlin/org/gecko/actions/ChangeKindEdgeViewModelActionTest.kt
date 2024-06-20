package org.gecko.actions

import org.gecko.exceptions.ModelException


import org.gecko.util.TestHelper
import org.gecko.viewmodel.EdgeViewModel
import org.gecko.viewmodel.Kind
import org.junit.jupiter.api.*

class ChangeKindEdgeViewModelActionTest {
    private var edge: EdgeViewModel? = null
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
            geckoViewModel.root

        val stateViewModel1 = viewModelFactory.createStateViewModelIn(rootSystemViewModel)
        val stateViewModel2 = viewModelFactory.createStateViewModelIn(rootSystemViewModel)

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
