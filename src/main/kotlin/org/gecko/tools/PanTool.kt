package org.gecko.tools

import javafx.scene.Cursor
import org.gecko.actions.ActionManager
import org.gecko.view.views.ViewElementPane

/**
 * A concrete representation of a pan-[Tool], utilized for moving the view.
 */
class PanTool(actionManager: ActionManager) : Tool(actionManager, ToolType.PAN, true) {
    override fun visitView(pane: ViewElementPane) {
        super.visitView(pane)
        pane.draw().isPannable = true
        pane.draw().cursor = Cursor.OPEN_HAND
    }
}
