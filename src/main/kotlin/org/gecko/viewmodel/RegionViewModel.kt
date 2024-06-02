package org.gecko.viewmodel


import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.scene.paint.Color
import org.gecko.exceptions.ModelException
import org.gecko.model.*
import tornadofx.getValue
import tornadofx.setValue
import java.lang.System
import java.util.*

/**
 * Represents an abstraction of a [Region] model element. A [RegionViewModel] is described by a
 * [Color], a set of [StateViewModel]s, a [ContractViewModel] and an invariant. Contains methods for
 * managing the afferent data and updating the target-[Region].
 */
class RegionViewModel(id: Int, target: Region, val contract: ContractViewModel) :
    BlockViewModelElement<Region>(id, target) {
    val colorProperty: Property<Color>
    val invariantProperty: StringProperty = SimpleStringProperty(target.invariant.condition)
    val statesProperty: ObservableList<StateViewModel> = FXCollections.observableArrayList()

    init {
        val random = Random(System.currentTimeMillis())
        val red = random.nextInt(MAXIMUM_RGB_COLOR_VALUE)
        val green = random.nextInt(MAXIMUM_RGB_COLOR_VALUE)
        val blue = random.nextInt(MAXIMUM_RGB_COLOR_VALUE)
        this.colorProperty = SimpleObjectProperty(Color.rgb(red, green, blue, 0.5))
    }

    @Throws(ModelException::class)
    override fun updateTarget() {
        super.updateTarget()
        target.invariant.condition = (invariantProperty.value)
        target.preAndPostCondition.preCondition!!.condition = (contract.precondition)
        target.preAndPostCondition.postCondition!!.condition = (contract.postcondition)

        target.states.clear()
        target!!.addStates(statesProperty.map { it.target }.toSet())
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

    override fun <S> accept(visitor: PositionableViewModelElementVisitor<S>): S {
        return visitor.visit(this)
    }

    var invariant: String by invariantProperty
    var color: Color by colorProperty

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

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is RegionViewModel) {
            return false
        }
        return id == o.id
    }

    companion object {
        const val MAXIMUM_RGB_COLOR_VALUE = 255
    }
}
