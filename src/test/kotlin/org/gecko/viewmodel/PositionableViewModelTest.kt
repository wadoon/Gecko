package org.gecko.viewmodel

import org.gecko.exceptions.ModelException


import org.gecko.util.TestHelper
import org.junit.jupiter.api.*

class PositionableViewModelTest {
    var geckoViewModel: GeckoViewModel = TestHelper.createGeckoViewModel()
    var viewModelFactory: ViewModelFactory = geckoViewModel.viewModelFactory
    var root: SystemViewModel = geckoViewModel.currentEditor!!.currentSystem
    var stateViewModel1: StateViewModel = viewModelFactory.createStateViewModelIn(root)
    var stateViewModel2: StateViewModel = viewModelFactory.createStateViewModelIn(root)

    @Test
    @Throws(ModelException::class)
    fun edgeViewModelTest() {
        val edgeViewModel = viewModelFactory.createEdgeViewModelIn(root, stateViewModel1, stateViewModel2)
        Assertions.assertEquals(stateViewModel1, edgeViewModel.source)
        Assertions.assertEquals(stateViewModel2, edgeViewModel.destination)
        Assertions.assertEquals(stateViewModel1.incomingEdges.size, 0)
        Assertions.assertEquals(stateViewModel1.outgoingEdges.size, 1)
        Assertions.assertEquals(stateViewModel2.incomingEdges.size, 1)
        Assertions.assertEquals(stateViewModel2.outgoingEdges.size, 0)

        val contractViewModel = viewModelFactory.createContractViewModelIn(stateViewModel1)
        edgeViewModel.contract = contractViewModel
        Assertions.assertEquals(contractViewModel, edgeViewModel.contract)
        Assertions.assertEquals(stateViewModel1.contracts.size, 1)
    }
}
