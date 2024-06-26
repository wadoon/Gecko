package org.gecko.view.views.viewelement

import javafx.beans.binding.Bindings
import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import kotlin.math.min
import org.gecko.viewmodel.Port
import org.gecko.viewmodel.System
import org.gecko.viewmodel.SystemConnection
import org.gecko.viewmodel.Visibility

/**
 * Represents a type of [BlockViewElement] implementing the [ViewElement] interface, which
 * encapsulates an [System].
 */
class SystemViewElement(System: System) : BlockViewElement(System), ViewElement<System> {
    override val target: System = System
    val nameProperty: StringProperty = SimpleStringProperty()
    val codeProperty: StringProperty = SimpleStringProperty()
    val portsProperty: ListProperty<Port> = SimpleListProperty(FXCollections.observableArrayList())
    val inputPortsAligner = VBox()
    val outputPortsAligner = VBox()
    val portViewElements: ListProperty<PortViewElement> =
        SimpleListProperty(FXCollections.observableArrayList<PortViewElement>())

    val positionListener =
        ChangeListener { _: ObservableValue<out Point2D?>?, _: Point2D?, _: Point2D? ->
            reorderPorts()
        }

    init {
        bindViewModel()
        addPortPositionListeners()
        constructVisualization()
    }

    override fun drawElement(): Node {
        return this
    }

    override var isSelected: Boolean = false

    override val position: Point2D
        get() = target.position

    override fun accept(visitor: ViewElementVisitor) {
        visitor.visit(this)
        portViewElements.forEach { portViewElement: PortViewElement ->
            visitor.visit(portViewElement)
        }
    }

    fun bindViewModel() {
        nameProperty.bind(target.nameProperty)
        codeProperty.bind(target.codeProperty)
        prefWidthProperty()
            .bind(Bindings.createDoubleBinding({ target.size.x }, target.sizeProperty))
        prefHeightProperty()
            .bind(Bindings.createDoubleBinding({ target.size.y }, target.sizeProperty))
        portsProperty.bind(target.portsProperty)

        target.positionProperty.addListener {
            observable: ObservableValue<out Point2D?>?,
            oldValue: Point2D?,
            newValue: Point2D? ->
            updatePortViewModels()
        }
        updatePortViewModels()
    }

    fun constructVisualization() {
        val container = HBox()
        container.setPrefSize(prefWidth, prefHeight)
        val portContainers = setupPortContainers()
        container.children.addAll(portContainers.first(), centeredNameLabel, portContainers.last())
        children.addAll(backgroundRectangle, container)
        portsProperty.forEach { Port: Port? -> this.addPort(Port) }
        portsProperty.addListener { change: ListChangeListener.Change<out Port> ->
            this.onPortsChanged(change)
        }
    }

    fun onPortsChanged(change: ListChangeListener.Change<out Port>) {
        while (change.next()) {
            if (change.wasAdded()) {
                change.addedSubList.forEach { this.addPort(it) }
            } else if (change.wasRemoved()) {
                change.removed.forEach { this.removePort(it) }
            }
        }
    }

    fun updatePortViewModels() {
        for (portViewElement in portViewElements) {
            portViewElement.viewModel.setSystemPortSize(portViewElement.viewSize)

            val portViewElementPositionInScene =
                portViewElement.localToScene(portViewElement.viewPosition)

            // translate the port position to the world coordinate system
            val calculatedWorldPosition =
                sceneToLocal(portViewElementPositionInScene)
                    .add(position)
                    .subtract(portViewElement.viewPosition)
            portViewElement.viewModel.setSystemPortPosition(calculatedWorldPosition)
        }
    }

    fun addPort(Port: Port?) {
        Port!!.visibilityProperty.addListener {
            observable: ObservableValue<out Visibility>,
            oldValue: Visibility?,
            newValue: Visibility? ->
            this.onVisibilityChanged(observable, oldValue, newValue)
        }
        val portViewElement = PortViewElement(Port)
        portViewElement.layoutYProperty().addListener {
            observable: ObservableValue<out Number?>?,
            oldValue: Number?,
            newValue: Number? ->
            updatePortViewModels()
        }
        portViewElements.add(portViewElement)
        if (Port.visibility == Visibility.INPUT) {
            inputPortsAligner.children.add(portViewElement)
        } else if (Port.visibility == Visibility.OUTPUT) {
            outputPortsAligner.children.add(portViewElement)
        }
        Port.incomingConnections.addListener {
            change: ListChangeListener.Change<out SystemConnection> ->
            this.onConnectionChanged(change)
        }
        Port.outgoingConnections.addListener {
            change: ListChangeListener.Change<out SystemConnection> ->
            this.onConnectionChanged(change)
        }
        reorderPorts()
    }

    fun removePort(Port: Port) {
        // This is safe, since the portViewElement should be present in the list
        val portViewElement =
            portViewElements.filter { pve: PortViewElement -> pve.viewModel == Port }.first()
        portViewElements.remove(portViewElement)
        if (Port.visibility == Visibility.INPUT) {
            inputPortsAligner.children.remove(portViewElement)
        } else if (Port.visibility == Visibility.OUTPUT) {
            outputPortsAligner.children.remove(portViewElement)
        } else {
            inputPortsAligner.children.remove(portViewElement)
            outputPortsAligner.children.remove(portViewElement)
        }
        Port.incomingConnections.removeListener {
            change: ListChangeListener.Change<out SystemConnection> ->
            this.onConnectionChanged(change)
        }
        Port.outgoingConnections.removeListener {
            change: ListChangeListener.Change<out SystemConnection> ->
            this.onConnectionChanged(change)
        }
        reorderPorts()
    }

    fun onVisibilityChanged(
        observable: ObservableValue<out Visibility?>,
        oldValue: Visibility?,
        newValue: Visibility?
    ) {
        if (oldValue == newValue) {
            return
        }
        val portViewModel =
            portsProperty.filter { pvm: Port? -> pvm!!.visibilityProperty === observable }.first()
        val portViewElement =
            portViewElements
                .filter { pve: PortViewElement -> pve.viewModel == portViewModel }
                .first()
        if (newValue == Visibility.INPUT) {
            outputPortsAligner.children.remove(portViewElement)
            inputPortsAligner.children.add(portViewElement)
        } else if (newValue == Visibility.OUTPUT) {
            inputPortsAligner.children.remove(portViewElement)
            outputPortsAligner.children.add(portViewElement)
        } else {
            inputPortsAligner.children.remove(portViewElement)
            outputPortsAligner.children.remove(portViewElement)
        }
        reorderPorts()
    }

    fun setupPortContainers(): List<HBox> {
        val result: MutableList<HBox> = ArrayList()
        for (aligner in java.util.List.of<VBox>(inputPortsAligner, outputPortsAligner)) {
            // HBox container to ensure horizontal alignment
            val container = HBox()
            // Center names vertically and space them out
            VBox.setVgrow(aligner, Priority.ALWAYS)
            aligner.alignment = Pos.CENTER
            aligner.spacing = PORT_SPACING.toDouble()
            container.children.add(aligner)
            // Width isn't set yet, so we need to listen to it
            widthProperty().addListener {
                observable: ObservableValue<out Number>?,
                oldValue: Number?,
                newValue: Number ->
                // x/3 to evenly divide into left, center, right
                container.prefWidth = newValue.toDouble() / 3
            }
            result.add(container)
        }
        result.last().alignment = Pos.CENTER_RIGHT
        return result
    }

    val backgroundRectangle: Rectangle
        get() {
            val background = Rectangle()
            background.widthProperty().bind(widthProperty())
            background.heightProperty().bind(heightProperty())
            background.arcWidth = BACKGROUND_ROUNDING.toDouble()
            background.arcHeight = BACKGROUND_ROUNDING.toDouble()
            background.fill = Color.LIGHTGRAY
            return background
        }

    val centeredNameLabel: Node
        get() {
            val nameLabel = Label()
            nameLabel.textProperty().bind(nameProperty)
            // Center name vertically
            val nameContainer = VBox()
            nameContainer.children.add(nameLabel)
            nameContainer.alignment = Pos.CENTER
            // Center name horizontally. Assumes that the port containers are of equal width and
            // always present
            val spacer = HBox()
            spacer.alignment = Pos.CENTER
            HBox.setHgrow(spacer, Priority.ALWAYS)
            spacer.children.add(nameContainer)
            return spacer
        }

    fun reorderPorts() {
        val cmp = { p1: PortViewElement, p2: PortViewElement -> this.compare(p1, p2) }
        val inputs =
            inputPortsAligner.children.map { PortViewElement::class.java.cast(it) }.sortedWith(cmp)
        val outputs =
            outputPortsAligner.children.map { PortViewElement::class.java.cast(it) }.sortedWith(cmp)
        inputPortsAligner.children.setAll(inputs)
        outputPortsAligner.children.setAll(outputs)
    }

    fun compare(p1: PortViewElement, p2: PortViewElement): Int {
        return getOtherPortY(p1.viewModel).compareTo(getOtherPortY(p2.viewModel))
    }

    fun getSortPosition(Port: Port?): Point2D {
        if (Port == null) return Point2D.ZERO

        if (isVariableBlock(Port)) {
            return Port.center
        }
        return Port.systemPositionProperty.value.add(Port.systemPortOffsetProperty.value)
    }

    fun getOtherPortY(Port: Port): Double {
        val connections: List<SystemConnection> =
            if (Port.visibility == Visibility.INPUT) Port.incomingConnections
            else Port.outgoingConnections
        var minY = Double.MAX_VALUE
        for (connection in connections) {
            minY =
                if (connection.source == Port) {
                    min(minY, getSortPosition(connection.destination).y)
                } else {
                    min(minY, getSortPosition(connection.source).y)
                }
        }
        return minY
    }

    fun isVariableBlock(Port: Port): Boolean {
        return target.parent!!.ports.contains(Port)
    }

    private val edgePointListener = { change: ListChangeListener.Change<out Point2D> ->
        this.onEdgePointsChanged(change)
    }

    fun onConnectionChanged(change: ListChangeListener.Change<out SystemConnection>) {
        while (change.next()) {

            if (change.wasAdded()) {
                change.addedSubList.forEach { it.edgePoints.addListener(edgePointListener) }
            } else if (change.wasRemoved()) {
                change.removed.forEach { connection ->
                    connection.edgePoints.removeListener(edgePointListener)
                }
            }
        }
        reorderPorts()
    }

    fun onEdgePointsChanged(change: ListChangeListener.Change<out Point2D?>) {
        reorderPorts()
        while (change.next()) {
            if (change.wasAdded()) {
                // change.getAddedSubList().forEach(point -> point.addListener(positionListener));
            } else if (change.wasRemoved()) {
                // change.getRemoved().forEach(point -> point.removeListener(positionListener));
            }
        }
    }

    fun addPortPositionListeners() {
        for (portViewModel in portsProperty) {
            val connections: MutableList<SystemConnection> =
                ArrayList(portViewModel!!.incomingConnections)
            connections.addAll(portViewModel.outgoingConnections)
            for (connection in connections) {
                connection.edgePoints.addListener(edgePointListener)
                // connection.getEdgePoints().forEach(point -> point.addListener(positionListener));
            }
        }
    }

    override val zPriority: Int = 30
}

private const val PORT_SPACING = 10
