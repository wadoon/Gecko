package org.gecko.tools

import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import org.gecko.actions.Action
import org.gecko.actions.ActionManager
import org.gecko.view.views.ViewElementPane
import org.gecko.view.views.viewelement.PortViewElement
import org.gecko.view.views.viewelement.SystemViewElement
import org.gecko.view.views.viewelement.VariableBlockViewElement
import org.gecko.viewmodel.Port

/**
 * A concrete representation of a system-connection-creating-[Tool], utilized for connecting a source- and a
 * destination-[Port] through a
 * [SystemConnectionViewModel][org.gecko.viewmodel.SystemConnectionViewModel]. Holds the
 * source-[Port].
 */
class SystemConnectionCreatorTool(actionManager: ActionManager) :
    Tool(actionManager, ToolType.CONNECTION_CREATOR, false) {
    var previousPort: Port?

    init {
        previousPort = null
    }

    override fun visitView(pane: ViewElementPane) {
        super.visitView(pane)
        pane.draw().cursor = Cursor.CROSSHAIR
    }

    override fun visit(portViewElement: PortViewElement) {
        super.visit(portViewElement)
        portViewElement.onMouseClicked = EventHandler<MouseEvent> { event: MouseEvent ->
            if (event.button != MouseButton.PRIMARY) {
                return@EventHandler
            }
            setPortViewModel(portViewElement.viewModel)
        }
    }

    override fun visit(variableBlockViewElement: VariableBlockViewElement) {
        super.visit(variableBlockViewElement)
        variableBlockViewElement.onMouseClicked = EventHandler<MouseEvent> { event: MouseEvent ->
            if (event.button != MouseButton.PRIMARY) {
                return@EventHandler
            }
            setPortViewModel(variableBlockViewElement.target)
        }
    }

    override fun visit(systemViewElement: SystemViewElement) {
        super.visit(systemViewElement)
        //Pass events to the port view elements
        systemViewElement.onMouseClicked = null
    }

    fun setPortViewModel(viewModel: Port?) {
        if (previousPort != null) {
            val createAction: Action = actionManager.actionFactory
                .createCreateSystemConnection(previousPort!!, viewModel!!)
            actionManager.run(createAction)
        }
        previousPort = viewModel
    }
}
