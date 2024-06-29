package org.gecko.view.inspector.element.container

import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.element.InspectorColorPicker
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.viewmodel.Region

/**
 * Represents a type of [LabeledInspectorElement]. Contains an [InspectorLabel] and an
 * [InspectorColorPicker].
 */
class InspectorRegionColorItem(actionManager: ActionManager, Region: Region) :
    LabeledInspectorElement(
        InspectorLabel(ResourceHandler.Companion.color),
        InspectorColorPicker(actionManager, Region)
    )
