package org.gecko.view.inspector.element.container

import javafx.scene.layout.*

import org.gecko.actions.*
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.element.InspectorElement
import org.gecko.view.inspector.element.button.InspectorCollapseContractButton
import org.gecko.view.inspector.element.button.InspectorRemoveContractButton
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.view.inspector.element.textfield.*
import org.gecko.viewmodel.ContractViewModel
import org.gecko.viewmodel.RegionViewModel
import org.gecko.viewmodel.StateViewModel

/**
 * Represents a type of [VBox] implementing the [InspectorElement] interface. Holds a reference to a
 * [ContractViewModel] and provides separate constructors for both a contract item of a
 * state-specific-[Inspector][org.gecko.view.inspector.Inspector] and a
 * region-specific-[Inspector][org.gecko.view.inspector.Inspector]. The [InspectorContractItem] contains thus
 * [InspectorPreconditionField]s, [InspectorPostconditionField]s and an {link InspectorInvariantField} in
 * the case of a region.
 */

class InspectorContractItem : VBox, InspectorElement<VBox> {
    var viewModel: ContractViewModel? = null

    /**
     * Constructor for the State contract item.
     *
     * @param actionManager     Action manager
     * @param stateViewModel    State view model
     * @param contractViewModel Contract view model
     */
    constructor(actionManager: ActionManager, stateViewModel: StateViewModel?, contractViewModel: ContractViewModel) {
        this.viewModel = contractViewModel

        // Contract fields:
        val contractFields: MutableList<InspectorAreaField> = ArrayList()

        val contractConditions = GridPane()

        val preConditionLabel = InspectorLabel(ResourceHandler.Companion.pre_condition)
        val preConditionField: InspectorAreaField = InspectorPreconditionField(actionManager, contractViewModel)
        contractFields.add(preConditionField)
        preConditionField.prefWidthProperty().bind(widthProperty().subtract(InspectorElement.Companion.FIELD_OFFSET))
        contractConditions.add(preConditionLabel, 0, 0)
        contractConditions.add(preConditionField, 1, 0)


        val postConditionLabel =
            InspectorLabel(ResourceHandler.Companion.post_condition)
        val postConditionField: InspectorAreaField = InspectorPostconditionField(actionManager, contractViewModel)
        contractFields.add(postConditionField)
        postConditionField.prefWidthProperty().bind(widthProperty().subtract(InspectorElement.Companion.FIELD_OFFSET))
        contractConditions.add(postConditionLabel, 0, 1)
        contractConditions.add(postConditionField, 1, 1)
        val contractNameBox = HBox()
        val deleteButtonSpacer = HBox()
        HBox.setHgrow(deleteButtonSpacer, Priority.ALWAYS)

        // Contract name
        contractNameBox.children
            .addAll(
                InspectorCollapseContractButton(contractFields),
                InspectorRenameField(actionManager, contractViewModel), deleteButtonSpacer,
                InspectorRemoveContractButton(actionManager, stateViewModel, contractViewModel)
            )

        // Build the contract item
        children.addAll(contractNameBox, contractConditions)
    }

    /**
     * Constructor for the Region contract item.
     *
     * @param actionManager   Action manager
     * @param regionViewModel Region view model
     */
    constructor(actionManager: ActionManager, regionViewModel: RegionViewModel) {
        val regionConditions = GridPane()
        addContractItem(
            ResourceHandler.Companion.pre_condition,
            InspectorPreconditionField(actionManager, regionViewModel.contract), 0, regionConditions
        )
        addContractItem(
            ResourceHandler.Companion.post_condition,
            InspectorPostconditionField(actionManager, regionViewModel.contract), 1, regionConditions
        )
        addContractItem(
            ResourceHandler.Companion.invariant,
            InspectorInvariantField(actionManager, regionViewModel), 2, regionConditions
        )

        // Build the contract item
        children.add(regionConditions)
    }

    fun addContractItem(label: String, field: InspectorAreaField, row: Int, gridPane: GridPane) {
        gridPane.add(InspectorLabel(label), 0, row)
        field.prefWidthProperty().bind(widthProperty().subtract(CONTRACT_FIELD_OFFSET))
        gridPane.add(field, 1, row)
    }

    override val control = this

    companion object {
        val CONTRACT_FIELD_OFFSET: Int = InspectorElement.Companion.FIELD_OFFSET + 10
    }
}
