package org.gecko.tools;

import org.gecko.actions.ActionManager;

public class ZoomTool extends Tool {

    private static final String NAME = "Zoom Tool";

    public ZoomTool(ActionManager actionManager) {
        super(actionManager);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getIconPath() {
        //TODO stub
        return null;
    }
}
