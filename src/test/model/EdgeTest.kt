package org.gecko.model

import org.gecko.exceptions.ModelException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class EdgeTest {
    @Test
    fun testInitializedEdge() {
        Assertions.assertNotNull(edge)
        Assertions.assertNotNull(edge!!.source)
        Assertions.assertNotNull(edge!!.destination)
        Assertions.assertNotNull(edge!!.contract)
        Assertions.assertNotNull(edge!!.kind)
    }

    @Test
    fun setPriority() {
        Assertions.assertThrows(ModelException::class.java) { edge!!.priority = (-1).toUInt() }
        Assertions.assertDoesNotThrow { edge!!.priority = 3u }
    }

    @Test
    fun setContractToNull() {
        edge!!.contract = null
        Assertions.assertNull(edge!!.contract)
    }

    @Test
    fun testNullParametersInEdge() {
        var edge: Edge? = null
        try {
            edge = Edge(
                0, State(1, "source"), State(2, "destination"),
                Contract(3, "contract", Condition("true"), Condition("true")), Kind.HIT, 0
            )
        } catch (e: ModelException) {
            Assertions.fail<Any>("Failed to create edge for testing purposes of its setters.")
        }

        try {
            edge!!.source = (null)
        } catch (e: NullPointerException) {
            Assertions.assertNotNull(edge!!.source)
        }

        try {
            edge!!.destination = (null)
        } catch (e: NullPointerException) {
            Assertions.assertNotNull(edge!!.destination)
        }

        try {
            edge!!.kind = null
        } catch (e: NullPointerException) {
            Assertions.assertNotNull(edge!!.kind)
        }

        Assertions.assertNotNull(edge!!.source)
        Assertions.assertNotNull(edge.destination)
        Assertions.assertNotNull(edge.kind)
    }

    companion object {
        var edge: Edge? = null
        var source: State? = null
        var destination: State? = null
        var condition: Condition? = null
        var contract: Contract? = null

        @BeforeAll
        fun setUp() {
            try {
                source = State(0, "source")
                destination = State(1, "destination")
                condition = Condition("true")
                contract = Contract(2, "contract", condition!!, condition!!)
            } catch (e: ModelException) {
                Assertions.fail<Any>("States or contract for testing purposes of the edge could not be initialized.")
            }

            Assertions.assertThrows(ModelException::class.java) {
                edge = Edge(2, source!!, destination!!, contract!!, Kind.HIT, -5)
            }

            try {
                edge = Edge(2, source!!, destination!!, contract!!, Kind.HIT, 0)
            } catch (e: ModelException) {
                Assertions.fail<Any>("Edge could not be initialized.")
            }
        }
    }
}
