package org.gecko.model

import org.gecko.exceptions.ModelException
import org.junit.jupiter.api.*

class VariableTest {
    @Test
    fun setName() {
        Assertions.assertThrows(NullPointerException::class.java) { variable!!.name = null }
        Assertions.assertThrows(ModelException::class.java) { variable!!.name = "" }
        Assertions.assertDoesNotThrow { variable!!.name = "newName" }
        Assertions.assertEquals("newName", variable!!.name)
    }

    @Test
    fun type() {
        Assertions.assertThrows(ModelException::class.java) { variable!!.type = "" }
        Assertions.assertDoesNotThrow { variable!!.type = "long" }
        Assertions.assertEquals("long", variable!!.type)
    }

    @Test
    fun setValue() {
        Assertions.assertThrows(ModelException::class.java) { variable!!.value = "" }
        Assertions.assertDoesNotThrow { variable!!.value = null }
        Assertions.assertNull(variable!!.value)
        Assertions.assertDoesNotThrow { variable!!.value = "value" }
        Assertions.assertEquals("value", variable!!.value)
    }

    @Test
    fun builtinTypes() {
        Assertions.assertEquals(15, builtinTypes.size)
    }

    @Test
    fun testNullParametersInVariable() {
        var variable: Variable? = null
        try {
            variable = Variable(0u, "variable", "bool", visibility = Visibility.INPUT)
        } catch (e: ModelException) {
            Assertions.fail<Any>("Failed to create variable for testing purposes of a its setters.")
        }

        try {
            variable!!.name = null
        } catch (e: NullPointerException) {
            Assertions.assertNotNull(variable!!.name)
        } catch (e: ModelException) {
            Assertions.fail<Any>(GeckoModelTest.Companion.NULL_PARAMETERS_FAIL)
        }

        Assertions.assertNotNull(variable!!.name)
        Assertions.assertNotNull(variable.type)
        Assertions.assertNotNull(variable.visibility)
    }

    var variable = Variable(0, "variable", "int", visibility = Visibility.INPUT)

    fun setUp() {
        Assertions.assertThrows(ModelException::class.java) {
            variable = Variable(0u, "", null, visibility = Visibility.INPUT)
        }

        Assertions.assertThrows(ModelException::class.java) {
            variable = Variable(0u, "variable", "", visibility = Visibility.INPUT)
        }
    }
}