package org.gecko.viewmodel

import java.lang.System
import java.util.*
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.geometry.BoundingBox
import javafx.scene.paint.Color
import org.gecko.actions.ActionManager
import org.gecko.view.GeckoView
import org.gecko.view.contextmenu.RegionViewElementContextMenuBuilder
import org.gecko.view.contextmenu.ViewContextMenuBuilder
import org.gecko.view.inspector.builder.RegionInspectorBuilder
import org.gecko.view.views.viewelement.RegionViewElement
import org.gecko.view.views.viewelement.decorator.BlockElementScalerViewElementDecorator
import org.gecko.view.views.viewelement.decorator.SelectableViewElementDecorator
import org.gecko.view.views.viewelement.decorator.ViewElementDecorator
import tornadofx.getValue
import tornadofx.setValue

/**
 * Represents an abstraction of a [Region] model element. A [Region] is described by a [Color], a
 * set of [State]s, a [Contract] and an invariant. Contains methods for managing the afferent data
 * and updating the target-[Region].
 */
data class Region(val contract: Contract) : BlockElement(), Inspectable {
    val colorProperty: Property<Color> = SimpleObjectProperty<Color>(Color.WHITE)
    var color: Color by colorProperty

    val invariantProperty = SimpleObjectProperty(Condition(""))
    var invariant: Condition by invariantProperty

    val statesProperty = listProperty<State>()
    var states: ObservableList<State> by statesProperty

    override fun asJson() =
        super.asJson().apply {
            add("color", color.asJson())
            addProperty("invariant", invariant.value)
            add("contract", contract.asJson())
        }

    init {
        val random = Random(System.currentTimeMillis())
        val red = random.nextInt(MAXIMUM_RGB_COLOR_VALUE)
        val green = random.nextInt(MAXIMUM_RGB_COLOR_VALUE)
        val blue = random.nextInt(MAXIMUM_RGB_COLOR_VALUE)
        color = Color.rgb(red, green, blue, 0.5)
    }

    override val children: Sequence<Element>
        get() = sequenceOf()

    override fun view(actionManager: ActionManager, geckoView: GeckoView): ViewElementDecorator {
        val newRegionViewElement = RegionViewElement(this)
        val contextMenuBuilder: ViewContextMenuBuilder =
            RegionViewElementContextMenuBuilder(actionManager, this, geckoView)
        // setContextMenu(newRegionViewElement, contextMenuBuilder)
        return BlockElementScalerViewElementDecorator(
            SelectableViewElementDecorator(newRegionViewElement)
        )
    }

    /**
     * Checks if the given state is in the region and adds it to the region if it is.
     *
     * @param state the state to check
     */
    fun checkStateInRegion(state: State): Boolean {
        val regionBound = BoundingBox(position.x, position.y, size.x, size.y)
        val stateBound = BoundingBox(state.position.x, state.position.y, state.size.x, state.size.y)
        if (regionBound.intersects(stateBound)) {
            states.add(state)
            return true
        }
        return false
    }

    companion object {
        const val MAXIMUM_RGB_COLOR_VALUE = 255
    }

    override fun inspector(actionManager: ActionManager) =
        RegionInspectorBuilder(actionManager, this)
}
