package org.gecko.view.menubar

import com.pixelduke.control.Ribbon
import com.pixelduke.control.ribbon.Column
import com.pixelduke.control.ribbon.RibbonGroup
import com.pixelduke.control.ribbon.RibbonTab
import javafx.beans.binding.Bindings
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import org.gecko.actions.*
import org.gecko.application.GeckoIOManager
import org.gecko.io.*
import org.gecko.tools.*
import org.gecko.view.GeckoView
import org.gecko.view.ResourceHandler
import org.gecko.view.views.shortcuts.Shortcuts
import org.gecko.viewmodel.EditorViewModel
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign2.*
import java.io.File

/**
 * Represents a builder for the [MenuBar] displayed in the view, containing [Button]s in [Menu]s
 * grouped by category. Holds a reference to the built [MenuBar], the current [GeckoView] and the
 * [ActionManager], which allow for actions to be run from the menu bar. Relevant menus for the Gecko Graphic
 * Editor are "File" (running operations like creating, saving, loading, importing and exporting files), "Edit" (running
 * operations like undoing and redoing actions, cutting, copying and pasting or selecting and deselecting all elements),
 * "View" (running operations like changing the view, opening the parent system or zooming in and out of the view),
 * "Tools" (providing the active tools which can be selected in the current view) and "Help" (running operations like
 * finding an element by name matches, opening a comprehensive list of all shortcuts available or reading more
 * information about Gecko).
 */
class RibbonBuilder(val view: GeckoView, val actionManager: ActionManager) {
    val menuBar = Ribbon()

    init {
        val tab = RibbonTab("Home")
        tab.ribbonGroups.setAll(setupFileMenu(), setupCagen(), setupEditMenu(), setupZoom(), setupViewMenu())
        val tab2 = RibbonTab("Tools")
        tab2.ribbonGroups.setAll(setupToolsMenu())
        menuBar.tabs.setAll(tab, tab2)

        /*final var quick = new QuickAccessBar();
        quick.getRightButtons().add(
            new Button("save")
        menuBar.setQuickAccessBar(quick);
        );
        */
    }

    fun setupZoom(): RibbonGroup {
        val g = RibbonGroup()
        g.title = "Zoom"
        // Zooming commands:
        val zoomInButton = createBigButton(null, MaterialDesignM.MAGNIFY_PLUS)
        zoomInButton.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createZoomCenterAction(EditorViewModel.defaultZoomStep)
            )
        }
        view.addMnemonic(zoomInButton, Shortcuts.ZOOM_IN.get())

        val zoomOutButton = createBigButton(null, MaterialDesignM.MAGNIFY_MINUS)
        zoomOutButton.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createZoomCenterAction(1 / EditorViewModel.defaultZoomStep)
            )
        }
        view.addMnemonic(zoomOutButton, Shortcuts.ZOOM_OUT.get())
        g.nodes.addAll(zoomInButton, zoomOutButton)
        return g
    }

    fun build(): Ribbon {
        return menuBar
    }

    fun createBigButton(s: String?, icon: Ikon?): Button {
        val ico = if (icon == null) null else FontIcon.of(icon, 32)
        val b = Button(s, ico)
        b.contentDisplay = ContentDisplay.TOP
        if (s == null) b.contentDisplay = ContentDisplay.GRAPHIC_ONLY
        b.styleClass.add("big")
        b.isWrapText = true
        b.style = "-fx-font-size:80%;"
        return b
    }

    fun createColButton(s: String?, icon: Ikon?): Button {
        val ico = if (icon == null) null else FontIcon.of(icon, 16)
        val b = Button(s, ico)
        b.contentDisplay = ContentDisplay.LEFT
        b.styleClass.add("normal")
        b.isWrapText = false
        b.style = "-fx-font-size:80%;"
        return b
    }

    fun setupFileMenu(): RibbonGroup {
        val fileMenu = RibbonGroup()
        fileMenu.title = ResourceHandler.file
        val newFileItem = createBigButton("new", MaterialDesignF.FILE)
        newFileItem.onAction =
            EventHandler { e: ActionEvent? -> GeckoIOManager.createNewProject() }
        view.addMnemonic(newFileItem, Shortcuts.NEW.get())

        val openFileItem = createBigButton("open", MaterialDesignF.FOLDER_OPEN_OUTLINE)
        openFileItem.onAction =
            EventHandler { e: ActionEvent? -> GeckoIOManager.loadGeckoProject() }
        view.addMnemonic(openFileItem, Shortcuts.OPEN.get())

        val saveFileItem = createBigButton("save", MaterialDesignC.CONTENT_SAVE)
        saveFileItem.contentDisplay = ContentDisplay.TOP
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
        view.addMnemonic(saveFileItem, Shortcuts.SAVE.get())

        val saveAsFileItem = createBigButton("save_as", MaterialDesignC.CONTENT_SAVE_ALL)
        saveAsFileItem.onAction = EventHandler { e: ActionEvent? ->
            val fileToSaveTo: File? = GeckoIOManager.getSaveFileChooser(
                FileTypes.JSON
            )
            if (fileToSaveTo != null) {
                GeckoIOManager.saveGeckoProject(fileToSaveTo)
            }
        }
        view.addMnemonic(saveAsFileItem, Shortcuts.SAVE_AS.get())
        fileMenu.nodes.addAll(newFileItem, openFileItem, saveFileItem, saveAsFileItem)
        return fileMenu
    }

    fun setupCagen(): RibbonGroup {
        val g = RibbonGroup()
        g.title = "CAGEN"
        val importFileItem = createColButton("import", MaterialDesignF.FILE_IMPORT)
        view.addMnemonic(importFileItem, KeyCodeCombination(KeyCode.I, KeyCombination.SHORTCUT_DOWN))
        importFileItem.onAction = EventHandler { _: ActionEvent? ->
            val fileToImport: File? = GeckoIOManager.getOpenFileChooser(FileTypes.SYS)
            if (fileToImport != null) {
                GeckoIOManager.importAutomatonFile(fileToImport)
            }
        }

        val exportFileItem = createColButton("export", MaterialDesignF.FILE_EXPORT)
        view.addMnemonic(exportFileItem, KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN))
        exportFileItem.onAction = EventHandler { e: ActionEvent? ->
            val fileToSaveTo: File? = GeckoIOManager.getSaveFileChooser(
                FileTypes.SYS
            )
            if (fileToSaveTo != null) {
                GeckoIOManager.exportAutomatonFile(fileToSaveTo)
            }
        }
        g.nodes.setAll(
            createColumn(importFileItem, exportFileItem)
        )
        return g
    }

    fun createColumn(vararg nodes: Node): Column {
        val c = Column()
        c.children.addAll(*nodes)
        return c
    }

    fun setupEditMenu(): RibbonGroup {
        val editMenu = RibbonGroup() //ResourceHandler.edit);

        // Edit history navigation:
        val undoButton = createBigButton("undo", MaterialDesignU.UNDO)
        undoButton.onAction = EventHandler { e: ActionEvent? -> actionManager.undo() }
        view.addMnemonic(undoButton, Shortcuts.UNDO.get())

        val redoButton = createBigButton("redo", MaterialDesignR.REDO)
        undoButton.contentDisplay = ContentDisplay.TOP

        redoButton.onAction = EventHandler { e: ActionEvent? -> actionManager.redo() }
        view.addMnemonic(redoButton, Shortcuts.REDO.get())

        //SeparatorButton historyToDataTransferSeparator = new SeparatorButton();

        // Data transfer commands:
        val cutButton = createColButton("cut", MaterialDesignC.CONTENT_CUT)
        cutButton.contentDisplay = ContentDisplay.LEFT
        cutButton.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createCutPositionableViewModelElementAction()
            )
        }
        view.addMnemonic(cutButton, Shortcuts.CUT.get())

        val copyButton = createColButton("copy", MaterialDesignC.CONTENT_COPY)
        copyButton.contentDisplay = ContentDisplay.LEFT
        copyButton.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createCopyPositionableViewModelElementAction()
            )
        }
        view.addMnemonic(copyButton, Shortcuts.COPY.get())

        val pasteButton = createColButton("paste", MaterialDesignC.CONTENT_PASTE)
        pasteButton.contentDisplay = ContentDisplay.LEFT
        pasteButton.onAction = EventHandler { e: ActionEvent? ->
            val center = view.currentView!!.viewElementPane.screenCenterWorldCoords()
            actionManager.run(actionManager.actionFactory.createPastePositionableViewModelElementAction(center))
        }
        view.addMnemonic(pasteButton, Shortcuts.PASTE.get())

        // General selection commands:
        val selectAllButton = createColButton("select_all", MaterialDesignS.SELECT_ALL)
        selectAllButton.onAction = EventHandler { e: ActionEvent? ->
            val allElements = view.allDisplayedElements
            actionManager.run(actionManager.actionFactory.createSelectAction(allElements, true))
        }
        view.addMnemonic(selectAllButton, Shortcuts.SELECT_ALL.get())

        val deselectAllButton = createColButton("deselect_all", MaterialDesignS.SELECTION_OFF)
        deselectAllButton.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createDeselectAction()
            )
        }
        view.addMnemonic(deselectAllButton, Shortcuts.DESELECT_ALL.get())

        // renameRootSystemCustomButton = getRenameRootSystemCustomButton();
        val c1 = createColumn(cutButton, copyButton, pasteButton)
        val c2 = createColumn(selectAllButton, deselectAllButton)
        editMenu.nodes.addAll(undoButton, redoButton, c1, c2)
        return editMenu
    }

    fun setupViewMenu(): RibbonGroup {
        val viewMenu = RibbonGroup() // ResourceHandler.view);

        // View change commands:
        val changeViewButton = createColButton("change_view", MaterialDesignT.TOGGLE_SWITCH)
        changeViewButton.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory
                    .createViewSwitchAction(
                        view.currentView!!.viewModel.currentSystem,
                        !view.currentView!!.viewModel.isAutomatonEditor
                    )
            )
        }
        view.addMnemonic(changeViewButton, Shortcuts.SWITCH_EDITOR.get())

        val goToParentSystemButton = createColButton("go_to_parent_system", MaterialDesignA.ARROW_UP)
        goToParentSystemButton.onAction = EventHandler { e: ActionEvent? ->
            val isAutomatonEditor = view.currentView!!.viewModel.isAutomatonEditor
            val parentSystem = view.currentView!!.viewModel.parentSystem
            actionManager.run(actionManager.actionFactory.createViewSwitchAction(parentSystem, isAutomatonEditor))
        }
        view.addMnemonic(goToParentSystemButton, Shortcuts.OPEN_PARENT_SYSTEM_EDITOR.get())

        val focusSelectedElementButton =
            createColButton("focus_selected_element", MaterialDesignF.FOCUS_AUTO)
        focusSelectedElementButton.onAction =
            EventHandler { e: ActionEvent? -> view.currentView!!.viewModel.moveToFocusedElement() }
        view.addMnemonic(focusSelectedElementButton, Shortcuts.FOCUS_SELECTED_ELEMENT.get())


        //SeparatorButton viewSwitchToZoomSeparator = new SeparatorButton();


        //SeparatorButton zoomToAppearanceSeparator = new SeparatorButton();
        val toggleAppearanceButton = createColButton("toggle_appearance", MaterialDesignT.THEME_LIGHT_DARK)
        toggleAppearanceButton.onAction = EventHandler { e: ActionEvent? -> view.toggleAppearance() }
        view.addMnemonic(toggleAppearanceButton, Shortcuts.TOGGLE_APPEARANCE.get())

        val searchElementsButton = createColButton("search_elements", MaterialDesignT.TEXT_SEARCH)
        searchElementsButton.onAction = EventHandler { e: ActionEvent? -> view.currentView!!.toggleSearchWindow() }
        view.addMnemonic(searchElementsButton, Shortcuts.TOGGLE_SEARCH.get())

        val c1 = Column()
        c1.children.addAll(changeViewButton, goToParentSystemButton, focusSelectedElementButton)
        val c2 = Column()
        c2.children.addAll(toggleAppearanceButton, searchElementsButton)
        viewMenu.nodes.addAll(c1, c2)


        return viewMenu
    }

    fun setupToolsMenu(): RibbonGroup {
        val toolsMenu = RibbonGroup() // (ResourceHandler.tools);

        // General tools:
        val cursorButton = Button(ToolType.CURSOR.label)
        cursorButton.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createSelectToolAction(ToolType.CURSOR)
            )
        }
        view.addMnemonic(cursorButton, Shortcuts.CURSOR_TOOL.get())

        val marqueeButton = Button(ToolType.MARQUEE_TOOL.label)
        marqueeButton.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createSelectToolAction(ToolType.MARQUEE_TOOL)
            )
        }
        view.addMnemonic(marqueeButton, Shortcuts.MARQUEE_TOOL.get())

        val panButton = Button(ToolType.PAN.label)
        panButton.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createSelectToolAction(ToolType.PAN)
            )
        }
        view.addMnemonic(panButton, Shortcuts.PAN_TOOL.get())

        //SeparatorButton generalFromSystemSeparator = new SeparatorButton();

        // System view tools:
        val systemCreatorButton = toolButton(ToolType.SYSTEM_CREATOR, false)
        val systemConnectionCreatorButton = toolButton(ToolType.CONNECTION_CREATOR, false)
        val variableBlockCreatorButton = toolButton(ToolType.VARIABLE_BLOCK_CREATOR, false)

        //SeparatorButton systemFroAutomatonSeparator = new SeparatorButton();

        // Automaton view tools:
        val stateCreatorButton = toolButton(ToolType.STATE_CREATOR, true)
        val edgeCreatorButton = toolButton(ToolType.EDGE_CREATOR, true)
        val regionCreatorButton = toolButton(ToolType.REGION_CREATOR, true)
        toolsMenu.nodes
            .addAll(
                cursorButton, marqueeButton, panButton, systemCreatorButton,
                systemConnectionCreatorButton, variableBlockCreatorButton,
                stateCreatorButton, edgeCreatorButton, regionCreatorButton
            )

        return toolsMenu
    }

    fun toolButton(toolType: ToolType, isAutomatonTool: Boolean): Button {
        val toolButton = Button(toolType.label)
        toolButton.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createSelectToolAction(toolType)
            )
        }
        toolButton.disableProperty().bind(Bindings.createBooleanBinding({
            if (view.currentView == null) {
                return@createBooleanBinding true
            }
            view.currentView!!.viewModel.isAutomatonEditor != isAutomatonTool
        }, view.currentViewProperty))
        return toolButton
    }
}
