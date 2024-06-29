package org.gecko.actions

import org.gecko.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SelectionActionTest {
    @Test
    fun singleSelectionTest() {
        val selectAction: Action = actionFactory.createSelectAction(
            stateViewModel1, true
        )
        actionManager.run(selectAction)
        Assertions.assertEquals(1, geckoViewModel.currentEditor!!.selectionManager.currentSelection.size)

        actionManager.undo()
        Assertions.assertEquals(1, geckoViewModel.currentEditor!!.selectionManager.currentSelection.size)
    }

    @Test
    fun multiSelectionTest() {
        selectBunch()

        actionManager.undo()
        Assertions.assertEquals(4, geckoViewModel.currentEditor!!.selectionManager.currentSelection.size)
    }

    @Test
    fun deselectTest() {
        selectBunch()

        val deselectAction: Action = actionFactory.createDeselectAction()
        actionManager.run(deselectAction)
        Assertions.assertEquals(0, geckoViewModel.currentEditor!!.selectionManager.currentSelection.size)

        actionManager.undo()
        Assertions.assertEquals(0, geckoViewModel.currentEditor!!.selectionManager.currentSelection.size)
    }

    @Test
    fun selectEmpty() {
        selectBunch()

        val selectAction: Action = actionFactory.createSelectAction(setOf(), true)
        actionManager.run(selectAction)
        Assertions.assertEquals(0, geckoViewModel.currentEditor!!.selectionManager.currentSelection.size)
    }

    @Test
    fun selectionHistoryTest() {
        val selectNone: Action = actionFactory.createSelectAction(setOf(), true)
        actionManager.run(selectNone)
        Assertions.assertEquals(0, geckoViewModel.currentEditor!!.selectionManager.currentSelection.size)
        selectBunch()

        val previousSelectionAction: Action = actionFactory.createSelectionHistoryBackAction()
        actionManager.run(previousSelectionAction)
        Assertions.assertEquals(2, geckoViewModel.currentEditor!!.selectionManager.currentSelection.size)

        val nextSelectionAction: Action = actionFactory.createSelectionHistoryForwardAction()
        actionManager.run(nextSelectionAction)
        Assertions.assertEquals(4, geckoViewModel.currentEditor!!.selectionManager.currentSelection.size)
    }

    val geckoViewModel = TestHelper.createGeckoViewModel()
    val actionManager = ActionManager(geckoViewModel)
    val actionFactory = ActionFactory(geckoViewModel)
    val viewModelFactory = geckoViewModel.viewModelFactory
    val systemViewModel = geckoViewModel.root
    val stateViewModel1 = viewModelFactory.createState(systemViewModel)
    val stateViewModel2 = viewModelFactory.createState(systemViewModel)
    val stateViewModel3 = viewModelFactory.createState(systemViewModel)
    val stateViewModel4 = viewModelFactory.createState(systemViewModel)

    private fun selectBunch() {
        val selectAction1: Action = actionFactory.createSelectAction(
            stateViewModel1, true
        )
        actionManager.run(selectAction1)
        val selectAction2: Action = actionFactory.createSelectAction(
            stateViewModel2, false
        )
        actionManager.run(selectAction2)
        val selectAction3: Action =
            actionFactory.createSelectAction(setOf(stateViewModel3, stateViewModel4), false)
        actionManager.run(selectAction3)
        Assertions.assertEquals(4, geckoViewModel.currentEditor!!.selectionManager.currentSelection.size)
    }
}
