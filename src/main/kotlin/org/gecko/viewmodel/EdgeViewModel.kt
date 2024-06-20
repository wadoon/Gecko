package org.gecko.viewmodel


import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.geometry.Point2D
import tornadofx.getValue
import tornadofx.setValue

/**
 * Represents an abstraction of an [Edge] model element. An [EdgeViewModel] is described by a source- and a
 * destination-[StateViewModel]. It is also associated with one of the start-[StateViewModel]'s
 * [ContractViewModel]s, has a priority and a [Kind], which informs about how the associated
 * [ContractViewModel] is handled. Contains methods for managing the afferent data and updating the
 * target-[Edge].
 */
data class EdgeViewModel(
    val kindProperty: ObjectProperty<Kind> = SimpleObjectProperty(Kind.HIT),
    val priorityProperty: IntegerProperty = SimpleIntegerProperty(0),
    val contractProperty: ObjectProperty<ContractViewModel?> = SimpleObjectProperty(),
    val sourceProperty: ObjectProperty<StateViewModel?> = SimpleObjectProperty(),
    val destinationProperty: ObjectProperty<StateViewModel?> = SimpleObjectProperty(),
    val isLoopProperty: BooleanProperty = SimpleBooleanProperty(false),
    val orientationProperty: IntegerProperty = SimpleIntegerProperty(0),
) : PositionableViewModelElement() {
    constructor(src: StateViewModel, dst: StateViewModel) : this() {
        destination = dst
        source = src
    }

    /**
     * The list of edge points that define the path of the edge.
     */
    val startPointProperty: ObjectProperty<Point2D> = SimpleObjectProperty()
    val endPointProperty: ObjectProperty<Point2D> = SimpleObjectProperty()
    val startOffsetProperty: ObjectProperty<Point2D> = SimpleObjectProperty(Point2D.ZERO)
    val endOffsetProperty: ObjectProperty<Point2D> = SimpleObjectProperty(Point2D.ZERO)

    var priority: Int by priorityProperty
    var kind: Kind by kindProperty
    var contract: ContractViewModel? by contractProperty
    var source: StateViewModel? by sourceProperty
    var destination: StateViewModel? by destinationProperty

    val representation: String
        /**
         * Returns a string representation of this [EdgeViewModel] in the form of "priority. kind(contract)".
         *
         * @return a string representation of this [EdgeViewModel]
         */
        get() {
            var representation = ""
            representation += priority.toString() + ". "
            representation += kind.name
            if (contract != null) {
                representation += "(" + contract!!.name + ")"
            }
            return representation
        }

    var startPoint: Point2D
        get() = startPointProperty.value
        set(startPoint) {
            removeBindings()
            startPointProperty.value = startPoint
        }

    var endPoint: Point2D
        get() = endPointProperty.value
        set(endPoint) {
            removeBindings()
            endPointProperty.value = endPoint
        }

    val isLoop: Boolean
        get() = isLoopProperty.value

    init {
        sourceProperty.onChange { field, new ->
            field?.outgoingEdges!!.remove(this)
            removeBindings()
            setBindings()
            new?.outgoingEdges?.add(this)
        }

        destinationProperty.onChange { field, new ->
            field?.incomingEdges?.remove(this)
            removeBindings()
            setBindings()
            new?.incomingEdges?.add(this)
        }

        sourceProperty.set(source)
        destinationProperty.set(destination)
        isLoopProperty.bind(
            Bindings.createBooleanBinding(
                { this.source == this.destination }, sourceProperty,
                destinationProperty
            )
        )

        sizeProperty.value = Point2D.ZERO
        setBindings()

        this.source?.outgoingEdges?.add(this)
        this.destination?.incomingEdges?.add(this)
    }

    fun setBindings() {
        startPointProperty.bind(
            Bindings.createObjectBinding(
                { source!!.center!!.add(startOffsetProperty.value) }, startOffsetProperty, source!!.positionProperty
            )
        )
        endPointProperty.bind(
            Bindings.createObjectBinding(
                { destination!!.center!!.add(endOffsetProperty.value) },
                endOffsetProperty,
                destination!!.positionProperty
            )
        )
    }

    fun removeBindings() {
        startPointProperty.unbind()
        endPointProperty.unbind()
    }

    fun setStartOffsetProperty(startOffset: Point2D) {
        startOffsetProperty.value = startOffset
    }

    fun setEndOffsetProperty(endOffset: Point2D) {
        endOffsetProperty.value = endOffset
    }

    override fun <S> accept(visitor: PositionableViewModelElementVisitor<S>): S {
        return visitor.visit(this)
    }

    fun setOrientation(orientation: Int) {
        orientationProperty.value = orientation
    }

    override var center: Point2D?
        get() = startPointProperty.value.midpoint(endPointProperty.value)
        set(value) {}
}
