package org.gecko.model

import org.gecko.exceptions.ModelException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ConditionTest {
    var condition: Condition = Condition("condition")

    @Test
    fun setUp() {
        Assertions.assertThrows(ModelException::class.java) { condition = Condition("") }
    }

    @Test
    fun setCondition() {
        Assertions.assertThrows(ModelException::class.java) { condition.condition = "" }
        Assertions.assertDoesNotThrow { condition.condition = "newCondition" }
    }

    @Test
    fun and() {
        var other: Condition? = null
        try {
            other = Condition("other")
        } catch (_: ModelException) {
            Assertions.fail<Any>("Support condition could not be initialized.")
        }

        Assertions.assertNotNull(condition.and(other!!))
    }

    @Test
    fun not() {
        Assertions.assertNotNull(condition.not())
    }

    @Test
    fun testToString() {
        Assertions.assertEquals(condition.condition, condition.toString())
    }
}
