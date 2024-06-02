package org.gecko.model

import org.gecko.exceptions.ModelException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class AutomatonTest {
    @Test
    fun testInitializedDefaultAutomaton() {
        Assertions.assertNull(defaultAutomaton!!.startState)
        Assertions.assertNotNull(defaultAutomaton!!.regions)
        Assertions.assertNotNull(defaultAutomaton!!.states)
        Assertions.assertNotNull(defaultAutomaton!!.edges)
    }

    @Test
    fun testInitializedAutomatonWithStartState() {
        Assertions.assertNotNull(automatonWithStartState!!.startState)
        Assertions.assertNotNull(automatonWithStartState!!.regions)
        Assertions.assertNotNull(automatonWithStartState!!.states)
        Assertions.assertNotNull(automatonWithStartState!!.edges)
    }

    @Test
    fun testManagingRegions() {
        Assertions.assertTrue(automatonWithStartState!!.regions.isEmpty())

        automatonWithStartState!!.addRegion(region1!!)
        Assertions.assertFalse(automatonWithStartState!!.regions.isEmpty())

        automatonWithStartState!!.addRegion(region2!!)
        Assertions.assertEquals(2, automatonWithStartState!!.regions.size)

        automatonWithStartState!!.removeRegion(region1!!)
        Assertions.assertEquals(1, automatonWithStartState!!.regions.size)
        Assertions.assertFalse(automatonWithStartState!!.regions.contains(region1))

        automatonWithStartState!!.removeRegion(region2!!)
        Assertions.assertTrue(automatonWithStartState!!.regions.isEmpty())

        val regions: MutableSet<Region?> = HashSet()
        regions.add(region1)
        regions.add(region2)

        automatonWithStartState!!.addRegions(regions)
        Assertions.assertEquals(2, automatonWithStartState!!.regions.size)
        automatonWithStartState!!.removeRegions(regions)
        Assertions.assertTrue(automatonWithStartState!!.regions.isEmpty())
    }

    @Test
    fun regionsWithState() {
        region2!!.addState(ordinaryState!!)
        region2!!.addState(startState!!)

        defaultAutomaton!!.addRegion(region1!!)
        defaultAutomaton!!.addRegion(region2!!)

        Assertions.assertEquals(1, defaultAutomaton!!.getRegionsWithState(startState).size)
        Assertions.assertEquals(region2, defaultAutomaton!!.getRegionsWithState(startState).first())

        region2!!.removeState(startState!!)
        Assertions.assertTrue(defaultAutomaton!!.getRegionsWithState(startState).isEmpty())

        region2!!.removeState(ordinaryState!!)
        defaultAutomaton!!.removeRegion(region1!!)
        defaultAutomaton!!.removeRegion(region2!!)
    }

    @Test
    fun testManagingStartState() {
        Assertions.assertThrows(ModelException::class.java) { defaultAutomaton!!.startState = startState }

        defaultAutomaton!!.addState(startState!!)
        Assertions.assertDoesNotThrow { defaultAutomaton!!.startState = startState }
        Assertions.assertNotNull(defaultAutomaton!!.startState)

        defaultAutomaton!!.addState(ordinaryState!!)
        Assertions.assertThrows(ModelException::class.java) {
            defaultAutomaton!!.removeState(
                startState!!
            )
        }

        Assertions.assertDoesNotThrow {
            defaultAutomaton!!.removeState(
                ordinaryState!!
            )
        }
        Assertions.assertDoesNotThrow {
            defaultAutomaton!!.removeState(
                startState!!
            )
        }
        Assertions.assertNull(defaultAutomaton!!.startState)
    }

    @Test
    fun testManagingStates() {
        Assertions.assertDoesNotThrow {
            automatonWithStartState!!.removeState(
                startState!!
            )
        }
        automatonWithStartState!!.addState(startState!!)
        Assertions.assertDoesNotThrow { automatonWithStartState!!.startState = startState }

        Assertions.assertTrue(defaultAutomaton!!.states.isEmpty())

        defaultAutomaton!!.addState(startState!!)
        Assertions.assertFalse(defaultAutomaton!!.states.isEmpty())

        defaultAutomaton!!.addState(ordinaryState!!)
        Assertions.assertEquals(2, defaultAutomaton!!.states.size)

        Assertions.assertDoesNotThrow {
            defaultAutomaton!!.removeState(
                startState!!
            )
        }
        Assertions.assertEquals(1, defaultAutomaton!!.states.size)
        Assertions.assertFalse(defaultAutomaton!!.states.contains(startState))
        Assertions.assertDoesNotThrow {
            defaultAutomaton!!.removeState(
                startState!!
            )
        }

        Assertions.assertDoesNotThrow {
            defaultAutomaton!!.removeState(
                ordinaryState!!
            )
        }
        Assertions.assertTrue(defaultAutomaton!!.states.isEmpty())

        val states: MutableSet<State> = HashSet()
        states.add(startState)
        states.add(ordinaryState)

        defaultAutomaton!!.addStates(states)
        Assertions.assertEquals(2, defaultAutomaton!!.states.size)
        Assertions.assertDoesNotThrow { defaultAutomaton!!.removeStates(states) }
        Assertions.assertTrue(defaultAutomaton!!.states.isEmpty())

        Assertions.assertThrows(NullPointerException::class.java) { defaultAutomaton!!.addState(null) }
        Assertions.assertThrows(NullPointerException::class.java) { defaultAutomaton!!.addStates(null) }
        Assertions.assertThrows(NullPointerException::class.java) { defaultAutomaton!!.removeState(null) }
        Assertions.assertThrows(NullPointerException::class.java) { defaultAutomaton!!.removeStates(null) }
    }

    @get:Test
    val stateWithContract: Unit
        get() {
            ordinaryState!!.addContract(contract!!)
            defaultAutomaton!!.addState(startState!!)
            defaultAutomaton!!.addState(ordinaryState!!)

            Assertions.assertEquals(ordinaryState, defaultAutomaton!!.getStateWithContract(contract))
            Assertions.assertDoesNotThrow {
                defaultAutomaton!!.removeState(
                    ordinaryState!!
                )
            }
            Assertions.assertNull(defaultAutomaton!!.getStateWithContract(contract))
            Assertions.assertDoesNotThrow {
                defaultAutomaton!!.removeState(
                    startState!!
                )
            }
        }

    @get:Test
    val stateByName: Unit
        get() {
            defaultAutomaton!!.addState(startState!!)
            defaultAutomaton!!.addState(ordinaryState!!)

            Assertions.assertEquals(
                ordinaryState, defaultAutomaton!!.getStateByName(
                    ordinaryState!!.name!!
                )
            )
            Assertions.assertDoesNotThrow {
                defaultAutomaton!!.removeState(
                    ordinaryState!!
                )
            }
            Assertions.assertNull(
                defaultAutomaton!!.getStateByName(
                    ordinaryState!!.name!!
                )
            )
            Assertions.assertDoesNotThrow {
                defaultAutomaton!!.removeState(
                    startState!!
                )
            }
        }

    @Test
    fun testManagingEdges() {
        Assertions.assertTrue(defaultAutomaton!!.edges.isEmpty())

        defaultAutomaton!!.addEdge(edge1!!)
        Assertions.assertFalse(defaultAutomaton!!.edges.isEmpty())

        defaultAutomaton!!.addEdge(edge2!!)
        Assertions.assertEquals(2, defaultAutomaton!!.edges.size)

        defaultAutomaton!!.removeEdge(edge1!!)
        Assertions.assertEquals(1, defaultAutomaton!!.edges.size)
        Assertions.assertFalse(defaultAutomaton!!.edges.contains(edge1))

        defaultAutomaton!!.removeEdge(edge2!!)
        Assertions.assertTrue(defaultAutomaton!!.edges.isEmpty())

        val edges: MutableSet<Edge?> = HashSet()
        edges.add(edge1)
        edges.add(edge2)

        defaultAutomaton!!.addEdges(edges)
        Assertions.assertEquals(2, defaultAutomaton!!.edges.size)
        Assertions.assertDoesNotThrow { defaultAutomaton!!.removeEdges(edges) }
        Assertions.assertTrue(defaultAutomaton!!.edges.isEmpty())

        Assertions.assertThrows(NullPointerException::class.java) { defaultAutomaton!!.addEdge(null) }
        Assertions.assertThrows(NullPointerException::class.java) { defaultAutomaton!!.addEdges(null) }
        Assertions.assertThrows(NullPointerException::class.java) { defaultAutomaton!!.removeEdge(null) }
        Assertions.assertThrows(NullPointerException::class.java) { defaultAutomaton!!.removeEdges(null) }
    }

    @get:Test
    val outgoingEdges: Unit
        get() {
            defaultAutomaton!!.addEdge(edge1!!)
            defaultAutomaton!!.addEdge(edge2!!)
            Assertions.assertEquals(
                2, defaultAutomaton!!.getOutgoingEdges(
                    startState!!
                ).size
            )

            defaultAutomaton!!.removeEdge(edge2!!)
            Assertions.assertEquals(
                1, defaultAutomaton!!.getOutgoingEdges(
                    startState!!
                ).size
            )

            defaultAutomaton!!.removeEdge(edge1!!)
            Assertions.assertTrue(
                defaultAutomaton!!.getOutgoingEdges(
                    startState!!
                ).isEmpty()
            )
        }

    @get:Test
    val isEmpty: Unit
        get() {
            Assertions.assertTrue(defaultAutomaton!!.isEmpty)
            Assertions.assertFalse(automatonWithStartState!!.isEmpty)

            defaultAutomaton!!.addState(startState!!)
            Assertions.assertFalse(defaultAutomaton!!.isEmpty)
            Assertions.assertDoesNotThrow {
                defaultAutomaton!!.removeState(
                    startState!!
                )
            }

            defaultAutomaton!!.addEdge(edge1!!)
            Assertions.assertFalse(defaultAutomaton!!.isEmpty)
            Assertions.assertDoesNotThrow {
                defaultAutomaton!!.removeEdge(
                    edge1!!
                )
            }

            defaultAutomaton!!.addRegion(region1!!)
            Assertions.assertFalse(defaultAutomaton!!.isEmpty)
            Assertions.assertDoesNotThrow {
                defaultAutomaton!!.removeRegion(
                    region1!!
                )
            }
        }

    @Test
    fun allElements() {
        Assertions.assertTrue(defaultAutomaton!!.allElements.isEmpty())
        Assertions.assertFalse(automatonWithStartState!!.allElements.isEmpty())
    }

    var defaultAutomaton: Automaton? = null
    var automatonWithStartState: Automaton? = null
    var contract: Contract? = null
    var startState: State? = null
    var ordinaryState: State? = null
    var region1: Region? = null
    var region2: Region? = null
    var edge1: Edge? = null
    var edge2: Edge? = null


    @BeforeAll
    fun setUp() {
        defaultAutomaton = Automaton()
        contract = null
        var condition: Condition? = null

        try {
            condition = Condition("true")
            contract = Contract(2, "contract", condition, condition)
        } catch (e: ModelException) {
            Assertions.fail<Any>("Condition and contract for testing purposes of the automaton could not be initialized.")
        }

        try {
            startState = State(0, "startState")
            ordinaryState = State(1, "ordinaryState")
        } catch (e: ModelException) {
            Assertions.fail<Any>("States for testing purposes of the automaton could not be initialized.")
        }
        automatonWithStartState = Automaton()
        automatonWithStartState!!.addState(startState!!)
        Assertions.assertDoesNotThrow { automatonWithStartState!!.startState = startState }
        Assertions.assertNotNull(automatonWithStartState!!.startState)

        try {
            region1 = Region(3, "region1", condition!!, contract!!)
            region2 = Region(4, "region2", condition, contract!!)
        } catch (e: ModelException) {
            Assertions.fail<Any>("Regions for testing purposes of the automaton could not be initialized.")
        }

        try {
            edge1 = Edge(5u, startState!!, ordinaryState!!, contract!!, Kind.HIT, 0)
            edge2 = Edge(6u, startState!!, ordinaryState!!, contract!!, Kind.MISS, 1)
        } catch (e: ModelException) {
            Assertions.fail<Any>("Edges for testing purposes of the automaton could not be initialized.")
        }
    }
}