package org.gecko.view.inspector.element.button

import javafx.event.ActionEvent
import javafx.event.EventHandler
import org.gecko.view.inspector.element.textfield.InspectorAreaField
import java.util.function.Consumer

/**
 * Represents a type of [AbstractInspectorButton] used for expanding or collapsing a list of
 * [InspectorContractItem][org.gecko.view.inspector.element.container.InspectorContractItem].
 */
class InspectorCollapseContractButton(fields: List<InspectorAreaField>) : AbstractInspectorButton() {
    var expanded = false

    init {
        styleClass.add(ICON_STYLE_NAME)
        setPrefSize(DEFAULT_SIZE.toDouble(), DEFAULT_SIZE.toDouble())

        onAction = EventHandler { event: ActionEvent? ->
            fields.forEach(Consumer { obj: InspectorAreaField -> obj.toggleExpand() })
            expanded = !expanded
            if (expanded) {
                styleClass.remove(ICON_STYLE_NAME)
                styleClass.add(ICON_STYLE_NAME_EXPANDED)
            } else {
                styleClass.remove(ICON_STYLE_NAME_EXPANDED)
                styleClass.add(ICON_STYLE_NAME)
            }
        }
    }

    companion object {
        const val ICON_STYLE_NAME = "inspector-contract-expand-button"
        const val ICON_STYLE_NAME_EXPANDED = "inspector-contract-expanded-button"
        const val DEFAULT_SIZE = 18
    }
}
