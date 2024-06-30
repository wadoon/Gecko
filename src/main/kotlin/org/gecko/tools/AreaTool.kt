package org.gecko.tools

import javafx.event.EventHandler
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Rectangle
import kotlin.math.max
import kotlin.math.min
import org.gecko.actions.ActionManager
import org.gecko.view.views.ViewElementPane

/**
 * A concrete representation of an area-[Tool], utilized for marking a rectangle-formed area in the
 * view. Holds the starting position and the afferent [Rectangle].
 */
abstract class AreaTool(
    actionManager: ActionManager,
    toolType: ToolType,
    transparentElements: Boolean
) : Tool(actionManager, toolType, transparentElements) {
    var startPosition: Point2D? = null
    var startScreenPosition: Point2D? = null
    var area: Rectangle? = null
    var view: ScrollPane? = null

    override fun visitView(pane: ViewElementPane) {
        super.visitView(pane)
        view = pane.draw()
        val world = pane.world
        view!!.isPannable = false
        view!!.cursor = Cursor.CROSSHAIR
        startPosition = null

        world.onMousePressed = EventHandler { event: MouseEvent ->
            if (event.button != MouseButton.PRIMARY) {
                return@EventHandler
            }
            startPosition = pane.screenToLocalCoordinates(event.screenX, event.screenY)
            startScreenPosition = Point2D(event.screenX, event.screenY)
            area = createNewArea()
            area!!.x = startPosition!!.x
            area!!.y = startPosition!!.y
            world.children.add(area)
        }

        world.onMouseDragged = EventHandler { event: MouseEvent ->
            if (startPosition == null) {
                return@EventHandler
            }
            val dragPosition = pane.screenToLocalCoordinates(event.screenX, event.screenY)
            val areaBounds = calculateAreaBounds(startPosition, dragPosition)
            area!!.x = areaBounds.minX
            area!!.y = areaBounds.minY
            area!!.width = areaBounds.width
            area!!.height = areaBounds.height
        }

        world.onMouseReleased = EventHandler { event: MouseEvent ->
            if (startPosition == null) {
                return@EventHandler
            }
            world.children.remove(area)
            val endPos = pane.screenToWorldCoordinates(event.screenX, event.screenY)
            onAreaCreated(
                event,
                calculateAreaBounds(pane.screenToWorldCoordinates(startScreenPosition), endPos)
            )
            startPosition = null
            startScreenPosition = null
        }
    }

    protected fun calculateAreaBounds(startPosition: Point2D?, endPosition: Point2D?): Bounds {
        val topLeft =
            Point2D(min(startPosition!!.x, endPosition!!.x), min(startPosition.y, endPosition.y))
        val bottomRight =
            Point2D(max(startPosition.x, endPosition.x), max(startPosition.y, endPosition.y))
        return BoundingBox(
            topLeft.x,
            topLeft.y,
            bottomRight.x - topLeft.x,
            bottomRight.y - topLeft.y
        )
    }

    abstract fun createNewArea(): Rectangle?

    abstract fun onAreaCreated(event: MouseEvent, worldBounds: Bounds)
}
