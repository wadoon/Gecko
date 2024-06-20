package org.gecko.view.menubar

import javafx.beans.binding.Bindings
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.VBox
import org.gecko.actions.*
import org.gecko.application.GeckoIOManager
import org.gecko.io.*
import org.gecko.tools.*
import org.gecko.view.GeckoView
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.view.inspector.element.textfield.InspectorRenameField
import org.gecko.view.views.shortcuts.Shortcuts
import org.gecko.viewmodel.*
import java.io.File

/**
 * Represents a builder for the [MenuBar] displayed in the view, containing [MenuItem]s in [Menu]s
 * grouped by category. Holds a reference to the built [MenuBar], the current [GeckoView] and the
 * [ActionManager], which allow for actions to be run from the menu bar. Relevant menus for the Gecko Graphic
 * Editor are "File" (running operations like creating, saving, loading, importing and exporting files), "Edit" (running
 * operations like undoing and redoing actions, cutting, copying and pasting or selecting and deselecting all elements),
 * "View" (running operations like changing the view, opening the parent system or zooming in and out of the view),
 * "Tools" (providing the active tools which can be selected in the current view) and "Help" (running operations like
 * finding an element by name matches, opening a comprehensive list of all shortcuts available or reading more
 * information about Gecko).
 */
class MenuBarBuilder(val view: GeckoView, val actionManager: ActionManager) {
    val menuBar = MenuBar()

    init {
        menuBar.menus.addAll(setupFileMenu(), setupEditMenu(), setupViewMenu(), setupToolsMenu())
    }

    fun build(): MenuBar {
        return menuBar
    }

    fun setupFileMenu(): Menu {
        val fileMenu = Menu(ResourceHandler.file)

        val newFileItem = MenuItem(ResourceHandler.NEW)
        newFileItem.onAction =
            EventHandler { e: ActionEvent? -> GeckoIOManager.createNewProject() }
        newFileItem.accelerator = Shortcuts.NEW.get()

        val openFileItem = MenuItem(ResourceHandler.open)
        openFileItem.onAction =
            EventHandler { e: ActionEvent? -> GeckoIOManager.loadGeckoProject() }
        openFileItem.accelerator = Shortcuts.OPEN.get()

        val saveFileItem = MenuItem(ResourceHandler.save)
        saveFileItem.onAction = EventHandler { e: ActionEvent? ->
            val file: File? = GeckoIOManager.file
            if (file != null) {
                GeckoIOManager.saveGeckoProject(file)
            } else {
                val fileToSaveTo: File? = GeckoIOManager.getSaveFileChooser(FileTypes.JSON)
                if (fileToSaveTo != null) {
                    GeckoIOManager.saveGeckoProject(fileToSaveTo)
                    GeckoIOManager.file = fileToSaveTo
                }
            }
        }
        saveFileItem.accelerator = Shortcuts.SAVE.get()

        val saveAsFileItem = MenuItem(ResourceHandler.save_as)
        saveAsFileItem.onAction = EventHandler { e: ActionEvent? ->
            val fileToSaveTo = GeckoIOManager.getSaveFileChooser(FileTypes.JSON)!!
            if (fileToSaveTo != null) {
                GeckoIOManager.saveGeckoProject(fileToSaveTo)
            }
        }
        saveAsFileItem.accelerator = Shortcuts.SAVE_AS.get()

        val importFileItem = MenuItem(ResourceHandler.IMPORT)
        importFileItem.accelerator = KeyCodeCombination(KeyCode.I, KeyCombination.SHORTCUT_DOWN)
        importFileItem.onAction = EventHandler { e: ActionEvent? ->
            val fileToImport: File? = GeckoIOManager.getOpenFileChooser(
                FileTypes.SYS
            )
            if (fileToImport != null) {
                GeckoIOManager.importAutomatonFile(fileToImport)
            }
        }

        val exportFileItem = MenuItem(ResourceHandler.export)
        exportFileItem.accelerator = KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN)
        exportFileItem.onAction = EventHandler { e: ActionEvent? ->
            val fileToSaveTo: File? = GeckoIOManager.getSaveFileChooser(FileTypes.SYS)
            if (fileToSaveTo != null) {
                GeckoIOManager.exportAutomatonFile(fileToSaveTo)
            }
        }

        fileMenu.items
            .addAll(newFileItem, openFileItem, saveFileItem, saveAsFileItem, importFileItem, exportFileItem)
        return fileMenu
    }

    fun setupEditMenu(): Menu {
        val editMenu = Menu(ResourceHandler.edit)

        // Edit history navigation:
        val undoMenuItem = MenuItem(ResourceHandler.undo)
        undoMenuItem.onAction = EventHandler { e: ActionEvent? -> actionManager.undo() }
        undoMenuItem.accelerator = Shortcuts.UNDO.get()

        val redoMenuItem = MenuItem(ResourceHandler.redo)
        redoMenuItem.onAction = EventHandler { e: ActionEvent? -> actionManager.redo() }
        redoMenuItem.accelerator = Shortcuts.REDO.get()

        val historyToDataTransferSeparator = SeparatorMenuItem()

        // Data transfer commands:
        val cutMenuItem = MenuItem(ResourceHandler.cut)
        cutMenuItem.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createCutPositionableViewModelElementAction()
            )
        }
        cutMenuItem.accelerator = Shortcuts.CUT.get()

        val copyMenuItem = MenuItem(ResourceHandler.copy)
        copyMenuItem.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createCopyPositionableViewModelElementAction()
            )
        }
        copyMenuItem.accelerator = Shortcuts.COPY.get()

        val pasteMenuItem = MenuItem(ResourceHandler.paste)
        pasteMenuItem.onAction = EventHandler { e: ActionEvent? ->
            val center = view.currentView!!.viewElementPane.screenCenterWorldCoords()
            actionManager.run(actionManager.actionFactory.createPastePositionableViewModelElementAction(center))
        }
        pasteMenuItem.accelerator = Shortcuts.PASTE.get()

        // General selection commands:
        val selectAllMenuItem = MenuItem(ResourceHandler.select_all)
        selectAllMenuItem.onAction = EventHandler { e: ActionEvent? ->
            val allElements = view.allDisplayedElements
            actionManager.run(actionManager.actionFactory.createSelectAction(allElements, true))
        }
        selectAllMenuItem.accelerator = Shortcuts.SELECT_ALL.get()

        val deselectAllMenuItem = MenuItem(ResourceHandler.deselect_all)
        deselectAllMenuItem.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createDeselectAction()
            )
        }
        deselectAllMenuItem.accelerator = Shortcuts.DESELECT_ALL.get()

        val dataTransferToSelectionSeparator = SeparatorMenuItem()

        val renameRootSystemSeparator = SeparatorMenuItem()

        val renameRootSystemCustomMenuItem = renameRootSystemCustomMenuItem

        editMenu.items
            .addAll(
                undoMenuItem, redoMenuItem, historyToDataTransferSeparator, cutMenuItem, copyMenuItem,
                pasteMenuItem, dataTransferToSelectionSeparator, selectAllMenuItem, deselectAllMenuItem,
                renameRootSystemSeparator, renameRootSystemCustomMenuItem
            )

        return editMenu
    }

    val renameRootSystemCustomMenuItem: CustomMenuItem
        get() {
            val viewModel = view.viewModel
            val renameRootSystemTextField: TextField = InspectorRenameField(
                actionManager,
                viewModel.getViewModelElement(viewModel.geckoModel.root) as Renamable
            )
            val renameRootSystemLabel: Label = InspectorLabel(ResourceHandler.rename_root_system)
            val renameRootSystemContainer = VBox(renameRootSystemLabel, renameRootSystemTextField)
            val renameRootSystemCustomMenuItem = CustomMenuItem(renameRootSystemContainer, false)
            renameRootSystemCustomMenuItem.onAction =
                EventHandler { e: ActionEvent? -> renameRootSystemTextField.requestFocus() }
            return renameRootSystemCustomMenuItem
        }

    fun setupViewMenu(): Menu {
        val viewMenu = Menu(ResourceHandler.view)

        // View change commands:
        val changeViewMenuItem = MenuItem(ResourceHandler.change_view)
        changeViewMenuItem.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory
                    .createViewSwitchAction(
                        view.currentView!!.viewModel.currentSystem,
                        !view.currentView!!.viewModel.isAutomatonEditor
                    )
            )
        }
        changeViewMenuItem.accelerator = Shortcuts.SWITCH_EDITOR.get()

        val goToParentSystemMenuItem = MenuItem(ResourceHandler.go_to_parent_system)
        goToParentSystemMenuItem.onAction = EventHandler { e: ActionEvent? ->
            val isAutomatonEditor = view.currentView!!.viewModel.isAutomatonEditor
            val parentSystem = view.currentView!!.viewModel.parentSystem
            actionManager.run(actionManager.actionFactory.createViewSwitchAction(parentSystem, isAutomatonEditor))
        }
        goToParentSystemMenuItem.accelerator = Shortcuts.OPEN_PARENT_SYSTEM_EDITOR.get()

        val focusSelectedElementMenuItem =
            MenuItem(ResourceHandler.focus_selected_element)
        focusSelectedElementMenuItem.onAction =
            EventHandler { e: ActionEvent? -> view.currentView!!.viewModel.moveToFocusedElement() }
        focusSelectedElementMenuItem.accelerator = Shortcuts.FOCUS_SELECTED_ELEMENT.get()

        val viewSwitchToZoomSeparator = SeparatorMenuItem()

        // Zooming commands:
        val zoomInMenuItem = MenuItem(ResourceHandler.zoom_in)
        zoomInMenuItem.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createZoomCenterAction(EditorViewModel.defaultZoomStep)
            )
        }
        zoomInMenuItem.accelerator = Shortcuts.ZOOM_IN.get()

        val zoomOutMenuItem = MenuItem(ResourceHandler.zoom_out)
        zoomOutMenuItem.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(actionManager.actionFactory.createZoomCenterAction(1 / EditorViewModel.defaultZoomStep))
        }
        zoomOutMenuItem.accelerator = Shortcuts.ZOOM_OUT.get()

        val zoomToAppearanceSeparator = SeparatorMenuItem()

        val toggleAppearanceMenuItem = MenuItem(ResourceHandler.toggle_appearance)
        toggleAppearanceMenuItem.onAction = EventHandler { e: ActionEvent? -> view.toggleAppearance() }
        toggleAppearanceMenuItem.accelerator = Shortcuts.TOGGLE_APPEARANCE.get()

        val searchElementsMenuItem = MenuItem(ResourceHandler.search_elements)
        searchElementsMenuItem.onAction = EventHandler { e: ActionEvent? -> view.currentView!!.toggleSearchWindow() }
        searchElementsMenuItem.accelerator = Shortcuts.TOGGLE_SEARCH.get()

        viewMenu.items
            .addAll(
                changeViewMenuItem, goToParentSystemMenuItem, focusSelectedElementMenuItem,
                viewSwitchToZoomSeparator, zoomInMenuItem, zoomOutMenuItem, zoomToAppearanceSeparator,
                toggleAppearanceMenuItem, searchElementsMenuItem
            )

        return viewMenu
    }

    fun setupToolsMenu(): Menu {
        val toolsMenu = Menu(ResourceHandler.tools)

        // General tools:
        val cursorMenuItem = MenuItem(ToolType.CURSOR.label)
        cursorMenuItem.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createSelectToolAction(ToolType.CURSOR)
            )
        }
        cursorMenuItem.accelerator = Shortcuts.CURSOR_TOOL.get()

        val marqueeMenuItem = MenuItem(ToolType.MARQUEE_TOOL.label)
        marqueeMenuItem.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createSelectToolAction(ToolType.MARQUEE_TOOL)
            )
        }
        marqueeMenuItem.accelerator = Shortcuts.MARQUEE_TOOL.get()

        val panMenuItem = MenuItem(ToolType.PAN.label)
        panMenuItem.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createSelectToolAction(ToolType.PAN)
            )
        }
        panMenuItem.accelerator = Shortcuts.PAN_TOOL.get()

        val generalFromSystemSeparator = SeparatorMenuItem()

        // System view tools:
        val systemCreatorMenuItem = toolMenuItem(ToolType.SYSTEM_CREATOR, false)
        val systemConnectionCreatorMenuItem = toolMenuItem(ToolType.CONNECTION_CREATOR, false)
        val variableBlockCreatorMenuItem = toolMenuItem(ToolType.VARIABLE_BLOCK_CREATOR, false)

        val systemFroAutomatonSeparator = SeparatorMenuItem()

        // Automaton view tools:
        val stateCreatorMenuItem = toolMenuItem(ToolType.STATE_CREATOR, true)
        val edgeCreatorMenuItem = toolMenuItem(ToolType.EDGE_CREATOR, true)
        val regionCreatorMenuItem = toolMenuItem(ToolType.REGION_CREATOR, true)
        toolsMenu.items
            .addAll(
                cursorMenuItem, marqueeMenuItem, panMenuItem, generalFromSystemSeparator, systemCreatorMenuItem,
                systemConnectionCreatorMenuItem, variableBlockCreatorMenuItem, systemFroAutomatonSeparator,
                stateCreatorMenuItem, edgeCreatorMenuItem, regionCreatorMenuItem
            )

        return toolsMenu
    }

    fun toolMenuItem(toolType: ToolType, isAutomatonTool: Boolean): MenuItem {
        val toolMenuItem = MenuItem(toolType.label)
        toolMenuItem.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createSelectToolAction(toolType)
            )
        }
        toolMenuItem.disableProperty().bind(Bindings.createBooleanBinding({
            if (view.currentView == null) {
                return@createBooleanBinding true
            }
            view.currentView!!.viewModel.isAutomatonEditor != isAutomatonTool
        }, view.currentViewProperty))
        return toolMenuItem
    }
}
