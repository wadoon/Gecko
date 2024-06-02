package org.gecko.view.inspector.element.container

import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.element.InspectorColorPicker
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.viewmodel.RegionViewModel

/**
 * Represents a type of [LabeledInspectorElement]. Contains an [InspectorLabel] and an
 * [InspectorColorPicker].
 */
class InspectorRegionColorItem(actionManager: ActionManager, regionViewModel: RegionViewModel) :
    LabeledInspectorElement(
        InspectorLabel(ResourceHandler.Companion.color),
        InspectorColorPicker(actionManager, regionViewModel)
    )
