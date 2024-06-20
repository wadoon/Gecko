package org.gecko.tools

import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.input.*

import org.gecko.actions.*
import org.gecko.view.views.ViewElementPane
import org.gecko.view.views.viewelement.*
import org.gecko.view.views.viewelement.decorator.BlockElementScalerViewElementDecorator
import org.gecko.view.views.viewelement.decorator.ConnectionElementScalerViewElementDecorator
import org.gecko.view.views.viewelement.decorator.ElementScalerViewElementDecorator
import org.gecko.view.views.viewelement.decorator.SelectableViewElementDecorator
import org.gecko.viewmodel.EditorViewModel

/**
 * An abstract representation of a tool used in the Gecko Graphic Editor, characterized by a [ToolType]. Follows
 * the visitor pattern, implementing the [ViewElementVisitor] interface. Defines the [MouseEvent]
 * [EventHandler]s for each [ViewElement][org.gecko.view.views.viewelement.ViewElement].
 */
abstract class Tool protected constructor(
    protected val actionManager: ActionManager,
    val toolType: ToolType,
    val transparentElements: Boolean
) : ViewElementVisitor {
    /**
     * Applies listeners to the given view, which is assumed to be the current view of the application.
     *
     * @param pane the view to visit
     */
    open fun visitView(pane: ViewElementPane) {
        pane.draw().cursor = Cursor.DEFAULT
        setAllHandlers(pane.draw(), null)
        setAllHandlers(pane.world, null)
        pane.world.onScroll = EventHandler<ScrollEvent> { e: ScrollEvent ->
            if (!e.isControlDown || e.deltaY == 0.0) {
                return@EventHandler
            }
            e.consume()
            val defaultZoomStep: Double = EditorViewModel.defaultZoomStep
            val zoomFactor = if (e.deltaY > 0) defaultZoomStep else 1 / defaultZoomStep
            val pivot = pane.screenToWorldCoordinates(e.screenX, e.screenY)
            actionManager.run(actionManager.actionFactory.createZoomAction(pivot, zoomFactor))
        }
    }

    /**
     * Overriden by specific tools to handle interaction with a state view element. By default, consumes all events.
     *
     * @param stateViewElement the state view element to visit
     */
    override fun visit(stateViewElement: StateViewElement) {
        //We need to consume all events so that they don't propagate to the view
        setAllHandlers(
            stateViewElement,
            if (transparentElements) null else EventHandler { obj: MouseEvent? -> obj?.consume() })
    }

    /**
     * Overriden by specific tools to handle interaction with an edge view element. By default, consumes all events.
     *
     * @param edgeViewElement the edge view element to visit
     */
    override fun visit(edgeViewElement: EdgeViewElement) {
        setAllHandlers(
            edgeViewElement.pane,
            if (transparentElements) null else EventHandler { obj: MouseEvent? -> obj?.consume() })
    }

    /**
     * Overriden by specific tools to handle interaction with a region view element. By default, lets events pass
     * through.
     *
     * @param regionViewElement the region view element to visit
     */
    override fun visit(regionViewElement: RegionViewElement) {
        setAllHandlers(regionViewElement, null)
    }

    /**
     * Overriden by specific tools to handle interaction with a system view element. By default, consumes all events.
     *
     * @param systemViewElement the system view element to visit
     */
    override fun visit(systemViewElement: SystemViewElement) {
        setAllHandlers(
            systemViewElement,
            if (transparentElements) null else EventHandler { obj: MouseEvent? -> obj?.consume() })
    }

    /**
     * Overriden by specific tools to handle interaction with a system connection view element. By default, consumes all
     * events.
     *
     * @param systemConnectionViewElement the system connection view element to visit
     */
    override fun visit(systemConnectionViewElement: SystemConnectionViewElement) {
        setAllHandlers(
            systemConnectionViewElement.pane,
            if (transparentElements) null else EventHandler { obj: MouseEvent? -> obj?.consume() })
    }

    /**
     * Overriden by specific tools to handle interaction with a variable block view element. By default, consumes all
     * events.
     *
     * @param variableBlockViewElement the variable block view element to visit
     */
    override fun visit(variableBlockViewElement: VariableBlockViewElement) {
        setAllHandlers(
            variableBlockViewElement,
            if (transparentElements) null else EventHandler { obj: MouseEvent? -> obj?.consume() })
    }

    /**
     * Overriden by specific tools to handle interaction with a block element scaler view element decorator. By default,
     * lets events pass through.
     *
     * @param elementScalarViewElementDecorator the block element scaler view element decorator to visit
     */
    override fun visit(elementScalarViewElementDecorator: ElementScalerViewElementDecorator) {
        setAllHandlers(elementScalarViewElementDecorator.drawElement(), null)
    }

    /**
     * Overriden by specific tools to handle interaction with a connection element scaler view element decorator. By
     * default, lets events pass through.
     *
     * @param connectionElementScalerViewElementDecorator the connection element scaler view element decorator to visit
     */
    override fun visit(connectionElementScalerViewElementDecorator: ConnectionElementScalerViewElementDecorator) {
        setAllHandlers(connectionElementScalerViewElementDecorator.drawElement(), null)
    }

    /**
     * Overriden by specific tools to handle interaction with a block element scaler view element decorator. By default,
     * lets events pass through.
     *
     * @param blockElementScalerViewElementDecorator the block element scaler view element decorator to visit
     */
    override fun visit(blockElementScalerViewElementDecorator: BlockElementScalerViewElementDecorator) {
        setAllHandlers(blockElementScalerViewElementDecorator.drawElement(), null)
    }

    /**
     * Overriden by specific tools to handle interaction with a selectable view element decorator. By default, lets
     * events pass through.
     *
     * @param selectableViewElementDecorator the selectable view element decorator to visit
     */
    override fun visit(selectableViewElementDecorator: SelectableViewElementDecorator) {
        setAllHandlers(selectableViewElementDecorator.drawElement(), null)
    }

    /**
     * Overriden by specific tools to handle interaction with a port view element. By default, consumes all events.
     *
     * @param portViewElement the port view element to visit
     */
    override fun visit(portViewElement: PortViewElement) {
        setAllHandlers(
            portViewElement,
            if (transparentElements) null else EventHandler { obj: MouseEvent? -> obj?.consume() })
    }

    protected fun setAllHandlers(node: Node?, handler: EventHandler<MouseEvent?>?) {
        node!!.onMousePressed = handler
        node.onMouseDragged = handler
        node.onMouseReleased = handler
        node.onMouseClicked = handler
        node.onMouseMoved = handler
    }
}
