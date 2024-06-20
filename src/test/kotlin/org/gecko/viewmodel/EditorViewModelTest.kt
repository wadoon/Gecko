package org.gecko.viewmodel

import org.gecko.exceptions.ModelException


import org.gecko.tools.ToolType
import org.junit.jupiter.api.*
import java.util.List

internal class EditorViewModelTest {
    @get:Throws(ModelException::class)
    @get:Test
    val regionViewModels: Unit
        get() {
            val geckoModel = GeckoModel()
            val geckoViewModel = GeckoViewModel(geckoModel)
            val viewModelFactory = geckoViewModel.viewModelFactory
            val rootSystemViewModel = viewModelFactory.createSystemViewModelFrom(geckoModel.root)
            val regionViewModel2 = viewModelFactory.createRegionViewModelIn(rootSystemViewModel)
            val regionViewModel1 = viewModelFactory.createRegionViewModelIn(rootSystemViewModel)
            var stateViewModel: StateViewModel? = null
            try {
                stateViewModel = viewModelFactory.createStateViewModelIn(rootSystemViewModel)
            } catch (e: ModelException) {
                Assertions.fail<Any>()
            }
            geckoViewModel.switchEditor(rootSystemViewModel, true)
            val editorViewModel = geckoViewModel.currentEditor

            regionViewModel1.target.addState(stateViewModel!!.target)
            Assertions.assertEquals(editorViewModel.getRegionViewModels(stateViewModel), List.of(regionViewModel1))

            regionViewModel2.target.addState(stateViewModel.target)
            Assertions.assertTrue(
                editorViewModel.getRegionViewModels(stateViewModel)
                    .containsAll(List.of(regionViewModel1, regionViewModel2))
            )
        }

    @Test
    @Throws(ModelException::class)
    fun updateRegionViewModels() {
        val geckoModel = GeckoModel()
        val geckoViewModel = GeckoViewModel(geckoModel)
        val viewModelFactory = geckoViewModel.viewModelFactory
        val rootSystemViewModel = viewModelFactory.createSystemViewModelFrom(geckoModel.root)
        val regionViewModel = viewModelFactory.createRegionViewModelIn(rootSystemViewModel)
        var stateViewModel: StateViewModel? = null
        try {
            stateViewModel = viewModelFactory.createStateViewModelIn(rootSystemViewModel)
        } catch (e: ModelException) {
            Assertions.fail<Any>()
        }
        geckoViewModel.switchEditor(rootSystemViewModel, true)
        val editorViewModel = geckoViewModel.currentEditor

        stateViewModel!!.center = regionViewModel.center
        editorViewModel.updateRegions()
        Assertions.assertTrue(regionViewModel.target.states.contains(stateViewModel.target))

        val regionViewModels = editorViewModel.getRegionViewModels(
            stateViewModel
        )
        Assertions.assertTrue(regionViewModels.contains(regionViewModel))

        stateViewModel.center = regionViewModel.center!!.add(1000.0, 1000.0)
        editorViewModel.updateRegions()
        Assertions.assertFalse(regionViewModels.contains(regionViewModel))

        stateViewModel.center = regionViewModel.center
        editorViewModel.updateRegions()
        Assertions.assertTrue(regionViewModels.contains(regionViewModel))
    }

    @Test
    @Throws(ModelException::class)
    fun testToolSelections() {
        val geckoModel = GeckoModel()
        val geckoViewModel = GeckoViewModel(geckoModel)
        val viewModelFactory = geckoViewModel.viewModelFactory
        val rootSystemViewModel = viewModelFactory.createSystemViewModelFrom(geckoModel.root)

        geckoViewModel.switchEditor(rootSystemViewModel, true)
        val editorViewModel = geckoViewModel.currentEditor

        Assertions.assertEquals(editorViewModel.currentTool.toolType, ToolType.CURSOR)
        editorViewModel.setCurrentTool(ToolType.STATE_CREATOR)
        Assertions.assertEquals(editorViewModel.currentToolType, ToolType.STATE_CREATOR)
    }

    @Test
    @Throws(ModelException::class)
    fun testPositionableViewModelElements() {
        val geckoModel = GeckoModel()
        val geckoViewModel = GeckoViewModel(geckoModel)
        val viewModelFactory = geckoViewModel.viewModelFactory
        val rootSystemViewModel = viewModelFactory.createSystemViewModelFrom(geckoModel.root)
        val regionViewModel = viewModelFactory.createRegionViewModelIn(rootSystemViewModel)
        var stateViewModel: StateViewModel? = null
        try {
            stateViewModel = viewModelFactory.createStateViewModelIn(rootSystemViewModel)
        } catch (e: ModelException) {
            Assertions.fail<Any>()
        }
        geckoViewModel.switchEditor(rootSystemViewModel, true)
        val editorViewModel = geckoViewModel.currentEditor
        Assertions.assertEquals(stateViewModel!!.name, "Element_3")
        Assertions.assertEquals(editorViewModel.getElementsByName("Element_3").size, 1)

        Assertions.assertTrue(editorViewModel.positionableViewModelElements.contains(stateViewModel))
        Assertions.assertTrue(editorViewModel.containedPositionableViewModelElementsProperty.contains(regionViewModel))

        editorViewModel.selectionManager.select(stateViewModel)
        Assertions.assertEquals(editorViewModel.focusedElement, stateViewModel)
    }
}