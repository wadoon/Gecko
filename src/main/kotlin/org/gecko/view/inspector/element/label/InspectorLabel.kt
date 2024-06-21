package org.gecko.view.inspector.element.label

import javafx.scene.control.Label
import org.gecko.view.inspector.element.InspectorElement

/**
 * Represents a type of [Label] implementing the [InspectorElement] interface.
 */
class InspectorLabel(text: String?) : Label(text), InspectorElement<Label> {
    init {
        styleClass.add(STYLE_NAME)
    }

    override val control = this

    companion object {
        const val STYLE_NAME = "inspector-label"
    }
}
