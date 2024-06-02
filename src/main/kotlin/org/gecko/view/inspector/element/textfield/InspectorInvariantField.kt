package org.gecko.view.inspector.element.textfield

import org.gecko.actions.Action
import org.gecko.actions.ActionManager
import org.gecko.viewmodel.RegionViewModel

/**
 * A concrete representation of an [InspectorAreaField] for a [RegionViewModel], through which the invariant
 * of the region can be changed.
 */
class InspectorInvariantField(val actionManager: ActionManager, val regionViewModel: RegionViewModel) :
    InspectorAreaField(
        actionManager, regionViewModel.invariantProperty, false
    ) {
    override val action: Action
        get() = actionManager.actionFactory.createChangeInvariantViewModelElementAction(regionViewModel, text)
}
