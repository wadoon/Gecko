package org.gecko.view.contextmenu

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import org.gecko.actions.ActionManager
import org.gecko.view.GeckoView
import org.gecko.view.ResourceHandler
import org.gecko.view.views.shortcuts.Shortcuts
import org.gecko.viewmodel.SystemViewModel

/**
 * Represents a type of [ViewContextMenuBuilder] for a [ContextMenu] specific to a
 * [SystemViewElement][org.gecko.view.views.viewelement.SystemViewElement]. Contains [MenuItem]s that run
 * operations like opening or deleting the system.
 */
class SystemViewElementContextMenuBuilder(
    actionManager: ActionManager,
    val systemViewModel: SystemViewModel,
    geckoView: GeckoView
) : ViewContextMenuBuilder(actionManager, geckoView) {
    override fun build(): ContextMenu {
        val systemContextMenu = super.build()

        val dataTransferToSystemAccessSeparator = SeparatorMenuItem()

        // Access system commands:
        val openSystemMenuItem = MenuItem(ResourceHandler.Companion.open_system)
        openSystemMenuItem.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createViewSwitchAction(
                    systemViewModel, false
                )
            )
        }
        openSystemMenuItem.accelerator = Shortcuts.OPEN_CHILD_SYSTEM_EDITOR.get()

        val deleteMenuItem = MenuItem(ResourceHandler.Companion.delete)
        deleteMenuItem.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createDeletePositionableViewModelElementAction(systemViewModel)
            )
        }


        systemContextMenu!!.items.addAll(dataTransferToSystemAccessSeparator, openSystemMenuItem, deleteMenuItem)
        return systemContextMenu
    }
}
