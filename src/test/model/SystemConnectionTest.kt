package org.gecko.model

import org.gecko.exceptions.ModelException
import org.gecko.viewmodel.Visibility
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.ThrowingSupplier

class SystemConnectionTest {
    @Test
    var source: Unit = ()

    init {
        Assertions.assertNotNull(connection!!.source)
        Assertions.assertThrows(ModelException::class.java) { connection!!.source = (destination1!!) }
        Assertions.assertDoesNotThrow(ThrowingSupplier<Variable> { connection!!.source = (source2!!) })
        Assertions.assertNotNull(connection!!.source)
        Assertions.assertDoesNotThrow(ThrowingSupplier<Variable> { connection!!.source = (source1!!) })
    }

    @Test
    var destination: Unit = ()

    init {
        Assertions.assertNotNull(connection!!.destination)
        Assertions.assertDoesNotThrow(ThrowingSupplier<Variable> { connection!!.destination = (destination1!!) })
        Assertions.assertThrows(ModelException::class.java) { connection!!.destination = (source1!!) }
        Assertions.assertDoesNotThrow(ThrowingSupplier<Variable> { connection!!.destination = (destination2!!) })
        Assertions.assertNotNull(connection!!.destination)
        Assertions.assertDoesNotThrow(ThrowingSupplier<Variable> { connection!!.destination = (destination1!!) })

        try {
            SystemConnection(5, source2!!, destination2!!)
        } catch (e: ModelException) {
            Assertions.fail<Any>("Could not initialize connection for testing the behaviour of another connection.")
        }
        Assertions.assertThrows(ModelException::class.java) { connection!!.destination = (destination2!!) }
        Assertions.assertDoesNotThrow(ThrowingSupplier<Variable> { connection!!.destination = (destination1!!) })
    }

    companion object {
        var connection: SystemConnection? = null
        var source1: Variable? = null
        var source2: Variable? = null
        var destination1: Variable? = null
        var destination2: Variable? = null

        @BeforeAll
        fun setUp() {
            Assertions.assertDoesNotThrow(ThrowingSupplier<Variable> {
                source1 = Variable(0, "source1", "type", Visibility.OUTPUT)
            })
            Assertions.assertDoesNotThrow(ThrowingSupplier<Variable> {
                source2 = Variable(1, "source2", "type", Visibility.OUTPUT)
            })
            Assertions.assertDoesNotThrow(ThrowingSupplier<Variable> {
                destination1 = Variable(2, "destination1", "type", Visibility.INPUT)
            })
            Assertions.assertDoesNotThrow(ThrowingSupplier<Variable> {
                destination2 = Variable(3, "destination2", "type", Visibility.INPUT)
            })

            Assertions.assertThrows(NullPointerException::class.java) {
                connection = SystemConnection(4, null, destination1!!)
            }
            Assertions.assertThrows(NullPointerException::class.java) {
                connection = SystemConnection(4, source1!!, null)
            }

            Assertions.assertDoesNotThrow(ThrowingSupplier<SystemConnection> {
                connection = SystemConnection(4, source1!!, destination1!!)
            })
        }
    }
}