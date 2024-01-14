package org.gecko.view.inspector.element.container;

import javafx.scene.layout.HBox;
import org.gecko.view.inspector.InspectorColorPicker;
import org.gecko.view.inspector.element.InspectorElement;
import org.gecko.view.inspector.element.label.InspectorLabel;
import org.gecko.viewmodel.RegionViewModel;

public class InspectorRegionColorItem extends HBox implements InspectorElement<HBox> {

    public InspectorRegionColorItem(RegionViewModel regionViewModel) {
        getChildren()
                .addAll(
                        new InspectorLabel("L: Color"),
                        new InspectorColorPicker(regionViewModel.getColorProperty()));
    }

    @Override
    public HBox getControl() {
        return this;
    }
}
