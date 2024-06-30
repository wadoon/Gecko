package org.gecko.view.inspector.builder


import javafx.geometry.Insets
import javafx.scene.control.ScrollPane
import javafx.scene.layout.VBox
import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.Inspector
import org.gecko.view.inspector.element.InspectorElement
import org.gecko.view.inspector.element.InspectorSeparator
import org.gecko.view.inspector.element.button.InspectorDeleteButton
import org.gecko.view.inspector.element.button.InspectorOpenSystemButton
import org.gecko.view.inspector.element.button.InspectorSetStartStateButton
import org.gecko.view.inspector.element.combobox.InspectorContractComboBox
import org.gecko.view.inspector.element.container.*
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.view.inspector.element.list.InspectorContractList
import org.gecko.view.inspector.element.list.InspectorVariableList
import org.gecko.view.inspector.element.textfield.InspectorRenameField
import org.gecko.view.inspector.element.textfield.InspectorVariableValueField
import org.gecko.viewmodel.*

/**
 * An abstract representation of a generic builder for an [Inspector] that corresponds to a
 * [PositionableElement]. Holds a reference to the [ActionManager], which allows for operations to
 * be run from the inspector, and a list of [InspectorElement]s, which are added to a built [Inspector].
 */
abstract class AbstractInspectorBuilder<T : PositionableElement?> protected constructor(
    val actionManager: ActionManager,
    val viewModel: T
) {
    private val inspectorElements: MutableList<InspectorElement<*>> = ArrayList()

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


class AutomatonVariablePaneBuilder(actionManager: ActionManager, System: System) {
    private val scrollPane = ScrollPane()

    init {
        scrollPane.prefWidth = VARIABLE_PANE_WIDTH.toDouble()
        scrollPane.prefHeight = VARIABLE_PANE_HEIGHT.toDouble()
        scrollPane.minHeight = VARIABLE_PANE_HEIGHT.toDouble()
        scrollPane.maxHeight = VARIABLE_PANE_HEIGHT.toDouble()

        val content = VBox()
        val inputLabel = InspectorVariableLabel(actionManager, System, Visibility.INPUT)
        val inputList = InspectorVariableList(actionManager, System, Visibility.INPUT)
        val outputLabel = InspectorVariableLabel(actionManager, System, Visibility.OUTPUT)
        val outputList = InspectorVariableList(actionManager, System, Visibility.OUTPUT)

        content.children
            .addAll(inputLabel.control, inputList.control, outputLabel.control, outputList.control)
        content.spacing = ELEMENT_SPACING.toDouble()
        scrollPane.isFitToWidth = true
        scrollPane.padding = Insets(ELEMENT_PADDING)
        scrollPane.content = content
    }

    fun build(): ScrollPane {
        return scrollPane
    }

    companion object {
        const val VARIABLE_PANE_WIDTH = 320
        const val VARIABLE_PANE_HEIGHT = 240
        const val ELEMENT_SPACING = 10
        const val ELEMENT_PADDING = ELEMENT_SPACING / 2.0
    }
}

/**
 * Represents a type of [AbstractInspectorBuilder] of an [Inspector][org.gecko.view.inspector.Inspector] for
 * an [Edge]. Adds to the list of
 * [InspectorElement][org.gecko.view.inspector.element.InspectorElement]s, which are added to a built
 * [Inspector][org.gecko.view.inspector.Inspector], the following: an [InspectorKindPicker], two
 * [InspectorEdgeStateLabel]s for the source- and target-states, an [InspectorPriorityLabel] and an
 * [InspectorContractComboBox].
 */
class EdgeInspectorBuilder(actionManager: ActionManager, viewModel: Edge) :
    AbstractInspectorBuilder<Edge?>(actionManager, viewModel) {
    init {
        // Kind
        addInspectorElement(InspectorKindPicker(actionManager, viewModel))
        addInspectorElement(InspectorSeparator())

        // Connected states
        addInspectorElement(
            InspectorEdgeStateLabel(
                actionManager, viewModel.source!!,
                ResourceHandler.source
            )
        )
        addInspectorElement(
            InspectorEdgeStateLabel(
                actionManager, viewModel.destination!!,
                ResourceHandler.target
            )
        )
        addInspectorElement(InspectorSeparator())

        // Priority
        addInspectorElement(InspectorPriorityLabel(actionManager, viewModel))
        addInspectorElement(InspectorSeparator())

        // Contracts
        addInspectorElement(InspectorLabel(ResourceHandler.contract_plural))
        addInspectorElement(InspectorContractComboBox(actionManager, viewModel))
    }
}


/**
 * Represents a type of [AbstractInspectorBuilder] of an [Inspector] for a [Region]. Adds to
 * the list of [InspectorElement][org.gecko.view.inspector.element.InspectorElement]s, which are added to a built
 * [Inspector][org.gecko.view.inspector.Inspector], the following: an [InspectorRegionColorItem] and an
 * [InspectorContractItem].
 */
class RegionInspectorBuilder(actionManager: ActionManager, viewModel: Region) :
    AbstractInspectorBuilder<Region?>(actionManager, viewModel) {
    init {
        // Color
        addInspectorElement(InspectorRegionColorItem(actionManager, viewModel))
        addInspectorElement(InspectorSeparator())

        // Contracts
        addInspectorElement(InspectorLabel(ResourceHandler.contract))
        addInspectorElement(InspectorContractItem(actionManager, viewModel))
    }
}


/**
 * Represents a type of [AbstractInspectorBuilder] of an [Inspector][org.gecko.view.inspector.Inspector] for
 * a [State]. Adds to the list of
 * [InspectorElement][org.gecko.view.inspector.element.InspectorElement]s, which are added to a built
 * [Inspector][org.gecko.view.inspector.Inspector], the following: an [InspectorLabel] for each
 * [Region] of the [State], an [InspectorSetStartStateButton] and an
 * [InspectorContractList].
 */
class StateInspectorBuilder(actionManager: ActionManager, viewModel: State) :
    AbstractInspectorBuilder<State>(actionManager, viewModel) {
    init {
        // Region label
        addInspectorElement(InspectorLabel(ResourceHandler.region_plural))

        //val regionViewModelList = editorViewModel.getRegionViewModels(viewModel)
        //addInspectorElement(InspectorRegionList(regionViewModelList))

        addInspectorElement(InspectorSeparator())

        // Set start state
        addInspectorElement(InspectorSetStartStateButton(actionManager, viewModel))
        addInspectorElement(InspectorSeparator())

        // Contracts
        addInspectorElement(InspectorContractLabel(actionManager, viewModel))
        addInspectorElement(InspectorContractList(actionManager, viewModel))
    }
}

/**
 * Represents a type of [AbstractInspectorBuilder] of an [Inspector][org.gecko.view.inspector.Inspector] for
 * a [Port]. Adds to the list of
 * [InspectorElement][org.gecko.view.inspector.element.InspectorElement]s, which are added to a built
 * [Inspector][org.gecko.view.inspector.Inspector], the following: an [InspectorVisibilityPicker] and an
 * [InspectorTypeLabel].
 */
class VariableBlockInspectorBuilder(actionManager: ActionManager, viewModel: Port) :
    AbstractInspectorBuilder<Port?>(actionManager, viewModel) {
    init {
        // Visibility
        addInspectorElement(InspectorVisibilityPicker(actionManager, viewModel))
        addInspectorElement(InspectorSeparator())

        // Type
        addInspectorElement(InspectorTypeLabel(actionManager, viewModel))

        // Value
        addInspectorElement(InspectorLabel(ResourceHandler.variable_value))
        addInspectorElement(InspectorVariableValueField(actionManager, viewModel))
    }
}


/**
 * Represents a type of [AbstractInspectorBuilder] of an [Inspector][org.gecko.view.inspector.Inspector] for
 * a [System]. Adds to the list of
 * [InspectorElement][org.gecko.view.inspector.element.InspectorElement]s, which are added to a built
 * [Inspector][org.gecko.view.inspector.Inspector], the following: an [InspectorOpenSystemButton] and two
 * [InspectorVariableList]s for input- and output-[PortViewModel][org.gecko.viewmodel.Port]s of the
 * [System].
 */
class SystemInspectorBuilder(actionManager: ActionManager, viewModel: System) :
    AbstractInspectorBuilder<System?>(actionManager, viewModel) {
    init {
        // Open system button
        addInspectorElement(InspectorOpenSystemButton(actionManager, viewModel))

        addInspectorElement(InspectorSeparator())

        // Variables
        addInspectorElement(InspectorVariableLabel(actionManager, viewModel, Visibility.INPUT))
        addInspectorElement(InspectorVariableList(actionManager, viewModel, Visibility.INPUT))

        addInspectorElement(InspectorVariableLabel(actionManager, viewModel, Visibility.OUTPUT))
        addInspectorElement(InspectorVariableList(actionManager, viewModel, Visibility.OUTPUT))

        addInspectorElement(InspectorCodeSystemContainer(actionManager, viewModel))
    }
}
