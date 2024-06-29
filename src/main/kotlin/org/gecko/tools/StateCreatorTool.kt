package org.gecko.tools

import javafx.event.EventHandler
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import org.gecko.actions.Action
import org.gecko.actions.ActionManager
import org.gecko.view.views.ViewElementPane

/**
 * A concrete representation of a state-creating-[Tool], utilized for creating a
 * [StateViewModel][org.gecko.viewmodel.StateViewModel].
 */
class StateCreatorTool(actionManager: ActionManager) : Tool(actionManager, ToolType.STATE_CREATOR, false) {
    override fun visitView(pane: ViewElementPane) {
        super.visitView(pane)
        pane.draw().onMouseClicked = EventHandler<MouseEvent> { event: MouseEvent ->
            if (event.button != MouseButton.PRIMARY) {
                return@EventHandler
            }
            val position = pane.screenToWorldCoordinates(event.screenX, event.screenY)
            val createStateAction: Action =
                actionManager.actionFactory.createState(position)
            actionManager.run(createStateAction)
        }
    }
}
