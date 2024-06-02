package org.gecko.model

import org.gecko.exceptions.ModelException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.ThrowingSupplier

class GeckoModelTest {
    @get:Test
    val allSystems: Unit
        get() {
            Assertions.assertFalse(defaultModel!!.allSystems.isEmpty())
            Assertions.assertEquals(1, defaultModel!!.allSystems.size)
            Assertions.assertEquals("Element_0", defaultModel!!.allSystems.first!!.name)

            defaultModel!!.root.addChild(child!!)
            Assertions.assertNotEquals(1, defaultModel!!.allSystems.size)
            Assertions.assertTrue(defaultModel!!.allSystems.contains(childOfChild))

            defaultModel!!.root.removeChild(child!!)
        }

    @get:Test
    val systemWithVariable: Unit
        get() {
            defaultModel!!.root.addChild(child!!)
            Assertions.assertThrows(NullPointerException::class.java) { defaultModel!!.getSystemWithVariable(null) }
            Assertions.assertEquals(
                childOfChild, defaultModel!!.getSystemWithVariable(
                    variable!!
                )
            )
            defaultModel!!.root.removeChild(child!!)
        }

    @get:Test
    val isNameUnique: Unit
        get() {
            Assertions.assertThrows(NullPointerException::class.java) { defaultModel!!.isNameUnique(null) }

            defaultModel!!.root.addChild(child!!)
            Assertions.assertFalse(defaultModel!!.isNameUnique("childOfChild"))

            defaultModel!!.root.addVariable(variable!!)
            Assertions.assertFalse { defaultModel!!.isNameUnique("variable") }

            try {
                val condition = Condition("true")
                val contract1 = Contract(4, "contract1", condition, condition)
                val contract2 = Contract(5, "contract2", condition, condition)

                val state = State(6, "state")
                state.addContract(contract2)

                defaultModel!!.root.automaton.addRegion(Region(7, "region", condition, contract1))
                defaultModel!!.root.automaton.addState(state)
            } catch (e: ModelException) {
                Assertions.fail<Any>("Could not initialize region or state for testing purposes of the model.")
            }

            Assertions.assertFalse { defaultModel!!.isNameUnique("region") }
            Assertions.assertFalse { defaultModel!!.isNameUnique("state") }
            Assertions.assertFalse { defaultModel!!.isNameUnique("contract1") }
            Assertions.assertFalse { defaultModel!!.isNameUnique("contract2") }
        }

    @Test
    fun testConsiderationOfPreAndPostConditionNamesOfRegionsInUniqueNameCheck() {
        var model: GeckoModel? = null
        val system: System
        val region: Region
        try {
            model = GeckoModel()
            system = System(8, "system", null, Automaton())
            region = Region(
                9, "region", Condition("true"),
                Contract(10, "contract10", Condition("true"), Condition("true"))
            )
            system.automaton.addRegion(region)
            model.root.addChild(system)
        } catch (e: ModelException) {
            Assertions.fail<Any>("Failed to create system and / or region for testing purposes of the unique names check.")
        }

        Assertions.assertNotNull(model)
        Assertions.assertFalse(model!!.isNameUnique("contract10"))
        Assertions.assertFalse(model.isNameUnique("region"))
        Assertions.assertTrue(model.isNameUnique("contract"))
    }

    companion object {
        var defaultModel: GeckoModel? = null
        var modelFromRoot: GeckoModel? = null
        var root: System? = null
        var child: System? = null
        var childOfChild: System? = null
        var variable: Variable? = null
        const val NULL_PARAMETERS_FAIL: String = "Setter to null should throw before model intervenes."

        @BeforeAll
        fun setUp() {
            Assertions.assertDoesNotThrow(ThrowingSupplier<GeckoModel> { defaultModel = GeckoModel() })

            Assertions.assertThrows(NullPointerException::class.java) { modelFromRoot = GeckoModel(null) }
            Assertions.assertDoesNotThrow(ThrowingSupplier<System> { root = System(0, "root", null, Automaton()) })
            Assertions.assertDoesNotThrow(ThrowingSupplier<GeckoModel> {
                modelFromRoot = GeckoModel(
                    root!!
                )
            })

            Assertions.assertDoesNotThrow(ThrowingSupplier<System> { child = System(1, "child", null, Automaton()) })
            Assertions.assertDoesNotThrow(ThrowingSupplier<System> {
                childOfChild = System(2, "childOfChild", null, Automaton())
            })
            child!!.addChild(childOfChild!!)

            Assertions.assertDoesNotThrow(ThrowingSupplier<Variable> {
                variable = Variable(3, "variable", "type", Visibility.INPUT)
            })
            childOfChild!!.addVariable(variable!!)
        }
    }
}
