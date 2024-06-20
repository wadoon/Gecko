package org.gecko.actions

import org.gecko.exceptions.ModelException

import org.gecko.util.TestHelper
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.PositionableViewModelElement
import org.gecko.viewmodel.SystemViewModel
import org.junit.jupiter.api.*

internal class DeleteStateViewModelElementActionTest {
    private var elements: Set<PositionableViewModelElement>? = null
    private var actionManager: ActionManager? = null
    private var actionFactory: ActionFactory? = null
    private var geckoViewModel: GeckoViewModel? = null
    private var rootSystemViewModel: SystemViewModel? = null

    @BeforeEach
    @Throws(ModelException::class)
    fun setUp() {
        geckoViewModel = TestHelper.createGeckoViewModel()
        actionManager = ActionManager(geckoViewModel!!)
        actionFactory = ActionFactory(geckoViewModel!!)
        val viewModelFactory = geckoViewModel!!.viewModelFactory
        rootSystemViewModel = geckoViewModel!!.root

        val stateViewModel1 = viewModelFactory.createStateViewModelIn(rootSystemViewModel!!)
        val stateViewModel2 = viewModelFactory.createStateViewModelIn(rootSystemViewModel!!)
        elements = setOf<PositionableViewModelElement>(stateViewModel1, stateViewModel2)
        geckoViewModel!!.switchEditor(rootSystemViewModel!!, true)
    }

    @Test
    @Throws(ModelException::class)
    fun newStateBecomesStartStateAfterAllStatesAreDeleted() {
        val deleteAction: Action = actionFactory!!.createDeletePositionableViewModelElementAction(elements)
        actionManager!!.run(deleteAction)
        Assertions.assertEquals(0, rootSystemViewModel!!.automaton.states.size)
        val newState = geckoViewModel!!.viewModelFactory.createStateViewModelIn(rootSystemViewModel!!)
        Assertions.assertEquals(rootSystemViewModel!!.automaton.startState, newState)
    }
}