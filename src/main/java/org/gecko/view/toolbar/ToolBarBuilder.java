package org.gecko.view.toolbar;

import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.gecko.actions.Action;
import org.gecko.actions.ActionManager;
import org.gecko.tools.Tool;
import org.gecko.tools.ToolType;
import org.gecko.view.ResourceHandler;
import org.gecko.view.views.EditorView;
import org.gecko.view.views.shortcuts.Shortcuts;
import org.gecko.viewmodel.EditorViewModel;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

import static org.kordamp.ikonli.materialdesign2.MaterialDesignR.REDO;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignU.UNDO;

/**
 * Represents a builder for the {@link ToolBar} displayed in the view, containing a {@link ToggleGroup} with
 * {@link ToggleButton}s for each of the current view's available {@link Tool}s, as well as {@link ToggleButton}s for
 * running the undo and redo operations. Holds a reference to the built {@link ToolBar} and the current
 * {@link EditorView}.
 */
public class ToolBarBuilder {

    private static final String DEFAULT_TOOLBAR_ICON_STYLE_NAME = "toolbar-icon";
    private static final String UNDO_ICON_STYLE_NAME = "undo-toolbar-icon";
    private static final String REDO_ICON_STYLE_NAME = "redo-toolbar-icon";
    private static final int BUTTON_SIZE = 30;

    private final ToolBar toolBar = new ToolBar();
    private final EditorView editorView;

    public ToolBarBuilder(ActionManager actionManager, EditorView editorView, EditorViewModel editorViewModel) {
        this.editorView = editorView;
        toolBar.setOrientation(Orientation.VERTICAL);

        ToggleGroup toggleGroup = new ToggleGroup();

        toggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle == null) {
                toggleGroup.selectToggle(oldToggle);
            }
        });

        for (int i = 0; i < editorViewModel.getTools().size(); i++) {
            addTools(actionManager, toggleGroup, editorViewModel.getTools().get(i));

            // add separator
            if (i < editorViewModel.getTools().size() - 1) {
                toolBar.getItems().add(new Separator());
            }
        }

        // Undo and Redo buttons
        toolBar.getItems().add(new Separator());
        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        toolBar.getItems().add(spacer);

        Button undoButton = new Button(ResourceHandler.getString("Buttons", "undo"),
                FontIcon.of(UNDO, 24));

        String toolTip = "%s (%s)".formatted(
                ResourceHandler.getString("Tooltips", "undo"),
                Shortcuts.UNDO.get().getDisplayText());

        undoButton.setTooltip(new Tooltip(toolTip));
        undoButton.setOnAction(event -> actionManager.undo());
        //undoButton.getStyleClass().add(DEFAULT_TOOLBAR_ICON_STYLE_NAME);
        //undoButton.getStyleClass().add(UNDO_ICON_STYLE_NAME);

        var redoButton = new Button(ResourceHandler.getString("Buttons", "redo"),
                FontIcon.of(REDO, 24));
        toolTip =
                "%s (%s)".formatted(ResourceHandler.getString("Tooltips", "redo"), Shortcuts.REDO.get().getDisplayText());
        redoButton.setTooltip(new Tooltip(toolTip));
        redoButton.setOnAction(event -> actionManager.redo());
        redoButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        undoButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        toolBar.getItems().addAll(undoButton, redoButton);
    }

    private void addTools(ActionManager actionManager, ToggleGroup toggleGroup, List<Tool> toolList) {
        for (Tool tool : toolList) {
            ToolType toolType = tool.getToolType();
            ToggleButton toolButton = new ToggleButton(
                    toolType.getLabel(),
                    FontIcon.of(toolType.getIcon(),24));
            toolButton.getStyleClass().add(DEFAULT_TOOLBAR_ICON_STYLE_NAME);

            //Would like to bind the selectedproperty of the button here but cannot because of a javafx bug
            editorView.getViewModel().getCurrentToolProperty().addListener((observable, oldValue, newValue) ->
                    toolButton.setSelected(newValue == tool));
            toolButton.setSelected(editorView.getViewModel().getCurrentToolType() == toolType);

            toolButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    Action action = actionManager.getActionFactory().createSelectToolAction(toolType);
                    actionManager.run(action);
                }
            });

            //toolButton.getStyleClass().add(toolType.getIcon());
            Tooltip tooltip = new Tooltip("%s (%s)"
                    .formatted(toolType.getLabel(), toolType.getKeyCodeCombination().getDisplayText()));
            toolButton.setTooltip(tooltip);
            toolBar.getItems().add(toolButton);
            toggleGroup.getToggles().add(toolButton);
        }
    }

    public ToolBar build() {
        return toolBar;
    }
}
