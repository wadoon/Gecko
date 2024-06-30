package org.gecko.view.inspector.element.container

import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.element.InspectorElement
import org.gecko.view.inspector.element.button.InspectorCollapseContractButton
import org.gecko.view.inspector.element.button.InspectorRemoveContractButton
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.view.inspector.element.textfield.*
import org.gecko.viewmodel.Contract
import org.gecko.viewmodel.Region
import org.gecko.viewmodel.State

/**
 * Represents a type of [VBox] implementing the [InspectorElement] interface. Holds a reference to a
 * [Contract] and provides separate constructors for both a contract item of a
 * state-specific-[Inspector][org.gecko.view.inspector.Inspector] and a
 * region-specific-[Inspector][org.gecko.view.inspector.Inspector]. The [InspectorContractItem] contains thus
 * [InspectorPreconditionField]s, [InspectorPostconditionField]s and an {link InspectorInvariantField} in
 * the case of a region.
 */

class InspectorContractItem : VBox, InspectorElement<VBox> {
    var viewModel: Contract? = null

    /**
     * Constructor for the State contract item.
     *
     * @param actionManager     Action manager
     * @param state    State view model
     * @param Contract Contract view model
     */
    constructor(actionManager: ActionManager, state: State?, Contract: Contract) {
        this.viewModel = Contract

        // Contract fields:
        val contractFields: MutableList<InspectorAreaField> = ArrayList()

        val contractConditions = GridPane()

        val preConditionLabel = InspectorLabel(ResourceHandler.Companion.pre_condition)
        val preConditionField: InspectorAreaField = InspectorPreconditionField(actionManager, Contract)
        contractFields.add(preConditionField)
        preConditionField.prefWidthProperty().bind(widthProperty().subtract(InspectorElement.Companion.FIELD_OFFSET))
        contractConditions.add(preConditionLabel, 0, 0)
        contractConditions.add(preConditionField, 1, 0)


        val postConditionLabel =
            InspectorLabel(ResourceHandler.Companion.post_condition)
        val postConditionField: InspectorAreaField = InspectorPostconditionField(actionManager, Contract)
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
                InspectorRenameField(actionManager, Contract), deleteButtonSpacer,
                InspectorRemoveContractButton(actionManager, state, Contract)
            )

        // Build the contract item
        children.addAll(contractNameBox, contractConditions)
    }

    /**
     * Constructor for the Region contract item.
     *
     * @param actionManager   Action manager
     * @param Region Region view model
     */
    constructor(actionManager: ActionManager, Region: Region) {
        val regionConditions = GridPane()
        addContractItem(
            ResourceHandler.Companion.pre_condition,
            InspectorPreconditionField(actionManager, Region.contract), 0, regionConditions
        )
        addContractItem(
            ResourceHandler.Companion.post_condition,
            InspectorPostconditionField(actionManager, Region.contract), 1, regionConditions
        )
        addContractItem(
            ResourceHandler.Companion.invariant,
            InspectorInvariantField(actionManager, Region), 2, regionConditions
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
