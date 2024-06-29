package org.gecko.viewmodel


import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.geometry.Point2D
import org.gecko.actions.ActionManager
import org.gecko.view.GeckoView
import org.gecko.view.contextmenu.EdgeViewElementContextMenuBuilder
import org.gecko.view.inspector.builder.EdgeInspectorBuilder
import org.gecko.view.views.viewelement.EdgeViewElement
import org.gecko.view.views.viewelement.decorator.ConnectionElementScalerViewElementDecorator
import org.gecko.view.views.viewelement.decorator.ViewElementDecorator
import tornadofx.getValue
import tornadofx.setValue

/**
 * Represents an abstraction of an [Edge] model element. An [EdgeViewModel] is described by a source- and a
 * destination-[StateViewModel]. It is also associated with one of the start-[StateViewModel]'s
 * [ContractViewModel]s, has a priority and a [Kind], which informs about how the associated
 * [ContractViewModel] is handled. Contains methods for managing the afferent data and updating the
 * target-[Edge].
 */
class EdgeViewModel(src: StateViewModel, dst: StateViewModel) : PositionableViewModelElement(), Inspectable {
    val kindProperty: ObjectProperty<Kind> = SimpleObjectProperty(Kind.HIT)
    val priorityProperty: IntegerProperty = SimpleIntegerProperty(0)
    val contractProperty: ObjectProperty<ContractViewModel?> = nullableObjectProperty()
    val sourceProperty: ObjectProperty<StateViewModel> = objectProperty(src)
    val destinationProperty: ObjectProperty<StateViewModel> = objectProperty(dst)
    val isLoopProperty: BooleanProperty = SimpleBooleanProperty(false)
    val orientationProperty: IntegerProperty = SimpleIntegerProperty(0)

    var priority: Int by priorityProperty
    var kind: Kind by kindProperty
    var contract: ContractViewModel? by contractProperty
    var source: StateViewModel by sourceProperty
    var destination: StateViewModel by destinationProperty

    override fun asJson() = super.asJson().apply {
        addProperty("source", source.name)
        addProperty("destination", destination.name)
        add("contract",
            contract?.name?.let { JsonPrimitive(it) } ?: JsonNull.INSTANCE)
        addProperty("kind", kind.name)
        addProperty("priority", priority)
    }


    /**
     * The list of edge points that define the path of the edge.
     */
    val startOffsetProperty: ObjectProperty<Point2D> = SimpleObjectProperty(Point2D.ZERO)
    val startPointProperty: ObservableValue<Point2D> = Bindings.createObjectBinding(
        { source.center.add(startOffsetProperty.value) },
        startOffsetProperty,
        source.positionProperty
    )
    val endOffsetProperty: ObjectProperty<Point2D> = SimpleObjectProperty(Point2D.ZERO)
    val endPointProperty: ObservableValue<Point2D> = Bindings.createObjectBinding(
        { destination.center.add(endOffsetProperty.value) },
        endOffsetProperty,
        destinationProperty,
        destination.positionProperty
    )


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

    val startPoint: Point2D by startPointProperty
    val endPoint: Point2D by endPointProperty

    val isLoop: Boolean
        get() = isLoopProperty.value

    init {
        startPointProperty

        endPointProperty


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

        this.source.outgoingEdges.add(this)
        this.destination.incomingEdges?.add(this)
    }

    fun setBindings() {}

    fun removeBindings() {
        //startPointProperty.unbind()
        //endPointProperty.unbind()
    }

    fun setStartOffsetProperty(startOffset: Point2D) {
        startOffsetProperty.value = startOffset
    }

    fun setEndOffsetProperty(endOffset: Point2D) {
        endOffsetProperty.value = endOffset
    }

    override val children: Sequence<Element>
        get() = sequenceOf()

    override fun view(actionManager: ActionManager, geckoView: GeckoView): ViewElementDecorator {
        val newEdgeViewElement = EdgeViewElement(this)
        val contextMenuBuilder = EdgeViewElementContextMenuBuilder(actionManager, this, geckoView)
        //setContextMenu(newEdgeViewElement.pane, contextMenuBuilder)
        return ConnectionElementScalerViewElementDecorator(newEdgeViewElement)
    }

    fun setOrientation(orientation: Int) {
        orientationProperty.value = orientation
    }

    override val center: Point2D
        get() = startPointProperty.value.midpoint(endPointProperty.value)

    override fun inspector(actionManager: ActionManager) = EdgeInspectorBuilder(actionManager, this)
}
