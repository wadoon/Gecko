package org.gecko.view.menubar

import javafx.beans.binding.Bindings
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.gecko.MappedList
import org.gecko.actions.ActionManager
import org.gecko.application.GeckoIOManager
import org.gecko.io.FileTypes
import org.gecko.tools.ToolType
import org.gecko.view.GeckoView
import org.gecko.view.views.shortcuts.Shortcuts
import org.gecko.viewmodel.EditorViewModel
import org.gecko.viewmodel.PositionableViewModelElement
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
class ToolbarBuilder(view: GeckoView, private val actionManager: ActionManager) {
    private val menuBar = ToolBar()
    private val view: GeckoView = view

    init {
        menuBar.items.setAll(
            setupFileMenu(), setupCagen(), setupEditMenu(),
            setupVersionManagment(),
            setupZoom(), setupViewMenu()
        )

        //tab2.getRibbonGroups().setAll(setupToolsMenu());
        //menuBar.getTabs().setAll(tab, tab2);
    }

    private fun setupVersionManagment(): VBox {
        val btnVMgr = createColButton("Variants Manager", null)
        btnVMgr.onAction = EventHandler { it: ActionEvent? -> view.showVersionManager() }

        val btnActivateVariants = MenuButton("Activate Variants")
        val vg = view.viewModel.geckoModel.knownVariantGroupsProperty
        val mvg = MappedList(vg) { v ->
            val m = Menu(v.name)
            m.items.setAll(v.variants.stream().map { CheckMenuItem() }.toList())
            m
        }
        btnActivateVariants.items.setAll(mvg)
        return createColumn(btnVMgr, btnActivateVariants)
    }

    private fun setupZoom(): HBox {
        val g = HBox()
        //g.setTitle("Zoom");
        // Zooming commands:
        val zoomInButton = createBigButton(null, MaterialDesignM.MAGNIFY_PLUS)
        zoomInButton.onAction = EventHandler { _: ActionEvent? ->
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
        g.children.addAll(zoomInButton, zoomOutButton)
        return g
    }

    fun build(): ToolBar {
        return menuBar
    }

    fun createBigButton(txt: String?, icon: Ikon?): Button {
        val ico: FontIcon? = if (icon == null) null else FontIcon.of(icon, 32)
        val b = Button(txt, ico)
        b.contentDisplay = ContentDisplay.TOP
        if (txt == null) b.contentDisplay = ContentDisplay.GRAPHIC_ONLY
        b.styleClass.add("big")
        b.isWrapText = true
        b.style = "-fx-font-size:80%;"
        return b
    }

    fun createColButton(s: String?, icon: Ikon?): Button {
        val ico: FontIcon? = if (icon == null) null else FontIcon.of(icon, 16)
        val b = Button(s, ico)
        b.contentDisplay = ContentDisplay.LEFT
        b.styleClass.add("normal")
        b.isWrapText = false
        b.style = "-fx-font-size:80%;"
        return b
    }

    private fun setupFileMenu(): HBox {
        val fileMenu = HBox()

        //fileMenu.setTitle(ResourceHandler.getString("Labels", "file"));
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
                val fileToSaveTo = GeckoIOManager.getSaveFileChooser(FileTypes.JSON)
                if (fileToSaveTo != null) {
                    GeckoIOManager.saveGeckoProject(fileToSaveTo)
                    GeckoIOManager.file = (fileToSaveTo)
                }
            }
        }
        view.addMnemonic(saveFileItem, Shortcuts.SAVE.get())

        val saveAsFileItem = createBigButton("save_as", MaterialDesignC.CONTENT_SAVE_ALL)
        saveAsFileItem.onAction = EventHandler { e: ActionEvent? ->
            val fileToSaveTo = GeckoIOManager.getSaveFileChooser(FileTypes.JSON)
            if (fileToSaveTo != null) {
                GeckoIOManager.saveGeckoProject(fileToSaveTo)
            }
        }
        view.addMnemonic(saveAsFileItem, Shortcuts.SAVE_AS.get())
        fileMenu.children.addAll(newFileItem, openFileItem, saveFileItem, saveAsFileItem)
        return fileMenu
    }

    private fun setupCagen(): HBox {
        val g = HBox()
        //g.setTitle("CAGEN");
        val importFileItem = createColButton("import", MaterialDesignF.FILE_IMPORT)
        view.addMnemonic(importFileItem, KeyCodeCombination(KeyCode.I, KeyCombination.SHORTCUT_DOWN))
        importFileItem.onAction = EventHandler { e: ActionEvent? ->
            val fileToImport = GeckoIOManager.getOpenFileChooser(FileTypes.SYS)
            if (fileToImport != null) {
                GeckoIOManager.importAutomatonFile(fileToImport)
            }
        }

        val exportFileItem = createColButton("export", MaterialDesignF.FILE_EXPORT)
        view.addMnemonic(exportFileItem, KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN))
        exportFileItem.onAction = EventHandler { e: ActionEvent? ->
            val fileToSaveTo = GeckoIOManager.getSaveFileChooser(FileTypes.SYS)
            if (fileToSaveTo != null) {
                GeckoIOManager.exportAutomatonFile(fileToSaveTo)
            }
        }
        g.children.setAll(createColumn(importFileItem, exportFileItem))
        return g
    }

    private fun createColumn(vararg nodes: Node): VBox {
        val c = VBox()
        c.children.addAll(*nodes)
        return c
    }

    private fun setupEditMenu(): HBox {
        val editMenu = HBox() //ResourceHandler.getString("Labels", "edit"));

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
        cutButton.onAction =
            EventHandler { e: ActionEvent? -> actionManager.run(actionManager.actionFactory.createCutPositionableViewModelElementAction()) }
        view.addMnemonic(cutButton, Shortcuts.CUT.get())

        val copyButton = createColButton("copy", MaterialDesignC.CONTENT_COPY)
        copyButton.contentDisplay = ContentDisplay.LEFT
        copyButton.onAction =
            EventHandler { e: ActionEvent? -> actionManager.run(actionManager.actionFactory.createCopyPositionableViewModelElementAction()) }
        view.addMnemonic(copyButton, Shortcuts.COPY.get())

        val pasteButton = createColButton("paste", MaterialDesignC.CONTENT_PASTE)
        pasteButton.contentDisplay = ContentDisplay.LEFT
        pasteButton.onAction = EventHandler { e: ActionEvent? ->
            val center: Point2D = view.currentView!!.viewElementPane.screenCenterWorldCoords()
            actionManager.run(actionManager.actionFactory.createPastePositionableViewModelElementAction(center))
        }
        view.addMnemonic(pasteButton, Shortcuts.PASTE.get())

        // General selection commands:
        val selectAllButton = createColButton("select_all", MaterialDesignS.SELECT_ALL)
        selectAllButton.onAction = EventHandler { e: ActionEvent? ->
            val allElements: Set<PositionableViewModelElement<*>> = view.allDisplayedElements
            actionManager.run(actionManager.actionFactory.createSelectAction(allElements, true))
        }
        view.addMnemonic(selectAllButton, Shortcuts.SELECT_ALL.get())

        val deselectAllButton = createColButton("deselect_all", MaterialDesignS.SELECTION_OFF)
        deselectAllButton.onAction =
            EventHandler { e: ActionEvent? -> actionManager.run(actionManager.actionFactory.createDeselectAction()) }
        view.addMnemonic(deselectAllButton, Shortcuts.DESELECT_ALL.get())

        // renameRootSystemCustomButton = getRenameRootSystemCustomButton();
        val c1 = createColumn(cutButton, copyButton, pasteButton)
        val c2 = createColumn(selectAllButton, deselectAllButton)
        editMenu.children.addAll(undoButton, redoButton, c1, c2)
        return editMenu
    }

    /*private CustomButton getRenameRootSystemCustomButton() {
        GeckoViewModel viewModel = view.getViewModel();
        TextField renameRootSystemTextField = new InspectorRenameField(actionManager,
                (Renamable) viewModel.getViewModelElement(viewModel.getGeckoModel().getRoot()));
        Label renameRootSystemLabel = new InspectorLabel(ResourceHandler.getString("Inspector", "rename_root_system"));
        VBox renameRootSystemContainer = new VBox(renameRootSystemLabel, renameRootSystemTextField);
        CustomButton renameRootSystemCustomButton = new CustomButton(renameRootSystemContainer, false);
        renameRootSystemCustomButton.setOnAction(e -> renameRootSystemTextField.requestFocus());
        return renameRootSystemCustomButton;
    }*/
    private fun setupViewMenu(): HBox {
        val viewMenu = HBox() // ResourceHandler.getString("Labels", "view"));

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
            val isAutomatonEditor: Boolean = view.currentView!!.viewModel.isAutomatonEditor
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

        val c1 = VBox()
        c1.children.addAll(changeViewButton, goToParentSystemButton, focusSelectedElementButton)
        val c2 = VBox()
        c2.children.addAll(toggleAppearanceButton, searchElementsButton)
        viewMenu.children.addAll(c1, c2)


        return viewMenu
    }

    private fun setupToolsMenu(): HBox {
        val toolsMenu = HBox() // (ResourceHandler.getString("Labels", "tools"));

        // General tools:
        val cursorButton = Button(ToolType.CURSOR.label)
        cursorButton.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createSelectToolAction(
                    ToolType.CURSOR
                )
            )
        }
        view.addMnemonic(cursorButton, Shortcuts.CURSOR_TOOL.get())

        val marqueeButton = Button(ToolType.MARQUEE_TOOL.label)
        marqueeButton.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createSelectToolAction(
                    ToolType.MARQUEE_TOOL
                )
            )
        }
        view.addMnemonic(marqueeButton, Shortcuts.MARQUEE_TOOL.get())

        val panButton = Button(ToolType.PAN.label)
        panButton.onAction = EventHandler { e: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createSelectToolAction(
                    ToolType.PAN
                )
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
        toolsMenu.children
            .addAll(
                cursorButton, marqueeButton, panButton, systemCreatorButton,
                systemConnectionCreatorButton, variableBlockCreatorButton,
                stateCreatorButton, edgeCreatorButton, regionCreatorButton
            )

        return toolsMenu
    }

    private fun toolButton(toolType: ToolType, isAutomatonTool: Boolean): Button {
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
