package org.gecko.tools

import javafx.geometry.Bounds
import javafx.geometry.Point2D
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import org.gecko.actions.ActionManager
import org.gecko.viewmodel.BlockElement

/**
 * A concrete representation of a region-creating-[AreaTool], utilized for creating a
 * rectangle-formed-[RegionViewModel][org.gecko.viewmodel.Region]. Holds the [Color] of the drawn
 * region.
 */
class RegionCreatorTool(actionManager: ActionManager) :
    AreaTool(actionManager, ToolType.REGION_CREATOR, false) {
    var color: Color? = null

    override fun createNewArea(): Rectangle {
        val region = Rectangle()
        color = Color.color(Math.random(), Math.random(), Math.random())
        region.fill = color
        region.opacity = 0.5
        return region
    }

    override fun onAreaCreated(event: MouseEvent, worldBounds: Bounds) {
        if (
            worldBounds.width < BlockElement.Companion.MIN_WIDTH ||
                worldBounds.height < BlockElement.Companion.MIN_HEIGHT
        ) {
            return
        }
        actionManager.run(
            actionManager.actionFactory.createRegion(
                Point2D(worldBounds.minX, worldBounds.minY),
                Point2D(worldBounds.width, worldBounds.height),
                color
            )
        )
    }
}
