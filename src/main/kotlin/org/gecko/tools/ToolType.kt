package org.gecko.tools

import javafx.scene.input.KeyCodeCombination

import org.gecko.view.ResourceHandler
import org.gecko.view.views.shortcuts.Shortcuts
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.materialdesign2.*

/**
 * Enumerates the types of tools used in the Gecko Graphic Editor. Each [ToolType] is described by a label, an
 * icon and a [KeyCodeCombination].
 */

enum class ToolType(
    val label: String,
    val icon: Ikon,
    val keyCodeCombination: KeyCodeCombination?
) {
    CURSOR(ResourceHandler.Companion.cursor, MaterialDesignH.HAND, Shortcuts.CURSOR_TOOL.get()),
    MARQUEE_TOOL(ResourceHandler.Companion.marquee, MaterialDesignS.SELECT, Shortcuts.MARQUEE_TOOL.get()),
    PAN(ResourceHandler.Companion.pan, MaterialDesignP.PAN, Shortcuts.PAN_TOOL.get()),
    ZOOM_TOOL(ResourceHandler.Companion.zoom, MaterialDesignM.MAGNIFY, Shortcuts.ZOOM_TOOL.get()),
    SYSTEM_CREATOR(
        ResourceHandler.Companion.system_creator, MaterialDesignS.SHAPE_RECTANGLE_PLUS,
        Shortcuts.SYSTEM_CREATOR.get()
    ),
    STATE_CREATOR(
        ResourceHandler.Companion.state_creator,
        MaterialDesignS.SHAPE_CIRCLE_PLUS,
        Shortcuts.STATE_CREATOR.get()
    ),
    EDGE_CREATOR(
        ResourceHandler.Companion.edge_creator,
        MaterialDesignM.MICROSOFT_EDGE, Shortcuts.EDGE_CREATOR.get()
    ),
    REGION_CREATOR(
        ResourceHandler.Companion.region_creator, MaterialDesignS.SHAPE_SQUARE_ROUNDED_PLUS,
        Shortcuts.REGION_CREATOR.get()
    ),
    VARIABLE_BLOCK_CREATOR(
        ResourceHandler.Companion.variable_block_creator, MaterialDesignT.TEXT_BOX,
        Shortcuts.VARIABLE_BLOCK_CREATOR.get()
    ),
    CONNECTION_CREATOR(
        ResourceHandler.Companion.connection_creator, MaterialDesignT.TIMELINE_PLUS_OUTLINE,
        Shortcuts.CONNECTION_CREATOR.get()
    );
}
