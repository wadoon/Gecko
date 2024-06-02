package org.gecko.model

import kotlinx.serialization.Serializable

import org.gecko.exceptions.MissingViewModelElementException
import org.gecko.exceptions.ModelException

/**
 * Represents a contract in the domain model of a Gecko project. A [Contract] has a name and is described by a
 * pre- and a post-[Condition].
 */
@Serializable
data class Contract(
    override val id: UInt,
    override var name: String?,
    var preCondition: Condition,
    var postCondition: Condition
) : Element(), Renamable {
    @Throws(ModelException::class, MissingViewModelElementException::class)
    override fun accept(visitor: ElementVisitor) {
        visitor.visit(this)
    }
}
