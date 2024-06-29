package org.gecko.tools

import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import org.gecko.actions.Action
import org.gecko.actions.ActionManager
import org.gecko.view.views.ViewElementPane

/**
 * A concrete representation of a system-creating-[Tool], utilized for creating a
 * [SystemViewModel][org.gecko.viewmodel.System].
 */
class SystemCreatorTool(actionManager: ActionManager) : Tool(actionManager, ToolType.SYSTEM_CREATOR, false) {
    override fun visitView(pane: ViewElementPane) {
        super.visitView(pane)
        pane.draw().onMouseClicked = EventHandler { event: MouseEvent ->
            if (event.isConsumed) {
                return@EventHandler
            }
            if (event.button != MouseButton.PRIMARY) {
                return@EventHandler
            }
            val point = pane.screenToWorldCoordinates(Point2D(event.screenX, event.screenY))
            val createSystemAction: Action =
                actionManager.actionFactory.createSystem(point)
            actionManager.run(createSystemAction)
        }
    }
}
