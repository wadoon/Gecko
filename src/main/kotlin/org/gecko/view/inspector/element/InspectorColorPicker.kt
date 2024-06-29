package org.gecko.view.inspector.element

import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.scene.control.ColorPicker
import javafx.scene.paint.Color
import org.gecko.actions.ActionManager
import org.gecko.viewmodel.RegionViewModel

/**
 * Represents a type of [ColorPicker], implementing the [InspectorElement] interface. Used for changing the
 * color of a displayed [RegionViewModel].
 */
class InspectorColorPicker(actionManager: ActionManager, regionViewModel: RegionViewModel) : ColorPicker(),
    InspectorElement<ColorPicker> {
    init {
        value = regionViewModel.color
        regionViewModel.colorProperty.addListener { _: ObservableValue<out Color?>?, _: Color?, newValue: Color? ->
            value = newValue
        }

        onAction = EventHandler {
            actionManager.run(
                actionManager.actionFactory
                    .createChangeColorRegion(regionViewModel, value)
            )
        }
    }

    override val control = this
}
