package org.gecko.tools

import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import org.gecko.actions.Action
import org.gecko.actions.ActionManager
import org.gecko.view.views.ViewElementPane

/**
 * A concrete representation of a variable-block-creating-[Tool], utilized for creating a
 * [PortViewModel][org.gecko.viewmodel.Port].
 */
class VariableBlockCreatorTool(actionManager: ActionManager) :
    Tool(actionManager, ToolType.VARIABLE_BLOCK_CREATOR, false) {
    override fun visitView(pane: ViewElementPane) {
        super.visitView(pane)
        pane.draw().cursor = Cursor.CROSSHAIR
        pane.draw().onMouseClicked = EventHandler<MouseEvent> { event: MouseEvent ->
            if (event.button != MouseButton.PRIMARY) {
                return@EventHandler
            }
            val position = pane.screenToWorldCoordinates(event.screenX, event.screenY)
            val createVariableBlockAction: Action = actionManager.actionFactory.createVariable(position)
            actionManager.run(createVariableBlockAction)
        }
    }
}
