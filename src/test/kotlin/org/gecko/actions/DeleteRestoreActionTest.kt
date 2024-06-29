package org.gecko.actions


import org.gecko.viewmodel.GModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DeleteRestoreActionTest {
    private val gModel = GModel()
    private val actionManager = ActionManager(gModel)
    private val viewModelFactory = gModel.viewModelFactory
    private val root = gModel.currentEditor!!.currentSystem
    private val child = viewModelFactory.createSystem(root)
    private val state1 = viewModelFactory.createState(root)
    private val state2 = viewModelFactory.createState(child)

    @Test
    fun deleteElement() {
        gModel.switchEditor(root, true)
        val v = gModel.currentEditor!!.viewableElements
        Assertions.assertTrue(v.isNotEmpty())
        Assertions.assertTrue(state1 in v)
        actionManager.run(actionManager.actionFactory.createDeleteAction(setOf(state1)))
        Assertions.assertTrue(v.isEmpty())
    }

    @Test
    fun restoreElementCheckViewModel() {
        gModel.switchEditor(root, true)
        actionManager.run(
            actionManager.actionFactory.createDeleteAction(setOf(state1))
        )
        actionManager.undo()
        Assertions.assertTrue(
            gModel.currentEditor!!
                .viewableElements
                .contains(state1)
        )
    }

    @Test
    fun restoreElementCheckModel() {
        gModel.switchEditor(root, true)
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
        gModel.switchEditor(child, true)
        actionManager.run(actionManager.actionFactory.createDeleteAction(setOf(state2)))
        Assertions.assertTrue(gModel.currentEditor!!.viewableElements.isEmpty())
    }

    @Test
    fun restoreElementCheckChildSystemViewModel() {
        gModel.switchEditor(child, true)
        actionManager.run(
            actionManager.actionFactory.createDeleteAction(setOf(state2))
        )
        actionManager.undo()
        Assertions.assertTrue(
            gModel.currentEditor!!
                .viewableElements
                .contains(state2)
        )
    }

    @Test
    fun restoreElementCheckChildSystemModel() {
        gModel.switchEditor(child, true)
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
