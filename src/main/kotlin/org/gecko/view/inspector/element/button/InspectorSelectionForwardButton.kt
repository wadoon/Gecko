package org.gecko.view.inspector.element.button

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Tooltip
import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.views.shortcuts.Shortcuts
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign2.MaterialDesignA

/**
 * Represents a type of [AbstractInspectorButton] used for navigating forward in the selection history of the
 * [SelectionManager][org.gecko.viewmodel.SelectionManager].
 */
class InspectorSelectionForwardButton(actionManager: ActionManager) : AbstractInspectorButton() {
    init {
        styleClass.add(ICON_STYLE_NAME)
        val toolTip = "%s (%s)".format(
            ResourceHandler.inspector_selection_forward,
            Shortcuts.SELECTION_FORWARD.get().displayText
        )
        tooltip = Tooltip(toolTip)
        onAction = EventHandler { event: ActionEvent? ->
            actionManager.run(actionManager.actionFactory.createSelectionHistoryForwardAction())
        }
        graphic = FontIcon.of(MaterialDesignA.ARROW_RIGHT_BOLD_BOX_OUTLINE, 24)
    }

    companion object {
        const val ICON_STYLE_NAME = "inspector-selection-forward-button"
    }
}


fun String.format(fmt: String, vararg args: Any?) = String.format(fmt, *args)
