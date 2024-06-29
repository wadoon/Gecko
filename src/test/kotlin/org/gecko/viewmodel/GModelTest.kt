package org.gecko.viewmodel

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class GModelTest {
    @Test
    fun switchEditor1() {
        Assertions.assertEquals(1, gModel.openedEditorsProperty.size)
        Assertions.assertNotNull(gModel.currentEditor)
    }

    @Test
    fun switchEditor2() {
        gModel.switchEditor(rootSystem, false)
        Assertions.assertEquals(1, gModel.openedEditorsProperty.size)
    }

    @Test
    fun switchEditor3() {
        gModel.switchEditor(rootSystem, false)
        gModel.switchEditor(rootSystem, true)
        Assertions.assertEquals(2, gModel.openedEditorsProperty.size)
    }

    @Test
    fun switchEditorInitializeElements1() {
        gModel.switchEditor(rootSystem, false)
        val editorViewModel = gModel.currentEditor
        Assertions.assertFalse(
            editorViewModel!!.viewableElements.contains(
                rootSystem
            )
        )
        Assertions.assertTrue(
            editorViewModel.viewableElements
                .containsAll(listOf(childSystem1, childSystem2))
        )
    }

    @Test
    fun switchEditorInitializeElements2() {
        gModel.switchEditor(rootSystem, true)
        val editorViewModel = gModel.currentEditor
        Assertions.assertTrue(editorViewModel!!.viewableElements.contains(stateViewModel))
        Assertions.assertFalse(
            editorViewModel.viewableElements.contains(childSystem1)
        )
    }


    private var gModel: GModel
    private var rootSystem: System
    private var childSystem1: System
    private var childSystem2: System
    private var stateViewModel: StateViewModel

    init {
        gModel = GModel()
        val viewModelFactory = gModel.viewModelFactory
        rootSystem = gModel.currentEditor!!.currentSystem
        childSystem1 = viewModelFactory.createSystem(
            rootSystem
        )
        childSystem2 = viewModelFactory.createSystem(
            rootSystem
        )
        stateViewModel = viewModelFactory.createState(rootSystem)
    }
}