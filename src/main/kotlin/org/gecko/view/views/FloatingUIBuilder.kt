package org.gecko.view.views

import javafx.beans.binding.Bindings
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.views.shortcuts.Shortcuts
import org.gecko.viewmodel.EditorViewModel
import org.gecko.viewmodel.PositionableViewModelElement
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign2.MaterialDesignA
import org.kordamp.ikonli.materialdesign2.MaterialDesignC
import org.kordamp.ikonli.materialdesign2.MaterialDesignM
import org.kordamp.ikonli.materialdesign2.MaterialDesignR
import java.util.function.Consumer

/**
 * Represents a builder for floating UI elements in the view, like different kinds of [Button]s and
 * [Label]s.
 */
class FloatingUIBuilder(val actionManager: ActionManager, val editorViewModel: EditorViewModel) {
    fun buildZoomButtons(): Node {
        val zoomButtons = VBox()

        val zoomInButton = createStyledButton()
        zoomInButton.graphic = FontIcon.of(MaterialDesignM.MAGNIFY_PLUS, 24)
        zoomInButton.styleClass.add(ZOOM_IN_STYLE_CLASS)
        zoomInButton.onAction = EventHandler<ActionEvent> { event: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createZoomCenterAction(EditorViewModel.defaultZoomStep)
            )
        }
        val zoomInTooltip = "%s (%s)".format(
            ResourceHandler.zoom_in,
            Shortcuts.ZOOM_IN.get().displayText
        )
        zoomInButton.tooltip = Tooltip(zoomInTooltip)

        val zoomLabel = Label()
        zoomLabel.textProperty().bind(Bindings.createStringBinding({
            val zoom = editorViewModel.zoomScale
            String.format("%.0f%%", zoom * 100)
        }, editorViewModel.zoomScaleProperty))

        val zoomOutButton = createStyledButton()
        zoomOutButton.graphic = FontIcon.of(MaterialDesignM.MAGNIFY_MINUS, 24)
        zoomOutButton.styleClass.add(ZOOM_OUT_STYLE_CLASS)
        zoomOutButton.onAction = EventHandler<ActionEvent> { event: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createZoomCenterAction(1 / EditorViewModel.defaultZoomStep)
            )
        }
        val zoomOutTooltip = "%s (%s)".format(
            ResourceHandler.zoom_out,
            Shortcuts.ZOOM_OUT.get().displayText
        )
        zoomOutButton.tooltip = Tooltip(zoomOutTooltip)

        zoomButtons.children.addAll(zoomInButton, zoomLabel, zoomOutButton)
        return zoomButtons
    }

    fun buildCurrentViewLabel(): Node {
        val currentViewLabel = Label()
        currentViewLabel.textProperty()
            .bind(
                Bindings.createStringBinding(
                    { editorViewModel.currentSystem.name },
                    editorViewModel.currentSystem.nameProperty
                )
            )
        return currentViewLabel
    }

    fun buildSearchWindow(editorView: EditorView): Node {
        val searchBar = ToolBar()

        // Close Search:
        val closeButton = Button(CLOSE_BUTTON)
        closeButton.isCancelButton = true

        // Navigate Search:
        val backwardButton = Button(LEFT_BUTTON)
        backwardButton.isDisable = true

        val forwardButton = Button(RIGHT_BUTTON)
        forwardButton.isDisable = true

        val matchesLabel = Label()
        matchesLabel.textFill = Color.BLACK

        val matches: MutableList<PositionableViewModelElement> = ArrayList()
        val searchTextField = TextField()
        searchTextField.promptText = ResourceHandler.search

        searchBar.items.addAll(closeButton, searchTextField, backwardButton, forwardButton, matchesLabel)

        searchTextField.onAction = EventHandler<ActionEvent> { e: ActionEvent? ->
            editorViewModel.selectionManager.deselectAll()
            val oldSearchMatches: List<PositionableViewModelElement> = ArrayList(matches)
            oldSearchMatches.forEach(Consumer { o: PositionableViewModelElement -> matches.remove(o) })
            matches.addAll(editorViewModel.getElementsByName(searchTextField.text))
            if (!matches.isEmpty()) {
                actionManager.run(
                    actionManager.actionFactory.createFocusPositionableViewModelElementAction(matches.first())
                )
                matchesLabel.text = String.format(ResourceHandler.matches_format_string, 1, matches.size)
                backwardButton.isDisable = true
                forwardButton.isDisable = matches.size == 1
            } else {
                matchesLabel.text = String.format(ResourceHandler.matches_format_string, 0, 0)
                backwardButton.isDisable = true
                forwardButton.isDisable = true
            }
        }

        searchBar.focusedProperty()
            .addListener { observable: ObservableValue<out Boolean>?, oldValue: Boolean?, newValue: Boolean ->
                if (newValue) {
                    searchTextField.requestFocus()
                }
            }

        forwardButton.onAction = EventHandler { e: ActionEvent? ->
            if (!matches.isEmpty()) {
                searchNextResult(matches, matchesLabel, backwardButton, forwardButton, 1)
            }
        }

        closeButton.onAction = EventHandler<ActionEvent> { e: ActionEvent? ->
            searchTextField.text = ""
            matchesLabel.text = ""
            editorView.activateSearchWindow(false)
        }

        return searchBar
    }

    fun searchNextResult(
        matches: List<PositionableViewModelElement>,
        matchesLabel: Label,
        backwardButton: Button,
        forwardButton: Button,
        direction: Int
    ) {
        var currentPosition = matches.indexOf(editorViewModel.focusedElement)
        actionManager.run(
            actionManager.actionFactory
                .createFocusPositionableViewModelElementAction(matches[currentPosition + direction])
        )
        currentPosition += direction
        matchesLabel.text = String.format(
            ResourceHandler.matches_format_string, currentPosition + 1,
            matches.size
        )
        backwardButton.isDisable = currentPosition == 0
        forwardButton.isDisable = currentPosition == matches.size - 1
    }

    fun buildViewSwitchButtons(): Node {
        val viewSwitchButtons = HBox()
        val switchToSystemStyleClass = "floating-switch-to-system-button"
        val switchToAutomatonStyleClass = "floating-switch-to-automaton-button"
        val switchToParentSystemStyleClass = "floating-parent-system-switch-button"

        val switchViewButton = createStyledButton()
        //switchViewButton.getStyleClass()
        //    .add(editorViewModel.isAutomatonEditor() ? switchToSystemStyleClass : switchToAutomatonStyleClass);
        switchViewButton.onAction = EventHandler { event: ActionEvent? ->
            val automatonEditor = editorViewModel.isAutomatonEditor
            actionManager.run(
                actionManager.actionFactory
                    .createViewSwitchAction(editorViewModel.currentSystem, !automatonEditor)
            )
            /*switchViewButton.getStyleClass()
                .remove(automatonEditor ? switchToAutomatonStyleClass : switchToSystemStyleClass);
            switchViewButton.getStyleClass()
                .add(automatonEditor ? switchToSystemStyleClass : switchToAutomatonStyleClass);*/
            switchViewButton.graphic = FontIcon.of(
                if (automatonEditor) MaterialDesignR.RECTANGLE_OUTLINE else MaterialDesignC.CIRCLE_OUTLINE, 24
            )
        }

        switchViewButton.graphic = FontIcon.of(
            if (editorViewModel.isAutomatonEditor) MaterialDesignR.RECTANGLE_OUTLINE else MaterialDesignC.CIRCLE_OUTLINE,
            24
        )

        val switchViewTooltip = "%s (%s)".format(
            ResourceHandler.switch_view,
            Shortcuts.SWITCH_EDITOR.get().displayText
        )
        switchViewButton.tooltip = Tooltip(switchViewTooltip)

        viewSwitchButtons.children.add(switchViewButton)

        if (editorViewModel.parentSystem != null) {
            val parentSystemSwitchButton = createStyledButton()
            parentSystemSwitchButton.graphic = FontIcon.of(MaterialDesignA.ARROW_TOP_LEFT, 24)

            //parentSystemSwitchButton.getStyleClass().add(switchToParentSystemStyleClass);
            parentSystemSwitchButton.onAction = EventHandler { event: ActionEvent? ->
                actionManager.run(
                    actionManager.actionFactory
                        .createViewSwitchAction(editorViewModel.parentSystem, editorViewModel.isAutomatonEditor)
                )
            }
            val parentSystemSwitchTooltip =
                "%s (%s)".format(
                    ResourceHandler.parent_system,
                    Shortcuts.OPEN_PARENT_SYSTEM_EDITOR.get().displayText
                )
            parentSystemSwitchButton.tooltip = Tooltip(parentSystemSwitchTooltip)

            viewSwitchButtons.children.add(parentSystemSwitchButton)
        }


        return viewSwitchButtons
    }

    fun createStyledButton(): Button {
        val button = Button()
        button.styleClass.add(FLOATING_BUTTON_STYLE_CLASS)
        button.setPrefSize(DEFAULT_BUTTON_SIZE.toDouble(), DEFAULT_BUTTON_SIZE.toDouble())
        return button
    }

    companion object {
        const val DEFAULT_BUTTON_SIZE = 30
        const val FLOATING_BUTTON_STYLE_CLASS = "floating-ui-button"
        const val ZOOM_IN_STYLE_CLASS = "floating-zoom-in-button"
        const val ZOOM_OUT_STYLE_CLASS = "floating-zoom-out-button"
        const val CLOSE_BUTTON = "x"
        const val LEFT_BUTTON = "<"
        const val RIGHT_BUTTON = ">"
    }
}
