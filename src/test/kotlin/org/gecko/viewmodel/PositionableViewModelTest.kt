package org.gecko.viewmodel

import org.gecko.exceptions.ModelException


import org.gecko.util.TestHelper
import org.junit.jupiter.api.*

class PositionableViewModelTest {
    var gModel: GModel = TestHelper.createGeckoViewModel()
    var viewModelFactory: ViewModelFactory = gModel.viewModelFactory
    var root: System = gModel.currentEditor!!.currentSystem
    var state1: State = viewModelFactory.createState(root)
    var state2: State = viewModelFactory.createState(root)

    @Test
    @Throws(ModelException::class)
    fun edgeViewModelTest() {
        val edgeViewModel = viewModelFactory.createEdgeViewModelIn(root, state1, state2)
        Assertions.assertEquals(state1, edgeViewModel.source)
        Assertions.assertEquals(state2, edgeViewModel.destination)
        Assertions.assertEquals(state1.incomingEdges.size, 0)
        Assertions.assertEquals(state1.outgoingEdges.size, 1)
        Assertions.assertEquals(state2.incomingEdges.size, 1)
        Assertions.assertEquals(state2.outgoingEdges.size, 0)

        val contractViewModel = viewModelFactory.createContractViewModelIn(state1)
        edgeViewModel.contract = contractViewModel
        Assertions.assertEquals(contractViewModel, edgeViewModel.contract)
        Assertions.assertEquals(state1.contracts.size, 1)
    }
}
