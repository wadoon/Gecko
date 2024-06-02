package org.gecko.view.inspector.builder

import org.gecko.actions.ActionManager
import org.gecko.model.Visibility
import org.gecko.view.inspector.element.InspectorSeparator
import org.gecko.view.inspector.element.button.InspectorOpenSystemButton
import org.gecko.view.inspector.element.container.InspectorCodeSystemContainer
import org.gecko.view.inspector.element.container.InspectorVariableLabel
import org.gecko.view.inspector.element.list.InspectorVariableList
import org.gecko.viewmodel.SystemViewModel

/**
 * Represents a type of [AbstractInspectorBuilder] of an [Inspector][org.gecko.view.inspector.Inspector] for
 * a [SystemViewModel]. Adds to the list of
 * [InspectorElement][org.gecko.view.inspector.element.InspectorElement]s, which are added to a built
 * [Inspector][org.gecko.view.inspector.Inspector], the following: an [InspectorOpenSystemButton] and two
 * [InspectorVariableList]s for input- and output-[PortViewModel][org.gecko.viewmodel.PortViewModel]s of the
 * [SystemViewModel].
 */
class SystemInspectorBuilder(actionManager: ActionManager, viewModel: SystemViewModel) :
    AbstractInspectorBuilder<SystemViewModel?>(actionManager, viewModel) {
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
