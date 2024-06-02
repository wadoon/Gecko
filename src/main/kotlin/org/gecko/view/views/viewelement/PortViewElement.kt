package org.gecko.view.views.viewelement

import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Pane

import org.gecko.model.Visibility
import org.gecko.viewmodel.PortViewModel

/**
 * Represents a type of a [Pane] used for the visualization of a [PortViewModel], to which it holds a
 * reference, along with its [name][String] and [Visibility].
 */

class PortViewElement(val viewModel: PortViewModel?) : Pane() {
    val nameProperty: StringProperty = SimpleStringProperty(viewModel!!.name)
    val visibilityProperty: ObjectProperty<Visibility> = SimpleObjectProperty(
        viewModel!!.visibility
    )

    init {
        minWidth = MIN_WIDTH
        maxWidth = MAX_WIDTH
        bindToViewModel()
        constructVisualization()
    }

    val viewPosition: Point2D
        /**
         * Returns the position of the port view element.
         *
         * @return the position of the port view element
         */
        get() = Point2D(layoutX, layoutY)

    val viewSize: Point2D
        /**
         * Returns the size of the port view element.
         *
         * @return the size of the port view element
         */
        get() = Point2D(width, height)

    fun bindToViewModel() {
        nameProperty.bind(viewModel!!.nameProperty)
        visibilityProperty.bind(viewModel.visibilityProperty)
        visibilityProperty.addListener { observable: ObservableValue<out Visibility>?, oldValue: Visibility?, newValue: Visibility? -> updateBackgroundColor() }
        viewModel.systemPortOffsetProperty.bind(
            Bindings.createObjectBinding(
                { Point2D(layoutX, layoutY) }, layoutXProperty(), layoutYProperty()
            )
        )
    }

    fun constructVisualization() {
        val nameLabel = Label()
        nameLabel.textProperty().bind(nameProperty)
        nameLabel.maxWidth = MAX_WIDTH
        nameLabel.padding = PADDING
        updateBackgroundColor()
        children.add(nameLabel)
    }

    fun updateBackgroundColor() {
        val isInput = viewModel!!.visibility == Visibility.INPUT
        val background = Background(
            BackgroundFill(
                PortViewModel.Companion.getBackgroundColor(if (isInput) Visibility.OUTPUT else Visibility.INPUT),
                if (isInput) INPUT_RADII else OUTPUT_RADII, null
            )
        )
        setBackground(background)
    }

    companion object {
        const val MIN_WIDTH = 50.0
        const val MAX_WIDTH = 80.0
        val PADDING = Insets(2.0)
        val INPUT_RADII = CornerRadii(0.0, 3.0, 3.0, 0.0, false)
        val OUTPUT_RADII = CornerRadii(3.0, 0.0, 0.0, 3.0, false)
    }
}
