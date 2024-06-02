package org.gecko.view.inspector.element

import javafx.scene.control.*

/**
 * Represents a type of [Separator], implementing the [InspectorElement] interface. Serves as delimiter
 * between other inspector elements.
 */
class InspectorSeparator : Separator(), InspectorElement<Separator> {
    override val control get() = this
}
