package org.gecko.viewmodel


import org.gecko.tools.ToolType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.assertContains

internal class EditorViewModelTest {
    @Test
    fun regionViewModels() {
        val gModel = GModel()
        val viewModelFactory = gModel.viewModelFactory
        val rootSystemViewModel = gModel.root
        val region2 = viewModelFactory.createRegion(rootSystemViewModel)
        val region1 = viewModelFactory.createRegion(rootSystemViewModel)
        val state = viewModelFactory.createState(rootSystemViewModel)

        gModel.switchEditor(rootSystemViewModel, true)
        val editorViewModel = gModel.currentEditor

        region1.states.add(state)
        assertContains(editorViewModel.getRegions(state), region1)

        region2.states.add(state)
        assertContains(editorViewModel.getRegions(state), region2)
    }

    @Test
    fun updateRegionViewModels() {
        val gModel = GModel()
        val viewModelFactory = gModel.viewModelFactory
        val rootSystemViewModel = gModel.root
        val region = viewModelFactory.createRegion(rootSystemViewModel)
        val state = viewModelFactory.createState(rootSystemViewModel)
        gModel.switchEditor(rootSystemViewModel, true)
        val editorViewModel = gModel.currentEditor

        state.setPositionFromCenter(region.center)
        editorViewModel.updateRegions()
        assertTrue(region.states.contains(state))

        val r = editorViewModel.getRegions(state)
        assertTrue(r.contains(region))

        state.setPositionFromCenter(region.center.add(1000.0, 1000.0))
        editorViewModel.updateRegions()
        assertFalse(editorViewModel.getRegions(state).contains(region))

        state.setPositionFromCenter(region.center)
        editorViewModel.updateRegions()
        assertTrue(r.contains(region))
    }

    @Test
    fun testToolSelections() {
        val gModel = GModel()
        val rootSystemViewModel = gModel.root

        gModel.switchEditor(rootSystemViewModel, true)
        val editorViewModel = gModel.currentEditor

        assertEquals(editorViewModel.currentTool.toolType, ToolType.CURSOR)
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

        //DISABLED
        // assertEquals(1, editorViewModel.getElementsByName(stateViewModel.name).size)

        assertTrue(editorViewModel.viewableElements.contains(stateViewModel))
        assertTrue(editorViewModel.viewableElementsProperty.contains(regionViewModel))

        editorViewModel.selectionManager.select(stateViewModel)
        assertEquals(editorViewModel.focusedElement, stateViewModel)
    }
}