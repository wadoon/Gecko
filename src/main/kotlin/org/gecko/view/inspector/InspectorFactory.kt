package org.gecko.view.inspector

import javafx.scene.control.ScrollPane
import org.gecko.actions.ActionManager
import org.gecko.view.inspector.builder.*
import org.gecko.viewmodel.*


/**
 * Represents a factory for inspectors. Provides a method for the creation of an [Inspector] built by each of the
 * [AbstractInspectorBuilder]s.
 */
class InspectorFactory(val actionManager: ActionManager, val editorViewModel: EditorViewModel) {
    /**
     * Create an inspector for the given view model.
     *
     * @param viewElement The view model element to create an inspector for.
     * @return The inspector for the given view model.
     */
    fun createInspector(viewElement: PositionableViewModelElement<*>?): Inspector? {
        val visitor = InspectorFactoryVisitor(this)
        if (viewElement == null) {
            return null
        }
        viewElement.accept(visitor)
        return visitor.inspector
    }

    fun createStateInspector(stateViewModel: StateViewModel): Inspector? {
        return buildInspector(StateInspectorBuilder(actionManager, editorViewModel, stateViewModel))
    }

    fun createEdgeInspector(edgeViewModel: EdgeViewModel): Inspector? {
        return buildInspector(EdgeInspectorBuilder(actionManager, edgeViewModel))
    }

    fun createRegionInspector(regionViewModel: RegionViewModel): Inspector? {
        return buildInspector(RegionInspectorBuilder(actionManager, regionViewModel))
    }

    fun createSystemInspector(systemViewModel: SystemViewModel): Inspector? {
        return buildInspector(SystemInspectorBuilder(actionManager, systemViewModel))
    }

    fun createVariableBlockInspector(portviewModel: PortViewModel): Inspector? {
        return buildInspector(VariableBlockInspectorBuilder(actionManager, portviewModel))
    }

    fun createAutomatonVariablePane(): ScrollPane {
        return AutomatonVariablePaneBuilder(actionManager, editorViewModel.currentSystem).build()
    }

    fun buildInspector(builder: AbstractInspectorBuilder<*>): Inspector {
        return builder.build()
    }
}
