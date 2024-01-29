package org.gecko.view.inspector;


import org.gecko.actions.ActionManager;
import org.gecko.view.inspector.builder.AbstractInspectorBuilder;
import org.gecko.view.inspector.builder.EdgeInspectorBuilder;
import org.gecko.view.inspector.builder.RegionInspectorBuilder;
import org.gecko.view.inspector.builder.StateInspectorBuilder;
import org.gecko.view.inspector.builder.SystemInspectorBuilder;
import org.gecko.view.inspector.builder.VariableBlockInspectorBuilder;
import org.gecko.view.views.EditorView;
import org.gecko.viewmodel.EdgeViewModel;
import org.gecko.viewmodel.EditorViewModel;
import org.gecko.viewmodel.PortViewModel;
import org.gecko.viewmodel.PositionableViewModelElement;
import org.gecko.viewmodel.RegionViewModel;
import org.gecko.viewmodel.StateViewModel;
import org.gecko.viewmodel.SystemViewModel;

public class InspectorFactory {

    private final EditorView editorView;
    private final EditorViewModel editorViewModel;
    private final ActionManager actionManager;

    public InspectorFactory(ActionManager actionManager, EditorView editorView, EditorViewModel editorViewModel) {
        this.actionManager = actionManager;
        this.editorView = editorView;
        this.editorViewModel = editorViewModel;
    }

    public Inspector createInspector(PositionableViewModelElement<?> viewElement) {
        InspectorFactoryVisitor visitor = new InspectorFactoryVisitor(this);
        viewElement.accept(visitor);
        return visitor.getInspector();
    }

    public Inspector createStateInspector(StateViewModel stateViewModel) {
        return buildInspector(new StateInspectorBuilder(actionManager, editorViewModel, stateViewModel));
    }

    public Inspector createEdgeInspector(EdgeViewModel edgeViewModel) {
        return buildInspector(new EdgeInspectorBuilder(actionManager, edgeViewModel));
    }

    public Inspector createRegionInspector(RegionViewModel regionViewModel) {
        return buildInspector(new RegionInspectorBuilder(actionManager, regionViewModel));
    }

    public Inspector createSystemInspector(SystemViewModel systemViewModel) {
        return buildInspector(new SystemInspectorBuilder(actionManager, systemViewModel));
    }

    public Inspector createVariableBlockInspector(PortViewModel portviewModel) {
        return buildInspector(new VariableBlockInspectorBuilder(actionManager, portviewModel));
    }

    private Inspector buildInspector(AbstractInspectorBuilder<?> builder) {
        return builder.build();
    }
}
