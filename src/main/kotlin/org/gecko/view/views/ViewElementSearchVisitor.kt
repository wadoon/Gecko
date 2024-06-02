package org.gecko.view.views

import org.gecko.viewmodel.*

/**
 * Follows the visitor pattern, implementing the [PositionableViewModelElementVisitor] interface. Searches for
 * absolute or partial matches between a given [String] and the names of
 * [PositionableViewModelElement][org.gecko.viewmodel.PositionableViewModelElement]s, if present.
 */
class ViewElementSearchVisitor(val search: String) :
    PositionableViewModelElementVisitor<AbstractViewModelElement<*>?> {
    override fun visit(systemViewModel: SystemViewModel): SystemViewModel? {
        if (systemViewModel.name.lowercase().contains(search.lowercase())) {
            return systemViewModel
        }
        return null
    }

    override fun visit(regionViewModel: RegionViewModel): RegionViewModel? {
        if (regionViewModel.name.lowercase().contains(search.lowercase())) {
            return regionViewModel
        }
        return null
    }

    override fun visit(systemConnectionViewModel: SystemConnectionViewModel): SystemConnectionViewModel? {
        return null
    }

    override fun visit(edgeViewModel: EdgeViewModel): EdgeViewModel? {
        return null
    }

    override fun visit(stateViewModel: StateViewModel): StateViewModel? {
        if (stateViewModel.name.lowercase().contains(search.lowercase())) {
            return stateViewModel
        }
        for (contractViewModel in stateViewModel.contracts) {
            if (visit(contractViewModel)) {
                return stateViewModel
            }
        }
        return null
    }

    override fun visit(portViewModel: PortViewModel): PortViewModel? {
        if (portViewModel.name.lowercase().contains(search.lowercase())) {
            return portViewModel
        }
        return null
    }

    fun visit(contractViewModel: ContractViewModel): Boolean {
        return contractViewModel.name.lowercase().contains(search.lowercase())
    }
}
