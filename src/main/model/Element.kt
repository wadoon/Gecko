package org.gecko.model


import org.gecko.exceptions.MissingViewModelElementException
import org.gecko.exceptions.ModelException

/**
 * Represents an abstraction of an element in the domain model of a Gecko project. An [Element] has an id.
 */

abstract class Element {
    abstract val id: UInt

    @Throws(ModelException::class, MissingViewModelElementException::class)
    abstract fun accept(visitor: ElementVisitor)
}
