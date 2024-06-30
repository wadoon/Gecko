package org.gecko.view.inspector

import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.gecko.actions.ActionManager
import org.gecko.view.inspector.element.InspectorElement
import org.gecko.view.inspector.element.button.AbstractInspectorButton
import org.gecko.view.inspector.element.button.InspectorSelectionBackwardButton
import org.gecko.view.inspector.element.button.InspectorSelectionForwardButton

/**
 * Represents a [ScrollPane] that encapsulates [InspectorElement]s, which allow the properties of
 * Gecko elements to be modified from the view.
 */
class Inspector(elements: List<InspectorElement<*>>, actionManager: ActionManager) : ScrollPane() {
    init {
        val vBox = VBox()
        prefWidth = INSPECTOR_WIDTH.toDouble()

        // Inspector decorations
        val inspectorDecorations = HBox()

        // Selection forward/backward buttons
        val selectionButtons = HBox()
        val selectionBackwardButton: AbstractInspectorButton =
            InspectorSelectionBackwardButton(actionManager)
        val selectionForwardButton: AbstractInspectorButton =
            InspectorSelectionForwardButton(actionManager)
        selectionButtons.children.addAll(selectionBackwardButton, selectionForwardButton)

        HBox.setHgrow(selectionButtons, Priority.ALWAYS)
        inspectorDecorations.children.addAll(selectionButtons)

        vBox.children.add(inspectorDecorations)

        for (element in elements) {
            vBox.children.add(element.control)
        }
        padding = Insets(INSPECTOR_ELEMENT_PADDING)
        vBox.spacing = INSPECTOR_ELEMENT_SPACING.toDouble()
        styleClass.add(INSPECTOR_STYLE_NAME)
        content = vBox
    }

    val view: Node
        get() = this

    override fun requestFocus() {}

    companion object {
        const val INSPECTOR_ELEMENT_SPACING = 10
        const val INSPECTOR_ELEMENT_PADDING = INSPECTOR_ELEMENT_SPACING / 2.0
        const val INSPECTOR_WIDTH = 320
        const val INSPECTOR_STYLE_NAME = "inspector"
    }
}
