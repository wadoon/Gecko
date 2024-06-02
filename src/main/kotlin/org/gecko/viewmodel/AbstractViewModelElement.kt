package org.gecko.viewmodel

import org.gecko.exceptions.ModelException
import org.gecko.model.*
import java.util.*

/**
 * Represents an abstraction of a view model element of a Gecko project. An [AbstractViewModelElement] has an id
 * and a target-[Element], the data of which it can update.
 */
abstract class AbstractViewModelElement<T : Element>(
    /**
     * The unique identifier of this [AbstractViewModelElement].
     */
    val id: Int,
    /**
     * The target-[Element] in the model of this [AbstractViewModelElement].
     */
    val target: T
) {
    /**
     * Updates the target-[Element] in the model with the data of this [AbstractViewModelElement]. It has to
     * be called after the data of this [AbstractViewModelElement] has been updated to keep the model consistent
     * with the view model.
     *
     * @throws ModelException if the update fails because of a change, which is not allowed in the model
     */
    @Throws(ModelException::class)
    abstract fun updateTarget()

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AbstractViewModelElement<*>

        return id == other.id
    }
}
