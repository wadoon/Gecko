package org.gecko.view.inspector.element.button

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Tooltip
import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.views.shortcuts.Shortcuts
import org.gecko.viewmodel.System

/**
 * Represents a type of [AbstractInspectorButton] used for opening a [System] by switching to the
 * system view corresponding to the given system.
 */
class InspectorOpenSystemButton(actionManager: ActionManager, System: System?) :
    AbstractInspectorButton() {
    init {
        styleClass.add(STYLE)
        text = ResourceHandler.inspector_open_system
        val toolTip =
            "%s (%s)"
                .format(
                    ResourceHandler.inspector_open_system,
                    Shortcuts.OPEN_CHILD_SYSTEM_EDITOR.get().displayText
                )
        tooltip = Tooltip(toolTip)
        prefWidth = WIDTH.toDouble()
        onAction = EventHandler { event: ActionEvent? ->
            actionManager.run(actionManager.actionFactory.createViewSwitchAction(System, false))
        }
    }

    companion object {
        const val STYLE = "inspector-open-system-button"
        const val WIDTH = 300
    }
}
