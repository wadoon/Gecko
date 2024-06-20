package org.gecko.viewmodel

import org.gecko.exceptions.ModelException


import org.junit.jupiter.api.*
import java.util.List

internal class GeckoViewModelTest {
    @Test
    fun switchEditor1() {
        Assertions.assertEquals(1, geckoViewModel!!.openedEditorsProperty.size)
        Assertions.assertNotNull(geckoViewModel!!.currentEditor)
    }

    @Test
    fun switchEditor2() {
        geckoViewModel!!.switchEditor(rootSystemViewModel!!, false)
        Assertions.assertEquals(1, geckoViewModel!!.openedEditorsProperty.size)
    }

    @Test
    fun switchEditor3() {
        geckoViewModel!!.switchEditor(rootSystemViewModel!!, false)
        geckoViewModel!!.switchEditor(rootSystemViewModel!!, true)
        Assertions.assertEquals(2, geckoViewModel!!.openedEditorsProperty.size)
    }

    @Test
    fun switchEditorInitializeElements1() {
        geckoViewModel!!.switchEditor(rootSystemViewModel!!, false)
        val editorViewModel = geckoViewModel!!.currentEditor
        Assertions.assertFalse(
            editorViewModel.containedPositionableViewModelElementsProperty.contains(
                rootSystemViewModel
            )
        )
        Assertions.assertTrue(
            editorViewModel.containedPositionableViewModelElementsProperty
                .containsAll(List.of(childSystemViewModel1, childSystemViewModel2))
        )
    }

    @Test
    fun switchEditorInitializeElements2() {
        geckoViewModel!!.switchEditor(rootSystemViewModel!!, true)
        val editorViewModel = geckoViewModel!!.currentEditor
        Assertions.assertTrue(editorViewModel.containedPositionableViewModelElementsProperty.contains(stateViewModel))
        Assertions.assertFalse(
            editorViewModel.containedPositionableViewModelElementsProperty.contains(childSystemViewModel1)
        )
    }

    companion object {
        private var geckoModel: GeckoModel? = null
        private var geckoViewModel: GeckoViewModel? = null
        private var rootSystemViewModel: SystemViewModel? = null
        private var childSystemViewModel1: SystemViewModel? = null
        private var childSystemViewModel2: SystemViewModel? = null
        private var stateViewModel: StateViewModel? = null

        @BeforeAll
        @Throws(ModelException::class)
        fun setUp() {
            geckoModel = GeckoModel()
            geckoViewModel = GeckoViewModel(geckoModel!!)
            val viewModelFactory = geckoViewModel!!.viewModelFactory
            rootSystemViewModel = geckoViewModel!!.currentEditor.currentSystem
            childSystemViewModel1 = viewModelFactory.createSystemViewModelIn(
                rootSystemViewModel!!
            )
            childSystemViewModel2 = viewModelFactory.createSystemViewModelIn(
                rootSystemViewModel!!
            )
            try {
                stateViewModel = viewModelFactory.createStateViewModelIn(rootSystemViewModel!!)
            } catch (e: Exception) {
                Assertions.fail<Any>()
            }
        }
    }
}