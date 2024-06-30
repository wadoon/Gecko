package org.gecko.view.views.viewelement

import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.paint.Color
import org.gecko.viewmodel.SystemConnection
import org.gecko.viewmodel.Visibility
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
