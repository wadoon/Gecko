package org.gecko.view.views.viewelement

import javafx.beans.property.*
import javafx.collections.ObservableList
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.paint.Color

import org.gecko.viewmodel.SystemConnectionViewModel
import org.gecko.viewmodel.Visibility
import tornadofx.getValue
import tornadofx.setValue

/**
 * Represents a type of [ConnectionViewElement] implementing the [ViewElement] interface, which encapsulates
 * an [SystemConnectionViewModel].
 */

class SystemConnectionViewElement(override val target: SystemConnectionViewModel) :
    ConnectionViewElement(target.edgePoints), ViewElement<SystemConnectionViewModel> {
    val visibilityProperty: Property<Visibility> = SimpleObjectProperty()
    var visibility by visibilityProperty

    val typeProperty: StringProperty = SimpleStringProperty()
    var type by typeProperty

    init {
        constructVisualization()
    }

    override fun drawElement(): Node {
        return line
    }

    override val edgePoints: ObservableList<Point2D>
        get() = edgePoints

    override fun setEdgePoint(index: Int, point: Point2D): Boolean {
        setEdgePoint(index, point)
        return true
    }

    override val position: Point2D
        get() = position

    override fun accept(visitor: ViewElementVisitor) {
        visitor.visit(this)
    }

    fun constructVisualization() {
        line.stroke = Color.BLACK
        line.isSmooth = true
    }

    override val zPriority: Int = 20
}
