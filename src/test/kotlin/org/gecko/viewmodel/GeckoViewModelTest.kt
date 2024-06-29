package org.gecko.viewmodel

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class GeckoViewModelTest {
    @Test
    fun switchEditor1() {
        Assertions.assertEquals(1, geckoViewModel.openedEditorsProperty.size)
        Assertions.assertNotNull(geckoViewModel.currentEditor)
    }

    @Test
    fun switchEditor2() {
        geckoViewModel.switchEditor(rootSystemViewModel, false)
        Assertions.assertEquals(1, geckoViewModel.openedEditorsProperty.size)
    }

    @Test
    fun switchEditor3() {
        geckoViewModel.switchEditor(rootSystemViewModel, false)
        geckoViewModel.switchEditor(rootSystemViewModel, true)
        Assertions.assertEquals(2, geckoViewModel.openedEditorsProperty.size)
    }

    @Test
    fun switchEditorInitializeElements1() {
        geckoViewModel.switchEditor(rootSystemViewModel, false)
        val editorViewModel = geckoViewModel.currentEditor
        Assertions.assertFalse(
            editorViewModel!!.containedPositionableViewModelElementsProperty.contains(
                rootSystemViewModel
            )
        )
        Assertions.assertTrue(
            editorViewModel.containedPositionableViewModelElementsProperty
                .containsAll(listOf(childSystemViewModel1, childSystemViewModel2))
        )
    }

    @Test
    fun switchEditorInitializeElements2() {
        geckoViewModel.switchEditor(rootSystemViewModel, true)
        val editorViewModel = geckoViewModel.currentEditor
        Assertions.assertTrue(editorViewModel!!.containedPositionableViewModelElementsProperty.contains(stateViewModel))
        Assertions.assertFalse(
            editorViewModel.containedPositionableViewModelElementsProperty.contains(childSystemViewModel1)
        )
    }


    private var geckoViewModel: GeckoViewModel
    private var rootSystemViewModel: SystemViewModel
    private var childSystemViewModel1: SystemViewModel
    private var childSystemViewModel2: SystemViewModel
    private var stateViewModel: StateViewModel

    init {
        geckoViewModel = GeckoViewModel()
        val viewModelFactory = geckoViewModel.viewModelFactory
        rootSystemViewModel = geckoViewModel.currentEditor!!.currentSystem
        childSystemViewModel1 = viewModelFactory.createSystem(
            rootSystemViewModel
        )
        childSystemViewModel2 = viewModelFactory.createSystem(
            rootSystemViewModel
        )
        stateViewModel = viewModelFactory.createState(rootSystemViewModel)
    }
}