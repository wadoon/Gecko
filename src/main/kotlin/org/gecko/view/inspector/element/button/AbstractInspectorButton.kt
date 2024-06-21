package org.gecko.view.inspector.element.button

import javafx.scene.control.Button
import org.gecko.view.inspector.element.InspectorElement

/**
 * An abstract representation of a type of [Button], implementing the [InspectorElement] interface.
 */
abstract class AbstractInspectorButton : Button(), InspectorElement<Button> {
    init {
        styleClass.add(ICON_STYLE_NAME)
        setPrefSize(DEFAULT_SIZE.toDouble(), DEFAULT_SIZE.toDouble())
    }

    override val control get() = this

    companion object {
        const val ICON_STYLE_NAME = "inspector-button"
        const val DEFAULT_SIZE = 24
    }
}
