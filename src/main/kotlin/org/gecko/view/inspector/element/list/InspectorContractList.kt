package org.gecko.view.inspector.element.list

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import org.gecko.actions.ActionManager
import org.gecko.view.inspector.element.container.InspectorContractItem
import org.gecko.viewmodel.ContractViewModel
import org.gecko.viewmodel.StateViewModel

/**
 * A concrete representation of an [AbstractInspectorList] encapsulating an [InspectorContractItem].
 */
class InspectorContractList(actionManager: ActionManager, stateViewModel: StateViewModel) :
    AbstractInspectorList<InspectorContractItem>() {
    init {
        minHeight = MIN_HEIGHT
        val items: ObservableList<InspectorContractItem> = super.items
        val contractViewModels: ObservableList<ContractViewModel> = stateViewModel.contractsProperty

        // Initialize inspector items
        for (contractViewModel in contractViewModels) {
            items.add(InspectorContractItem(actionManager, stateViewModel, contractViewModel))
        }

        // Create a listener for contractViewModels changes and update inspector items accordingly
        val contractViewModelListener = ListChangeListener { change: ListChangeListener.Change<out ContractViewModel> ->
            while (change.next()) {
                if (change.wasAdded()) {
                    for (item in change.addedSubList) {
                        val newContractItem =
                            InspectorContractItem(actionManager, stateViewModel, item)
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

        contractViewModels.addListener(contractViewModelListener)
    }

    companion object {
        const val MIN_HEIGHT = 50.0
        const val CONTRACT_ITEM_OFFSET = 20
    }
}
