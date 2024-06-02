package org.gecko.viewmodel

import org.gecko.exceptions.ModelException
import org.gecko.model.GeckoModel
import org.gecko.model.ModelFactory

import org.gecko.util.TestHelper
import org.junit.jupiter.api.*

class PositionableViewModelTest {
    var geckoViewModel: GeckoViewModel? = null
    var geckoModel: GeckoModel? = null
    var viewModelFactory: ViewModelFactory? = null
    var modelFactory: ModelFactory? = null
    var root: SystemViewModel? = null
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
        try {
            stateViewModel1 = viewModelFactory!!.createStateViewModelIn(root!!)
            stateViewModel2 = viewModelFactory!!.createStateViewModelIn(root!!)
        } catch (e: ModelException) {
            Assertions.fail<Any>()
        }
    }

    @Test
    @Throws(ModelException::class)
    fun edgeViewModelTest() {
        val edgeViewModel = viewModelFactory!!.createEdgeViewModelIn(root!!, stateViewModel1!!, stateViewModel2!!)
        Assertions.assertEquals(stateViewModel1, edgeViewModel.source)
        Assertions.assertEquals(stateViewModel2, edgeViewModel.destination)
        Assertions.assertEquals(stateViewModel1!!.incomingEdges.size, 0)
        Assertions.assertEquals(stateViewModel1!!.outgoingEdges.size, 1)
        Assertions.assertEquals(stateViewModel2!!.incomingEdges.size, 1)
        Assertions.assertEquals(stateViewModel2!!.outgoingEdges.size, 0)

        val contractViewModel = viewModelFactory!!.createContractViewModelIn(stateViewModel1!!)
        edgeViewModel.contract = contractViewModel
        Assertions.assertEquals(contractViewModel, edgeViewModel.contract)
        Assertions.assertEquals(stateViewModel1!!.contracts.size, 1)
    }
}
