package org.gecko.tools;

import org.gecko.actions.ActionManager;

public class PanTool extends Tool {

    private static final String NAME = "Pan Tool";

    public PanTool(ActionManager actionManager) {
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
