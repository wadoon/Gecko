package org.gecko.view.inspector.element

import javafx.scene.Node

/**
 * Represents a generic interface that encapsulates a subtype of [Node] displayed in an
 * [Inspector][org.gecko.view.inspector.Inspector]. The provided method must be implemented by
 * concrete inspector elements.
 */
interface InspectorElement<T : Node?> {
    val control: T

    companion object {
        const val FIELD_OFFSET: Int = 50
    }
}
