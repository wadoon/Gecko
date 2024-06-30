package org.gecko.model

import org.gecko.exceptions.ModelException
import org.gecko.viewmodel.Visibility
import org.junit.jupiter.api.*

class SystemTest {
    @Test
    fun testInitializedSystem() {
        Assertions.assertNotNull(system.name)
        Assertions.assertTrue(system.code == null || !system.code.isEmpty())
        Assertions.assertNotNull(system.automaton)

        Assertions.assertNotNull(system.children)
        Assertions.assertNotNull(system.connections)
        Assertions.assertNotNull(system.variables)

        Assertions.assertTrue(system.children.isEmpty())
        Assertions.assertTrue(system.connections.isEmpty())
        Assertions.assertTrue(system.variables.isEmpty())
    }

    @Test
    fun setName() {
        Assertions.assertNotNull(system.name)

        Assertions.assertThrows(NullPointerException::class.java) { system.name = null }
        Assertions.assertThrows(ModelException::class.java) { system.name = "" }
        Assertions.assertDoesNotThrow { system.name = "newName" }

        Assertions.assertNotNull(system.name)
        Assertions.assertEquals("newName", system.name)
    }

    @Test
    fun setCode() {
        Assertions.assertNull(system.code)

        Assertions.assertThrows(ModelException::class.java) { system.code = "" }
        Assertions.assertDoesNotThrow { system.code = "newCode" }

        Assertions.assertNotNull(system.code)
        Assertions.assertEquals("newCode", system.code)
    }

    @Test
    fun manageChildren() {
        Assertions.assertTrue(system.children.isEmpty())

        system.addChild(child1)
        Assertions.assertFalse(system.children.isEmpty())

        system.addChild(child2)
        Assertions.assertEquals(2, system.children.size)

        Assertions.assertDoesNotThrow {
            system.removeChild(
                child1
            )
        }
        Assertions.assertEquals(1, system.children.size)
        Assertions.assertFalse(system.children.contains(child1))

        Assertions.assertDoesNotThrow {
            system.removeChild(
                child2
            )
        }
        Assertions.assertTrue(system.children.isEmpty())

        val children = hashSetOf(child1, child2)
        system.addChildren(children)

        Assertions.assertEquals(2, system.children.size)
        Assertions.assertDoesNotThrow { system.removeChildren(children) }
        Assertions.assertTrue(system.children.isEmpty())
    }

    @Test
    fun childByName() {
        Assertions.assertNull(system.getChildByName("child1"))

        system.addChild(child1)
        system.addChild(child2)

        Assertions.assertEquals(child1, system.getChildByName("child1"))
        Assertions.assertEquals(child2, system.getChildByName("child2"))

        system.removeChild(child1)
        system.removeChild(child2)
    }

    @Test
    fun allChildren() {
        Assertions.assertTrue(system.allChildren.isEmpty())
        val children = hashSetOf(child1, child2)
        system.addChildren(children)
        Assertions.assertEquals(children.toList(), system.allChildren)
        system.removeChild(child2)
        Assertions.assertNotEquals(children.toList(), system.allChildren)
        system.addChild(child1)
        Assertions.assertEquals(1, system.allChildren.size)
    }

    @Test
    fun childSystemWithVariable() {
        child2.addVariable(childVariable1)
        system.addChild(child1)
        system.addChild(child2)

        Assertions.assertEquals(child2, system.getChildSystemWithVariable(childVariable1))
        Assertions.assertDoesNotThrow {
            system.removeChild(
                child2
            )
        }
        Assertions.assertNull(system.getChildSystemWithVariable(childVariable1))
        Assertions.assertDoesNotThrow {
            system.removeChild(
                child1
            )
        }
    }

    @Test
    fun manageConnections() {
        Assertions.assertTrue(system.connections.isEmpty())

        system.addConnection(connection1)
        Assertions.assertFalse(system.connections.isEmpty())

        system.addConnection(connection2)
        Assertions.assertEquals(2, system.connections.size)

        system.removeConnection(connection1)
        Assertions.assertEquals(1, system.connections.size)
        Assertions.assertFalse(system.connections.contains(connection1))

        system.removeConnection(connection2)
        Assertions.assertTrue(system.connections.isEmpty())

        val connections: MutableSet<SystemConnection> = HashSet()
        connections.add(connection1)
        connections.add(connection2)

        system.addConnections(connections)
        Assertions.assertEquals(2, system.connections.size)
        Assertions.assertDoesNotThrow { system.removeConnections(connections) }
        Assertions.assertTrue(system.connections.isEmpty())
    }

    @Test
    fun manageVariables() {
        Assertions.assertTrue(system.variables.isEmpty())

        system.addVariable(variable1)
        Assertions.assertFalse(system.variables.isEmpty())

        system.addVariable(variable2)
        Assertions.assertEquals(2, system.variables.size)

        Assertions.assertDoesNotThrow {
            system.removeVariable(
                variable1
            )
        }
        Assertions.assertEquals(1, system.variables.size)
        Assertions.assertFalse(system.variables.contains(variable1))

        Assertions.assertDoesNotThrow {
            system.removeVariable(
                variable2
            )
        }
        Assertions.assertTrue(system.variables.isEmpty())

        val variables: MutableSet<Variable> = HashSet()
        variables.add(variable1)
        variables.add(variable2)

        system.addVariables(variables)
        Assertions.assertEquals(2, system.variables.size)
        Assertions.assertDoesNotThrow { system.removeVariables(variables) }
        Assertions.assertTrue(system.variables.isEmpty())
    }

    @Test
    fun variableByName() {
        Assertions.assertNull(system.getVariableByName("var1"))

        system.addVariable(variable1)
        system.addVariable(variable2)

        Assertions.assertEquals(variable1, system.getVariableByName("var1"))
        Assertions.assertEquals(variable2, system.getVariableByName("var2"))

        system.removeVariable(variable1)
        system.removeVariable(variable2)
    }

    @Test
    fun allElements() {
        Assertions.assertTrue(system.allElements.isEmpty())

        system.addChild(child1)
        system.addVariable(variable1)
        system.addConnection(connection1)
        Assertions.assertEquals(3, system.allElements.size)

        system.addVariable(variable1)
        Assertions.assertEquals(3, system.allElements.size)

        system.removeChild(child1)
        system.removeVariable(variable1)
        system.removeConnection(connection1)
    }

    @Test
    fun testNullParametersInSystem() {
        val system = System(0u, "system", "", Automaton())

        try {
            system.name = null
        } catch (e: NullPointerException) {
            Assertions.assertNotNull(system!!.name)
        } catch (e: ModelException) {
            Assertions.fail<Any>(GeckoModelTest.Companion.NULL_PARAMETERS_FAIL)
        }

        Assertions.assertNotNull(system.name)
        Assertions.assertNotNull(system.automaton)
    }

    val system = System(0u, "system", "", Automaton())
    val child1 = System(1u, "child1", "", Automaton())
    val child2 = System(2u, "child2", "", Automaton())
    val variable1 = Variable(3u, "var1", "type", visibility = Visibility.INPUT)
    val variable2 = Variable(4u, "var2", "type", visibility = Visibility.OUTPUT)

    val childVariable1 = Variable(5u, "childVar1", "type", visibility = Visibility.INPUT)
    val childVariable2 = Variable(6u, "childVar2", "type", visibility = Visibility.OUTPUT)

    val connection1 = SystemConnection(7u, childVariable2, childVariable1)
    val connection2 = SystemConnection(8u, childVariable2, childVariable1)

    @Test
    fun setUp() {
        Assertions.assertThrows(ModelException::class.java) { System(0u, "", "", Automaton()) }
        Assertions.assertThrows(ModelException::class.java) { System(0u, "system", "", Automaton()) }
    }
}