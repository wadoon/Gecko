package org.gecko.model

import kotlinx.serialization.Serializable
import org.gecko.exceptions.MissingViewModelElementException
import org.gecko.exceptions.ModelException

/**
 * Represents a region in the domain model of a Gecko project. A [Region] has a name and is described by a set of
 * [State]s, a [Contract] and an invariant-[Condition]. Contains methods for managing the afferent
 * data.
 */
@Serializable
data class Region(
    override val id: UInt,
    override var name: String?,
    var invariant: Condition,
    val preAndPostCondition: Contract,
    val states: MutableSet<State> = hashSetOf()
) : Element(), Renamable {
    fun validate() {
        if (name!!.isEmpty()) {
            throw ModelException("Region's name is invalid.")
        }
    }

    @Throws(ModelException::class, MissingViewModelElementException::class)
    override fun accept(visitor: ElementVisitor) {
        visitor.visit(this)
    }

    fun addState(state: State) {
        states.add(state)
    }

    fun addStates(states: Set<State>) {
        for (state in states) {
            addState(state)
        }
    }

    fun removeState(state: State) {
        states.remove(state)
    }

    fun removeStates(states: Set<State>) {
        for (state in states) {
            removeState(state)
        }
    }
}
