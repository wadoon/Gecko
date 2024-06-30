package org.gecko.viewmodel


import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Point2D
import org.gecko.actions.ActionManager
import org.gecko.view.GeckoView
import org.gecko.view.contextmenu.StateViewElementContextMenuBuilder
import org.gecko.view.contextmenu.ViewContextMenuBuilder
import org.gecko.view.inspector.builder.AbstractInspectorBuilder
import org.gecko.view.inspector.builder.StateInspectorBuilder
import org.gecko.view.views.viewelement.StateViewElement
import org.gecko.view.views.viewelement.decorator.SelectableViewElementDecorator
import org.gecko.view.views.viewelement.decorator.ViewElementDecorator
import tornadofx.getValue
import tornadofx.setValue

/**
 * Represents an abstraction of a [State] model element. A [State] is described by a set of
 * [Contract]s and can target either a regular or a start-[State]. Contains methods for managing
 * the afferent data and updating the target-[State].
 */
data class State(
    val isStartStateProperty: BooleanProperty = SimpleBooleanProperty(false),
    val contractsProperty: ListProperty<Contract> = SimpleListProperty(FXCollections.observableArrayList()),
    val incomingEdgesProperty: ListProperty<Edge> = SimpleListProperty(FXCollections.observableArrayList()),
    val outgoingEdgesProperty: ListProperty<Edge> = SimpleListProperty(FXCollections.observableArrayList()),
) : BlockElement(), Inspectable {
    var isStartState by isStartStateProperty
    var contracts by contractsProperty
    var incomingEdges by incomingEdgesProperty
    var outgoingEdges: ObservableList<Edge> by outgoingEdgesProperty

    override fun asJson() = super.asJson().apply {
        addProperty("isStartState", isStartState)
        add("contracts", contracts.asJsonArray())
    }

    init {
        name = AutoNaming.name("State_")
        addEdgeListeners()
    }

    fun addContract(contract: Contract) {
        contractsProperty.add(contract)
    }

    fun removeContract(contract: Contract) {
        contractsProperty.remove(contract)
    }

    override val children: Sequence<Element>
        get() = contracts.asSequence()

    override fun view(actionManager: ActionManager, geckoView: GeckoView): ViewElementDecorator {
        val newStateViewElement = StateViewElement(this)

        val contextMenuBuilder: ViewContextMenuBuilder =
            StateViewElementContextMenuBuilder(actionManager, this, geckoView)
        //setContextMenu(newStateViewElement, contextMenuBuilder)
        return SelectableViewElementDecorator(newStateViewElement)
    }

    fun addEdgeListeners() {
        updateEdgeOffset()
        incomingEdgesProperty.addListener { _: ListChangeListener.Change<out Edge?>? -> updateEdgeOffset() }
        outgoingEdgesProperty.addListener { _: ListChangeListener.Change<out Edge?>? -> updateEdgeOffset() }
        positionProperty.addListener { _: ObservableValue<out Point2D?>?, oldValue: Point2D?, newValue: Point2D? -> updateEdgeOffset() }
    }

    fun updateEdgeOffset() {
        notifyOtherState()
        setEdgeOffsets()
    }

    fun notifyOtherState() {
        outgoingEdges.forEach { edge: Edge -> edge.destination.setEdgeOffsets() }
        incomingEdges.forEach { edge: Edge -> edge.source.setEdgeOffsets() }
    }

    fun setEdgeOffsets() {
        val intersectionOrientationEdges = intersectionOrientationEdges
        sortEdges(intersectionOrientationEdges)
        var loopOrientation = 0
        var min = Int.MAX_VALUE
        for (orientation in 0 until ORIENTATIONS) {
            val count =
                intersectionOrientationEdges[orientation]!!.size + intersectionOrientationEdges[(orientation + 1) % ORIENTATIONS]!!.size
            if (count < min) {
                min = count
                loopOrientation = orientation
            }
        }

        for (edge in intersectionOrientationEdges[LOOPS]!!) {
            intersectionOrientationEdges[loopOrientation]!!.addLast(edge)
            intersectionOrientationEdges[(loopOrientation + 1) % ORIENTATIONS]!!.addFirst(edge)
            edge.setOrientation(loopOrientation)
        }

        for (orientation in 0 until ORIENTATIONS) {
            val count = intersectionOrientationEdges[orientation]!!.size
            val width = size.x
            val height = size.y
            val part = (if (orientation % 2 == 0) width else height) / (count + 1)
            var offset = part
            for (edge in intersectionOrientationEdges[orientation]!!) {
                var isSource = edge.source == this
                if (edge.isLoop) {
                    isSource = loopOrientation == orientation
                }
                when (orientation) {
                    0 -> setOffset(edge, isSource, -width / 2 + offset, -height / 2)
                    1 -> setOffset(edge, isSource, width / 2, -height / 2 + offset)
                    2 -> setOffset(edge, isSource, width / 2 - offset, height / 2)
                    3 -> setOffset(edge, isSource, -width / 2, height / 2 - offset)
                    else -> continue
                }
                offset += part
            }
        }
    }

    fun getOtherEdgePoint(edge: Edge): Point2D {
        if (edge.source == this)
            return edge.destination.center
        return edge.source.center
    }

    fun compareEdges(e1: Edge, e2: Edge, orientation: Int): Int {
        val compare = when (orientation) {
            0 -> getOtherEdgePoint(e1).x.compareTo(getOtherEdgePoint(e2).x)
            1 -> getOtherEdgePoint(e1).y.compareTo(getOtherEdgePoint(e2).y)
            2 -> getOtherEdgePoint(e2).x.compareTo(getOtherEdgePoint(e1).x)
            3 -> getOtherEdgePoint(e2).y.compareTo(getOtherEdgePoint(e1).y)
            else -> 0
        }
//        if (compare == 0) {
//            val equalCompare = e1.compareTo(e2)
//            return if (orientation > 1) -equalCompare else equalCompare
//        }
        return compare
    }

    fun sortEdges(intersectionOrientationEdges: Map<Int, MutableList<Edge>>) {
        for (orientation in 0 until ORIENTATIONS) {
            val finalOrientation = orientation
            intersectionOrientationEdges[orientation]!!.sortWith { e1: Edge, e2: Edge ->
                compareEdges(
                    e1, e2, finalOrientation
                )
            }
        }
    }

    fun setOffset(edge: Edge, isSource: Boolean, x: Double, y: Double) {
        if (isSource) {
            edge.setStartOffsetProperty(Point2D(x, y))
        } else {
            edge.setEndOffsetProperty(Point2D(x, y))
        }
    }

    val intersectionOrientationEdges: Map<Int, MutableList<Edge>>
        get() {
            val intersectionOrientationEdges: MutableMap<Int, MutableList<Edge>> = HashMap()
            for (i in 0 until ORIENTATIONS + 1) {
                intersectionOrientationEdges[i] = ArrayList()
            }
            val edges: MutableList<Edge> = ArrayList<Edge>(incomingEdges)
            edges.addAll(outgoingEdges.reversed())
            for (edge in edges) {
                if (edge.isLoop && !intersectionOrientationEdges[LOOPS]!!.contains(edge)) {
                    intersectionOrientationEdges[LOOPS]!!.add(edge)
                    continue
                }
                val p1 = edge.source.center
                val p2 = edge.destination.center
                val orientation = getIntersectionOrientation(p1, p2)
                if (orientation != -1) {
                    intersectionOrientationEdges[orientation]!!.add(edge)
                }
            }
            return intersectionOrientationEdges
        }

    fun getIntersectionOrientation(p1: Point2D, p2: Point2D): Int {
        val edgePoints: List<Point2D> = ArrayList(
            listOf(
                position, position.add(size.x, 0.0), position.add(size), position.add(0.0, size.y)
            )
        )
        for (i in edgePoints.indices) {
            val p3 = edgePoints[i]
            val p4 = edgePoints[(i + 1) % ORIENTATIONS]
            if (lineIntersectsLine(p1, p2, p3, p4)) {
                return i
            }
        }
        return -1
    }

    companion object {
        const val LOOPS = 4
        const val ORIENTATIONS = 4
        fun lineIntersectsLine(l1p1: Point2D, l1p2: Point2D, l2p1: Point2D, l2p2: Point2D): Boolean {
            val s1X = l1p2.x - l1p1.x
            val s1Y = l1p2.y - l1p1.y
            val s2X = l2p2.x - l2p1.x
            val s2Y = l2p2.y - l2p1.y

            val v = -s2X * s1Y + s1X * s2Y
            val s = (-s1Y * (l1p1.x - l2p1.x) + s1X * (l1p1.y - l2p1.y)) / v
            val t = (s2X * (l1p1.y - l2p1.y) - s2Y * (l1p1.x - l2p1.x)) / v

            return s in 0.0..1.0 && t >= 0 && t <= 1
        }
    }

    override fun inspector(actionManager: ActionManager): AbstractInspectorBuilder<*> =
        StateInspectorBuilder(actionManager, this)
}
