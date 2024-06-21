package org.gecko.view.toolbar

import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.control.*
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.gecko.actions.Action
import org.gecko.actions.ActionManager
import org.gecko.tools.Tool
import org.gecko.view.ResourceHandler
import org.gecko.view.views.EditorView
import org.gecko.view.views.shortcuts.Shortcuts
import org.gecko.viewmodel.EditorViewModel
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign2.MaterialDesignR
import org.kordamp.ikonli.materialdesign2.MaterialDesignU

/**
 * Represents a builder for the [ToolBar] displayed in the view, containing a [ToggleGroup] with
 * [ToggleButton]s for each of the current view's available [Tool]s, as well as [ToggleButton]s for
 * running the undo and redo operations. Holds a reference to the built [ToolBar] and the current
 * [EditorView].
 */
class ToolBarBuilder(
    actionManager: ActionManager,
    val editorView: EditorView,
    editorViewModel: EditorViewModel
) {
    val toolBar = FlowPane() //ToolBar()

    init {
        toolBar.orientation = Orientation.VERTICAL

        val toggleGroup = ToggleGroup()

        toggleGroup.selectedToggleProperty()
            .addListener { observable: ObservableValue<out Toggle?>?, oldToggle: Toggle?, newToggle: Toggle? ->
                if (newToggle == null) {
                    toggleGroup.selectToggle(oldToggle)
                }
            }

        for (i in editorViewModel.tools.indices) {
            addTools(actionManager, toggleGroup, editorViewModel.tools[i])

            // add separator
            if (i < editorViewModel.tools.size - 1) {
                toolBar.children.add(Separator())
            }
        }

        // Undo and Redo buttons
        toolBar.children.add(Separator())
        val spacer = VBox()
        VBox.setVgrow(spacer, Priority.ALWAYS)

        toolBar.children.add(spacer)

        val undoButton = Button(
            ResourceHandler.Companion.undo,
            FontIcon.of(MaterialDesignU.UNDO, 24)
        )

        var toolTip = "%s (%s)".format(
            ResourceHandler.Companion.undo,
            Shortcuts.UNDO.get().displayText
        )

        undoButton.tooltip = Tooltip(toolTip)
        undoButton.onAction = EventHandler { event: ActionEvent? -> actionManager.undo() }

        //undoButton.getStyleClass().add(DEFAULT_TOOLBAR_ICON_STYLE_NAME);
        //undoButton.getStyleClass().add(UNDO_ICON_STYLE_NAME);
        val redoButton = Button(ResourceHandler.redo, FontIcon.of(MaterialDesignR.REDO, 24))
        toolTip = String.format("%s (%s)", ResourceHandler.redo, Shortcuts.REDO.get().displayText)
        redoButton.tooltip = Tooltip(toolTip)
        redoButton.onAction = EventHandler { event: ActionEvent? -> actionManager.redo() }
        redoButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY
        undoButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY
        //toolBar.children.addAll(undoButton, redoButton)
    }

    fun addTools(actionManager: ActionManager, toggleGroup: ToggleGroup, toolList: List<Tool>) {
        for (tool in toolList) {
            val toolType = tool.toolType
            val toolButton = ToggleButton(
                toolType.label,
                FontIcon.of(toolType.icon, 24)
            )
            toolButton.styleClass.add(DEFAULT_TOOLBAR_ICON_STYLE_NAME)

            //Would like to bind the selectedproperty of the button here but cannot because of a javafx bug
            editorView.viewModel.currentToolProperty.addListener { observable: ObservableValue<out Tool>?, oldValue: Tool?, newValue: Tool ->
                toolButton.isSelected = newValue === tool
            }
            toolButton.isSelected = editorView.viewModel.currentToolType == toolType

            toolButton.selectedProperty()
                .addListener { observable: ObservableValue<out Boolean>?, oldValue: Boolean?, newValue: Boolean ->
                    if (newValue) {
                        val action: Action = actionManager.actionFactory.createSelectToolAction(toolType)
                        actionManager.run(action)
                    }
                }

            //toolButton.getStyleClass().add(toolType.getIcon());
            val tooltip = Tooltip("%s (%s)".format(toolType.label, toolType.keyCodeCombination?.displayText))
            toolButton.tooltip = tooltip
            toolBar.children.add(toolButton)
            toggleGroup.toggles.add(toolButton)
        }
    }

    fun build(): javafx.scene.Node {
        return toolBar
    }

    companion object {
        const val DEFAULT_TOOLBAR_ICON_STYLE_NAME = "toolbar-icon"
        const val UNDO_ICON_STYLE_NAME = "undo-toolbar-icon"
        const val REDO_ICON_STYLE_NAME = "redo-toolbar-icon"
        const val BUTTON_SIZE = 30
    }
}
