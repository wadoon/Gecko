package org.gecko.actions


import org.gecko.viewmodel.GeckoViewModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class DeleteRestoreActionTest {
    @Test
    fun deleteElement() {
        geckoViewModel.switchEditor(rootSystemViewModel, true)
        actionManager.run(
            actionManager.actionFactory.createDeletePositionableViewModelElementAction(setOf(stateViewModel))
        )
        Assertions.assertTrue(geckoViewModel.currentEditor!!.containedPositionableViewModelElementsProperty.isEmpty())
    }

    @Test
    fun restoreElementCheckViewModel() {
        geckoViewModel.switchEditor(rootSystemViewModel, true)
        actionManager.run(
            actionManager.actionFactory.createDeletePositionableViewModelElementAction(setOf(stateViewModel))
        )
        actionManager.undo()
        Assertions.assertTrue(
            geckoViewModel.currentEditor!!
                .containedPositionableViewModelElementsProperty
                .contains(stateViewModel)
        )
    }

    @Test
    fun restoreElementCheckModel() {
        geckoViewModel.switchEditor(rootSystemViewModel, true)
        actionManager.run(
            actionManager.actionFactory.createDeletePositionableViewModelElementAction(setOf(stateViewModel))
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
        geckoViewModel.switchEditor(childSystemViewModel1, true)
        actionManager.run(
            actionManager.actionFactory.createDeletePositionableViewModelElementAction(setOf(stateViewModel2))
        )
        Assertions.assertTrue(geckoViewModel.currentEditor!!.containedPositionableViewModelElementsProperty.isEmpty())
    }

    @Test
    fun restoreElementCheckChildSystemViewModel() {
        geckoViewModel.switchEditor(childSystemViewModel1, true)
        actionManager.run(
            actionManager.actionFactory.createDeletePositionableViewModelElementAction(setOf(stateViewModel2))
        )
        actionManager.undo()
        Assertions.assertTrue(
            geckoViewModel.currentEditor!!
                .containedPositionableViewModelElementsProperty
                .contains(stateViewModel2)
        )
    }

    @Test
    fun restoreElementCheckChildSystemModel() {
        geckoViewModel.switchEditor(childSystemViewModel1, true)
        actionManager.run(
            actionManager.actionFactory.createDeletePositionableViewModelElementAction(setOf(stateViewModel2))
        )
        actionManager.undo()
        Assertions.assertTrue(
            childSystemViewModel1.automaton.states.contains(
                stateViewModel2
            )
        )
    }

    private var geckoViewModel = GeckoViewModel()
    private var actionManager = ActionManager(geckoViewModel)
    val viewModelFactory = geckoViewModel.viewModelFactory
    private var rootSystemViewModel = geckoViewModel.currentEditor!!.currentSystem
    private var childSystemViewModel1 = viewModelFactory.createSystemViewModelIn(rootSystemViewModel)
    private var stateViewModel = viewModelFactory.createStateViewModelIn(rootSystemViewModel)
    private var stateViewModel2 = viewModelFactory.createStateViewModelIn(childSystemViewModel1)
}
