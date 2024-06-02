package org.gecko.view.inspector.builder

import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.element.InspectorSeparator
import org.gecko.view.inspector.element.button.InspectorSetStartStateButton
import org.gecko.view.inspector.element.container.InspectorContractLabel
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.view.inspector.element.list.InspectorContractList
import org.gecko.view.inspector.element.list.InspectorRegionList
import org.gecko.viewmodel.EditorViewModel
import org.gecko.viewmodel.StateViewModel

/**
 * Represents a type of [AbstractInspectorBuilder] of an [Inspector][org.gecko.view.inspector.Inspector] for
 * a [StateViewModel]. Adds to the list of
 * [InspectorElement][org.gecko.view.inspector.element.InspectorElement]s, which are added to a built
 * [Inspector][org.gecko.view.inspector.Inspector], the following: an [InspectorLabel] for each
 * [RegionViewModel] of the [StateViewModel], an [InspectorSetStartStateButton] and an
 * [InspectorContractList].
 */
class StateInspectorBuilder(actionManager: ActionManager, editorViewModel: EditorViewModel, viewModel: StateViewModel) :
    AbstractInspectorBuilder<StateViewModel>(actionManager, viewModel) {
    init {
        // Region label
        addInspectorElement(InspectorLabel(ResourceHandler.region_plural))
        val regionViewModelList = editorViewModel.getRegionViewModels(viewModel)
        addInspectorElement(InspectorRegionList(regionViewModelList))

        addInspectorElement(InspectorSeparator())

        // Set start state
        addInspectorElement(InspectorSetStartStateButton(actionManager, viewModel))
        addInspectorElement(InspectorSeparator())

        // Contracts
        addInspectorElement(InspectorContractLabel(actionManager, viewModel))
        addInspectorElement(InspectorContractList(actionManager, viewModel))
    }
}
