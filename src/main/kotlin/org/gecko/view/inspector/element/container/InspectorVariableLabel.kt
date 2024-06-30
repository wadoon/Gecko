package org.gecko.view.inspector.element.container

import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.element.button.InspectorAddVariableButton
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.viewmodel.System
import org.gecko.viewmodel.Visibility

/**
 * Represents a type of [LabeledInspectorElement]. Contains an [InspectorLabel] and an
 * [InspectorAddVariableButton].
 */
class InspectorVariableLabel(
    actionManager: ActionManager,
    viewModel: System,
    visibility: Visibility
) :
    LabeledInspectorElement(
        InspectorLabel(getLabel(visibility)),
        InspectorAddVariableButton(actionManager, viewModel, visibility)
    ) {
    companion object {
        // Cant be public because it is called in the super constructor
        fun getLabel(visibility: Visibility): String {
            return when (visibility) {
                Visibility.INPUT -> ResourceHandler.Companion.input
                Visibility.OUTPUT -> ResourceHandler.Companion.output
                else -> ""
            }
        }
    }
}
