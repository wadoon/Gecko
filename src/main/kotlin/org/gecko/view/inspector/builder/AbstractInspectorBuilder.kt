package org.gecko.view.inspector.builder


import org.gecko.actions.ActionManager
import org.gecko.view.inspector.Inspector
import org.gecko.view.inspector.element.InspectorElement
import org.gecko.view.inspector.element.InspectorSeparator
import org.gecko.view.inspector.element.button.InspectorDeleteButton
import org.gecko.view.inspector.element.container.LabeledInspectorElement
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.view.inspector.element.textfield.InspectorRenameField
import org.gecko.viewmodel.PositionableViewModelElement
import org.gecko.viewmodel.Renamable

/**
 * An abstract representation of a generic builder for an [Inspector] that corresponds to a
 * [PositionableViewModelElement]. Holds a reference to the [ActionManager], which allows for operations to
 * be run from the inspector, and a list of [InspectorElement]s, which are added to a built [Inspector].
 */
abstract class AbstractInspectorBuilder<T : PositionableViewModelElement<*>?> protected constructor(
    val actionManager: ActionManager,
    val viewModel: T
) {
    val inspectorElements: MutableList<InspectorElement<*>> = ArrayList()

    init {
        // Name field if applicable
        try {
            val renameField = InspectorRenameField(actionManager, viewModel as Renamable)
            val label = InspectorLabel("Name")
            addInspectorElement(LabeledInspectorElement(label, renameField))
            addInspectorElement(InspectorSeparator())
        } catch (e: ClassCastException) {
            // Do nothing
        }
    }

    protected fun addInspectorElement(element: InspectorElement<*>) {
        inspectorElements.add(element)
    }

    protected fun removeInspectorElement(element: InspectorElement<*>) {
        inspectorElements.remove(element)
    }

    fun build(): Inspector {
        // Element delete button
        inspectorElements.add(InspectorDeleteButton(actionManager, viewModel))
        return Inspector(inspectorElements, actionManager)
    }
}
