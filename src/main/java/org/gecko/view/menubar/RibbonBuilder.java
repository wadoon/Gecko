package org.gecko.view.menubar;

import com.pixelduke.control.Ribbon;
import com.pixelduke.control.ribbon.Column;
import com.pixelduke.control.ribbon.RibbonGroup;
import com.pixelduke.control.ribbon.RibbonTab;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import org.gecko.actions.ActionManager;
import org.gecko.application.GeckoIOManager;
import org.gecko.io.FileTypes;
import org.gecko.tools.ToolType;
import org.gecko.view.GeckoView;
import org.gecko.view.ResourceHandler;
import org.gecko.viewmodel.EditorViewModel;
import org.gecko.viewmodel.PositionableViewModelElement;
import org.gecko.viewmodel.SystemViewModel;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import java.io.File;
import java.util.Set;

/**
 * Represents a builder for the {@link MenuBar} displayed in the view, containing {@link Button}s in {@link Menu}s
 * grouped by category. Holds a reference to the built {@link MenuBar}, the current {@link GeckoView} and the
 * {@link ActionManager}, which allow for actions to be run from the menu bar. Relevant menus for the Gecko Graphic
 * Editor are "File" (running operations like creating, saving, loading, importing and exporting files), "Edit" (running
 * operations like undoing and redoing actions, cutting, copying and pasting or selecting and deselecting all elements),
 * "View" (running operations like changing the view, opening the parent system or zooming in and out of the view),
 * "Tools" (providing the active tools which can be selected in the current view) and "Help" (running operations like
 * finding an element by name matches, opening a comprehensive list of all shortcuts available or reading more
 * information about Gecko).
 */
public class RibbonBuilder {
    private final Ribbon menuBar = new Ribbon();
    private final GeckoView view;
    private final ActionManager actionManager;

    public RibbonBuilder(GeckoView view, ActionManager actionManager) {
        this.view = view;
        this.actionManager = actionManager;

        var tab = new RibbonTab("Home");
        tab.getRibbonGroups().setAll(setupFileMenu(), setupCagen(), setupEditMenu(), setupZoom(), setupViewMenu());
        var tab2 = new RibbonTab("Tools");
        tab2.getRibbonGroups().setAll(setupToolsMenu());
        menuBar.getTabs().setAll(tab, tab2);

    }

    private RibbonGroup setupZoom() {
        var g = new RibbonGroup();
        g.setTitle("Zoom");
        // Zooming commands:
        Button zoomInButton = createBigButton("zoom_in", MaterialDesign.MDI_MAGNIFY_PLUS);
        zoomInButton.setOnAction(e -> actionManager.run(
                actionManager.getActionFactory().createZoomCenterAction(EditorViewModel.getDefaultZoomStep())));
        //zoomInButton.setAccelerator(Shortcuts.ZOOM_IN.get());

        Button zoomOutButton = createBigButton("zoom_out", MaterialDesign.MDI_MAGNIFY_MINUS);
        zoomOutButton.setOnAction(e -> actionManager.run(
                actionManager.getActionFactory().createZoomCenterAction(1 / EditorViewModel.getDefaultZoomStep())));
        //zoomOutButton.setAccelerator(Shortcuts.ZOOM_OUT.get());
        g.getNodes().addAll(zoomInButton, zoomOutButton);
        return g;
    }

    public Ribbon build() {
        return menuBar;
    }

    public Button createBigButton(String s, Ikon icon) {
        final var ico = icon == null ? null : FontIcon.of(icon, 32);
        Button b = new Button(ResourceHandler.getString("Buttons", s), ico);
        b.setContentDisplay(ContentDisplay.TOP);
        b.getStyleClass().add("big");
        b.setWrapText(true);
        return b;
    }

    public Button createColButton(String s, Ikon icon) {
        final var ico = icon == null ? null : FontIcon.of(icon, 16);
        Button b = new Button(ResourceHandler.getString("Buttons", s), ico);
        b.setContentDisplay(ContentDisplay.LEFT);
        b.getStyleClass().add("normal");
        b.setWrapText(false);
        return b;
    }

    private RibbonGroup setupFileMenu() {
        var fileMenu = new RibbonGroup();
        fileMenu.setTitle(ResourceHandler.getString("Labels", "file"));

        Button newFileItem = createBigButton("new", MaterialDesign.MDI_FILE);
        newFileItem.setOnAction(e -> GeckoIOManager.getInstance().createNewProject());
        //newFileItem.setAccelerator(Shortcuts.NEW.get());

        Button openFileItem = createBigButton("open", MaterialDesign.MDI_OPEN_IN_NEW);
        openFileItem.setOnAction(e -> GeckoIOManager.getInstance().loadGeckoProject());
        //openFileItem.setAccelerator(Shortcuts.OPEN.get());

        Button saveFileItem = createBigButton("save", MaterialDesign.MDI_CONTENT_SAVE);
        saveFileItem.setContentDisplay(ContentDisplay.TOP);
        saveFileItem.setOnAction(e -> {
            File file = GeckoIOManager.getInstance().getFile();
            if (file != null) {
                GeckoIOManager.getInstance().saveGeckoProject(file);
            } else {
                File fileToSaveTo = GeckoIOManager.getInstance().getSaveFileChooser(FileTypes.JSON);
                if (fileToSaveTo != null) {
                    GeckoIOManager.getInstance().saveGeckoProject(fileToSaveTo);
                    GeckoIOManager.getInstance().setFile(fileToSaveTo);
                }
            }
        });
        //saveFileItem.setAccelerator(Shortcuts.SAVE.get());

        Button saveAsFileItem = createBigButton("save_as", MaterialDesign.MDI_CONTENT_SAVE_ALL);
        saveAsFileItem.setOnAction(e -> {
            File fileToSaveTo = GeckoIOManager.getInstance().getSaveFileChooser(FileTypes.JSON);
            if (fileToSaveTo != null) {
                GeckoIOManager.getInstance().saveGeckoProject(fileToSaveTo);
            }
        });
        //saveAsFileItem.setAccelerator(Shortcuts.SAVE_AS.get());
        fileMenu.getNodes().addAll(newFileItem, openFileItem, saveFileItem, saveAsFileItem);
        return fileMenu;
    }

    private RibbonGroup setupCagen() {
        var g = new RibbonGroup();
        g.setTitle("CAGEN");
        Button importFileItem = createBigButton("import", MaterialDesign.MDI_FILE_IMPORT);
        //importFileItem.setAccelerator(new KeyCodeCombination(KeyCode.I, KeyCombination.SHORTCUT_DOWN));
        importFileItem.setOnAction(e -> {
            File fileToImport = GeckoIOManager.getInstance().getOpenFileChooser(FileTypes.SYS);
            if (fileToImport != null) {
                GeckoIOManager.getInstance().importAutomatonFile(fileToImport);
            }
        });

        Button exportFileItem = createBigButton("export", MaterialDesign.MDI_FILE_EXPORT);
        //exportFileItem.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN));
        exportFileItem.setOnAction(e -> {
            File fileToSaveTo = GeckoIOManager.getInstance().getSaveFileChooser(FileTypes.SYS);
            if (fileToSaveTo != null) {
                GeckoIOManager.getInstance().exportAutomatonFile(fileToSaveTo);
            }
        });
        g.getNodes().setAll(importFileItem, exportFileItem);
        return g;
    }

    private RibbonGroup setupEditMenu() {
        var editMenu = new RibbonGroup();//ResourceHandler.getString("Labels", "edit"));

        // Edit history navigation:
        Button undoButton = createBigButton("undo", MaterialDesign.MDI_UNDO);
        undoButton.setOnAction(e -> actionManager.undo());
        //undoButton.setAccelerator(Shortcuts.UNDO.get());

        Button redoButton = createBigButton("redo", MaterialDesign.MDI_REDO);
        undoButton.setContentDisplay(ContentDisplay.TOP);

        redoButton.setOnAction(e -> actionManager.redo());
        //redoButton.setAccelerator(Shortcuts.REDO.get());

        //SeparatorButton historyToDataTransferSeparator = new SeparatorButton();

        // Data transfer commands:
        Button cutButton = createColButton("cut", MaterialDesign.MDI_CONTENT_CUT);
        cutButton.setContentDisplay(ContentDisplay.TOP);
        cutButton.setOnAction(
                e -> actionManager.run(actionManager.getActionFactory().createCutPositionableViewModelElementAction()));
        //cutButton.setAccelerator(Shortcuts.CUT.get());

        Button copyButton = createColButton("copy", MaterialDesign.MDI_CONTENT_COPY);
        copyButton.setContentDisplay(ContentDisplay.TOP);
        copyButton.setOnAction(
                e -> actionManager.run(actionManager.getActionFactory().createCopyPositionableViewModelElementAction()));
        //copyButton.setAccelerator(Shortcuts.COPY.get());

        Button pasteButton = createColButton("paste", MaterialDesign.MDI_CONTENT_PASTE);
        pasteButton.setContentDisplay(ContentDisplay.TOP);
        pasteButton.setOnAction(e -> {
            Point2D center = view.getCurrentView().getViewElementPane().screenCenterWorldCoords();
            actionManager.run(actionManager.getActionFactory().createPastePositionableViewModelElementAction(center));
        });
        //pasteButton.setAccelerator(Shortcuts.PASTE.get());

        // General selection commands:
        Button selectAllButton = createColButton("select_all", null);
        selectAllButton.setOnAction(e -> {
            Set<PositionableViewModelElement<?>> allElements = view.getAllDisplayedElements();
            actionManager.run(actionManager.getActionFactory().createSelectAction(allElements, true));
        });
        //selectAllButton.setAccelerator(Shortcuts.SELECT_ALL.get());

        Button deselectAllButton = createColButton("deselect_all", null);
        deselectAllButton.setOnAction(
                e -> actionManager.run(actionManager.getActionFactory().createDeselectAction()));
        //deselectAllButton.setAccelerator(Shortcuts.DESELECT_ALL.get());
        // renameRootSystemCustomButton = getRenameRootSystemCustomButton();

        var c1 = new Column();
        c1.getChildren().addAll(cutButton, copyButton, pasteButton);

        var c2 = new Column();
        c2.getChildren().setAll(selectAllButton, deselectAllButton);

        editMenu.getNodes().addAll(undoButton, redoButton, c1, c2);
        return editMenu;
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

    private RibbonGroup setupViewMenu() {
        var viewMenu = new RibbonGroup(); // ResourceHandler.getString("Labels", "view"));

        // View change commands:
        Button changeViewButton = createColButton("change_view", null);
        changeViewButton.setOnAction(e -> actionManager.run(actionManager.getActionFactory()
                .createViewSwitchAction(view.getCurrentView().getViewModel().getCurrentSystem(),
                        !view.getCurrentView().getViewModel().isAutomatonEditor())));
        //changeViewButton.setAccelerator(Shortcuts.SWITCH_EDITOR.get());

        Button goToParentSystemButton = createColButton("go_to_parent_system", null);
        goToParentSystemButton.setOnAction(e -> {
            boolean isAutomatonEditor = view.getCurrentView().getViewModel().isAutomatonEditor();
            SystemViewModel parentSystem = view.getCurrentView().getViewModel().getParentSystem();
            actionManager.run(actionManager.getActionFactory().createViewSwitchAction(parentSystem, isAutomatonEditor));
        });
        //goToParentSystemButton.setAccelerator(Shortcuts.OPEN_PARENT_SYSTEM_EDITOR.get());

        Button focusSelectedElementButton =
                createColButton("focus_selected_element", null);
        focusSelectedElementButton.setOnAction(e -> view.getCurrentView().getViewModel().moveToFocusedElement());
        //focusSelectedElementButton.setAccelerator(Shortcuts.FOCUS_SELECTED_ELEMENT.get());

        //SeparatorButton viewSwitchToZoomSeparator = new SeparatorButton();


        //SeparatorButton zoomToAppearanceSeparator = new SeparatorButton();

        Button toggleAppearanceButton = createColButton("toggle_appearance", MaterialDesign.MDI_THEME_LIGHT_DARK);
        toggleAppearanceButton.setOnAction(e -> view.toggleAppearance());
        //toggleAppearanceButton.setAccelerator(Shortcuts.TOGGLE_APPEARANCE.get());

        Button searchElementsButton = createColButton("search_elements", MaterialDesign.MDI_MAGNIFY);
        searchElementsButton.setOnAction(e -> view.getCurrentView().toggleSearchWindow());
        //searchElementsButton.setAccelerator(Shortcuts.TOGGLE_SEARCH.get());

        var c1 = new Column();
        c1.getChildren().addAll(changeViewButton, goToParentSystemButton, focusSelectedElementButton);
        var c2 = new Column();
        c2.getChildren().addAll(toggleAppearanceButton, searchElementsButton);
        viewMenu.getNodes().addAll(c1, c2);


        return viewMenu;
    }

    private RibbonGroup setupToolsMenu() {
        var toolsMenu = new RibbonGroup();// (ResourceHandler.getString("Labels", "tools"));

        // General tools:
        Button cursorButton = new Button(ToolType.CURSOR.getLabel());
        cursorButton.setOnAction(
                e -> actionManager.run(actionManager.getActionFactory().createSelectToolAction(ToolType.CURSOR)));
        //cursorButton.setAccelerator(Shortcuts.CURSOR_TOOL.get());

        Button marqueeButton = new Button(ToolType.MARQUEE_TOOL.getLabel());
        marqueeButton.setOnAction(
                e -> actionManager.run(actionManager.getActionFactory().createSelectToolAction(ToolType.MARQUEE_TOOL)));
        //marqueeButton.setAccelerator(Shortcuts.MARQUEE_TOOL.get());

        Button panButton = new Button(ToolType.PAN.getLabel());
        panButton.setOnAction(
                e -> actionManager.run(actionManager.getActionFactory().createSelectToolAction(ToolType.PAN)));
        //panButton.setAccelerator(Shortcuts.PAN_TOOL.get());

        //SeparatorButton generalFromSystemSeparator = new SeparatorButton();

        // System view tools:
        Button systemCreatorButton = toolButton(ToolType.SYSTEM_CREATOR, false);
        Button systemConnectionCreatorButton = toolButton(ToolType.CONNECTION_CREATOR, false);
        Button variableBlockCreatorButton = toolButton(ToolType.VARIABLE_BLOCK_CREATOR, false);

        //SeparatorButton systemFroAutomatonSeparator = new SeparatorButton();

        // Automaton view tools:
        Button stateCreatorButton = toolButton(ToolType.STATE_CREATOR, true);
        Button edgeCreatorButton = toolButton(ToolType.EDGE_CREATOR, true);
        Button regionCreatorButton = toolButton(ToolType.REGION_CREATOR, true);
        toolsMenu.getNodes()
                .addAll(cursorButton, marqueeButton, panButton, systemCreatorButton,
                        systemConnectionCreatorButton, variableBlockCreatorButton,
                        stateCreatorButton, edgeCreatorButton, regionCreatorButton);

        return toolsMenu;
    }

    private Button toolButton(ToolType toolType, boolean isAutomatonTool) {
        Button toolButton = new Button(toolType.getLabel());
        toolButton.setOnAction(
                e -> actionManager.run(actionManager.getActionFactory().createSelectToolAction(toolType)));
        toolButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            if (view.getCurrentView() == null) {
                return true;
            }
            return view.getCurrentView().getViewModel().isAutomatonEditor() != isAutomatonTool;
        }, view.getCurrentViewProperty()));
        return toolButton;
    }
}
