package org.gecko.view.inspector.element.textfield

import org.gecko.actions.Action
import org.gecko.actions.ActionManager
import org.gecko.viewmodel.Renamable

/**
 * A concrete representation of an [InspectorTextField] for a [Renamable], through which the name of the
 * element can be changed.
 */
class InspectorRenameField(actionManager: ActionManager, val renamable: Renamable) :
    InspectorTextField(renamable.nameProperty, actionManager) {
    override val action: Action
        get() = actionManager.actionFactory.createRenameViewModelElementAction(renamable, text)
}
