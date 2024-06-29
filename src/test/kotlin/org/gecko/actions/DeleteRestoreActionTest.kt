package org.gecko.actions


import org.gecko.viewmodel.GeckoViewModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DeleteRestoreActionTest {
    private val geckoViewModel = GeckoViewModel()
    private val actionManager = ActionManager(geckoViewModel)
    private val viewModelFactory = geckoViewModel.viewModelFactory
    private val root = geckoViewModel.currentEditor!!.currentSystem
    private val child = viewModelFactory.createSystem(root)
    private val state1 = viewModelFactory.createState(root)
    private val state2 = viewModelFactory.createState(child)

    @Test
    fun deleteElement() {
        geckoViewModel.switchEditor(root, true)
        val v = geckoViewModel.currentEditor!!.containedPositionableViewModelElementsProperty
        Assertions.assertTrue(v.isNotEmpty())
        Assertions.assertTrue(state1 in v)
        actionManager.run(actionManager.actionFactory.createDeleteAction(setOf(state1)))
        Assertions.assertTrue(v.isEmpty())
    }

    @Test
    fun restoreElementCheckViewModel() {
        geckoViewModel.switchEditor(root, true)
        actionManager.run(
            actionManager.actionFactory.createDeleteAction(setOf(state1))
        )
        actionManager.undo()
        Assertions.assertTrue(
            geckoViewModel.currentEditor!!
                .containedPositionableViewModelElementsProperty
                .contains(state1)
        )
    }

    @Test
    fun restoreElementCheckModel() {
        geckoViewModel.switchEditor(root, true)
        actionManager.run(
            actionManager.actionFactory.createDeleteAction(setOf(state1))
        )
        actionManager.undo()
        /*Assertions.assertTrue(
            geckoModel!!.root.automaton.states.contains(
                stateViewModel
            )
        )*/
    }

    @Test
    fun deleteElementInChildSystem() {
        geckoViewModel.switchEditor(child, true)
        actionManager.run(actionManager.actionFactory.createDeleteAction(setOf(state2)))
        Assertions.assertTrue(geckoViewModel.currentEditor!!.containedPositionableViewModelElementsProperty.isEmpty())
    }

    @Test
    fun restoreElementCheckChildSystemViewModel() {
        geckoViewModel.switchEditor(child, true)
        actionManager.run(
            actionManager.actionFactory.createDeleteAction(setOf(state2))
        )
        actionManager.undo()
        Assertions.assertTrue(
            geckoViewModel.currentEditor!!
                .containedPositionableViewModelElementsProperty
                .contains(state2)
        )
    }

    @Test
    fun restoreElementCheckChildSystemModel() {
        geckoViewModel.switchEditor(child, true)
        actionManager.run(
            actionManager.actionFactory.createDeleteAction(setOf(state2))
        )
        actionManager.undo()
        Assertions.assertTrue(
            child.automaton.states.contains(
                state2
            )
        )
    }
}
