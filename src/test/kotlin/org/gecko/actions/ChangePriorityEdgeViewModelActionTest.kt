package org.gecko.actions

import org.gecko.exceptions.ModelException

import org.gecko.util.TestHelper
import org.gecko.viewmodel.EdgeViewModel
import org.junit.jupiter.api.*

class ChangePriorityEdgeViewModelActionTest {
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
            viewModelFactory.createSystemViewModelFrom(geckoViewModel.geckoModel.root)

        val stateViewModel1 = viewModelFactory.createStateViewModelIn(rootSystemViewModel)
        val stateViewModel2 = viewModelFactory.createStateViewModelIn(rootSystemViewModel)

        edge = viewModelFactory.createEdgeViewModelIn(rootSystemViewModel, stateViewModel1, stateViewModel2)
    }

    @Test
    fun run() {
        val changePriorityAction: Action = actionFactory!!.createModifyEdgeViewModelPriorityAction(
            edge!!, 4
        )
        actionManager!!.run(changePriorityAction)
        Assertions.assertEquals(4, edge!!.priority)
    }

    @get:Test
    val undoAction: Unit
        get() {
            val changePriorityAction: Action = actionFactory!!.createModifyEdgeViewModelPriorityAction(
                edge!!, 4
            )
            val beforePriority = edge!!.priority
            actionManager!!.run(changePriorityAction)
            actionManager!!.undo()
            Assertions.assertEquals(beforePriority, edge!!.priority)
            Assertions.assertEquals(beforePriority, edge!!.target.priority)
        }
}
