package org.gecko.actions

import org.gecko.exceptions.ModelException
import org.gecko.util.TestHelper
import org.gecko.viewmodel.PositionableViewModelElement
import org.gecko.viewmodel.SystemViewModel
import org.junit.jupiter.api.*

internal class DeleteStateViewModelElementActionTest {
    private var geckoViewModel = TestHelper.createGeckoViewModel()

    private var actionManager: ActionManager = ActionManager(geckoViewModel)
    private var actionFactory: ActionFactory = ActionFactory(geckoViewModel)
    private var elements: Set<PositionableViewModelElement>
    private var rootSystemViewModel: SystemViewModel = geckoViewModel.root

    init {
        val viewModelFactory = geckoViewModel.viewModelFactory
        val stateViewModel1 = viewModelFactory.createStateViewModelIn(rootSystemViewModel)
        val stateViewModel2 = viewModelFactory.createStateViewModelIn(rootSystemViewModel)
        elements = setOf(stateViewModel1, stateViewModel2)
        geckoViewModel.switchEditor(rootSystemViewModel, true)
    }

    @Test
    @Throws(ModelException::class)
    fun newStateBecomesStartStateAfterAllStatesAreDeleted() {
        val deleteAction: Action = actionFactory.createDeletePositionableViewModelElementAction(elements)
        actionManager.run(deleteAction)
        Assertions.assertEquals(0, rootSystemViewModel.automaton.states.size)
        val newState = geckoViewModel.viewModelFactory.createStateViewModelIn(rootSystemViewModel)
        Assertions.assertEquals(rootSystemViewModel.automaton.startState, newState)
    }
}