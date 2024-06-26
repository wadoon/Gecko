package org.gecko.view.contextmenu

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import org.gecko.actions.ActionManager
import org.gecko.view.GeckoView
import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.State

/**
 * Represents a type of [ViewContextMenuBuilder] for a [ContextMenu] specific to a
 * [StateViewElement][org.gecko.view.views.viewelement.StateViewElement]. Contains [MenuItem]s that
 * run operations like setting the state as start-state or deleting the state.
 */
class StateViewElementContextMenuBuilder(
    actionManager: ActionManager,
    val state: State,
    geckoView: GeckoView
) : ViewContextMenuBuilder(actionManager, geckoView) {
    override fun build(): ContextMenu {
        val stateContextMenu = super.build()

        val dataTransferToStateEditingSeparator = SeparatorMenuItem()

        // State editing commands:
        val startStateMenuItem = MenuItem(ResourceHandler.Companion.set_start_state)
        startStateMenuItem.isDisable = state.isStartState
        startStateMenuItem.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createSetStartStateViewModelElementAction(state)
            )
        }

        val deleteMenuItem = MenuItem(ResourceHandler.Companion.delete)
        deleteMenuItem.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(actionManager.actionFactory.createDeleteAction(state))
        }

        stateContextMenu!!
            .items
            .addAll(dataTransferToStateEditingSeparator, startStateMenuItem, deleteMenuItem)
        return stateContextMenu
    }
}
