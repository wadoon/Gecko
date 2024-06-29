package org.gecko.actions

import org.gecko.tools.ToolType
import org.gecko.util.TestHelper
import org.gecko.viewmodel.GModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SelectToolActionTest {
    private var gModel: GModel = TestHelper.createGeckoViewModel()
    private var actionManager: ActionManager = ActionManager(gModel)
    private var actionFactory: ActionFactory = ActionFactory(gModel)

    init {
        val rootSystemViewModel = gModel.root
        gModel.switchEditor(rootSystemViewModel, true)
    }

    @Test
    fun selectCursorTool() {
        val selectToolAction: Action = actionFactory.createSelectToolAction(ToolType.CURSOR)
        actionManager.run(selectToolAction)
        Assertions.assertEquals(ToolType.CURSOR, gModel.currentEditor!!.currentToolType)
    }

    @Test
    fun undoSelectCursorTool() {
        val stateCreatorToolAction: Action = actionFactory.createSelectToolAction(ToolType.STATE_CREATOR)
        actionManager.run(stateCreatorToolAction)
        Assertions.assertEquals(ToolType.STATE_CREATOR, gModel.currentEditor!!.currentToolType)
        val selectToolAction: Action = actionFactory.createSelectToolAction(ToolType.CURSOR)
        actionManager.run(selectToolAction)
        actionManager.undo()
        Assertions.assertEquals(ToolType.CURSOR, gModel.currentEditor!!.currentToolType)
    }
}
