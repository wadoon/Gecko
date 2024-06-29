package org.gecko.viewmodel


import org.gecko.exceptions.ModelException
import org.gecko.tools.ToolType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class EditorViewModelTest {
    @Test
    fun regionViewModels() {
        val geckoViewModel = GeckoViewModel()
        val viewModelFactory = geckoViewModel.viewModelFactory
        val rootSystemViewModel = geckoViewModel.root
        val regionViewModel2 = viewModelFactory.createRegion(rootSystemViewModel)
        val regionViewModel1 = viewModelFactory.createRegion(rootSystemViewModel)
        val stateViewModel: StateViewModel = viewModelFactory.createState(rootSystemViewModel)
        geckoViewModel.switchEditor(rootSystemViewModel, true)
        val editorViewModel = geckoViewModel.currentEditor!!

        regionViewModel1.addState(stateViewModel)
        assertEquals(editorViewModel.getRegions(stateViewModel), listOf(regionViewModel1))

        regionViewModel2.addState(stateViewModel)
        assertTrue(
            editorViewModel.getRegions(stateViewModel).containsAll(listOf(regionViewModel1, regionViewModel2))
        )
    }

    @Test
    fun updateRegionViewModels() {
        val geckoViewModel = GeckoViewModel()
        val viewModelFactory = geckoViewModel.viewModelFactory
        val rootSystemViewModel = geckoViewModel.root
        val regionViewModel = viewModelFactory.createRegion(rootSystemViewModel)
        var stateViewModel: StateViewModel? = null
        try {
            stateViewModel = viewModelFactory.createState(rootSystemViewModel)
        } catch (e: ModelException) {
            fail<Any>()
        }
        geckoViewModel.switchEditor(rootSystemViewModel, true)
        val editorViewModel = geckoViewModel.currentEditor

        stateViewModel!!.setPositionFromCenter(regionViewModel.center)
        editorViewModel!!.updateRegions()
        assertTrue(regionViewModel.states.contains(stateViewModel))

        val regionViewModels = editorViewModel.getRegions(
            stateViewModel
        )
        assertTrue(regionViewModels.contains(regionViewModel))

        stateViewModel.setPositionFromCenter(regionViewModel.center.add(1000.0, 1000.0))
        editorViewModel.updateRegions()
        assertFalse(regionViewModels.contains(regionViewModel))

        stateViewModel.setPositionFromCenter(regionViewModel.center)
        editorViewModel.updateRegions()
        assertTrue(regionViewModels.contains(regionViewModel))
    }

    @Test
    fun testToolSelections() {
        val geckoViewModel = GeckoViewModel()
        val viewModelFactory = geckoViewModel.viewModelFactory
        val rootSystemViewModel = geckoViewModel.root

        geckoViewModel.switchEditor(rootSystemViewModel, true)
        val editorViewModel = geckoViewModel.currentEditor

        assertEquals(editorViewModel!!.currentTool.toolType, ToolType.CURSOR)
        editorViewModel.setCurrentTool(ToolType.STATE_CREATOR)
        assertEquals(editorViewModel.currentToolType, ToolType.STATE_CREATOR)
    }

    @Test
    fun testPositionableViewModelElements() {
        val geckoViewModel = GeckoViewModel()
        val viewModelFactory = geckoViewModel.viewModelFactory
        val rootSystemViewModel = geckoViewModel.root
        val regionViewModel = viewModelFactory.createRegion(rootSystemViewModel)
        val stateViewModel = viewModelFactory.createState(rootSystemViewModel)
        geckoViewModel.switchEditor(rootSystemViewModel, true)
        val editorViewModel = geckoViewModel.currentEditor

        assertTrue(stateViewModel.name.startsWith("State_"))

        assertEquals(1, editorViewModel!!.getElementsByName(stateViewModel.name).size)

        assertTrue(editorViewModel.positionableViewModelElements.contains(stateViewModel))
        assertTrue(editorViewModel.containedPositionableViewModelElementsProperty.contains(regionViewModel))

        editorViewModel.selectionManager.select(stateViewModel)
        assertEquals(editorViewModel.focusedElement, stateViewModel)
    }
}