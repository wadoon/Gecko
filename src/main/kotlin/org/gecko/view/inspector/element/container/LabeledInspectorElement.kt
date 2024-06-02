package org.gecko.view.inspector.element.container

import javafx.geometry.Pos
import javafx.scene.layout.*
import org.gecko.view.inspector.element.InspectorElement
import org.gecko.view.inspector.element.label.InspectorLabel

/**
 * Represents a type of [HBox] implementing the [InspectorElement] interface. Contains an
 * [InspectorLabel] and the labeled [InspectorElement].
 */
open class LabeledInspectorElement(label: InspectorLabel, element: InspectorElement<*>) : HBox(),
    InspectorElement<HBox> {
    init {
        val spacer = HBox()
        setHgrow(spacer, Priority.ALWAYS)
        val labelBox = VBox()
        labelBox.children.add(label.control)
        labelBox.alignment = Pos.CENTER
        children.addAll(labelBox, spacer, element.control)
    }

    override val control
        get() = this
}
