package org.gecko.view.views.viewelement.decorator

import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Node

import org.gecko.view.views.viewelement.ViewElement
import org.gecko.view.views.viewelement.ViewElementVisitor


open class ElementScalerViewElementDecorator(decoratorTarget: ViewElement<*>) : ViewElementDecorator(decoratorTarget) {
    val decoratedNode: Group
    val scalers: MutableList<ElementScalerBlock> = mutableListOf()

    override fun drawElement(): Node {
        return decoratedNode
    }

    override var isSelected: Boolean
        get() = super.isSelected
        set(value) {
            if (scalers != null) {
                for (scaler in scalers) {
                    scaler.isVisible = value
                }
            }
            super.isSelected = value
            decoratorTarget.isSelected = value
        }

    override val position: Point2D
        get() = decoratorTarget.position

    init {
        decoratedNode = Group(decoratorTarget.drawElement())
        for (i in decoratorTarget.edgePoints.indices) {
            val scalerBlock = ElementScalerBlock(i, this, SCALER_SIZE.toDouble(), SCALER_SIZE.toDouble())
            //TODO disabled scalerBlock.setFill(Color.RED);
            scalerBlock.fill = null
            scalers.add(scalerBlock)
            //decoratedNode.getChildren().add(scalerBlock);
        }
        isSelected = false
    }


    override fun accept(visitor: ViewElementVisitor) {
        visitor.visit(this)
        decoratorTarget.accept(visitor)
    }

    companion object {
        const val SCALER_SIZE = 10
    }
}
