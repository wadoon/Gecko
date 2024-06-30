package org.gecko.view.inspector.element.container

import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.element.button.InspectorAddContractButton
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.viewmodel.State

/**
 * Represents a type of [LabeledInspectorElement]. Contains an [InspectorLabel] and an
 * [InspectorAddContractButton].
 */
class InspectorContractLabel(actionManager: ActionManager, viewModel: State) :
    LabeledInspectorElement(
        InspectorLabel(ResourceHandler.contract_plural),
        InspectorAddContractButton(actionManager, viewModel)
    )
