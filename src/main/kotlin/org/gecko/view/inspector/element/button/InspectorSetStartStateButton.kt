package org.gecko.view.inspector.element.button

import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.ToggleButton
import javafx.scene.control.Tooltip
import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.element.InspectorElement
import org.gecko.viewmodel.State

/**
 * Represents a type of [AbstractInspectorButton] used for setting a [State] as start-state.
 */
class InspectorSetStartStateButton(actionManager: ActionManager, state: State) : ToggleButton(),
    InspectorElement<ToggleButton> {
    init {
        styleClass.add(START_STATE_STYLE)
        maxWidth = Double.MAX_VALUE
        text = ResourceHandler.Companion.set_start_state
        tooltip = Tooltip(ResourceHandler.Companion.set_start_state)
        update(state.isStartState)
        state.isStartStateProperty.addListener { observable: ObservableValue<out Boolean>?, oldValue: Boolean?, newValue: Boolean ->
            update(
                newValue
            )
        }

        onAction = EventHandler { event: ActionEvent? ->
            actionManager.run(
                actionManager.actionFactory.createSetStartStateViewModelElementAction(state)
            )
        }
    }

    fun update(newValue: Boolean) {
        isSelected = newValue
        isDisable = newValue
    }

    override val control get() = this

    companion object {
        const val START_STATE_STYLE = "inspector-start-state-button"
    }
}
