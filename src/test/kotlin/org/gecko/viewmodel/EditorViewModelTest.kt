package org.gecko.viewmodel


import org.gecko.exceptions.ModelException
import org.gecko.tools.ToolType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class EditorViewModelTest {
    @Test
    fun regionViewModels() {
        val gModel = GModel()
        val viewModelFactory = gModel.viewModelFactory
        val rootSystemViewModel = gModel.root
        val regionViewModel2 = viewModelFactory.createRegion(rootSystemViewModel)
        val regionViewModel1 = viewModelFactory.createRegion(rootSystemViewModel)
        val stateViewModel: StateViewModel = viewModelFactory.createState(rootSystemViewModel)
        gModel.switchEditor(rootSystemViewModel, true)
        val editorViewModel = gModel.currentEditor!!

        regionViewModel1.addState(stateViewModel)
        assertEquals(editorViewModel.getRegions(stateViewModel), listOf(regionViewModel1))

        regionViewModel2.addState(stateViewModel)
        assertTrue(
            editorViewModel.getRegions(stateViewModel).containsAll(listOf(regionViewModel1, regionViewModel2))
        )
    }

    @Test
    fun updateRegionViewModels() {
        val gModel = GModel()
        val viewModelFactory = gModel.viewModelFactory
        val rootSystemViewModel = gModel.root
        val regionViewModel = viewModelFactory.createRegion(rootSystemViewModel)
        var stateViewModel: StateViewModel? = null
        try {
            stateViewModel = viewModelFactory.createState(rootSystemViewModel)
        } catch (e: ModelException) {
            fail<Any>()
        }
        gModel.switchEditor(rootSystemViewModel, true)
        val editorViewModel = gModel.currentEditor

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
        val gModel = GModel()
        val viewModelFactory = gModel.viewModelFactory
        val rootSystemViewModel = gModel.root

        gModel.switchEditor(rootSystemViewModel, true)
        val editorViewModel = gModel.currentEditor

        assertEquals(editorViewModel!!.currentTool.toolType, ToolType.CURSOR)
        editorViewModel.setCurrentTool(ToolType.STATE_CREATOR)
        assertEquals(editorViewModel.currentToolType, ToolType.STATE_CREATOR)
    }

    @Test
    fun testPositionableViewModelElements() {
        val gModel = GModel()
        val viewModelFactory = gModel.viewModelFactory
        val rootSystemViewModel = gModel.root
        val regionViewModel = viewModelFactory.createRegion(rootSystemViewModel)
        val stateViewModel = viewModelFactory.createState(rootSystemViewModel)
        gModel.switchEditor(rootSystemViewModel, true)
        val editorViewModel = gModel.currentEditor

        assertTrue(stateViewModel.name.startsWith("State_"))

        assertEquals(1, editorViewModel!!.getElementsByName(stateViewModel.name).size)

        assertTrue(editorViewModel.viewableElements.contains(stateViewModel))
        assertTrue(editorViewModel.viewableElementsProperty.contains(regionViewModel))

        editorViewModel.selectionManager.select(stateViewModel)
        assertEquals(editorViewModel.focusedElement, stateViewModel)
    }
}