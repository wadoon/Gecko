package org.gecko.view.inspector.element.container

import org.gecko.actions.ActionManager
import org.gecko.view.inspector.element.button.InspectorFocusButton
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.viewmodel.State

/**
 * Represents a type of [LabeledInspectorElement]. Contains an [InspectorLabel] and an
 * [InspectorFocusButton] for a [State].
 */
class InspectorEdgeStateLabel(actionManager: ActionManager, state: State, name: String) :
    LabeledInspectorElement(
        InspectorLabel(name + ": " + state.name),
        InspectorFocusButton(actionManager, state)
    )
