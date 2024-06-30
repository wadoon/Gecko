package org.gecko.view.inspector.element.list

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import org.gecko.actions.ActionManager
import org.gecko.view.inspector.element.container.InspectorContractItem
import org.gecko.viewmodel.Contract
import org.gecko.viewmodel.State

/**
 * A concrete representation of an [AbstractInspectorList] encapsulating an [InspectorContractItem].
 */
class InspectorContractList(actionManager: ActionManager, state: State) :
    AbstractInspectorList<InspectorContractItem>() {
    init {
        minHeight = MIN_HEIGHT
        val items: ObservableList<InspectorContractItem> = super.items
        val Contracts: ObservableList<Contract> = state.contractsProperty

        // Initialize inspector items
        for (contractViewModel in Contracts) {
            items.add(InspectorContractItem(actionManager, state, contractViewModel))
        }

        // Create a listener for contractViewModels changes and update inspector items accordingly
        val contractViewModelListener = ListChangeListener { change: ListChangeListener.Change<out Contract> ->
            while (change.next()) {
                if (change.wasAdded()) {
                    for (item in change.addedSubList) {
                        val newContractItem =
                            InspectorContractItem(actionManager, state, item)
                        newContractItem.prefWidthProperty().bind(widthProperty().subtract(CONTRACT_ITEM_OFFSET))
                        items.add(newContractItem)
                    }
                } else if (change.wasRemoved()) {
                    for (item in change.removed) {
                        items.removeIf { inspectorContractItem: InspectorContractItem -> inspectorContractItem.viewModel == item }
                    }
                }
            }
        }

        Contracts.addListener(contractViewModelListener)
    }

    companion object {
        const val MIN_HEIGHT = 50.0
        const val CONTRACT_ITEM_OFFSET = 20
    }
}
