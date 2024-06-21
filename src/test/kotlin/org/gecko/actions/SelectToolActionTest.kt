package org.gecko.actions

import org.gecko.tools.ToolType
import org.gecko.util.TestHelper
import org.gecko.viewmodel.GeckoViewModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SelectToolActionTest {
    private var geckoViewModel: GeckoViewModel = TestHelper.createGeckoViewModel()
    private var actionManager: ActionManager = ActionManager(geckoViewModel)
    private var actionFactory: ActionFactory = ActionFactory(geckoViewModel)

    init {
        val rootSystemViewModel = geckoViewModel.root
        geckoViewModel.switchEditor(rootSystemViewModel, true)
    }

    @Test
    fun selectCursorTool() {
        val selectToolAction: Action = actionFactory.createSelectToolAction(ToolType.CURSOR)
        actionManager.run(selectToolAction)
        Assertions.assertEquals(ToolType.CURSOR, geckoViewModel.currentEditor!!.currentToolType)
    }

    @Test
    fun undoSelectCursorTool() {
        val stateCreatorToolAction: Action = actionFactory.createSelectToolAction(ToolType.STATE_CREATOR)
        actionManager.run(stateCreatorToolAction)
        Assertions.assertEquals(ToolType.STATE_CREATOR, geckoViewModel.currentEditor!!.currentToolType)
        val selectToolAction: Action = actionFactory.createSelectToolAction(ToolType.CURSOR)
        actionManager.run(selectToolAction)
        actionManager.undo()
        Assertions.assertEquals(ToolType.CURSOR, geckoViewModel.currentEditor!!.currentToolType)
    }
}
