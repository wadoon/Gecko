package org.gecko.viewmodel

import org.gecko.exceptions.MissingViewModelElementException
import org.gecko.exceptions.ModelException
import org.gecko.model.*

import org.gecko.util.TestHelper
import org.junit.jupiter.api.*

internal class ViewModelFactoryTest {
    var geckoViewModel: GeckoViewModel? = null
    var geckoModel: GeckoModel? = null
    var viewModelFactory: ViewModelFactory? = null
    var modelFactory: ModelFactory? = null
    var root: SystemViewModel? = null
    var systemViewModel1: SystemViewModel? = null
    var systemViewModel2: SystemViewModel? = null
    var systemViewModel11: SystemViewModel? = null
    var stateViewModel1: StateViewModel? = null
    var stateViewModel2: StateViewModel? = null

    @BeforeEach
    @Throws(ModelException::class)
    fun setUp() {
        geckoViewModel = TestHelper.createGeckoViewModel()
        geckoModel = geckoViewModel!!.geckoModel
        viewModelFactory = geckoViewModel!!.viewModelFactory
        modelFactory = geckoModel!!.modelFactory
        root = geckoViewModel!!.currentEditor.currentSystem
        systemViewModel1 = viewModelFactory!!.createSystemViewModelIn(root!!)
        systemViewModel2 = viewModelFactory!!.createSystemViewModelIn(root!!)
        systemViewModel11 = viewModelFactory!!.createSystemViewModelIn(systemViewModel1!!)
        try {
            stateViewModel1 = viewModelFactory!!.createStateViewModelIn(systemViewModel1!!)
            stateViewModel2 = viewModelFactory!!.createStateViewModelIn(systemViewModel1!!)
        } catch (e: ModelException) {
            Assertions.fail<Any>()
        }
    }

    @Test
    fun testModelStructure() {
        Assertions.assertTrue(root!!.target.children.contains(systemViewModel1!!.target))
        Assertions.assertTrue(systemViewModel1!!.target.children.contains(systemViewModel11!!.target))
    }

    @Test
    @Throws(ModelException::class)
    fun testAddPorts() {
        val portViewModel1 = viewModelFactory!!.createPortViewModelIn(systemViewModel1!!)
        val portViewModel2 = viewModelFactory!!.createPortViewModelIn(systemViewModel1!!)
        Assertions.assertTrue(systemViewModel1!!.target.variables.contains(portViewModel1.target))
        Assertions.assertTrue(systemViewModel1!!.target.variables.contains(portViewModel2.target))
        Assertions.assertTrue(systemViewModel1!!.portsProperty.contains(portViewModel1))
        Assertions.assertTrue(systemViewModel1!!.portsProperty.contains(portViewModel2))
    }

    @Test
    fun testModelToViewModel() {
        Assertions.assertEquals(
            systemViewModel1, geckoViewModel!!.getViewModelElement(
                systemViewModel1!!.target
            )
        )
        Assertions.assertEquals(
            systemViewModel11, geckoViewModel!!.getViewModelElement(
                systemViewModel11!!.target
            )
        )
    }

    @Test
    @Throws(ModelException::class)
    fun testAddSystemConnectionBetweenPorts() {
        val portViewModel1 = viewModelFactory!!.createPortViewModelIn(systemViewModel1!!)
        portViewModel1.visibility = (Visibility.OUTPUT)
        portViewModel1.updateTarget()
        val portViewModel2 = viewModelFactory!!.createPortViewModelIn(systemViewModel2!!)
        var systemConnectionViewModel: SystemConnectionViewModel? = null
        try {
            systemConnectionViewModel =
                viewModelFactory!!.createSystemConnectionViewModelIn(root!!, portViewModel1, portViewModel2)
        } catch (e: ModelException) {
            Assertions.fail<Any>()
        }
        Assertions.assertTrue(root!!.target.connections.contains(systemConnectionViewModel!!.target))
        Assertions.assertEquals(systemConnectionViewModel.target.source, portViewModel1.target)
        Assertions.assertEquals(systemConnectionViewModel.target.destination, portViewModel2.target)
        Assertions.assertEquals(systemConnectionViewModel.source, portViewModel1)
        Assertions.assertEquals(systemConnectionViewModel.destination, portViewModel2)
    }

    @Test
    @Throws(MissingViewModelElementException::class, ModelException::class)
    fun testAddSystemConnectionFrom() {
        Assertions.assertTrue(root!!.target.connections.isEmpty())
        val portViewModel1 = viewModelFactory!!.createPortViewModelIn(systemViewModel1!!)
        portViewModel1.visibility = (Visibility.OUTPUT)
        portViewModel1.updateTarget()
        val portViewModel2 = viewModelFactory!!.createPortViewModelIn(systemViewModel2!!)
        var systemConnectionViewModel: SystemConnectionViewModel? = null
        try {
            systemConnectionViewModel =
                viewModelFactory!!.createSystemConnectionViewModelIn(root!!, portViewModel1, portViewModel2)
        } catch (e: ModelException) {
            Assertions.fail<Any>()
        }

        viewModelFactory!!.createSystemConnectionViewModelFrom(systemConnectionViewModel!!.target)
        Assertions.assertEquals(1, root!!.target.connections.size)
    }

    @Test
    fun testStatesInSystem() {
        Assertions.assertTrue(systemViewModel1!!.target.automaton.states.contains(stateViewModel1!!.target))
        Assertions.assertTrue(systemViewModel1!!.target.automaton.states.contains(stateViewModel2!!.target))
        Assertions.assertNotNull(geckoViewModel!!.getViewModelElement(stateViewModel1!!.target))
        Assertions.assertNotNull(geckoViewModel!!.getViewModelElement(stateViewModel2!!.target))
    }

    @Test
    @Throws(ModelException::class)
    fun testAddContractsToState() {
        val contractViewModel1 = viewModelFactory!!.createContractViewModelIn(stateViewModel1!!)
        val contractViewModel2 = viewModelFactory!!.createContractViewModelIn(stateViewModel1!!)
        Assertions.assertTrue(stateViewModel1!!.target.contracts.contains(contractViewModel1.target))
        Assertions.assertTrue(stateViewModel1!!.target.contracts.contains(contractViewModel2.target))
        Assertions.assertTrue(stateViewModel1!!.contractsProperty.contains(contractViewModel1))
        Assertions.assertTrue(stateViewModel1!!.contractsProperty.contains(contractViewModel2))
    }

    @Test
    @Throws(ModelException::class)
    fun testAddEdgesToSystem() {
        val edgeViewModel1 =
            viewModelFactory!!.createEdgeViewModelIn(systemViewModel1!!, stateViewModel1!!, stateViewModel2!!)
        val edgeViewModel2 =
            viewModelFactory!!.createEdgeViewModelIn(systemViewModel1!!, stateViewModel1!!, stateViewModel1!!)
        Assertions.assertTrue(systemViewModel1!!.target.automaton.edges.contains(edgeViewModel1.target))
        Assertions.assertTrue(systemViewModel1!!.target.automaton.edges.contains(edgeViewModel2.target))
        Assertions.assertEquals(stateViewModel2, edgeViewModel1.destination)
        Assertions.assertEquals(stateViewModel1, edgeViewModel2.destination)
        Assertions.assertEquals(stateViewModel1, edgeViewModel1.source)
        Assertions.assertEquals(stateViewModel1, edgeViewModel2.source)
    }

    @Test
    @Throws(ModelException::class)
    fun testCreateStateFromModelWithContracts() {
        val state = modelFactory!!.createState(systemViewModel1!!.target.automaton)
        val contract1 = modelFactory!!.createContract(state)
        contract1.name = "contract1"
        val contract2 = modelFactory!!.createContract(state)
        val stateViewModel = viewModelFactory!!.createStateViewModelFrom(state)
        Assertions.assertTrue(stateViewModel.target.contracts.contains(contract1))
        Assertions.assertTrue(stateViewModel.target.contracts.contains(contract2))
        Assertions.assertEquals(2, stateViewModel.contractsProperty.size)
        Assertions.assertEquals("contract1", stateViewModel.contractsProperty.first().name)
    }

    @Test
    @Throws(ModelException::class)
    fun testCreateEdgeFromModelFail() {
        val source = modelFactory!!.createState(systemViewModel1!!.target.automaton)
        val destination = modelFactory!!.createState(systemViewModel1!!.target.automaton)
        val edge = modelFactory!!.createEdge(systemViewModel1!!.target.automaton, source, destination)
        Assertions.assertThrows(MissingViewModelElementException::class.java) {
            viewModelFactory!!.createEdgeViewModelFrom(
                edge
            )
        }
    }

    @Test
    @Throws(ModelException::class)
    fun testCreateEdgeFromModel() {
        val edge = modelFactory!!.createEdge(
            systemViewModel1!!.target.automaton, stateViewModel1!!.target,
            stateViewModel2!!.target
        )
        try {
            val edgeViewModel = viewModelFactory!!.createEdgeViewModelFrom(edge)
            Assertions.assertEquals(edge, edgeViewModel.target)
            Assertions.assertEquals(stateViewModel1, edgeViewModel.source)
            Assertions.assertEquals(stateViewModel2, edgeViewModel.destination)
        } catch (e: MissingViewModelElementException) {
            Assertions.fail<Any>()
        }
    }

    @Test
    @Throws(ModelException::class)
    fun testCreateRegionFromModel() {
        val region = modelFactory!!.createRegion(systemViewModel1!!.target.automaton)
        region.addState(stateViewModel1!!.target)
        region.addState(stateViewModel2!!.target)
        try {
            val regionViewModel = viewModelFactory!!.createRegionViewModelFrom(region)
            Assertions.assertEquals(region, regionViewModel.target)
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
        val system = modelFactory!!.createSystem(root!!.target)
        val variable1 = modelFactory!!.createVariable(system)
        val variable2 = modelFactory!!.createVariable(system)
        // system.addVariables(Set.of(variable1, variable2));
        val systemViewModel = viewModelFactory!!.createSystemViewModelFrom(system)
        Assertions.assertNotNull(geckoViewModel!!.getViewModelElement(variable1))
        Assertions.assertNotNull(geckoViewModel!!.getViewModelElement(variable2))
        Assertions.assertEquals(2, systemViewModel.portsProperty.size)
    }
}