package org.gecko.view.contextmenu


import javafx.beans.binding.Bindings
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import org.gecko.actions.ActionManager
import org.gecko.view.GeckoView
import org.gecko.view.ResourceHandler
import org.gecko.view.views.EditorView
import org.gecko.view.views.shortcuts.Shortcuts
import org.gecko.viewmodel.EditorViewModel

/**
 * Represents a builder for a general purpose [ContextMenu] in the view, containing [MenuItem]s that run the
 * cut, copy, paste, select all and deselect all operations. Holds therefore a reference to the [ActionManager]
 * and to the built [ContextMenu].
 */
open class ViewContextMenuBuilder {
    protected val actionManager: ActionManager


    protected var editorViewModel: EditorViewModel?


    var contextMenu: ContextMenu? = null
    val editorView: EditorView?

    constructor(actionManager: ActionManager, geckoView: GeckoView) {
        this.actionManager = actionManager
        this.editorViewModel = null
        this.editorView = geckoView.currentView
    }

    constructor(actionManager: ActionManager, editorViewModel: EditorViewModel?, editorView: EditorView?) {
        this.actionManager = actionManager
        this.editorViewModel = editorViewModel
        this.editorView = editorView
    }

    open fun build(): ContextMenu? {
        val contextMenu = ContextMenu()

        // Data transfer commands:
        val cutMenuItem = MenuItem(ResourceHandler.cut)
        cutMenuItem.onAction = EventHandler { e: ActionEvent? ->
            //actionManager.run(actionManager.actionFactory.createCopyPositionableViewModelElementAction())
            actionManager.run(actionManager.actionFactory.createDeletePositionableViewModelElementAction())
        }
        cutMenuItem.accelerator = Shortcuts.CUT.get()
        if (editorViewModel != null) {
            cutMenuItem.disableProperty()
                .bind(
                    Bindings.createBooleanBinding(
                        { editorViewModel!!.selectionManager.currentSelection.isEmpty() },
                        editorViewModel!!.selectionManager.currentSelectionProperty
                    )
                )
        }

        val copyMenuItem = MenuItem(ResourceHandler.copy)
        copyMenuItem.onAction = EventHandler { e: ActionEvent? ->
            TODO()
            //    actionManager.run(actionManager.actionFactory.createCopyPositionableViewModelElementAction())
        }
        copyMenuItem.accelerator = Shortcuts.COPY.get()
        if (editorViewModel != null) {
            copyMenuItem.disableProperty()
                .bind(
                    Bindings.createBooleanBinding(
                        { editorViewModel!!.selectionManager.currentSelection.isEmpty() },
                        editorViewModel!!.selectionManager.currentSelectionProperty
                    )
                )
        }

        val pasteMenuItem = MenuItem(ResourceHandler.paste)
        pasteMenuItem.onAction = EventHandler { e: ActionEvent? ->
            val center = editorView!!.viewElementPane.screenCenterWorldCoords()
            //actionManager.run(actionManager.actionFactory.createPastePositionableViewModelElementAction(center))
            TODO()
        }
        pasteMenuItem.accelerator = Shortcuts.PASTE.get()

        val separatorMenuItem = SeparatorMenuItem()
        separatorMenuItem.text = ""

        val selectMenuItem = MenuItem(ResourceHandler.select_all)
        selectMenuItem.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory
                    .createSelectAction(editorViewModel!!.positionableViewModelElements, true)
            )
        }
        selectMenuItem.accelerator = Shortcuts.SELECT_ALL.get()
        if (editorViewModel != null) {
            selectMenuItem.disableProperty()
                .bind(
                    Bindings.createBooleanBinding(
                        { editorViewModel!!.containedPositionableViewModelElementsProperty.isEmpty() },
                        editorViewModel!!.containedPositionableViewModelElementsProperty
                    )
                )
        }

        val deselectMenuItem = MenuItem(ResourceHandler.deselect_all)
        deselectMenuItem.onAction =
            EventHandler { e: ActionEvent? -> actionManager.run(actionManager.actionFactory.createDeselectAction()) }
        deselectMenuItem.accelerator = Shortcuts.DESELECT_ALL.get()
        if (editorViewModel != null) {
            deselectMenuItem.disableProperty()
                .bind(
                    Bindings.createBooleanBinding(
                        { editorViewModel!!.selectionManager.currentSelection.isEmpty() },
                        editorViewModel!!.selectionManager.currentSelectionProperty
                    )
                )
        }

        contextMenu.items
            .addAll(cutMenuItem, copyMenuItem, pasteMenuItem, separatorMenuItem, selectMenuItem, deselectMenuItem)

        this.contextMenu = contextMenu
        return contextMenu
    }
}
