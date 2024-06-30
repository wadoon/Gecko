package org.gecko.view.contextmenu

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import org.gecko.actions.ActionManager
import org.gecko.view.GeckoView
import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.Region

/**
 * Represents a type of [ViewContextMenuBuilder] for a [ContextMenu] specific to a
 * [RegionViewElement][org.gecko.view.views.viewelement.RegionViewElement]. Contains [MenuItem]s
 * that run operations like deleting the edge.
 */
class RegionViewElementContextMenuBuilder(
    actionManager: ActionManager,
    val Region: Region,
    geckoView: GeckoView
) : ViewContextMenuBuilder(actionManager, geckoView) {
    override fun build(): ContextMenu {
        val regionContextMenu = super.build()

        val dataTransferToRegionEditingSeparator = SeparatorMenuItem()

        // Region editing commands:
        val deleteMenuItem = MenuItem(ResourceHandler.Companion.delete)
        deleteMenuItem.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(actionManager.actionFactory.createDeleteAction(Region))
        }

        regionContextMenu!!.items.addAll(dataTransferToRegionEditingSeparator, deleteMenuItem)
        return regionContextMenu
    }
}
