package org.gecko.model

import kotlinx.serialization.Serializable
import org.gecko.exceptions.MissingViewModelElementException
import org.gecko.exceptions.ModelException
import org.gecko.viewmodel.Visibility

/**
 * Represents a variable in the domain model of a Gecko project. A [Variable] has a name, a type and a
 * [Visibility].
 */
@Serializable
data class Variable(
    override val id: UInt, override var name: String?,
    var type: String,
    var value: String = "",
    var visibility: Visibility,
    var hasIncomingConnection: Boolean = false
) : Element(), Renamable {
    @Throws(ModelException::class, MissingViewModelElementException::class)
    override fun accept(visitor: ElementVisitor) {
        visitor.visit(this)
    }
}

