package org.gecko.view.contextmenu

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import org.gecko.actions.ActionManager
import org.gecko.view.GeckoView
import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.SystemConnectionViewModel

/**
 * Represents a type of [ViewContextMenuBuilder] for a [ContextMenu] specific to a
 * [SystemConnectionViewElement][org.gecko.view.views.viewelement.SystemConnectionViewElement]. Contains
 * [MenuItem]s that run operations like deleting the connection.
 */
class SystemConnectionViewElementContextMenuBuilder(
    actionManager: ActionManager,
    val systemConnectionViewModel: SystemConnectionViewModel,
    geckoView: GeckoView
) : ViewContextMenuBuilder(actionManager, geckoView) {
    override fun build(): ContextMenu {
        val systemConnectionContextMenu = super.build()

        val dataTransferToEdgeEditingSeparator = SeparatorMenuItem()

        // SystemConnection editing commands:
        val deleteMenuItem = MenuItem(ResourceHandler.Companion.delete)
        deleteMenuItem.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory
                    .createDeletePositionableViewModelElementAction(systemConnectionViewModel)
            )
        }

        systemConnectionContextMenu!!.items.addAll(dataTransferToEdgeEditingSeparator, deleteMenuItem)
        return systemConnectionContextMenu
    }
}
