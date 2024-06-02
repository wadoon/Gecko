package org.gecko.view.inspector.element.list

import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.layout.VBox

import org.gecko.view.inspector.element.InspectorElement

/**
 * An abstract representation of a [ListView] encapsulating a type of [InspectorElement].
 */

abstract class AbstractInspectorList<T : InspectorElement<out Node>> protected constructor() : VBox(),
    InspectorElement<VBox> {
    protected var items: ObservableList<T>

    init {
        styleClass.add(STYLE_CLASS)
        spacing = SPACING
        items = FXCollections.observableArrayList()

        items.addListener { change: ListChangeListener.Change<out T> ->
            while (change.next()) {
                if (change.wasAdded()) {
                    val added = change.addedSubList.map { it.control }
                    children.addAll(added)
                } else if (change.wasRemoved()) {
                    val removed = change.removed.map { it.control }
                    children.removeAll(removed)
                }
            }
        }
    }

    override val control get() = this

    companion object {
        const val SPACING = 5.0
        const val STYLE_CLASS = "inspector-list"
    }
}
