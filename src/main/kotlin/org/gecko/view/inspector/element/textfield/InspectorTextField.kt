package org.gecko.view.inspector.element.textfield

import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.*
import org.gecko.actions.*
import org.gecko.view.inspector.element.InspectorElement

/**
 * A concrete representation of an [TextField] implementing the [InspectorElement] interface, which
 * encapsulates a [TextField].
 */
abstract class InspectorTextField protected constructor(
    val stringProperty: StringProperty,
    val actionManager: ActionManager
) : TextField(), InspectorElement<TextField> {
    init {
        text = stringProperty.get()
        stringProperty.addListener { observable: ObservableValue<out String?>?, oldValue: String?, newValue: String? ->
            text = newValue
        }
        onAction = EventHandler { event: ActionEvent? ->
            updateText()
        }
        focusedProperty().addListener { observable: ObservableValue<out Boolean?>?, oldValue: Boolean?, newValue: Boolean? ->
            if (!newValue!!) {
                updateText()
            }
        }
    }

    protected open fun updateText() {
        if (text.isEmpty()) {
            text = stringProperty.get()
        }
        parent.requestFocus()
        if (text == stringProperty.get()) {
            return
        }
        actionManager.run(action)
        text = stringProperty.get()
    }

    protected abstract val action: Action?

    override val control
        get() = this
}
