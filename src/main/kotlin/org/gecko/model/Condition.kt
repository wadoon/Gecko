package org.gecko.model

import kotlinx.serialization.Serializable

/**
 * Represents a condition in the domain model of a Gecko project.
 */
@Serializable
data class Condition(var condition: String) {
    fun and(other: Condition): Condition {
        val newCondition = if (other.condition == TRUE_CONDITION) {
            condition
        } else if (condition == TRUE_CONDITION) {
            other.condition
        } else {
            String.format(AND_CONDITIONS, condition, other.condition)
        }
        // This and other are always valid
        return Condition(newCondition)
    }

    fun not() = Condition(String.format(NOT_CONDITION, condition))

    override fun toString() = condition

    companion object {
        const val AND_CONDITIONS = "(%s) & (%s)"
        const val NOT_CONDITION = "! (%s)"
        const val TRUE_CONDITION = "true"
        fun trueCondition(): Condition = Condition(TRUE_CONDITION)
    }
}
