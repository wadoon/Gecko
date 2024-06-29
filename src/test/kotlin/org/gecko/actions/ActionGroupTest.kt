package org.gecko.actions

import javafx.scene.paint.Color
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.RegionViewModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ActionGroupTest {
    val geckoViewModel = GeckoViewModel()
    var actionManager: ActionManager = ActionManager(geckoViewModel)
    var actionFactory: ActionFactory = ActionFactory(geckoViewModel)
    val viewModelFactory = geckoViewModel.viewModelFactory
    val rootSystemViewModel = geckoViewModel.root
    var region1: RegionViewModel = viewModelFactory.createRegion(rootSystemViewModel)
    var region2: RegionViewModel = viewModelFactory.createRegion(rootSystemViewModel)

    init {
        geckoViewModel.switchEditor(rootSystemViewModel, true)
    }

    @Test
    fun testUndoActionReturnsNullWithNonUndoableAction() {
        val changeColor1Action: Action =
            actionFactory.createChangeColorRegion(region1, Color(1.0, 1.0, 1.0, 0.0))
        val focusAction: Action = actionFactory.createFocusPositionableViewModelElementAction(
            region2
        )
        val actionGroup = ActionGroup(arrayListOf(changeColor1Action, focusAction))
        actionManager.run(actionGroup)
        Assertions.assertNull(actionGroup.getUndoAction(actionFactory))
    }

    @Test
    fun testUndoActionNotNull() {
        val changeColor1Action: Action =
            actionFactory.createChangeColorRegion(region1, Color(1.0, 1.0, 1.0, 0.0))
        val actionGroup = ActionGroup(arrayListOf(changeColor1Action))
        actionManager.run(actionGroup)
        val undoAction = actionGroup.getUndoAction(actionFactory)
        Assertions.assertNotEquals(undoAction, null)
    }

    @Test
    fun testActionGroupRunsInOrder() {
        val changeColor1Action: Action =
            actionFactory.createChangeColorRegion(region1, Color(1.0, 1.0, 1.0, 0.0))
        val changeColor2Action: Action =
            actionFactory.createChangeColorRegion(region1, Color(0.0, 0.0, 0.0, 0.0))
        val actionGroup = ActionGroup(arrayListOf(changeColor1Action, changeColor2Action))
        actionManager.run(actionGroup)
        Assertions.assertEquals(Color(0.0, 0.0, 0.0, 0.0), region1.color)
    }

    @Test
    fun testOrderOfUndoActions() {
        val beforeChange = region1.color
        val changeColor1Action: Action =
            actionFactory.createChangeColorRegion(region1, Color(1.0, 1.0, 1.0, 0.0))
        val changeColor2Action: Action =
            actionFactory.createChangeColorRegion(region1, Color(0.0, 0.0, 0.0, 0.0))
        val actionGroup = ActionGroup(arrayListOf(changeColor1Action, changeColor2Action))
        actionManager.run(actionGroup)
        Assertions.assertEquals(Color(0.0, 0.0, 0.0, 0.0), region1.color)
        val undoAction = actionGroup.getUndoAction(actionFactory)
        Assertions.assertNotEquals(undoAction, null)
        actionManager.run(undoAction!!)
        Assertions.assertEquals(beforeChange, region1.color)
    }
}