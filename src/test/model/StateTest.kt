package org.gecko.model

import org.gecko.exceptions.ModelException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class StateTest {
    var state: State = State(0u, "state")
    var contract1 = Contract(1u, "contract1", Condition("true"), Condition("true"))
    var contract2 = Contract(2u, "contract2", Condition("false"), Condition("false"))

    @Test
    fun setUp() {
        Assertions.assertThrows(NullPointerException::class.java) { State(0u, null) }
        Assertions.assertThrows(ModelException::class.java) { State(0u, "") }
    }

    @Test
    fun setName() {
        Assertions.assertNotNull(state.name)

        Assertions.assertThrows(NullPointerException::class.java) { state.name = null }
        Assertions.assertThrows(ModelException::class.java) { state.name = "" }
        Assertions.assertDoesNotThrow { state.name = "newName" }

        Assertions.assertNotNull(state.name)
        Assertions.assertEquals("newName", state.name)
    }

    @Test
    fun manageContracts() {
        Assertions.assertTrue(state.contracts.isEmpty())

        state.addContract(contract1)
        Assertions.assertFalse(state.contracts.isEmpty())

        state.addContract(contract2)
        Assertions.assertEquals(2, state.contracts.size)

        Assertions.assertDoesNotThrow {
            state.removeContract(
                contract1
            )
        }
        Assertions.assertEquals(1, state.contracts.size)
        Assertions.assertFalse(state.contracts.contains(contract1))

        Assertions.assertDoesNotThrow {
            state.removeContract(
                contract2
            )
        }
        Assertions.assertTrue(state.contracts.isEmpty())

        val contracts = hashSetOf(contract1, contract2)
        state.addContracts(contracts)
        Assertions.assertEquals(2, state.contracts.size)
        Assertions.assertDoesNotThrow { state.removeContracts(contracts) }
        Assertions.assertTrue(state.contracts.isEmpty())
    }
}
