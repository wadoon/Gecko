package org.gecko.tools

import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import org.gecko.actions.ActionManager
import org.gecko.view.views.ViewElementPane
import org.gecko.viewmodel.EditorViewModel

/**
 * A concrete representation of a zoom-[Tool], utilized for zooming in and out in the view.
 */
class ZoomTool(actionManager: ActionManager) : Tool(actionManager, ToolType.ZOOM_TOOL, true) {
    override fun visitView(pane: ViewElementPane) {
        super.visitView(pane)
        pane.draw().cursor = Cursor.CROSSHAIR

        pane.draw().onMouseClicked = EventHandler { event: MouseEvent ->
            val position = pane.screenToWorldCoordinates(event.screenX, event.screenY)
            if (event.isShiftDown) {
                actionManager.run(
                    actionManager.actionFactory
                        .createZoomAction(position, 1 / EditorViewModel.defaultZoomStep)
                )
            } else {
                actionManager.run(
                    actionManager.actionFactory.createZoomAction(
                        position,
                        EditorViewModel.defaultZoomStep
                    )
                )
            }
        }
    }
}
