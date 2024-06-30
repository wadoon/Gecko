package org.gecko.view.views.viewelement.decorator

import javafx.collections.ListChangeListener
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import org.gecko.view.views.viewelement.ViewElement
import org.gecko.view.views.viewelement.ViewElementVisitor

/**
 * Represents a type of [ElementScalerViewElementDecorator] following the decorator pattern for
 * scaling purposes of a connection.
 */
class ConnectionElementScalerViewElementDecorator(decoratorTarget: ViewElement<*>) :
    ElementScalerViewElementDecorator(decoratorTarget) {
    var lastEdgePointCnt = 0

    init {
        // create new scaler block if edge points list change
        edgePoints.addListener { change: ListChangeListener.Change<out Point2D> ->
            this.updateEdgePoints(change)
        }
    }

    override fun accept(visitor: ViewElementVisitor) {
        visitor.visit(this)
        decoratorTarget.accept(visitor)
    }

    override val zPriority: Int
        get() = decoratorTarget.zPriority + (if (isSelected) IGNORE_Z_PRIORITY else 0)

    fun updateEdgePoints(change: ListChangeListener.Change<out Point2D>) {
        if (change.list.size == lastEdgePointCnt) {
            for (scaler in scalers) {
                scaler.refreshListeners()
            }
            return
        }

        decoratedNode.children.removeAll(scalers)
        scalers.clear()
        createScalerBlock(0)
        createScalerBlock(change.list.size - 1)
        lastEdgePointCnt = change.list.size
    }

    fun createScalerBlock(change: Int) {
        val scalerBlock = ElementScalerBlock(change, this, SCALER_SIZE, SCALER_SIZE)
        scalerBlock.fill = Color.RED
        scalers.add(scalerBlock)
        decoratedNode.children.add(scalerBlock)
    }

    companion object {
        const val SCALER_SIZE = 10.0
        const val IGNORE_Z_PRIORITY = 10000
    }
}
