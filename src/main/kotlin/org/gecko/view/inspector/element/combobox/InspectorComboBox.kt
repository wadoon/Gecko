package org.gecko.view.inspector.element.combobox

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.ComboBox
import org.gecko.actions.Action
import org.gecko.actions.ActionManager
import org.gecko.view.inspector.element.InspectorElement

/**
 * An abstract representation of a [ComboBox] implementing the [InspectorElement] interface.
 */
abstract class InspectorComboBox<T>(actionManager: ActionManager, items: List<T>?, property: Property<T>) :
    ComboBox<T>(), InspectorElement<ComboBox<T>> {
    init {
        getItems().setAll(items)
        value = property.value
        property.addListener { observable: ObservableValue<out T>?, oldValue: T, newValue: T -> setValue(newValue) }
        onAction = EventHandler<ActionEvent> { event: ActionEvent? ->
            if (value == property.value) {
                return@EventHandler
            }
            actionManager.run(action)
        }
    }

    protected abstract val action: Action?
    override val control get() = this
}
