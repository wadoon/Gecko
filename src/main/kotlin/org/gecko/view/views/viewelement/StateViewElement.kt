package org.gecko.view.views.viewelement

import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox

import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.ContractViewModel
import org.gecko.viewmodel.StateViewModel

/**
 * Represents a type of [BlockViewElement] implementing the [ViewElement] interface, which encapsulates an
 * [StateViewModel].
 */

class StateViewElement(stateViewModel: StateViewModel) : BlockViewElement(stateViewModel), ViewElement<StateViewModel> {
    override val target: StateViewModel = stateViewModel
    val nameProperty: StringProperty = SimpleStringProperty()
    val isStartStateProperty: BooleanProperty = SimpleBooleanProperty()
    val contractsProperty: ListProperty<ContractViewModel> = SimpleListProperty()

    override var isSelected: Boolean = false

    init {
        bindViewModel()
        constructVisualization()
    }

    override fun drawElement(): Node {
        return this
    }

    override val position: Point2D
        get() = position

    override fun accept(visitor: ViewElementVisitor) {
        visitor.visit(this)
    }

    fun constructVisualization() {
        styleClass.add(STYLE)

        val contents = VBox()
        contents.prefWidthProperty().bind(prefWidthProperty())
        contents.prefHeightProperty().bind(prefHeightProperty())
        contents.padding = Insets(SPACING.toDouble())

        // State name:
        val stateName = Pane()
        colorStateName(stateName)
        // Color the state name according to its type
        isStartStateProperty.addListener { observable: ObservableValue<out Boolean?>?, oldValue: Boolean?, newValue: Boolean? ->
            colorStateName(stateName)
        }
        stateName.styleClass.add(INNER_STYLE)

        val name = Label(target.name)
        name.textProperty().bind(target.nameProperty)

        // center the label
        name.layoutXProperty()
            .bind(
                Bindings.createDoubleBinding(
                    { (stateName.width - name.width) / 2 },
                    stateName.widthProperty(), name.widthProperty()
                )
            )
        name.layoutYProperty()
            .bind(
                Bindings.createDoubleBinding(
                    { (stateName.height - name.height) / 2 },
                    stateName.heightProperty(), name.heightProperty()
                )
            )

        stateName.children.add(name)

        contents.children.add(stateName)
        contents.children.add(Separator())

        // Contracts
        val contracts: Labeled = Label(
            ResourceHandler.contract_plural + ": " + contractsProperty
                .size
        )
        contracts.textProperty()
            .bind(Bindings.createStringBinding({
                (ResourceHandler.contract_plural + ": "
                        + contractsProperty.size)
            }, contractsProperty))

        contents.children.add(contracts)

        val contractsPane = VBox()

        refreshContracts(contractsPane)
        contractsProperty.addListener { observable: ObservableValue<out ObservableList<ContractViewModel?>>?, oldValue: ObservableList<ContractViewModel?>?, newValue: ObservableList<ContractViewModel?>? ->
            refreshContracts(
                contractsPane
            )
        }

        contents.children.add(contractsPane)
        children.addAll(contents)
    }

    fun refreshContracts(contractsPane: VBox) {
        contractsPane.children.clear()
        contractsPane.spacing = SPACING.toDouble()

        for (i in 0 until MAX_CONTRACT_CNT) {
            if (i >= contractsProperty.size) {
                break
            }

            val contract = contractsProperty[i]
            val contractBox = VBox()
            contractBox.styleClass.add(INNER_INNER_STYLE)

            val contractLabel = Label(contract.name)
            contractLabel.textProperty().bind(contract.nameProperty)

            val preconditionBox = HBox()
            val preconditionLabel = Label(ResourceHandler.pre_condition_short + ": ")
            val precondition = Label(contract.preCondition.value)
            precondition.textProperty().bind(contract.preCondition.valueProperty)
            preconditionBox.children.addAll(preconditionLabel, precondition)

            val postconditionBox = HBox()
            val postconditionLabel = Label(ResourceHandler.post_condition_short + ": ")
            val postcondition = Label(contract.postCondition.value)
            postcondition.textProperty().bind(contract.postCondition.valueProperty)
            postconditionBox.children.addAll(postconditionLabel, postcondition)

            contractBox.children.addAll(contractLabel, preconditionBox, postconditionBox)
            contractsPane.children.add(contractBox)
        }
    }

    fun colorStateName(stateName: Pane) {
        if (isStartStateProperty.value) {
            stateName.styleClass.clear()
            stateName.styleClass.add(START_STATE_STYLE)
        } else {
            stateName.styleClass.clear()
            stateName.styleClass.add(NON_START_STATE_STYLE)
        }
    }

    fun bindViewModel() {
        nameProperty.bind(target.nameProperty)
        isStartStateProperty.bind(target.isStartStateProperty)
        contractsProperty.bind(target.contractsProperty)
        prefWidthProperty().bind(
            Bindings.createDoubleBinding({ target.size.x }, target.sizeProperty)
        )
        prefHeightProperty().bind(
            Bindings.createDoubleBinding({ target.size.y }, target.sizeProperty)
        )
    }

    override val zPriority: Int = 30

    companion object {
        const val SPACING = 5
        const val MAX_CONTRACT_CNT = 4
        const val START_STATE_STYLE = "state-view-element-start"
        const val NON_START_STATE_STYLE = "state-view-element-non-start"
        const val STYLE = "state-view-element"
        const val INNER_STYLE = "state-inner-view-element"
        const val INNER_INNER_STYLE = "state-inner-inner-view-element"
    }
}
