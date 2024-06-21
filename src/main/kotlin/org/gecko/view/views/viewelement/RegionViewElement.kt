package org.gecko.view.views.viewelement

import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.ListChangeListener
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.RegionViewModel
import org.gecko.viewmodel.StateViewModel

/**
 * Represents a type of [BlockViewElement] implementing the [ViewElement] interface, which encapsulates an
 * [RegionViewModel].
 */

class RegionViewElement(override val target: RegionViewModel) : BlockViewElement(target), ViewElement<RegionViewModel> {
    val nameProperty: StringProperty = SimpleStringProperty()
    val colorProperty: Property<Color> = SimpleObjectProperty()
    val invariantProperty: StringProperty = SimpleStringProperty()
    val states: MutableList<StateViewModel> = ArrayList()
    override var isSelected: Boolean = false

    init {
        bindViewModel()
        constructViewElement()
    }

    override fun drawElement(): Node {
        return this
    }

    override fun setEdgePoint(index: Int, point: Point2D): Boolean {
        return target.manipulate(
            edgePoints[(index + edgePoints.size / 2) % edgePoints.size], point
        )
    }

    override val position: Point2D
        get() = position

    fun bindViewModel() {
        nameProperty.bind(target.nameProperty)
        colorProperty.bind(target.colorProperty)
        invariantProperty.bind(target.invariant.valueProperty)
        val listener = ListChangeListener { change: ListChangeListener.Change<out StateViewModel> ->
            while (change.next()) {
                if (change.wasAdded()) {
                    states.addAll(change.addedSubList)
                }
                if (change.wasRemoved()) {
                    states.removeAll(change.removed)
                }
            }
        }
        target.statesProperty.addListener(listener)
        prefWidthProperty().bind(
            Bindings.createDoubleBinding({ target.size.x }, target.sizeProperty)
        )
        prefHeightProperty().bind(
            Bindings.createDoubleBinding({ target.size.y }, target.sizeProperty)
        )
    }

    fun constructViewElement() {
        styleClass.add(STYLE)

        val background = Rectangle()
        background.widthProperty().bind(widthProperty())
        background.heightProperty().bind(heightProperty())
        background.fillProperty()
            .bind(
                Bindings.createObjectBinding(
                    {
                        Color(
                            colorProperty.value.red, colorProperty.value.green,
                            colorProperty.value.blue, 0.5
                        )
                    }, colorProperty
                )
            )
        background.arcHeight = BACKGROUND_ROUNDING.toDouble()
        background.arcWidth = BACKGROUND_ROUNDING.toDouble()
        val gridPane = GridPane()
        gridPane.styleClass.add(INNER_STYLE)

        val nameDesc = Label(ResourceHandler.name + ": ")
        val name = Label()
        name.textProperty().bind(nameProperty)
        val preConditionDesc = Label(ResourceHandler.pre_condition_short + ":")
        val preCondition = Label()
        preCondition.textProperty().bind(target.contract.preCondition.valueProperty)
        val postConditionDesc = Label(ResourceHandler.post_condition_short + ":")
        val postCondition = Label()
        postCondition.textProperty().bind(target.contract.postCondition.valueProperty)
        val invariantDesc = Label(ResourceHandler.invariant_short + ": ")
        val invariant = Label()
        invariant.textProperty().bind(invariantProperty)
        gridPane.add(nameDesc, 0, 0)
        gridPane.add(name, 1, 0)
        gridPane.add(preConditionDesc, 0, 1)
        gridPane.add(preCondition, 1, 1)
        gridPane.add(postConditionDesc, 0, 2)
        gridPane.add(postCondition, 1, 2)
        gridPane.add(invariantDesc, 0, 3)
        gridPane.add(invariant, 1, 3)
        children.addAll(background, gridPane)
    }

    override fun accept(visitor: ViewElementVisitor) {
        visitor.visit(this)
    }

    override val zPriority: Int = 10

    companion object {
        const val STYLE = "region-view-element"
        const val INNER_STYLE = "region-inner-view-element"
    }
}
