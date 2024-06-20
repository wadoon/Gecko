package org.gecko.model

import kotlinx.serialization.Serializable
import org.gecko.exceptions.MissingViewModelElementException
import org.gecko.exceptions.ModelException

/**
 * Represents an edge in the domain model of a Gecko project. An [Edge] is described by a source- and a
 * destination-[State]. It is also associated with one of the start-[State]'s [Contract]s, has a
 * priority and a [Kind], which informs about how the associated [Contract] is handled.
 */
@Serializable
data class Edge(
    override val id: UInt,
    var source: State,
    var destination: State,
    var contract: Contract,
    var kind: Kind,
    var priority: UInt = UInt.MIN_VALUE,
) : Element() {
    @Throws(ModelException::class, MissingViewModelElementException::class)
    override fun accept(visitor: ElementVisitor) {
        visitor.visit(this)
    }
}
