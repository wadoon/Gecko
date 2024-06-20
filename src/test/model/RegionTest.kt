package org.gecko.model

import org.gecko.exceptions.ModelException
import org.junit.jupiter.api.*

class RegionTest {
    var regionWithNullConditions: Region? = null
    var condition: Condition = Condition.trueCondition()
    var regionWithValidConditions = Region(2u, "region", condition, Contract(3u, "preAndPost", condition, condition))
    var preAndPostCondition = Contract(0u, "contract", condition, condition)
    var state1 = State(4u, "state1")
    var state2 = State(5u, "state2")

    @Test
    fun setName() {
        Assertions.assertThrows(NullPointerException::class.java) { regionWithValidConditions.name = null }
        Assertions.assertThrows(ModelException::class.java) { regionWithValidConditions.name = "" }
        Assertions.assertDoesNotThrow { regionWithValidConditions.name = "newName" }
    }

    @Test
    fun setInvariant() {
        Assertions.assertNotNull(regionWithValidConditions.invariant)
        Assertions.assertThrows(ModelException::class.java) {
            regionWithValidConditions.invariant = Condition("")
        }
        Assertions.assertDoesNotThrow { regionWithValidConditions.invariant = Condition("false") }
        Assertions.assertNotNull(regionWithValidConditions.invariant)
    }

    @Test
    fun setPreAndPostCondition() {
        Assertions.assertNotNull(regionWithValidConditions.preAndPostCondition)
        Assertions.assertNotNull(regionWithValidConditions.preAndPostCondition.preCondition)
        Assertions.assertNotNull(regionWithValidConditions.preAndPostCondition.postCondition)

        Assertions.assertNotNull(regionWithValidConditions.preAndPostCondition.preCondition)
        Assertions.assertNotNull(regionWithValidConditions.preAndPostCondition.postCondition)
    }

    @Test
    fun manageStates() {
        Assertions.assertTrue(regionWithValidConditions.states.isEmpty())

        regionWithValidConditions.addState(state1)
        Assertions.assertFalse(regionWithValidConditions.states.isEmpty())

        regionWithValidConditions.addState(state2)
        Assertions.assertEquals(2, regionWithValidConditions.states.size)

        Assertions.assertDoesNotThrow {
            regionWithValidConditions.removeState(
                state1
            )
        }
        Assertions.assertEquals(1, regionWithValidConditions.states.size)
        Assertions.assertFalse(regionWithValidConditions.states.contains(state1))

        Assertions.assertDoesNotThrow {
            regionWithValidConditions.removeState(
                state2
            )
        }
        Assertions.assertTrue(regionWithValidConditions.states.isEmpty())

        val states = hashSetOf(state1, state2)
        regionWithValidConditions.addStates(states)
        Assertions.assertEquals(2, regionWithValidConditions.states.size)
        Assertions.assertDoesNotThrow { regionWithValidConditions.removeStates(states) }
        Assertions.assertTrue(regionWithValidConditions.states.isEmpty())
    }

    @Test
    fun testNullParametersInRegion() {
        var region: Region? = null
        try {
            region = Region(
                0u, "region", Condition("true"),
                Contract(1u, "preAndPost", Condition("true"), Condition("true"))
            )
        } catch (e: ModelException) {
            Assertions.fail<Any>("Failed to create region for testing purposes of a its setters.")
        }

        try {
            region!!.name = null
        } catch (e: NullPointerException) {
            Assertions.assertNotNull(region!!.name)
        } catch (e: ModelException) {
            Assertions.fail<Any>(GeckoModelTest.Companion.NULL_PARAMETERS_FAIL)
        }

        Assertions.assertNotNull(region!!.name)
    }
}
