package org.gecko.view.menubar;

import com.pixelduke.control.Ribbon;
import com.pixelduke.control.ribbon.Column;
import com.pixelduke.control.ribbon.RibbonGroup;
import com.pixelduke.control.ribbon.RibbonTab;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.gecko.actions.ActionManager;
import org.gecko.application.GeckoIOManager;
import org.gecko.io.FileTypes;
import org.gecko.tools.ToolType;
import org.gecko.view.GeckoView;
import org.gecko.view.ResourceHandler;
import org.gecko.view.views.shortcuts.Shortcuts;
import org.gecko.viewmodel.EditorViewModel;
import org.gecko.viewmodel.PositionableViewModelElement;
import org.gecko.viewmodel.SystemViewModel;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignS;

import java.io.File;
import java.util.Set;

import static org.kordamp.ikonli.materialdesign2.MaterialDesignA.ARROW_UP;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignC.*;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignF.*;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignM.MAGNIFY_MINUS;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignM.MAGNIFY_PLUS;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignR.REDO;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignT.*;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignU.UNDO;

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

        /*final var quick = new QuickAccessBar();
        quick.getRightButtons().add(
            new Button("save")
        menuBar.setQuickAccessBar(quick);
        );
        */
    }

    private RibbonGroup setupZoom() {
        var g = new RibbonGroup();
        g.setTitle("Zoom");
        // Zooming commands:
        Button zoomInButton = createBigButton(null, MAGNIFY_PLUS);
        zoomInButton.setOnAction(e -> actionManager.run(
                actionManager.getActionFactory().createZoomCenterAction(EditorViewModel.getDefaultZoomStep())));
        view.addMnemonic(zoomInButton, Shortcuts.ZOOM_IN.get());

        Button zoomOutButton = createBigButton(null, MAGNIFY_MINUS);
        zoomOutButton.setOnAction(e -> actionManager.run(
                actionManager.getActionFactory().createZoomCenterAction(1 / EditorViewModel.getDefaultZoomStep())));
        view.addMnemonic(zoomOutButton,Shortcuts.ZOOM_OUT.get());
        g.getNodes().addAll(zoomInButton, zoomOutButton);
        return g;
    }

    public Ribbon build() {
        return menuBar;
    }

    public Button createBigButton(String s, Ikon icon) {
        final var ico = icon == null ? null : FontIcon.of(icon, 32);
        Button b = new Button(s, ico);
        b.setContentDisplay(ContentDisplay.TOP);
        if (s == null) b.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        b.getStyleClass().add("big");
        b.setWrapText(true);
        b.setStyle("-fx-font-size:80%;");
        return b;
    }

    public Button createColButton(String s, Ikon icon) {
        final var ico = icon == null ? null : FontIcon.of(icon, 16);
        Button b = new Button(s, ico);
        b.setContentDisplay(ContentDisplay.LEFT);
        b.getStyleClass().add("normal");
        b.setWrapText(false);
        b.setStyle("-fx-font-size:80%;");
        return b;
    }

    private RibbonGroup setupFileMenu() {
        var fileMenu = new RibbonGroup();
        fileMenu.setTitle(ResourceHandler.file);
        Button newFileItem = createBigButton("new", FILE);
        newFileItem.setOnAction(e -> GeckoIOManager.getInstance().createNewProject());
        view.addMnemonic(newFileItem,Shortcuts.NEW.get());

        Button openFileItem = createBigButton("open", FOLDER_OPEN_OUTLINE);
        openFileItem.setOnAction(e -> GeckoIOManager.getInstance().loadGeckoProject());
        view.addMnemonic(openFileItem,Shortcuts.OPEN.get());

        Button saveFileItem = createBigButton("save", CONTENT_SAVE);
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
        view.addMnemonic(saveFileItem,Shortcuts.SAVE.get());

        Button saveAsFileItem = createBigButton("save_as", CONTENT_SAVE_ALL);
        saveAsFileItem.setOnAction(e -> {
            File fileToSaveTo = GeckoIOManager.getInstance().getSaveFileChooser(FileTypes.JSON);
            if (fileToSaveTo != null) {
                GeckoIOManager.getInstance().saveGeckoProject(fileToSaveTo);
            }
        });
        view.addMnemonic(saveAsFileItem,Shortcuts.SAVE_AS.get());
        fileMenu.getNodes().addAll(newFileItem, openFileItem, saveFileItem, saveAsFileItem);
        return fileMenu;
    }

    private RibbonGroup setupCagen() {
        var g = new RibbonGroup();
        g.setTitle("CAGEN");
        Button importFileItem = createColButton("import", FILE_IMPORT);
        view.addMnemonic(importFileItem, new KeyCodeCombination(KeyCode.I, KeyCombination.SHORTCUT_DOWN));
        importFileItem.setOnAction(e -> {
            File fileToImport = GeckoIOManager.getInstance().getOpenFileChooser(FileTypes.SYS);
            if (fileToImport != null) {
                GeckoIOManager.getInstance().importAutomatonFile(fileToImport);
            }
        });

        Button exportFileItem = createColButton("export", FILE_EXPORT);
        view.addMnemonic(exportFileItem,new KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN));
        exportFileItem.setOnAction(e -> {
            File fileToSaveTo = GeckoIOManager.getInstance().getSaveFileChooser(FileTypes.SYS);
            if (fileToSaveTo != null) {
                GeckoIOManager.getInstance().exportAutomatonFile(fileToSaveTo);
            }
        });
        g.getNodes().setAll(
                createColumn(importFileItem, exportFileItem));
        return g;
    }

    private Column createColumn(Node... nodes) {
        var c = new Column();
        c.getChildren().addAll(nodes);
        return c;
    }

    private RibbonGroup setupEditMenu() {
        var editMenu = new RibbonGroup();//ResourceHandler.edit);

        // Edit history navigation:
        Button undoButton = createBigButton("undo", UNDO);
        undoButton.setOnAction(e -> actionManager.undo());
        view.addMnemonic(undoButton, Shortcuts.UNDO.get());

        Button redoButton = createBigButton("redo", REDO);
        undoButton.setContentDisplay(ContentDisplay.TOP);

        redoButton.setOnAction(e -> actionManager.redo());
        view.addMnemonic(redoButton,Shortcuts.REDO.get());

        //SeparatorButton historyToDataTransferSeparator = new SeparatorButton();

        // Data transfer commands:
        Button cutButton = createColButton("cut", CONTENT_CUT);
        cutButton.setContentDisplay(ContentDisplay.LEFT);
        cutButton.setOnAction(
                e -> actionManager.run(actionManager.getActionFactory().createCutPositionableViewModelElementAction()));
        view.addMnemonic(cutButton,Shortcuts.CUT.get());

        Button copyButton = createColButton("copy", CONTENT_COPY);
        copyButton.setContentDisplay(ContentDisplay.LEFT);
        copyButton.setOnAction(
                e -> actionManager.run(actionManager.getActionFactory().createCopyPositionableViewModelElementAction()));
        view.addMnemonic(copyButton,Shortcuts.COPY.get());

        Button pasteButton = createColButton("paste", CONTENT_PASTE);
        pasteButton.setContentDisplay(ContentDisplay.LEFT);
        pasteButton.setOnAction(e -> {
            Point2D center = view.getCurrentView().getViewElementPane().screenCenterWorldCoords();
            actionManager.run(actionManager.getActionFactory().createPastePositionableViewModelElementAction(center));
        });
        view.addMnemonic(pasteButton,Shortcuts.PASTE.get());

        // General selection commands:
        Button selectAllButton = createColButton("select_all", MaterialDesignS.SELECT_ALL);
        selectAllButton.setOnAction(e -> {
            Set<PositionableViewModelElement<?>> allElements = view.getAllDisplayedElements();
            actionManager.run(actionManager.getActionFactory().createSelectAction(allElements, true));
        });
        view.addMnemonic(selectAllButton,Shortcuts.SELECT_ALL.get());

        Button deselectAllButton = createColButton("deselect_all", MaterialDesignS.SELECTION_OFF);
        deselectAllButton.setOnAction(
                e -> actionManager.run(actionManager.getActionFactory().createDeselectAction()));
        view.addMnemonic(deselectAllButton,Shortcuts.DESELECT_ALL.get());
        // renameRootSystemCustomButton = getRenameRootSystemCustomButton();

        var c1 = createColumn(cutButton, copyButton, pasteButton);
        var c2 = createColumn(selectAllButton, deselectAllButton);
        editMenu.getNodes().addAll(undoButton, redoButton, c1, c2);
        return editMenu;
    }

    private RibbonGroup setupViewMenu() {
        var viewMenu = new RibbonGroup(); // ResourceHandler.view);

        // View change commands:
        Button changeViewButton = createColButton("change_view", TOGGLE_SWITCH);
        changeViewButton.setOnAction(e -> actionManager.run(actionManager.getActionFactory()
                .createViewSwitchAction(view.getCurrentView().getViewModel().getCurrentSystem(),
                        !view.getCurrentView().getViewModel().isAutomatonEditor())));
        view.addMnemonic(changeViewButton,Shortcuts.SWITCH_EDITOR.get());

        Button goToParentSystemButton = createColButton("go_to_parent_system", ARROW_UP);
        goToParentSystemButton.setOnAction(e -> {
            boolean isAutomatonEditor = view.getCurrentView().getViewModel().isAutomatonEditor();
            SystemViewModel parentSystem = view.getCurrentView().getViewModel().getParentSystem();
            actionManager.run(actionManager.getActionFactory().createViewSwitchAction(parentSystem, isAutomatonEditor));
        });
        view.addMnemonic(goToParentSystemButton,Shortcuts.OPEN_PARENT_SYSTEM_EDITOR.get());

        Button focusSelectedElementButton =
                createColButton("focus_selected_element", FOCUS_AUTO);
        focusSelectedElementButton.setOnAction(e -> view.getCurrentView().getViewModel().moveToFocusedElement());
        view.addMnemonic(focusSelectedElementButton,Shortcuts.FOCUS_SELECTED_ELEMENT.get());

        //SeparatorButton viewSwitchToZoomSeparator = new SeparatorButton();


        //SeparatorButton zoomToAppearanceSeparator = new SeparatorButton();

        Button toggleAppearanceButton = createColButton("toggle_appearance", THEME_LIGHT_DARK);
        toggleAppearanceButton.setOnAction(e -> view.toggleAppearance());
        view.addMnemonic(toggleAppearanceButton,Shortcuts.TOGGLE_APPEARANCE.get());

        Button searchElementsButton = createColButton("search_elements", TEXT_SEARCH);
        searchElementsButton.setOnAction(e -> view.getCurrentView().toggleSearchWindow());
        view.addMnemonic(searchElementsButton,Shortcuts.TOGGLE_SEARCH.get());

        var c1 = new Column();
        c1.getChildren().addAll(changeViewButton, goToParentSystemButton, focusSelectedElementButton);
        var c2 = new Column();
        c2.getChildren().addAll(toggleAppearanceButton, searchElementsButton);
        viewMenu.getNodes().addAll(c1, c2);


        return viewMenu;
    }

    private RibbonGroup setupToolsMenu() {
        var toolsMenu = new RibbonGroup();// (ResourceHandler.tools);

        // General tools:
        Button cursorButton = new Button(ToolType.CURSOR.getLabel());
        cursorButton.setOnAction(
                e -> actionManager.run(actionManager.getActionFactory().createSelectToolAction(ToolType.CURSOR)));
        view.addMnemonic(cursorButton,Shortcuts.CURSOR_TOOL.get());

        Button marqueeButton = new Button(ToolType.MARQUEE_TOOL.getLabel());
        marqueeButton.setOnAction(
                e -> actionManager.run(actionManager.getActionFactory().createSelectToolAction(ToolType.MARQUEE_TOOL)));
        view.addMnemonic(marqueeButton,Shortcuts.MARQUEE_TOOL.get());

        Button panButton = new Button(ToolType.PAN.getLabel());
        panButton.setOnAction(
                e -> actionManager.run(actionManager.getActionFactory().createSelectToolAction(ToolType.PAN)));
        view.addMnemonic(panButton,Shortcuts.PAN_TOOL.get());

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
