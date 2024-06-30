package org.gecko.view.views

import javafx.beans.binding.Bindings
import javafx.beans.property.DoubleProperty
import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Pane
import javafx.scene.transform.Scale
import org.gecko.view.views.viewelement.ViewElement
import org.gecko.viewmodel.*
import kotlin.math.max
import kotlin.math.min

class ViewElementPane(val evm: EditorViewModel) {
    val offset: Property<Point2D> = objectProperty(Point2D.ZERO)
    val widthPadding: DoubleProperty = doubleProperty(0.0)
    val heightPadding: DoubleProperty = doubleProperty(0.0)
    var minWorldPosition: Point2D
    var maxWorldPosition: Point2D

    val pane = ScrollPane()
    val world = Pane()
    val elements = listProperty<ViewElement<*>>()
    val nodeToElement: MutableMap<Node?, ViewElement<*>> = HashMap()

    init {
        this.minWorldPosition = Point2D.ZERO
        this.maxWorldPosition = Point2D.ZERO

        setupListeners()

        widthPadding.bind(
            Bindings.createDoubleBinding({ pane.viewportBounds.width }, pane.viewportBoundsProperty())
        )
        heightPadding.bind(
            Bindings.createDoubleBinding({ pane.viewportBounds.height }, pane.viewportBoundsProperty())
        )

        pane.content = world

        world.style = "-fx-background-color: white"
    }

    fun draw(): ScrollPane {
        return pane
    }

    fun onSelectionChanged() {
        orderChildren()
    }

    fun addElement(element: ViewElement<*>) {
        // Save the pivot to refocus after adding the element because changing the world size will change the pivot
        val oldPivot = evm.pivot
        val target = element.target
        elements.add(element)

        val node = element.drawElement()
        nodeToElement[node] = element
        node.layoutX = worldTolocalCoordinates(target!!.position).x
        node.layoutY = worldTolocalCoordinates(target.position).y
        node.layoutXProperty()
            .bind(
                Bindings.createDoubleBinding(
                    { worldTolocalCoordinates(target.position).x },
                    target.positionProperty, offset
                )
            )
        node.layoutYProperty()
            .bind(
                Bindings.createDoubleBinding(
                    { worldTolocalCoordinates(target.position).y },
                    target.positionProperty, offset
                )
            )

        node.transforms.setAll(Scale(evm.zoomScale, evm.zoomScale, 0.0, 0.0))
        evm.zoomScaleProperty.addListener { obs: ObservableValue<out Number>?, oldV: Number?, newV: Number ->
            //Can't bind to zoomScale because the pivot of (0, 0) is important to keep world coordinates consistent with
            //visual position
            node.transforms.setAll(Scale(newV.toDouble(), newV.toDouble(), 0.0, 0.0))
        }
        target.positionProperty.addListener { obs: ObservableValue<out Point2D?>?, oldV: Point2D?, newV: Point2D? ->
            if (target.isCurrentlyModified()) {
                return@addListener
            }
            updateWorldSize(evm.pivot)
        }
        orderChildren()
        updateWorldSize(oldPivot)
    }

    fun removeElement(element: ViewElement<*>?) {
        elements.remove(element)
        orderChildren()
    }

    fun findViewElement(element: PositionableElement?) =
        elements.firstOrNull { e -> e.target == element }

    fun focusWorldCoordinates(worldCoords: Point2D) {
        if (java.lang.Double.isNaN(worldCoords.x) || java.lang.Double.isNaN(worldCoords.y)) {
            return
        }
        focusLocalCoordinates(worldTolocalCoordinates(worldCoords))
    }

    fun worldTolocalCoordinates(worldCoords: Point2D): Point2D {
        return worldCoords.multiply(evm.zoomScale).add(offset.value)
    }

    fun localToWorldCoordinates(screenCoords: Point2D): Point2D {
        return screenCoords.subtract(offset.value).multiply(1 / evm.zoomScale)
    }

    fun screenToLocalCoordinates(screenCoords: Point2D?): Point2D {
        return pane.screenToLocal(screenCoords).add(localViewPortPosition())
    }

    fun screenToLocalCoordinates(x: Double, y: Double): Point2D {
        return screenToLocalCoordinates(Point2D(x, y))
    }

    fun localToScreenCoordinates(localCoords: Point2D): Point2D {
        return pane.localToScreen(localCoords.subtract(localViewPortPosition()))
    }

    @Suppress("unused")
    fun worldToScreenCoordinates(worldCoords: Point2D): Point2D {
        return localToScreenCoordinates(worldTolocalCoordinates(worldCoords))
    }

    @Suppress("unused")
    fun worldToScreenCoordinates(x: Double, y: Double): Point2D {
        return worldToScreenCoordinates(Point2D(x, y))
    }

    fun screenToWorldCoordinates(screenCoords: Point2D?): Point2D {
        return localToWorldCoordinates(screenToLocalCoordinates(screenCoords))
    }

    fun screenToWorldCoordinates(x: Double, y: Double): Point2D {
        return screenToWorldCoordinates(Point2D(x, y))
    }

    fun focusLocalCoordinates(localCoords: Point2D) {
        val h = (localCoords.x - pane.viewportBounds.width / 2) / (world.width
                - pane.viewportBounds.width)
        val v = (localCoords.y - pane.viewportBounds.height / 2) / (world.height
                - pane.viewportBounds.height)
        pane.hvalue = h
        pane.vvalue = v
    }

    fun updateWorldSize(oldPivot: Point2D) {
        updateWorldSize()
        evm.pivot = oldPivot
    }

    fun updateWorldSize() {
        updateMinAndMaxWorldPosition()
        val min = Point2D(min(0.0, minWorldPosition.x), min(0.0, minWorldPosition.y))
        val max = Point2D(max(0.0, maxWorldPosition.x), max(0.0, maxWorldPosition.y))
        val localMin = worldTolocalCoordinates(min)
        val localMax = worldTolocalCoordinates(max)
        val newWidth = localMax.x - localMin.x + widthPadding.get() * 2
        val newHeight = localMax.y - localMin.y + heightPadding.get() * 2
        world.setMinSize(newWidth, newHeight)
        pane.layout()
        updateOffset()
    }

    fun localViewPortPosition(): Point2D {
        val h = if (java.lang.Double.isNaN(pane.hvalue)) 0.0 else pane.hvalue
        val v = if (java.lang.Double.isNaN(pane.vvalue)) 0.0 else pane.vvalue
        val x = h * (world.width - pane.viewportBounds.width)
        val y = v * (world.height - pane.viewportBounds.height)
        return Point2D(x, y)
    }

    fun screenCenterWorldCoords(): Point2D {
        //Can't use screenToLocal because we don't want the pane.localToScreen() offset
        val screenCenter =
            Point2D(pane.viewportBounds.width / 2, pane.viewportBounds.height / 2)
        val localScreenCenter = screenCenter.add(localViewPortPosition())
        return localToWorldCoordinates(localScreenCenter)
    }

    fun updateOffset() {
        val x = max(0.0, -minWorldPosition.x * evm.zoomScale) + widthPadding.get()
        val y = max(0.0, -minWorldPosition.y * evm.zoomScale) + heightPadding.get()
        offset.value = Point2D(x, y)
    }

    fun setupListeners() {
        evm.needsRefocusProperty.addListener { obs: ObservableValue<out Boolean>?, oldV: Boolean?, newV: Boolean ->
            if (newV) {
                focusWorldCoordinates(evm.pivot)
                evm.needsRefocusProperty.set(false)
            }
        }
        evm.zoomScaleProperty.addListener { obs: ObservableValue<out Number?>?, oldV: Number?, newV: Number? ->
            updateWorldSize(evm.pivot)
        }
        pane.hvalueProperty().addListener { obs: ObservableValue<out Number?>?, oldH: Number?, newH: Number? ->
            evm.updatePivot(screenCenterWorldCoords())
        }
        pane.vvalueProperty().addListener { obs: ObservableValue<out Number?>?, oldV: Number?, newV: Number? ->
            evm.updatePivot(screenCenterWorldCoords())
        }
        widthPadding.addListener { obs: ObservableValue<out Number?>?, oldV: Number?, newV: Number? ->
            updateWorldSize(evm.pivot)
        }
        heightPadding.addListener { obs: ObservableValue<out Number?>?, oldV: Number?, newV: Number? ->
            updateWorldSize(evm.pivot)
        }
    }

    fun updateMinAndMaxWorldPosition() {
        if (elements.isEmpty() || elements.all { it.target!!.isCurrentlyModified() }) {
            return
        }
        var minX = Double.MAX_VALUE
        var minY = Double.MAX_VALUE
        var maxX = -Double.MAX_VALUE
        var maxY = -Double.MAX_VALUE
        for (element in elements) {
            val target = element.target
            if (target!!.position.x < minX) {
                minX = target.position.x
            }
            if (target.position.y < minY) {
                minY = target.position.y
            }
            if (target.position.x + target.size.x > maxX) {
                maxX = target.position.x + target.size.x
            }
            if (target.position.y + target.size.y > maxY) {
                maxY = target.position.y + target.size.y
            }
        }
        minWorldPosition = Point2D(minX, minY)
        maxWorldPosition = Point2D(maxX, maxY)
    }

    fun orderChildren() {
        val newElements =
            elements.map { obj: ViewElement<*> -> obj.drawElement() }
                .filter { !world.children.contains(it) }
        world.children.addAll(newElements)
        val removedElements = world.children
            .mapNotNull { nodeToElement[it] }
            .filter { !elements.contains(it) }
            .map { it.drawElement() }
        world.children.removeAll(removedElements)
        FXCollections.sort(world.children,
            Comparator.comparingInt { nodeToElement[it]?.zPriority ?: 0 })
    }
}
