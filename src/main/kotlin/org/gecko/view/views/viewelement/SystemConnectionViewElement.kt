package org.gecko.view.views.viewelement

import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.paint.Color
import org.gecko.viewmodel.SystemConnection
import org.gecko.viewmodel.Visibility
import org.gecko.viewmodel.stringProperty
import tornadofx.getValue
import tornadofx.setValue

/**
 * Represents a type of [ConnectionViewElement] implementing the [ViewElement] interface, which
 * encapsulates an [SystemConnection].
 */
class SystemConnectionViewElement(override val target: SystemConnection) :
    ConnectionViewElement(target.edgePoints), ViewElement<SystemConnection> {
    val visibilityProperty: Property<Visibility> = SimpleObjectProperty()
    var visibility by visibilityProperty

    val typeProperty = stringProperty("")
    var type by typeProperty

    override val edgePoints: ObservableList<Point2D> = FXCollections.observableArrayList()
    override var position: Point2D = Point2D(0.0, 0.0)


    init {
        constructVisualization()
    }

    override fun drawElement(): Node {
        return line
    }

    override fun setEdgePoint(index: Int, point: Point2D): Boolean {
        edgePoints[index] = point
        return true
    }

    override fun accept(visitor: ViewElementVisitor) {
        visitor.visit(this)
    }

    fun constructVisualization() {
        line.stroke = Color.BLACK
        line.isSmooth = true
    }

    override val zPriority: Int = 20
}
