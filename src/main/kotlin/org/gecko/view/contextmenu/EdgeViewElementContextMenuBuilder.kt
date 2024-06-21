package org.gecko.view.contextmenu

import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import org.gecko.actions.ActionManager
import org.gecko.view.GeckoView
import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.EdgeViewModel
import org.gecko.viewmodel.Kind

/**
 * Represents a type of [ViewContextMenuBuilder] for a [ContextMenu] specific to an
 * [EdgeViewElement][org.gecko.view.views.viewelement.EdgeViewElement]. Contains [MenuItem]s that run
 * operations like changing the edge's kind or deleting the edge.
 */
class EdgeViewElementContextMenuBuilder(
    actionManager: ActionManager,
    val edgeViewModel: EdgeViewModel,
    geckoView: GeckoView
) : ViewContextMenuBuilder(actionManager, geckoView) {
    override fun build(): ContextMenu {
        val edgeContextMenu = super.build()

        val dataTransferToEdgeEditingSeparator = SeparatorMenuItem()

        // Edge editing commands:
        val changeKindMenu = Menu(ResourceHandler.change_kind)

        for (kind in Kind.entries) {
            val kindMenuItem = createKindMenuItem(kind)

            if (edgeViewModel.kind == kind) {
                kindMenuItem.isDisable = true
            }

            changeKindMenu.items.add(kindMenuItem)
        }

        val deleteMenuItem = MenuItem(ResourceHandler.delete)
        deleteMenuItem.onAction = EventHandler {
            actionManager.run(
                actionManager.actionFactory.createDeletePositionableViewModelElementAction(edgeViewModel)
            )
        }

        edgeContextMenu!!.items.addAll(dataTransferToEdgeEditingSeparator, changeKindMenu, deleteMenuItem)
        return edgeContextMenu
    }

    fun createKindMenuItem(kind: Kind): MenuItem {
        val kindMenuItem = MenuItem(kind.name)
        kindMenuItem.onAction = EventHandler {
            actionManager.run(
                actionManager.actionFactory.createChangeKindAction(
                    edgeViewModel, kind
                )
            )
        }
        return kindMenuItem
    }
}
