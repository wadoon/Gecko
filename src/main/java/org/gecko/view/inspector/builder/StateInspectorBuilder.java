package org.gecko.view.inspector.builder;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import org.gecko.actions.ActionManager;
import org.gecko.view.inspector.element.InspectorSeparator;
import org.gecko.view.inspector.element.button.InspectorSetStartStateButton;
import org.gecko.view.inspector.element.container.InspectorContractLabel;
import org.gecko.view.inspector.element.label.InspectorLabel;
import org.gecko.view.inspector.element.list.InspectorContractList;
import org.gecko.view.inspector.element.list.InspectorRegionList;
import org.gecko.viewmodel.EditorViewModel;
import org.gecko.viewmodel.RegionViewModel;
import org.gecko.viewmodel.StateViewModel;

public class StateInspectorBuilder extends AbstractInspectorBuilder<StateViewModel> {

    private final List<InspectorLabel> regionLabels;

    public StateInspectorBuilder(
        ActionManager actionManager, EditorViewModel editorViewModel, StateViewModel viewModel) {
        super(actionManager, viewModel);

        regionLabels = new ArrayList<>();

        // Region label
        addInspectorElement(new InspectorLabel("Regions"));
        ObservableList<RegionViewModel> regionViewModelList = editorViewModel.getRegionViewModels(viewModel);
        addInspectorElement(new InspectorRegionList(regionViewModelList));

        addInspectorElement(new InspectorSeparator());

        // Set start state
        addInspectorElement(new InspectorSetStartStateButton(actionManager, viewModel));
        addInspectorElement(new InspectorSeparator());

        // Contracts
        addInspectorElement(new InspectorContractLabel(actionManager, viewModel));
        addInspectorElement(new InspectorContractList(actionManager, viewModel));
    }
}
