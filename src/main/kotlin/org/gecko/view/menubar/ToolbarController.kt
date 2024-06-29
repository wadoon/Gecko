package org.gecko.view.menubar

import javafx.beans.binding.Bindings
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.geometry.Orientation
import javafx.geometry.Point2D
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.gecko.actions.ActionManager
import org.gecko.application.GeckoManager
import org.gecko.io.FileTypes
import org.gecko.tools.ToolType
import org.gecko.view.GeckoView
import org.gecko.view.views.shortcuts.Shortcuts
import org.gecko.viewmodel.EditorViewModel
import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.PositionableViewModelElement
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign2.*
import tornadofx.*

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
class ToolbarController(
    private val manager: GeckoManager,
    private val view: GeckoView,
    val model: GModel,
    private val actionManager: ActionManager
) : View() {


    override val root = toolbar {
        setupFileMenu()
        separator(Orientation.VERTICAL)
        setupCagen()
        separator(Orientation.VERTICAL)
        setupEditMenu()
        //setupVersionManagment(),
        //setupZoom(),
        setupViewMenu()
    }

    /*private fun setupVersionManagment(): VBox {
        val btnVMgr = createColButton("Variants Manager", null)
        //btnVMgr.onAction = EventHandler { it: ActionEvent? -> view.showVersionManager() }

        val btnActivateVariants = MenuButton("Activate Variants")
        val vg = model.knownVariantGroupsProperty
        val mvg = MappedList(vg) { v ->
            val m = Menu(v.name)
            m.items.setAll(v.variants.stream().map { CheckMenuItem() }.toList())
            m
        }
        btnActivateVariants.items.setAll(mvg)
        return createColumn(btnVMgr, btnActivateVariants)
    }*/

    private fun setupZoom() {
        // Zooming commands:
        bigbutton("", MaterialDesignM.MAGNIFY_PLUS) {
            action {
                actionManager.run(
                    actionManager.actionFactory.createZoomCenterAction(EditorViewModel.defaultZoomStep)
                )
            }
            view.addMnemonic(this, Shortcuts.ZOOM_IN.get())
        }

        val zoomOutButton = bigbutton("", MaterialDesignM.MAGNIFY_MINUS)
        zoomOutButton.action {
            actionManager.run(
                actionManager.actionFactory.createZoomCenterAction(1 / EditorViewModel.defaultZoomStep)
            )
        }
        view.addMnemonic(zoomOutButton, Shortcuts.ZOOM_OUT.get())
    }

    fun EventTarget.bigbutton(txt: String, icon: Ikon?, op: Button.() -> Unit = {}): Button {
        val ico: FontIcon? = if (icon == null) null else FontIcon.of(icon, 32)
        return button(txt, ico) {
            contentDisplay = ContentDisplay.TOP
            styleClass.add("big")
            isWrapText = true
            style = "-fx-font-size:80%;"
            this.op()
        }
    }

    fun EventTarget.createColButton(s: String, icon: Ikon?, op: Button.() -> Unit = {}): Button {
        val ico: FontIcon? = if (icon == null) null else FontIcon.of(icon, 12)
        return button(s, ico) {
            contentDisplay = ContentDisplay.RIGHT
            styleClass.add("normal")
            //b.isWrapText = false
            style = "-fx-font-size:80%;"
            style {
                fontSize = 80.0.percent
                startMargin = 2.0.pt
                endMargin = 2.0.pt
            }
            op()
        }
    }

    private fun ToolBar.setupFileMenu() {
        //fileMenu.setTitle(ResourceHandler.getString("Labels", "file"));
        button("New", FontIcon(MaterialDesignF.FILE)) {
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            action { manager.createNewProject() }
            view.addMnemonic(this, Shortcuts.NEW.get())
        }

        button("Open", FontIcon(MaterialDesignF.FOLDER_OPEN_OUTLINE)) {
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            action { manager.loadGeckoProject() }
            view.addMnemonic(this, Shortcuts.OPEN.get())
        }

        button("Save", FontIcon(MaterialDesignC.CONTENT_SAVE)) {
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            action {
                val file = manager.latestFile
                if (file != null) {
                    manager.saveGeckoProject(file)
                } else {
                    val fileToSaveTo = manager.getSaveFileChooser(FileTypes.JSON)
                    if (fileToSaveTo != null) {
                        manager.saveGeckoProject(fileToSaveTo)
                        manager.latestFile = (fileToSaveTo)
                    }
                }
            }
            view.addMnemonic(this, Shortcuts.SAVE.get())
        }

        button("Save as", FontIcon(MaterialDesignC.CONTENT_SAVE_ALL)) {
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            action {
                val fileToSaveTo = manager.getSaveFileChooser(FileTypes.JSON)
                if (fileToSaveTo != null) {
                    manager.saveGeckoProject(fileToSaveTo)
                }
            }
            view.addMnemonic(this, Shortcuts.SAVE_AS.get())
        }
    }

    private fun ToolBar.setupCagen() {
        splitmenubutton("Verify", FontIcon(MaterialDesignP.PLAY_BOX)) {
            item("Import from CAGEN", null, FontIcon(MaterialDesignF.FILE_IMPORT)) {
                //view.addMnemonic(this, KeyCodeCombination(KeyCode.I, KeyCombination.SHORTCUT_DOWN))
                action {
                    val fileToImport = manager.getOpenFileChooser(FileTypes.SYS)
                    if (fileToImport != null) {
                        manager.importAutomatonFile(fileToImport)
                    }
                }
            }
            item(
                "Export", KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN),
                FontIcon(MaterialDesignF.FILE_EXPORT)
            ) {
                //view.addMnemonic(this, KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN))
                action {
                    val fileToSaveTo = manager.getSaveFileChooser(FileTypes.SYS)
                    if (fileToSaveTo != null) {
                        manager.exportAutomatonFile(fileToSaveTo)
                    }
                }
            }
        }
    }

    private fun ToolBar.setupEditMenu() {
        // Edit history navigation:
        bigbutton("undo", MaterialDesignU.UNDO) {
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            action { actionManager.undo() }
            view.addMnemonic(this, Shortcuts.UNDO.get())
        }
        bigbutton("redo", MaterialDesignR.REDO) {
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            action { actionManager.redo() }
            view.addMnemonic(this, Shortcuts.REDO.get())
        }

        separator(Orientation.VERTICAL)

        // Data transfer commands:
        createColButton("cut", MaterialDesignC.CONTENT_CUT) {
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            action {
                //actionManager.run(actionManager.actionFactory.createCutPositionableViewModelElementAction())
            }
            view.addMnemonic(this, Shortcuts.CUT.get())
        }
        createColButton("copy", MaterialDesignC.CONTENT_COPY) {
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            action {
                TODO() //actionManager.run(actionManager.actionFactory.createCopyPositionableViewModelElementAction())
            }
            view.addMnemonic(this, Shortcuts.COPY.get())
        }
        createColButton("paste", MaterialDesignC.CONTENT_PASTE) {
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            action {
                val center: Point2D = view.currentView!!.viewElementPane.screenCenterWorldCoords()
                //actionManager.run(actionManager.actionFactory.createPastePositionableViewModelElementAction(center))
                TODO()
            }
            view.addMnemonic(this, Shortcuts.PASTE.get())
        }

        separator(Orientation.VERTICAL)
        // General selection commands:
        //vbox {
        button("Select All", FontIcon(MaterialDesignS.SELECT_ALL)) {
            action {
                val allElements: Set<PositionableViewModelElement> = view.allDisplayedElements
                actionManager.run(actionManager.actionFactory.createSelectAction(allElements, true))
            }
            view.addMnemonic(this, Shortcuts.SELECT_ALL.get())
        }
        button("Deselect", FontIcon(MaterialDesignS.SELECTION_OFF)) {
            action { actionManager.run(actionManager.actionFactory.createDeselectAction()) }
            view.addMnemonic(this, Shortcuts.DESELECT_ALL.get())
        }
        //}
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
        changeViewButton.action {
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
        goToParentSystemButton.action {
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
        val toggleAppearanceButton = createColButton("Dark/Light", MaterialDesignT.THEME_LIGHT_DARK)
        toggleAppearanceButton.action { view.toggleAppearance() }
        view.addMnemonic(toggleAppearanceButton, Shortcuts.TOGGLE_APPEARANCE.get())

        //val searchElementsButton = createColButton("search_elements", MaterialDesignT.TEXT_SEARCH)
        //searchElementsButton.action{ view.currentView!!.toggleSearchWindow() }
        //view.addMnemonic(searchElementsButton, Shortcuts.TOGGLE_SEARCH.get())

        val c1 = VBox()
        c1.children.addAll(changeViewButton, goToParentSystemButton, focusSelectedElementButton)
        val c2 = VBox()
        c2.children.addAll(toggleAppearanceButton)//, searchElementsButton)
        viewMenu.children.addAll(c1, c2)


        return viewMenu
    }

    private fun ToolBar.setupToolsMenu(): HBox {
        val toolsMenu = HBox() // (ResourceHandler.getString("Labels", "tools"));

        // General tools:
        val cursorButton = Button(ToolType.CURSOR.label)
        cursorButton.action {
            actionManager.run(
                actionManager.actionFactory.createSelectToolAction(
                    ToolType.CURSOR
                )
            )
        }
        view.addMnemonic(cursorButton, Shortcuts.CURSOR_TOOL.get())

        val marqueeButton = Button(ToolType.MARQUEE_TOOL.label)
        marqueeButton.action {
            actionManager.run(
                actionManager.actionFactory.createSelectToolAction(
                    ToolType.MARQUEE_TOOL
                )
            )
        }
        view.addMnemonic(marqueeButton, Shortcuts.MARQUEE_TOOL.get())

        val panButton = Button(ToolType.PAN.label)
        panButton.action {
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
        toolButton.action {
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
