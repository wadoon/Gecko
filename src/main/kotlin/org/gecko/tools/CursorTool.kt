package org.gecko.tools

import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import org.gecko.actions.Action
import org.gecko.actions.ActionManager
import org.gecko.view.views.ViewElementPane
import org.gecko.view.views.viewelement.*
import org.gecko.view.views.viewelement.decorator.BlockElementScalerViewElementDecorator
import org.gecko.view.views.viewelement.decorator.ConnectionElementScalerViewElementDecorator
import org.gecko.view.views.viewelement.decorator.ElementScalerBlock
import org.gecko.viewmodel.*
import java.util.function.Consumer

/**
 * A concrete representation of an area-[Tool], utilized for marking a rectangle-formed area in the view. Holds
 * the [starting position][Point2D]  and the afferent [javafx.scene.shape.Rectangle].
 */
class CursorTool(
    actionManager: ActionManager,
    val selectionManager: SelectionManager,
    val editorViewModel: EditorViewModel
) : Tool(actionManager, ToolType.CURSOR, false) {
    var isDragging = false
    var startDragPosition: Point2D? = null
    var previousDragPosition: Point2D? = null
    var oldPosition: Point2D? = null
    var oldSize: Point2D? = null
    var viewPane: ViewElementPane? = null

    override fun visitView(pane: ViewElementPane) {
        super.visitView(pane)
        pane.draw().cursor = Cursor.DEFAULT
        pane.draw().isPannable = false
        viewPane = pane
        setDeselectHandler(pane.draw())
    }

    override fun visit(stateViewElement: StateViewElement) {
        super.visit(stateViewElement)
        setDragAndSelectHandlers(stateViewElement)
    }

    override fun visit(systemViewElement: SystemViewElement) {
        super.visit(systemViewElement)
        setDragAndSelectHandlers(systemViewElement)
        setOpenSystemHandler(systemViewElement)
    }

    override fun visit(edgeViewElement: EdgeViewElement) {
        super.visit(edgeViewElement)
        edgeViewElement.drawElement().onMouseClicked = EventHandler { event: MouseEvent ->
            if (event.button != MouseButton.PRIMARY) {
                return@EventHandler
            }
            selectElement(edgeViewElement, !event.isShiftDown)
            event.consume()
        }
    }

    override fun visit(variableBlockViewElement: VariableBlockViewElement) {
        super.visit(variableBlockViewElement)
        setDragAndSelectHandlers(variableBlockViewElement)
    }

    override fun visit(regionViewElement: RegionViewElement) {
        super.visit(regionViewElement)
        setDragAndSelectHandlers(regionViewElement)
    }

    override fun visit(systemConnectionViewElement: SystemConnectionViewElement) {
        super.visit(systemConnectionViewElement)
        systemConnectionViewElement.pane.onMouseClicked = EventHandler { event: MouseEvent ->
            if (event.button != MouseButton.PRIMARY) {
                return@EventHandler
            }
            selectElement(systemConnectionViewElement, !event.isShiftDown)
            event.consume()
        }
    }

    override fun visit(connectionElementScalerViewElementDecorator: ConnectionElementScalerViewElementDecorator) {
        super.visit(connectionElementScalerViewElementDecorator)

        for (scaler in connectionElementScalerViewElementDecorator.scalers!!) {
            setConnectionScalerElementsHandlers(scaler)
        }
    }

    override fun visit(blockElementScalerViewElementDecorator: BlockElementScalerViewElementDecorator) {
        super.visit(blockElementScalerViewElementDecorator)
        for (scaler in blockElementScalerViewElementDecorator.scalers!!) {
            setBlockScalerElementHandlers(scaler)
        }
    }

    fun setDeselectHandler(node: Node?) {
        node!!.onMousePressed = EventHandler { event: MouseEvent ->
            if (event.button != MouseButton.PRIMARY) {
                return@EventHandler
            }
            selectionManager.deselectAll()
            event.consume()
        }
    }

    fun setBlockScalerElementHandlers(scaler: ElementScalerBlock) {
        scaler.onMousePressed = EventHandler { event: MouseEvent ->
            if (isDragging) {
                return@EventHandler
            }
            if (event.button != MouseButton.PRIMARY) {
                return@EventHandler
            }

            startDraggingElementHandler(event)
            scaler.isDragging = true
            oldPosition = scaler.decoratorTarget.target?.position
            oldSize = scaler.decoratorTarget.target?.size
            startDragPosition = viewPane!!.screenToWorldCoordinates(event.screenX, event.screenY)
            isDragging = true
            event.consume()
        }

        scaler.onMouseDragged = EventHandler { event: MouseEvent ->
            if (!isDragging) {
                return@EventHandler
            }
            val newPosition = viewPane!!.screenToWorldCoordinates(event.screenX, event.screenY)
            if (!scaler.setCenter(newPosition)) {
                runResizeAction(scaler)
                cancelDrag(scaler)
            }
            event.consume()
        }

        scaler.onMouseReleased = EventHandler { event: MouseEvent ->
            if (!isDragging) {
                return@EventHandler
            }
            scaler.setCenter(viewPane!!.screenToWorldCoordinates(event.screenX, event.screenY))
            runResizeAction(scaler)
            cancelDrag(scaler)
            event.consume()
        }
    }

    fun runResizeAction(scaler: ElementScalerBlock) {
        scaler.isDragging = false
        val target = scaler.decoratorTarget.target as BlockViewModelElement
        val resizeAction: Action = actionManager.actionFactory
            .createScaleBlockViewModelElementAction(target, scaler, oldPosition, oldSize, true)
        actionManager.run(resizeAction)
    }

    fun setConnectionScalerElementsHandlers(scaler: ElementScalerBlock) {
        scaler.onMousePressed = EventHandler { event: MouseEvent ->
            if (event.button != MouseButton.PRIMARY) {
                return@EventHandler
            }
            startDraggingElementHandler(event)
            scaler.isDragging = true
            scaler.decoratorTarget.target?.setCurrentlyModified(true)
            event.consume()
        }
        scaler.onMouseDragged = EventHandler { event: MouseEvent ->
            if (!isDragging) {
                return@EventHandler
            }
            val eventPosition = viewPane!!.screenToWorldCoordinates(event.screenX, event.screenY)
            val delta = eventPosition.subtract(previousDragPosition)
            scaler.layoutPosition = scaler.layoutPosition.add(delta)
            previousDragPosition = eventPosition
            event.consume()
        }
        scaler.onMouseReleased = EventHandler { event: MouseEvent ->
            if (!isDragging) {
                return@EventHandler
            }
            val endWorldPos = viewPane!!.screenToWorldCoordinates(event.screenX, event.screenY)
            scaler.layoutPosition = scaler.layoutPosition.add(startDragPosition!!.subtract(endWorldPos))

            val moveAction = if (editorViewModel.isAutomatonEditor) {
                actionManager.actionFactory
                    .createMoveEdgeViewModelElementAction(
                        scaler.decoratorTarget.target as EdgeViewModel,
                        scaler, endWorldPos.subtract(startDragPosition)
                    )
            } else {
                actionManager.actionFactory
                    .createMoveSystemConnectionViewModelElementAction(
                        scaler.decoratorTarget.target as SystemConnectionViewModel, scaler,
                        endWorldPos.subtract(startDragPosition)
                    )
            }

            actionManager.run(moveAction)
            scaler.decoratorTarget.target!!.setCurrentlyModified(false)
            cancelDrag(scaler)
            event.consume()
        }
    }

    fun setOpenSystemHandler(systemViewElement: SystemViewElement) {
        systemViewElement.onMouseClicked = EventHandler { event: MouseEvent ->
            if (event.button != MouseButton.PRIMARY) {
                return@EventHandler
            }
            if (event.clickCount == 2) {
                val openSystemAction: Action =
                    actionManager.actionFactory.createViewSwitchAction(systemViewElement.target, false)
                actionManager.run(openSystemAction)
            }
            event.consume()
        }
    }

    fun setDragAndSelectHandlers(element: ViewElement<*>) {
        element.drawElement().onMousePressed = EventHandler { event: MouseEvent ->
            if (event.button != MouseButton.PRIMARY) {
                return@EventHandler
            }
            startDraggingElementHandler(event)

            if (!element.isSelected) {
                selectElement(element, !event.isShiftDown)
            }
            event.consume()
        }
        element.drawElement().onMouseDragged = EventHandler { event: MouseEvent ->
            dragElementsHandler(event)
            event.consume()
        }
        element.drawElement().onMouseReleased = EventHandler { event: MouseEvent ->
            stopDraggingElementHandler(event)
            if (event.button == MouseButton.PRIMARY && element.isSelected && startDragPosition!!.distance(
                    previousDragPosition
                ) * editorViewModel.zoomScale < DRAG_THRESHOLD
            ) {
                selectElement(element, !event.isShiftDown)
            }
            event.consume()
        }
    }

    fun startDraggingElementHandler(event: MouseEvent) {
        isDragging = true
        startDragPosition = viewPane!!.screenToWorldCoordinates(event.screenX, event.screenY)
        previousDragPosition = startDragPosition
        event.consume()
    }

    fun dragElementsHandler(event: MouseEvent) {
        if (!isDragging) {
            return
        }
        val eventPosition = viewPane!!.screenToWorldCoordinates(event.screenX, event.screenY)
        val delta = eventPosition.subtract(previousDragPosition)
        selectionManager.currentSelection.forEach(Consumer { element: PositionableViewModelElement ->
            element.setCurrentlyModified(true)
            element.position = element.position.add(delta)
        })
        previousDragPosition = eventPosition
        event.consume()
    }

    fun stopDraggingElementHandler(event: MouseEvent) {
        if (!isDragging) {
            return
        }
        val endWorldPos = viewPane!!.screenToWorldCoordinates(event.screenX, event.screenY)
        selectionManager.currentSelection.forEach(Consumer { element: PositionableViewModelElement ->
            element.setCurrentlyModified(false)
            element.position = element.position.add(startDragPosition!!.subtract(endWorldPos))
        })
        val moveAction: Action = actionManager.actionFactory
            .createMoveBlockViewModelElementAction(endWorldPos.subtract(startDragPosition))
        actionManager.run(moveAction)
        cancelDrag()
        event.consume()
    }

    fun selectElement(viewElement: ViewElement<*>, newSelection: Boolean) {
        val select: Action = actionManager.actionFactory.createSelectAction(viewElement.target!!, newSelection)
        actionManager.run(select)
    }

    fun cancelDrag(scaler: ElementScalerBlock) {
        scaler.isDragging = false
        cancelDrag()
    }

    fun cancelDrag() {
        isDragging = false
    }

    companion object {
        const val DRAG_THRESHOLD = 4.0
    }
}
