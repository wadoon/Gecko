package org.gecko.tools

import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import org.gecko.actions.Action
import org.gecko.actions.ActionManager
import org.gecko.view.views.ViewElementPane
import org.gecko.view.views.viewelement.StateViewElement
import org.gecko.viewmodel.StateViewModel

/**
 * A concrete representation of an edge-creating-[Tool], utilized for connecting a source- and a
 * destination-[StateViewModel] through an [org.gecko.viewmodel.EdgeViewModel]. Holds the
 * source-[StateViewModel].
 */
class EdgeCreatorTool(actionManager: ActionManager) : Tool(actionManager, ToolType.EDGE_CREATOR, false) {
    var source: StateViewModel?

    init {
        source = null
    }

    override fun visitView(pane: ViewElementPane) {
        super.visitView(pane)
        pane.draw().cursor = Cursor.CROSSHAIR
    }

    override fun visit(stateViewElement: StateViewElement) {
        super.visit(stateViewElement)
        source = null
        stateViewElement.onMouseClicked = EventHandler<MouseEvent> { event: MouseEvent ->
            if (event.button != MouseButton.PRIMARY) {
                return@EventHandler
            }
            if (source == null) {
                source = stateViewElement.target
            } else {
                val createEdgeAction: Action = actionManager.actionFactory
                    .createCreateEdgeViewModelElementAction(source, stateViewElement.target)
                actionManager.run(createEdgeAction)
                source = null
            }
        }
    }
}
