package org.gecko.actions

import javafx.scene.paint.Color
import org.gecko.exceptions.ModelException

import org.gecko.util.TestHelper
import org.gecko.viewmodel.RegionViewModel
import org.junit.jupiter.api.*

internal class ChangeColorRegionViewModelElementActionTest {
    lateinit var region1: RegionViewModel
    lateinit var actionManager: ActionManager
    lateinit var actionFactory: ActionFactory

    @BeforeEach
    @Throws(ModelException::class)
    fun setUp() {
        val geckoViewModel = TestHelper.createGeckoViewModel()
        actionManager = ActionManager(geckoViewModel)
        actionFactory = ActionFactory(geckoViewModel)
        val viewModelFactory = geckoViewModel.viewModelFactory
        val rootSystemViewModel =
            geckoViewModel.root
        region1 = viewModelFactory.createRegionViewModelIn(rootSystemViewModel)
        geckoViewModel.switchEditor(rootSystemViewModel, true)
    }

    @Test
    fun run() {
        val color = Color(1.0, 1.0, 1.0, 0.0)
        val changeColorRegionViewModelElementAction: Action =
            actionFactory.createChangeColorRegionViewModelElementAction(region1, color)
        actionManager.run(changeColorRegionViewModelElementAction)
        Assertions.assertEquals(color, region1.color)
    }

    @Test
    fun undoAction() {
        val color = Color(0.0, 0.0, 0.0, 0.0)
        val changeColorRegionViewModelElementAction: Action =
            actionFactory.createChangeColorRegionViewModelElementAction(region1, color)
        val beforeChangeColor = region1.color
        actionManager.run(changeColorRegionViewModelElementAction)
        actionManager.undo()
        Assertions.assertEquals(beforeChangeColor, region1.color)
    }
}