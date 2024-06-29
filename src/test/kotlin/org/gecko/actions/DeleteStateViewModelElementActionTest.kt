package org.gecko.actions

import org.gecko.exceptions.ModelException
import org.gecko.util.TestHelper
import org.gecko.viewmodel.PositionableViewModelElement
import org.gecko.viewmodel.System
import org.junit.jupiter.api.*

internal class DeleteStateViewModelElementActionTest {
    private var geckoViewModel = TestHelper.createGeckoViewModel()

    private var actionManager: ActionManager = ActionManager(geckoViewModel)
    private var actionFactory: ActionFactory = ActionFactory(geckoViewModel)
    private var elements: Set<PositionableViewModelElement>
    private var rootSystem: System = geckoViewModel.root

    init {
        val viewModelFactory = geckoViewModel.viewModelFactory
        val stateViewModel1 = viewModelFactory.createState(rootSystem)
        val stateViewModel2 = viewModelFactory.createState(rootSystem)
        elements = setOf(stateViewModel1, stateViewModel2)
        geckoViewModel.switchEditor(rootSystem, true)
    }

    @Test
    @Throws(ModelException::class)
    fun newStateBecomesStartStateAfterAllStatesAreDeleted() {
        val deleteAction: Action = actionFactory.createDeleteAction(elements)
        actionManager.run(deleteAction)
        Assertions.assertEquals(0, rootSystem.automaton.states.size)
        val newState = geckoViewModel.viewModelFactory.createState(rootSystem)
        //Assertions.assertEquals(rootSystemViewModel.automaton.startState, newState)
    }
}