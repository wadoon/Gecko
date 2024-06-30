package org.gecko.view.contextmenu

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import org.gecko.actions.ActionManager
import org.gecko.view.GeckoView
import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.Port

/**
 * Represents a type of [ViewContextMenuBuilder] for a [ContextMenu] specific to a
 * [PortViewElement][org.gecko.view.views.viewelement.PortViewElement]. Contains [MenuItem]s that
 * run operations like deleting the port.
 */
class VariableBlockViewElementContextMenuBuilder(
    actionManager: ActionManager,
    val Port: Port,
    geckoView: GeckoView
) : ViewContextMenuBuilder(actionManager, geckoView) {
    override fun build(): ContextMenu {
        val variableBlockContextMenu = super.build()

        val dataTransferToVariableBlockEditingSeparator = SeparatorMenuItem()

        // Variable Block editing commands:
        val deleteMenuItem = MenuItem(ResourceHandler.Companion.delete)
        deleteMenuItem.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(actionManager.actionFactory.createDeleteAction(Port))
        }

        variableBlockContextMenu!!
            .items
            .addAll(dataTransferToVariableBlockEditingSeparator, deleteMenuItem)
        return variableBlockContextMenu
    }
}
