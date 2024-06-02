package org.gecko.view.inspector.element.textfield

import javafx.beans.Observable
import javafx.event.EventHandler
import javafx.scene.control.Spinner
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import org.gecko.actions.ActionManager
import org.gecko.view.inspector.element.InspectorElement
import org.gecko.viewmodel.EdgeViewModel

/**
 * Represents a type of [Spinner] encapsulating an [Integer] and implementing the [InspectorElement]
 * interface. Used to change the priority of an [EdgeViewModel].
 */
class InspectorPriorityField(val actionManager: ActionManager, val edgeViewModel: EdgeViewModel) :
    Spinner<Int>(
        MIN_PRIORITY, MAX_PRIORITY, edgeViewModel.priority
    ), InspectorElement<Spinner<Int>> {
    init {
        isEditable = true
        editor.textProperty()
            .addListener { _, oldValue, newValue ->
                if (!newValue.matches("-?\\d*".toRegex())) {
                    editor.text = oldValue
                }
            }
        edgeViewModel.priorityProperty
            .addListener { event: Observable? -> valueFactory.setValue(edgeViewModel.priority) }


        onKeyPressed = EventHandler<KeyEvent> { event: KeyEvent ->
            if (event.code != KeyCode.ENTER) {
                return@EventHandler
            }
            parent.requestFocus()
            if (editor.text.isEmpty()) {
                editor.text = edgeViewModel.priority.toString()
                commitValue()
                return@EventHandler
            }
            if (value == edgeViewModel.priority) {
                return@EventHandler
            }
            actionManager.run(
                actionManager.actionFactory.createModifyEdgeViewModelPriorityAction(edgeViewModel, value!!)
            )
        }
    }

    override fun decrement(steps: Int) {
        super.decrement(steps)
        actionManager.run(
            actionManager.actionFactory.createModifyEdgeViewModelPriorityAction(edgeViewModel, value!!)
        )
    }

    override fun increment(steps: Int) {
        super.increment(steps)
        actionManager.run(
            actionManager.actionFactory.createModifyEdgeViewModelPriorityAction(edgeViewModel, value!!)
        )
    }

    override val control
        get() = this

    companion object {
        const val MAX_PRIORITY = 100
        const val MIN_PRIORITY = 0
    }
}
