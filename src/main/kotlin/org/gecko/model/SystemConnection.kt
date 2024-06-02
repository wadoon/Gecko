package org.gecko.model

import kotlinx.serialization.Serializable

import org.gecko.exceptions.MissingViewModelElementException
import org.gecko.exceptions.ModelException

/**
 * Represents a system connection in the domain model of a Gecko project. A [SystemConnection] is described by a
 * source- and a destination-[Variable].
 */
@Serializable
data class SystemConnection(
    override val id: UInt,
    var source: Variable,
    var destination: Variable,
) : Element() {
    fun validate() {
        if (source == destination) {
            throw ModelException("The source and the destination of a connection cannot be the same.")
        }

        if (this.destination != null && (this.destination == destination)) {
            return
        }
        if (this.destination != null && destination.hasIncomingConnection) {
            throw ModelException("The destination already has an incoming connection.")
        }
        if (destination == source) {
            throw ModelException("The source and the destination of a connection cannot be the same.")
        }

        if (this.destination != null) {
            this.destination!!.hasIncomingConnection = false
        }
        this.destination = destination
        this.destination!!.hasIncomingConnection = true
    }

    @Throws(ModelException::class, MissingViewModelElementException::class)
    override fun accept(visitor: ElementVisitor) {
        visitor.visit(this)
    }
}
