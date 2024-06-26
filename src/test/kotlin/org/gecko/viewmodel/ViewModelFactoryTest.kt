package org.gecko.viewmodel

import org.gecko.exceptions.MissingViewModelElementException
import org.gecko.exceptions.ModelException


import org.gecko.util.TestHelper
import org.junit.jupiter.api.*

internal class ViewModelFactoryTest {
    var gModel: GModel = TestHelper.createGeckoViewModel()
    var viewModelFactory: ViewModelFactory = gModel.viewModelFactory
    var root: System = gModel.currentEditor!!.currentSystem
    var System1: System = viewModelFactory.createSystem(root)
    var System2: System = viewModelFactory.createSystem(root)
    var System11: System = viewModelFactory.createSystem(System1)
    var state1: State = viewModelFactory.createState(System1)
    var state2: State = viewModelFactory.createState(System1)

    @Test
    fun testModelStructure() {
        Assertions.assertTrue(root.subSystems.contains(System1))
        Assertions.assertTrue(System1.subSystems.contains(System11))
    }

    @Test
    @Throws(ModelException::class)
    fun testAddPorts() {
        val portViewModel1 = viewModelFactory.createPort(System1)
        val portViewModel2 = viewModelFactory.createPort(System1)
        Assertions.assertTrue(System1.ports.contains(portViewModel1))
        Assertions.assertTrue(System1.ports.contains(portViewModel2))
        Assertions.assertTrue(System1.portsProperty.contains(portViewModel1))
        Assertions.assertTrue(System1.portsProperty.contains(portViewModel2))
    }

    @Test
    fun testModelToViewModel() {
        Assertions.assertEquals(
            System1,
            System1
        )
        Assertions.assertEquals(
            System11, System11
        )
    }

    @Test
    @Throws(ModelException::class)
    fun testAddSystemConnectionBetweenPorts() {
        val portViewModel1 = viewModelFactory.createPort(System1)
        portViewModel1.visibility = (Visibility.OUTPUT)
        val portViewModel2 = viewModelFactory.createPort(System2)
        val systemConnectionViewModel =
            viewModelFactory.createSystemConnectionViewModelIn(root, portViewModel1, portViewModel2)
        Assertions.assertTrue(root.connections.contains(systemConnectionViewModel))
        Assertions.assertEquals(systemConnectionViewModel.source, portViewModel1)
        Assertions.assertEquals(systemConnectionViewModel.destination, portViewModel2)
        Assertions.assertEquals(systemConnectionViewModel.source, portViewModel1)
        Assertions.assertEquals(systemConnectionViewModel.destination, portViewModel2)
    }

    @Test
    @Throws(MissingViewModelElementException::class, ModelException::class)
    fun testAddSystemConnectionFrom() {
        Assertions.assertTrue(root.connections.isEmpty())
        val portViewModel1 = viewModelFactory.createPort(System1)
        portViewModel1.visibility = (Visibility.OUTPUT)
        val portViewModel2 = viewModelFactory.createPort(System2)
        viewModelFactory.createSystemConnectionViewModelIn(root, portViewModel1, portViewModel2)
        Assertions.assertEquals(1, root.connections.size)
    }

    @Test
    fun testStatesInSystem() {
        Assertions.assertTrue(System1.automaton.states.contains(state1))
        Assertions.assertTrue(System1.automaton.states.contains(state2))
    }

    @Test
    @Throws(ModelException::class)
    fun testAddContractsToState() {
        val contractViewModel1 = viewModelFactory.createContractViewModelIn(state1)
        val contractViewModel2 = viewModelFactory.createContractViewModelIn(state1)
        Assertions.assertTrue(state1.contracts.contains(contractViewModel1))
        Assertions.assertTrue(state1.contracts.contains(contractViewModel2))
        Assertions.assertTrue(state1.contractsProperty.contains(contractViewModel1))
        Assertions.assertTrue(state1.contractsProperty.contains(contractViewModel2))
    }

    @Test
    @Throws(ModelException::class)
    fun testAddEdgesToSystem() {
        val edgeViewModel1 =
            viewModelFactory.createEdgeViewModelIn(System1, state1, state2)
        val edgeViewModel2 =
            viewModelFactory.createEdgeViewModelIn(System1, state1, state1)
        Assertions.assertTrue(System1.automaton.edges.contains(edgeViewModel1))
        Assertions.assertTrue(System1.automaton.edges.contains(edgeViewModel2))
        Assertions.assertEquals(state2, edgeViewModel1.destination)
        Assertions.assertEquals(state1, edgeViewModel2.destination)
        Assertions.assertEquals(state1, edgeViewModel1.source)
        Assertions.assertEquals(state1, edgeViewModel2.source)
    }
    /*
        @Test
        @Throws(ModelException::class)
        fun testCreateStateFromModelWithContracts() {
            val state = modelFactory!!.createState(systemViewModel1.automaton)
            val contract1 = modelFactory!!.createContract(state)
            contract1.name = "contract1"
            val contract2 = modelFactory!!.createContract(state)
            val stateViewModel = viewModelFactory.createStateViewModelFrom(state)
            Assertions.assertTrue(stateViewModel.contracts.contains(contract1))
            Assertions.assertTrue(stateViewModel.contracts.contains(contract2))
            Assertions.assertEquals(2, stateViewModel.contractsProperty.size)
            Assertions.assertEquals("contract1", stateViewModel.contractsProperty.first().name)
        }

        @Test
        @Throws(ModelException::class)
        fun testCreateEdgeFromModelFail() {
            val source = modelFactory!!.createState(systemViewModel1.automaton)
            val destination = modelFactory!!.createState(systemViewModel1.automaton)
            val edge = modelFactory!!.createEdge(systemViewModel1.automaton, source, destination)
            Assertions.assertThrows(MissingViewModelElementException::class.java) {
                viewModelFactory.createEdgeViewModelFrom(
                    edge
                )
            }
        }

        @Test
        @Throws(ModelException::class)
        fun testCreateEdgeFromModel() {
            val edge = modelFactory!!.createEdge(
                systemViewModel1.automaton, stateViewModel1,
                stateViewModel2
            )
            try {
                val edgeViewModel = viewModelFactory.createEdgeViewModelFrom(edge)
                Assertions.assertEquals(edge, edgeViewModel)
                Assertions.assertEquals(stateViewModel1, edgeViewModel.source)
                Assertions.assertEquals(stateViewModel2, edgeViewModel.destination)
            } catch (e: MissingViewModelElementException) {
                Assertions.fail<Any>()
            }
        }

        @Test
        @Throws(ModelException::class)
        fun testCreateRegionFromModel() {
            val region = modelFactory!!.createRegion(systemViewModel1.automaton)
            region.addState(stateViewModel1)
            region.addState(stateViewModel2)
            try {
                val regionViewModel = viewModelFactory.createRegionViewModelFrom(region)
                Assertions.assertEquals(region, regionViewModel)
                Assertions.assertEquals(2, regionViewModel.statesProperty.size)
                Assertions.assertTrue(regionViewModel.statesProperty.contains(stateViewModel1))
                Assertions.assertTrue(regionViewModel.statesProperty.contains(stateViewModel2))
            } catch (e: MissingViewModelElementException) {
                Assertions.fail<Any>()
            }
        }

        @Test
        @Throws(ModelException::class)
        fun testCreateSystemFromModel() {
            val system = modelFactory!!.createSystem(root)
            val variable1 = modelFactory!!.createVariable(system)
            val variable2 = modelFactory!!.createVariable(system)
            // system.addVariables(Set.of(variable1, variable2));
            val systemViewModel = viewModelFactory.createSystemViewModelFrom(system)
            Assertions.assertNotNull(geckoViewModel.getViewModelElement(variable1))
            Assertions.assertNotNull(geckoViewModel.getViewModelElement(variable2))
            Assertions.assertEquals(2, systemViewModel.portsProperty.size)
        }
        */
}