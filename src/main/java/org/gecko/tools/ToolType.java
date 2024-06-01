package org.gecko.tools;

import javafx.scene.input.KeyCodeCombination;
import lombok.Getter;
import org.gecko.view.ResourceHandler;
import org.gecko.view.views.shortcuts.Shortcuts;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign2.*;

import static org.kordamp.ikonli.materialdesign2.MaterialDesignS.SHAPE_RECTANGLE_PLUS;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignS.SHAPE_SQUARE_ROUNDED_PLUS;

/**
 * Enumerates the types of tools used in the Gecko Graphic Editor. Each {@link ToolType} is described by a label, an
 * icon and a {@link KeyCodeCombination}.
 */
@Getter
public enum ToolType {
    CURSOR(ResourceHandler.cursor, MaterialDesignH.HAND, Shortcuts.CURSOR_TOOL.get()),
    MARQUEE_TOOL(ResourceHandler.marquee, MaterialDesignS.SELECT, Shortcuts.MARQUEE_TOOL.get()),
    PAN(ResourceHandler.pan, MaterialDesignP.PAN, Shortcuts.PAN_TOOL.get()),
    ZOOM_TOOL(ResourceHandler.zoom, MaterialDesignM.MAGNIFY, Shortcuts.ZOOM_TOOL.get()),
    SYSTEM_CREATOR(ResourceHandler.system_creator, SHAPE_RECTANGLE_PLUS,
            Shortcuts.SYSTEM_CREATOR.get()),
    STATE_CREATOR(ResourceHandler.state_creator,
            MaterialDesignS.SHAPE_CIRCLE_PLUS,
            Shortcuts.STATE_CREATOR.get()),
    EDGE_CREATOR(ResourceHandler.edge_creator,
            MaterialDesignM.MICROSOFT_EDGE, Shortcuts.EDGE_CREATOR.get()),
    REGION_CREATOR(ResourceHandler.region_creator, SHAPE_SQUARE_ROUNDED_PLUS,
            Shortcuts.REGION_CREATOR.get()),
    VARIABLE_BLOCK_CREATOR(ResourceHandler.variable_block_creator, MaterialDesignT.TEXT_BOX,
            Shortcuts.VARIABLE_BLOCK_CREATOR.get()),
    CONNECTION_CREATOR(ResourceHandler.connection_creator, MaterialDesignT.TIMELINE_PLUS_OUTLINE,
            Shortcuts.CONNECTION_CREATOR.get());

    private final String label;
    private final Ikon icon;
    @SuppressWarnings("ImmutableEnumChecker")
    private final KeyCodeCombination keyCodeCombination;

    ToolType(String label, Ikon icon, KeyCodeCombination keyCodeCombination) {
        this.label = label;
        this.icon = icon;
        this.keyCodeCombination = keyCodeCombination;
    }
}
