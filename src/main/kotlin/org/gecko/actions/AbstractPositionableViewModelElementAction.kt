package org.gecko.actions

import org.gecko.viewmodel.PositionableElement

/**
 * An abstract representation of an [Action] that has a target-[PositionableElement].
 */
abstract class AbstractPositionableViewModelElementAction : Action() {
    abstract val target: PositionableElement

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is AbstractPositionableViewModelElementAction) {
            return false
        }
        return target == o.target
    }

    override fun hashCode(): Int {
        return target.hashCode()
    }
}
