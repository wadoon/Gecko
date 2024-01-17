package org.gecko.tools;

import javafx.scene.Node;
import org.gecko.view.views.viewelement.EdgeViewElement;
import org.gecko.view.views.viewelement.RegionViewElement;
import org.gecko.view.views.viewelement.StateViewElement;
import org.gecko.view.views.viewelement.SystemConnectionViewElement;
import org.gecko.view.views.viewelement.SystemViewElement;
import org.gecko.view.views.viewelement.VariableBlockViewElement;

public class StateCreatorTool extends Tool {

    private static final String NAME = "State Creator";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getIconPath() {
        //TODO stub
        return null;
    }

    @Override
    public void visitView(Node view) {
        //TODO stub
    }

    @Override
    public void visit(StateViewElement stateViewElement) {

    }

    @Override
    public void visit(EdgeViewElement edgeViewElement) {

    }

    @Override
    public void visit(RegionViewElement regionViewElement) {

    }

    @Override
    public void visit(SystemViewElement systemViewElement) {

    }

    @Override
    public void visit(SystemConnectionViewElement systemConnectionViewElement) {

    }

    @Override
    public void visit(VariableBlockViewElement variableBlockViewElement) {

    }
}
