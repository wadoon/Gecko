package org.gecko.tools

import javafx.geometry.Bounds
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import org.gecko.actions.ActionManager
import org.gecko.viewmodel.EditorViewModel

/**
 * A concrete representation of a marquee-[AreaTool], utilized for marking a rectangle-formed area
 * in the view and thus selecting the covered [org.gecko.view.views.viewelement.ViewElement]s. Holds
 * the current [EditorViewModel]..
 */
class MarqueeTool(actionManager: ActionManager, val editorViewModel: EditorViewModel) :
    AreaTool(actionManager, ToolType.MARQUEE_TOOL, true) {
    override fun createNewArea(): Rectangle {
        val marquee = Rectangle()
        marquee.fill = null
        marquee.stroke = Color.BLUE
        marquee.strokeWidth = STROKE_WIDTH
        marquee.strokeDashArray.addAll(STROKE_DASH, STROKE_DASH)
        return marquee
    }

    override fun onAreaCreated(event: MouseEvent, worldAreaBounds: Bounds) {
        val elements = editorViewModel.getElementsInArea(worldAreaBounds)
        if (elements.isEmpty()) {
            return
        }
        actionManager.run(
            actionManager.actionFactory.createSelectAction(elements, !event.isShiftDown)
        )
    }

    companion object {
        const val STROKE_WIDTH = 1.0
        const val STROKE_DASH = 5.0
    }
}
