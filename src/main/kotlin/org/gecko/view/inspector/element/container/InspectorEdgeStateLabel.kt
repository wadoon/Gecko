package org.gecko.view.inspector.element.container

import org.gecko.actions.ActionManager
import org.gecko.view.inspector.element.button.InspectorFocusButton
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.viewmodel.StateViewModel

/**
 * Represents a type of [LabeledInspectorElement]. Contains an [InspectorLabel] and an
 * [InspectorFocusButton] for a [StateViewModel].
 */
class InspectorEdgeStateLabel(actionManager: ActionManager, stateViewModel: StateViewModel, name: String) :
    LabeledInspectorElement(
        InspectorLabel(name + ": " + stateViewModel.name),
        InspectorFocusButton(actionManager, stateViewModel)
    )
