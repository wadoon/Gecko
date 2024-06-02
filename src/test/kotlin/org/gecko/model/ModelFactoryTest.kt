package org.gecko.model

import org.gecko.exceptions.ModelException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.ThrowingSupplier

class ModelFactoryTest {
    var model: GeckoModel? = null
    var factory: ModelFactory? = null

    @BeforeEach
    fun setUp() {
        try {
            model = GeckoModel()
        } catch (e: ModelException) {
            Assertions.fail<Any>("Could not initialize model for testing purposes of the model factory.")
        }

        factory = ModelFactory(model!!)
    }

    @Test
    fun createState() {
        Assertions.assertThrows(NullPointerException::class.java) { factory!!.createState(null) }
        Assertions.assertDoesNotThrow<State> { factory!!.createState(Automaton()) }
    }

    @Test
    fun createEdge() {
        Assertions.assertThrows(
            NullPointerException::class.java
        ) { factory!!.createEdge(null, State(0, "source"), State(1, "destination")) }
        Assertions.assertThrows(
            NullPointerException::class.java
        ) { factory!!.createEdge(Automaton(), null, State(1, "destination")) }
        Assertions.assertThrows(
            NullPointerException::class.java
        ) { factory!!.createEdge(Automaton(), State(0, "source"), null) }

        val automaton = Automaton()
        var source: State? = null
        var destination: State? = null
        try {
            source = State(0, "source")
            destination = State(1, "destination")
        } catch (e: ModelException) {
            Assertions.fail<Any>("Failed to create source and destination states for testing purposes of the creation of an edge.")
        }
        Assertions.assertNotNull(source)
        Assertions.assertNotNull(destination)
        automaton.addState(source!!)
        automaton.addState(destination!!)

        try {
            factory!!.createEdge(automaton, source, destination)
        } catch (e: ModelException) {
            Assertions.fail<Any>("Failed to create edge from valid source and destination states.")
        }
    }

    @Test
    fun createSystem() {
        Assertions.assertThrows(NullPointerException::class.java) { factory!!.createSystem(null) }
        Assertions.assertDoesNotThrow<System> { factory!!.createSystem(System(0, "parent", null, Automaton())) }
    }

    @Test
    fun createVariable() {
        Assertions.assertThrows(NullPointerException::class.java) { factory!!.createVariable(null) }
        Assertions.assertDoesNotThrow<Variable> { factory!!.createVariable(System(0, "system", null, Automaton())) }
    }

    @Test
    fun createSystemConnection() {
        var system: System? = null
        val child1: System
        val child2: System
        var variable1: Variable? = null
        var variable2: Variable? = null
        try {
            system = System(0, "system", null, Automaton())
            child1 = System(1, "child1", null, Automaton())
            child2 = System(2, "child2", null, Automaton())
            system.addChild(child1)
            child1.parent = system
            system.addChild(child2)
            child2.parent = system

            variable1 = Variable(3, "var1", "type", Visibility.OUTPUT)
            variable2 = Variable(4, "var2", "type", Visibility.INPUT)

            child1.addVariable(variable1)
            child2.addVariable(variable2)
        } catch (e: ModelException) {
            Assertions.fail<Any>("Failed to create elements for testing purposes of a system connection.")
        }

        Assertions.assertNotNull(system)
        model!!.root.addChild(system!!)
        system.parent = model!!.root

        var systemConnection: SystemConnection? = null
        try {
            systemConnection = factory!!.createSystemConnection(null, variable1!!, variable2!!)
        } catch (e: NullPointerException) {
            Assertions.assertNull(systemConnection)
        } catch (e: ModelException) {
            Assertions.fail<Any>("NullPointerException should be thrown before the model intervenes.")
        }

        try {
            systemConnection = factory!!.createSystemConnection(system, null, variable2!!)
        } catch (e: NullPointerException) {
            Assertions.assertNull(systemConnection)
        } catch (e: ModelException) {
            Assertions.fail<Any>("NullPointerException should be thrown before the model intervenes.")
        }

        try {
            systemConnection = factory!!.createSystemConnection(system, variable1!!, null)
        } catch (e: NullPointerException) {
            Assertions.assertNull(systemConnection)
        } catch (e: ModelException) {
            Assertions.fail<Any>("NullPointerException should be thrown before the model intervenes.")
        }

        Assertions.assertNull(systemConnection)
        try {
            factory!!.createSystemConnection(system, variable1!!, variable2!!)
        } catch (e: ModelException) {
            Assertions.fail<Any>("System connection creation failed.")
        }
    }

    @Test
    fun createContract() {
        Assertions.assertThrows(NullPointerException::class.java) { factory!!.createContract(null) }
        Assertions.assertDoesNotThrow<Contract> { factory!!.createContract(State(0, "state")) }
    }

    @Test
    fun createRegion() {
        Assertions.assertThrows(NullPointerException::class.java) { factory!!.createRegion(null) }
        Assertions.assertDoesNotThrow<Region> { factory!!.createRegion(Automaton()) }
    }

    @Test
    fun createCondition() {
        Assertions.assertThrows(NullPointerException::class.java) { factory!!.createCondition(null) }
        Assertions.assertDoesNotThrow<Condition> { factory!!.createCondition("true") }
    }

    @get:Test
    val defaultName: Unit
        get() {
            val children = arrayOfNulls<System>(2)

            try {
                children[0] = System(1, "Element_1", null, Automaton())
            } catch (e: ModelException) {
                Assertions.fail<Any>("Could not initialize system for testing purposes of getting the right default name.")
            }
            model!!.root.addChild(children[0]!!)

            Assertions.assertDoesNotThrow(ThrowingSupplier<System> {
                children[1] = factory!!.createSystem(
                    model!!.root
                )
            })
            Assertions.assertNotEquals("Element_1", children[1]!!.name)
            Assertions.assertEquals("Element_2", children[1]!!.name)
            Assertions.assertTrue(model!!.root.children.contains(children[1]))
        }

    @Test
    fun createEdgeBetweenInvalidStates() {
        val automaton = Automaton()
        var source: State? = null
        var destination: State? = null
        var other: State? = null
        var edge: Edge? = null
        try {
            source = State(0, "source")
            destination = State(1, "destination")
            other = State(2, "other")
        } catch (e: ModelException) {
            Assertions.fail<Any>("Failed to create source and destination states for testing purposes of the creation of an edge.")
        }
        Assertions.assertNotNull(source)
        Assertions.assertNotNull(destination)
        Assertions.assertNotNull(other)

        try { // Empty automaton:
            edge = factory!!.createEdge(automaton, source!!, destination!!)
        } catch (e: RuntimeException) {
            Assertions.assertNull(edge)
        } catch (e: ModelException) {
            Assertions.fail<Any>("RuntimeException should be thrown before the model intervenes.")
        }

        automaton.addState(other!!)
        try { // Source and destination not in automaton:
            edge = factory!!.createEdge(automaton, source!!, destination!!)
        } catch (e: RuntimeException) {
            Assertions.assertNull(edge)
        } catch (e: ModelException) {
            Assertions.fail<Any>("RuntimeException should be thrown before the model intervenes.")
        }

        automaton.addState(source!!)
        try { // Destination not in automaton:
            edge = factory!!.createEdge(automaton, source, destination!!)
        } catch (e: RuntimeException) {
            Assertions.assertNull(edge)
        } catch (e: ModelException) {
            Assertions.fail<Any>("RuntimeException should be thrown before the model intervenes.")
        }

        automaton.addState(destination!!)
        try {
            edge = factory!!.createEdge(automaton, source, destination)
        } catch (e: ModelException) {
            Assertions.fail<Any>("Failed to create edge.")
        } catch (e: RuntimeException) {
            Assertions.fail<Any>("Failed to connect valid states with a new edge.")
        }

        automaton.addState(source)
        automaton.addState(destination)
    }

    @Test
    fun createSystemConnectionBetweenInvalidStates() {
        var parent: System? = null
        var child1: System? = null
        var child2: System? = null
        var var1: Variable? = null
        var var2: Variable? = null
        var connection: SystemConnection? = null
        try {
            parent = factory!!.createSystem(model!!.root)
            child1 = System(1, "child1", null, Automaton())
            child2 = System(2, "child2", null, Automaton())
            var1 = Variable(3, "var1", "int", Visibility.INPUT)
            var2 = Variable(4, "var2", "int", Visibility.INPUT)
        } catch (e: ModelException) {
            Assertions.fail<Any>("Failed to create source and destination states for testing purposes of the creation of an edge.")
        }
        Assertions.assertNotNull(parent)
        Assertions.assertNotNull(child1)
        Assertions.assertNotNull(child2)
        Assertions.assertNotNull(var1)
        Assertions.assertNotNull(var2)

        try { // Variables with no parents are not part of the model:
            connection = factory!!.createSystemConnection(parent!!, var1!!, var2!!)
        } catch (e: RuntimeException) {
            Assertions.assertNull(connection)
        } catch (e: ModelException) {
            Assertions.fail<Any>("RuntimeException should be thrown before the model intervenes.")
        }
        Assertions.assertNull(connection)

        parent!!.addVariable(var1!!)
        try { // Variable 2 with no parent is not part of the model:
            connection = factory!!.createSystemConnection(parent, var1, var2!!)
        } catch (e: RuntimeException) {
            Assertions.assertNull(connection)
        } catch (e: ModelException) {
            Assertions.fail<Any>("RuntimeException should be thrown before the model intervenes.")
        }
        Assertions.assertNull(connection)

        parent.addVariable(var2!!)
        try { // Variables have the same parent:
            connection = factory!!.createSystemConnection(parent, var1, var2)
        } catch (e: ModelException) {
            Assertions.assertNull(connection)
        }
        Assertions.assertNull(connection)

        parent.removeVariable(var2)
        child1!!.addVariable(var2)
        try { // Valid variables, but child1 is not child of parent:
            connection = factory!!.createSystemConnection(parent, var1, var2)
        } catch (e: RuntimeException) {
            Assertions.assertNull(connection)
        } catch (e: ModelException) {
            Assertions.fail<Any>("RuntimeException should be thrown before the model intervenes.")
        }
        Assertions.assertNull(connection)

        parent.addChild(child1)
        child1.parent = parent
        var2.visibility = (Visibility.OUTPUT)
        try { // Variable 1 in parent, variable 2 in child, but wrong visibilities:
            connection = factory!!.createSystemConnection(parent, var1, var2)
        } catch (e: ModelException) {
            Assertions.assertNull(connection)
        }
        Assertions.assertNull(connection)

        var1.visibility = (Visibility.OUTPUT)
        try { // Variable 1 in parent, variable 2 in child, but wrong visibilities:
            connection = factory!!.createSystemConnection(parent, var1, var2)
        } catch (e: ModelException) {
            Assertions.assertNull(connection)
        }
        Assertions.assertNull(connection)

        parent.removeVariable(var1)
        child2!!.addVariable(var1)
        try { // Variables in children, but child2 not child of parent:
            connection = factory!!.createSystemConnection(parent, var2, var1)
        } catch (e: RuntimeException) {
            Assertions.assertNull(connection)
        } catch (e: ModelException) {
            Assertions.fail<Any>("RuntimeException should be thrown before the model intervenes.")
        }
        Assertions.assertNull(connection)

        parent.addChild(child2)
        child2.parent = parent
        try { // Variables in children, but wrong visibilities:
            connection = factory!!.createSystemConnection(parent, var2, var1)
        } catch (e: ModelException) {
            Assertions.assertNull(connection)
        }
        Assertions.assertNull(connection)

        var2.visibility = (Visibility.INPUT)
        try {
            connection = factory!!.createSystemConnection(parent, var1, var2)
        } catch (e: ModelException) {
            Assertions.fail<Any>("Failed to create system connection between valid variables.")
        }
    }
}
