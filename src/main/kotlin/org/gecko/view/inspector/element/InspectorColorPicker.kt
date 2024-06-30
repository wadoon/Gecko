package org.gecko.view.inspector.element

import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.scene.control.ColorPicker
import javafx.scene.paint.Color
import org.gecko.actions.ActionManager
import org.gecko.viewmodel.Region

/**
 * Represents a type of [ColorPicker], implementing the [InspectorElement] interface. Used for
 * changing the color of a displayed [Region].
 */
class InspectorColorPicker(actionManager: ActionManager, Region: Region) :
    ColorPicker(), InspectorElement<ColorPicker> {
    init {
        value = Region.color
        Region.colorProperty.addListener {
            _: ObservableValue<out Color?>?,
            _: Color?,
            newValue: Color? ->
            value = newValue
        }

        onAction = EventHandler {
            actionManager.run(actionManager.actionFactory.createChangeColorRegion(Region, value))
        }
    }

    override val control = this
}
