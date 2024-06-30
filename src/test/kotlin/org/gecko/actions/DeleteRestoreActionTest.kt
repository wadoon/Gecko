package org.gecko.actions


import org.gecko.viewmodel.GModel
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DeleteRestoreActionTest {
    private val gModel = GModel()
    private val actionManager = ActionManager(gModel)
    private val viewModelFactory = gModel.viewModelFactory
    private val root = gModel.currentEditor.currentSystem
    private val child = viewModelFactory.createSystem(root)
    private val state1 = viewModelFactory.createState(root)
    private val state2 = viewModelFactory.createState(child)

    @Test
    fun deleteElement() {
        gModel.switchEditor(root, true)
        val v = gModel.currentEditor.viewableElementsProperty
        assertTrue(v.isNotEmpty())
        assertTrue(state1 in v)
        actionManager.run(actionManager.actionFactory.createDeleteAction(setOf(state1)))
        assertTrue(v.isEmpty())
    }

    @Test
    fun restoreElementCheckViewModel() {
        gModel.switchEditor(root, true)
        actionManager.run(actionManager.actionFactory.createDeleteAction(setOf(state1)))
        actionManager.undo()
        assertTrue(gModel.currentEditor.viewableElementsProperty.contains(state1))
    }

    @Test
    fun restoreElementCheckModel() {
        gModel.switchEditor(root, true)
        actionManager.run(actionManager.actionFactory.createDeleteAction(setOf(state1)))
        actionManager.undo()
        assertTrue(gModel.root.automaton.states.contains(state1))
    }

    @Test
    fun deleteElementInChildSystem() {
        gModel.switchEditor(child, true)
        actionManager.run(actionManager.actionFactory.createDeleteAction(setOf(state2)))
        assertTrue(gModel.currentEditor.viewableElementsProperty.isEmpty())
    }

    @Test
    fun restoreElementCheckChildSystemViewModel() {
        gModel.switchEditor(child, true)
        actionManager.run(actionManager.actionFactory.createDeleteAction(setOf(state2)))
        actionManager.undo()
        assertTrue(gModel.currentEditor.viewableElementsProperty.contains(state2))
    }

    @Test
    fun restoreElementCheckChildSystemModel() {
        gModel.switchEditor(child, true)
        actionManager.run(actionManager.actionFactory.createDeleteAction(setOf(state2)))
        actionManager.undo()
        assertTrue(child.automaton.states.contains(state2))
    }
}
