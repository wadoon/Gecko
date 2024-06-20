package org.gecko.model

import org.gecko.exceptions.ModelException
import org.junit.jupiter.api.*

class ContractTest {
    @Test
    fun setName() {
        Assertions.assertThrows(NullPointerException::class.java) { contract.name = null }
        Assertions.assertThrows(ModelException::class.java) { contract.name = "" }
        Assertions.assertDoesNotThrow { contract.name = "newName" }
        Assertions.assertEquals("newName", contract.name)
    }

    @Test
    fun setPreAndPostConditions() {
        Assertions.assertThrows(NullPointerException::class.java) { contract.name = null }
        Assertions.assertThrows(ModelException::class.java) { contract.name = "" }
        Assertions.assertDoesNotThrow { contract.name = "newName" }
        Assertions.assertEquals("newName", contract.name)
    }

    @Test
    fun testToString() {
        Assertions.assertEquals(contract.name, contract.toString())
    }

    @Test
    fun testNullParametersInContract() {
        var contract: Contract? = null
        try {
            contract = Contract(0u, "contract", Condition("true"), Condition("true"))
        } catch (e: ModelException) {
            Assertions.fail<Any>("Failed to create conditions for testing purposes of a contract's setters.")
        }

        try {
            contract!!.name = null
        } catch (e: NullPointerException) {
            Assertions.assertNotNull(contract!!.name)
        } catch (e: ModelException) {
            Assertions.fail<Any>(GeckoModelTest.Companion.NULL_PARAMETERS_FAIL)
        }

        Assertions.assertNotNull(contract!!.name)
        Assertions.assertNotNull(contract.preCondition)
        Assertions.assertNotNull(contract.postCondition)
    }

    private var contract = Contract(
        0u, "contract",
        preCondition = Condition("pre"),
        postCondition = Condition("post")
    )
}
