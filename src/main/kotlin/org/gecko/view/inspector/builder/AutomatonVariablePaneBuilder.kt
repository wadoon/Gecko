package org.gecko.view.inspector.builder

import javafx.geometry.Insets
import javafx.scene.control.ScrollPane
import javafx.scene.layout.VBox
import org.gecko.actions.ActionManager
import org.gecko.model.Visibility
import org.gecko.view.inspector.element.container.InspectorVariableLabel
import org.gecko.view.inspector.element.list.InspectorVariableList
import org.gecko.viewmodel.SystemViewModel

class AutomatonVariablePaneBuilder(actionManager: ActionManager, systemViewModel: SystemViewModel) {
    val scrollPane = ScrollPane()

    init {
        scrollPane.prefWidth = VARIABLE_PANE_WIDTH.toDouble()
        scrollPane.prefHeight = VARIABLE_PANE_HEIGHT.toDouble()
        scrollPane.minHeight = VARIABLE_PANE_HEIGHT.toDouble()
        scrollPane.maxHeight = VARIABLE_PANE_HEIGHT.toDouble()

        val content = VBox()
        val inputLabel = InspectorVariableLabel(actionManager, systemViewModel, Visibility.INPUT)
        val inputList = InspectorVariableList(actionManager, systemViewModel, Visibility.INPUT)
        val outputLabel = InspectorVariableLabel(actionManager, systemViewModel, Visibility.OUTPUT)
        val outputList = InspectorVariableList(actionManager, systemViewModel, Visibility.OUTPUT)

        content.children
            .addAll(inputLabel.control, inputList.control, outputLabel.control, outputList.control)
        content.spacing = ELEMENT_SPACING.toDouble()
        scrollPane.isFitToWidth = true
        scrollPane.padding = Insets(ELEMENT_PADDING)
        scrollPane.content = content
    }

    fun build(): ScrollPane {
        return scrollPane
    }

    companion object {
        const val VARIABLE_PANE_WIDTH = 320
        const val VARIABLE_PANE_HEIGHT = 240
        const val ELEMENT_SPACING = 10
        const val ELEMENT_PADDING = ELEMENT_SPACING / 2.0
    }
}
