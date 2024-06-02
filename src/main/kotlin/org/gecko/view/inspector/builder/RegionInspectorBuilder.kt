package org.gecko.view.inspector.builder

import org.gecko.actions.*
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.element.InspectorSeparator
import org.gecko.view.inspector.element.container.InspectorContractItem
import org.gecko.view.inspector.element.container.InspectorRegionColorItem
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.viewmodel.RegionViewModel

/**
 * Represents a type of [AbstractInspectorBuilder] of an [Inspector] for a [RegionViewModel]. Adds to
 * the list of [InspectorElement][org.gecko.view.inspector.element.InspectorElement]s, which are added to a built
 * [Inspector][org.gecko.view.inspector.Inspector], the following: an [InspectorRegionColorItem] and an
 * [InspectorContractItem].
 */
class RegionInspectorBuilder(actionManager: ActionManager, viewModel: RegionViewModel) :
    AbstractInspectorBuilder<RegionViewModel?>(actionManager, viewModel) {
    init {
        // Color
        addInspectorElement(InspectorRegionColorItem(actionManager, viewModel))
        addInspectorElement(InspectorSeparator())

        // Contracts
        addInspectorElement(InspectorLabel(ResourceHandler.Companion.contract))
        addInspectorElement(InspectorContractItem(actionManager, viewModel))
    }
}
