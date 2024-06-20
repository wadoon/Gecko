package org.gecko.view.views.viewelement


import javafx.beans.property.IntegerProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.util.Pair
import org.gecko.viewmodel.ContractViewModel
import org.gecko.viewmodel.EdgeViewModel
import org.gecko.viewmodel.Kind
import org.gecko.viewmodel.onChange
import kotlin.math.*

/**
 * Represents a type of [ConnectionViewElement] implementing the [ViewElement] interface, which encapsulates
 * an [EdgeViewModel].
 */

class EdgeViewElement(edgeViewModel: EdgeViewModel) :
    ConnectionViewElement(listOf(edgeViewModel.startPoint, edgeViewModel.endPoint)),
    ViewElement<EdgeViewModel> {
    override val target: EdgeViewModel
    val contractProperty: Property<ContractViewModel>
    val priorityProperty: IntegerProperty
    val kindProperty: Property<Kind>
    val label: Label

    init {
        edgeViewModel.startPointProperty.onChange { _: Point2D?, n: Point2D? ->
            pathSource[0] = n
        }

        edgeViewModel.endPointProperty.onChange { _: Point2D?, n: Point2D? ->
            pathSource[1] = n
        }

        this.contractProperty = SimpleObjectProperty()
        this.priorityProperty = SimpleIntegerProperty()
        this.kindProperty = SimpleObjectProperty()
        contractProperty.bind(edgeViewModel.contractProperty)
        priorityProperty.bind(edgeViewModel.priorityProperty)
        kindProperty.bind(edgeViewModel.kindProperty)
        this.target = edgeViewModel

        this.label = Label()
        label.text = edgeViewModel.representation

        val updateLabelPosition =
            ChangeListener { _: ObservableValue<*>?, _: Any?, _: Any? -> calculateLabelPosition() }
        label.heightProperty().addListener(updateLabelPosition)
        label.widthProperty().addListener(updateLabelPosition)
        edgeViewModel.startPointProperty.addListener(updateLabelPosition)
        edgeViewModel.endPointProperty.addListener(updateLabelPosition)

        val updateLabel =
            ChangeListener { _: ObservableValue<*>?, _: Any?, _: Any? -> label.text = edgeViewModel.representation }

        val contract = edgeViewModel.contract
        contract?.nameProperty?.addListener(updateLabel)
        contractProperty.addListener { _: ObservableValue<out ContractViewModel>?, _: ContractViewModel?, newValue: ContractViewModel? ->
            label.text = edgeViewModel.representation
            newValue?.nameProperty?.addListener(updateLabel)
        }
        priorityProperty.addListener(updateLabel)
        kindProperty.addListener(updateLabel)

        pane.children.add(label)

        isLoopProperty.bind(edgeViewModel.isLoopProperty.and(edgeViewModel.isCurrentlyModified.not()))
        orientationProperty.bind(edgeViewModel.orientationProperty)

        isLoopProperty.addListener { _: ObservableValue<out Boolean?>?, _: Boolean?, _: Boolean? -> calculateLabelPosition() }
        constructVisualization()
        calculateLabelPosition()
    }

    fun calculateLabelPosition() {
        val first: Point2D
        val last: Point2D
        if (isLoopProperty.get()) {
            val loopPoints = loopPoints
            first = loopPoints.key
            last = loopPoints.value
        } else {
            first = target.startPoint
            last = target.endPoint
        }
        val mid = first.midpoint(last)
        val vec = last.subtract(first)
        val angle = atan2(vec.y, vec.x)
        val isVertical = abs(abs(angle) - Math.PI / 2) < Math.PI / 4
        val isPart = (angle > 0 && angle < Math.PI / 2) || (angle < -(1.0 / 2.0) * Math.PI && angle > -Math.PI)

        val p: Point2D
        val mp: Double
        if (isVertical) {
            mp = (label.height / 2) / sin(angle)
            p = vec.normalize().multiply(abs(mp)).multiply(sign(angle)).add(mid)
        } else {
            mp = (label.width / 2) / cos(angle)
            var sized = vec.normalize().multiply(mp)
            sized = if (isPart) sized.multiply(-1.0) else sized
            p = sized.add(mid)
        }

        val newPos = p.subtract(if (isPart) 0.0 else label.width, label.height)

        label.layoutX = newPos.x
        label.layoutY = newPos.y
    }

    val loopPoints: Pair<Point2D, Point2D>
        get() {
            val minY = min(target.startPoint.y, target.endPoint.y)
            val maxY = max(target.startPoint.y, target.endPoint.y)
            val offsetY = maxY - minY
            val minX = min(target.startPoint.x, target.endPoint.x)
            val maxX = max(target.startPoint.x, target.endPoint.x)
            val offsetX = maxX - minX
            return when (orientationProperty.get()) {
                0 -> Pair(Point2D(minX, minY - offsetY), Point2D(maxX + offsetX, minY - offsetY))
                1 -> Pair(
                    Point2D(minX, maxY + offsetY + LABEL_OFFSET),
                    Point2D(maxX + offsetX, maxY + offsetY + LABEL_OFFSET)
                )

                2 -> Pair(
                    Point2D(minX - offsetX, maxY + offsetY + LABEL_OFFSET),
                    Point2D(maxX, maxY + offsetY + LABEL_OFFSET)
                )

                3 -> Pair(Point2D(minX - offsetX, minY - offsetY), Point2D(maxX, minY - offsetY))
                else -> Pair(Point2D.ZERO, Point2D.ZERO)
            }
        }

    override fun drawElement(): Node {
        return pane
    }

    override val position: Point2D
        get() = target.position

    override fun accept(visitor: ViewElementVisitor) {
        visitor.visit(this)
    }

    fun constructVisualization() {
        line.stroke = Color.BLACK
        line.isSmooth = true
    }

    override val edgePoints: ObservableList<Point2D>
        get() = super.pathSource

    override fun setEdgePoint(index: Int, point: Point2D): Boolean {
        if (index == 0) {
            target.startPoint = point
            return true
        } else if (index == 1) {
            target.endPoint = point
            return true
        }
        return false
    }

    override val zPriority: Int = 20

    companion object {
        const val LABEL_OFFSET = 15.0
    }
}
