package org.gecko.model

import org.gecko.exceptions.MissingViewModelElementException
import org.gecko.exceptions.ModelException

/**
 * Represents a visitor pattern for performing operations on [Element]s. Concrete visitors must implement this
 * interface to define specific behavior for each [Element].
 */
interface ElementVisitor {
    @Throws(ModelException::class)
    fun visit(state: State)

    fun visit(contract: Contract?)

    @Throws(ModelException::class, MissingViewModelElementException::class)
    fun visit(systemConnection: SystemConnection)

    @Throws(ModelException::class)
    fun visit(variable: Variable)

    @Throws(ModelException::class)
    fun visit(system: System)

    @Throws(ModelException::class, MissingViewModelElementException::class)
    fun visit(region: Region)

    @Throws(ModelException::class, MissingViewModelElementException::class)
    fun visit(edge: Edge)
}
