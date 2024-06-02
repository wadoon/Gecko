package org.gecko.view.views.viewelement

import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.*

import org.gecko.model.*
import org.gecko.viewmodel.PortViewModel
import java.util.concurrent.Callable

/**
 * Represents a type of [BlockViewElement] implementing the [ViewElement] interface, which encapsulates an
 * [PortViewModel].
 */

class VariableBlockViewElement(override val target: PortViewModel) : BlockViewElement(target),
    ViewElement<PortViewModel> {
    val nameProperty: StringProperty = SimpleStringProperty()
    val typeProperty: StringProperty = SimpleStringProperty()
    val visibilityProperty: Property<Visibility> = SimpleObjectProperty()

    init {
        bindViewModel()
        constructVisualization()
    }

    override fun drawElement(): Node {
        return this
    }

    override val position: Point2D
        get() = target.position

    override fun accept(visitor: ViewElementVisitor) {
        visitor.visit(this)
    }

    fun bindViewModel() {
        nameProperty.bind(target.nameProperty)
        typeProperty.bind(target.typeProperty)
        visibilityProperty.bind(target.visibilityProperty)
        prefWidthProperty().bind(
            Bindings.createDoubleBinding({ target.size.x }, target.sizeProperty)
        )
        prefHeightProperty().bind(
            Bindings.createDoubleBinding({ target.size.y }, target.sizeProperty)
        )
    }

    fun constructVisualization() {
        val container = StackPane()
        val rectangle = Rectangle()
        rectangle.widthProperty().bind(widthProperty())
        rectangle.heightProperty().bind(heightProperty())
        rectangle.fillProperty()
            .bind(
                Bindings.createObjectBinding<Color>(
                    Callable<Color> { PortViewModel.Companion.getBackgroundColor(visibilityProperty.value) },
                    visibilityProperty
                )
            )
        rectangle.arcWidth = BACKGROUND_ROUNDING.toDouble()
        rectangle.arcHeight = BACKGROUND_ROUNDING.toDouble()
        container.children.add(rectangle)
        val label = Label()
        label.textProperty().bind(nameProperty)
        label.maxWidth = target.size.x
        label.alignment = Pos.CENTER
        container.children.add(label)
        children.add(container)
    }

    override val zPriority: Int = 30
}
