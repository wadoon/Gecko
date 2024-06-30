package org.gecko.view.inspector

import javafx.scene.control.ScrollPane
import org.gecko.actions.ActionManager
import org.gecko.view.inspector.builder.*
import org.gecko.viewmodel.*


/**
 * Represents a factory for inspectors. Provides a method for the creation of an [Inspector] built by each of the
 * [InspectorBuilder]s.
 */
class InspectorFactory(val actionManager: ActionManager, val editorViewModel: EditorViewModel) {
    /**
     * Create an inspector for the given view model.
     *
     * @param viewElement The view model element to create an inspector for.
     * @return The inspector for the given view model.
     */
    fun createInspector(viewElement: PositionableElement?) =
        (viewElement as? Inspectable)?.inspector(actionManager)?.build()

    fun createAutomatonVariablePane(): ScrollPane =
        AutomatonVariablePaneBuilder(actionManager, editorViewModel.currentSystem).build()
}
