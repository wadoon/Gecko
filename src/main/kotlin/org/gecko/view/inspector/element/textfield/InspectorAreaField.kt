package org.gecko.view.inspector.element.textfield

import javafx.beans.Observable
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import javafx.scene.control.*
import org.gecko.actions.*
import org.gecko.view.inspector.element.InspectorElement

abstract class InspectorAreaField(
    actionManager: ActionManager,
    stringProperty: StringProperty,
    isEmptyAllowed: Boolean
) : TextArea(), InspectorElement<TextArea> {
    init {
        text = stringProperty.get()
        stringProperty.addListener { _: ObservableValue<out String?>?, _: String?, newValue: String? ->
            text = newValue
        }
        prefHeight = MAX_HEIGHT.toDouble()
        isWrapText = true

        focusedProperty().addListener { _: Observable? ->
            if ((text == null && stringProperty.get() == null) || (text != null && text == stringProperty.get()) || (stringProperty.get() != null && stringProperty.get() == text)) {
                return@addListener
            }
            if ((text == null || text.isEmpty()) && !isEmptyAllowed) {
                text = stringProperty.get()
            }
            actionManager.run(action)
        }
    }

    protected abstract val action: Action?

    override val control
        get() = this


    fun toggleExpand() {
        prefHeight = (if (prefHeight == MAX_HEIGHT.toDouble()) EXPANDED_MAX_HEIGHT else MAX_HEIGHT).toDouble()
    }

    companion object {
        const val MAX_HEIGHT = 40
        const val EXPANDED_MAX_HEIGHT = 90
    }
}
