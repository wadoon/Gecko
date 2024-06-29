package org.gecko.viewmodel


import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
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
import java.lang.System
import java.util.*

/**
 * Represents an abstraction of a [Region] model element. A [Region] is described by a
 * [Color], a set of [StateViewModel]s, a [Contract] and an invariant. Contains methods for
 * managing the afferent data and updating the target-[Region].
 */
data class Region(val contract: Contract) : BlockViewModelElement(), Inspectable {
    val colorProperty: Property<Color> = SimpleObjectProperty<Color>(Color.WHITE)
    var color: Color by colorProperty

    val invariantProperty = SimpleObjectProperty(Condition(""))
    var invariant: Condition by invariantProperty

    val statesProperty = listProperty<StateViewModel>()
    var states: ObservableList<StateViewModel> by statesProperty

    override fun asJson() = super.asJson().apply {
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

    fun addState(state: StateViewModel) {
        statesProperty.add(state)
    }

    fun removeState(state: StateViewModel) {
        statesProperty.remove(state)
    }

    fun clearStates() {
        statesProperty.clear()
    }

    override val children: Sequence<Element>
        get() = sequenceOf()

    override fun view(actionManager: ActionManager, geckoView: GeckoView): ViewElementDecorator {
        val newRegionViewElement = RegionViewElement(this)
        val contextMenuBuilder: ViewContextMenuBuilder =
            RegionViewElementContextMenuBuilder(actionManager, this, geckoView)
        //setContextMenu(newRegionViewElement, contextMenuBuilder)
        return BlockElementScalerViewElementDecorator(SelectableViewElementDecorator(newRegionViewElement))
    }

    /**
     * Checks if the given state is in the region and adds it to the region if it is.
     *
     * @param state the state to check
     */
    fun checkStateInRegion(state: StateViewModel) {
        val regionBound: Bounds =
            BoundingBox(position.x, position.y, size.x, size.y)
        val stateBound: Bounds =
            BoundingBox(
                state.position.x, state.position.y, state.size.x,
                state.size.y
            )
        if (regionBound.intersects(stateBound)) {
            addState(state)
        }
    }

    fun includes(state: StateViewModel): Boolean {
        TODO()
        return true
    }

    companion object {
        const val MAXIMUM_RGB_COLOR_VALUE = 255
    }

    override fun inspector(actionManager: ActionManager) =
        RegionInspectorBuilder(actionManager, this)
}

