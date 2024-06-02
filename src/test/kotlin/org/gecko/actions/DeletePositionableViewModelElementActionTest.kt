package org.gecko.actions

import org.gecko.exceptions.ModelException
import org.gecko.model.*

import org.gecko.util.TestHelper
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.PositionableViewModelElement
import org.gecko.viewmodel.StateViewModel
import org.gecko.viewmodel.SystemViewModel
import org.junit.jupiter.api.*

class DeletePositionableViewModelElementActionTest {
    private var elements: Set<PositionableViewModelElement<*>>? = null
    private var stateViewModel1: StateViewModel? = null
    private var actionManager: ActionManager? = null
    private var actionFactory: ActionFactory? = null
    private var geckoViewModel: GeckoViewModel? = null
    private var rootSystemViewModel: SystemViewModel? = null

    @BeforeEach
    @Throws(ModelException::class)
    fun setUp() {
        geckoViewModel = TestHelper.createGeckoViewModel()
        actionManager = ActionManager(geckoViewModel!!)
        actionFactory = ActionFactory(geckoViewModel!!)
        val viewModelFactory = geckoViewModel!!.viewModelFactory
        rootSystemViewModel = viewModelFactory.createSystemViewModelFrom(geckoViewModel!!.geckoModel.root)

        stateViewModel1 = viewModelFactory.createStateViewModelIn(rootSystemViewModel!!)
        val stateViewModel2 = viewModelFactory.createStateViewModelIn(rootSystemViewModel!!)
        val edge =
            viewModelFactory.createEdgeViewModelIn(rootSystemViewModel!!, stateViewModel1!!, stateViewModel2)
        val contractViewModel = viewModelFactory.createContractViewModelIn(stateViewModel1!!)
        edge.contract = contractViewModel
        val regionViewModel = viewModelFactory.createRegionViewModelIn(rootSystemViewModel!!)
        val systemViewModel1 = viewModelFactory.createSystemViewModelIn(rootSystemViewModel!!)
        val systemViewModel2 = viewModelFactory.createSystemViewModelIn(rootSystemViewModel!!)
        val portViewModel1 = viewModelFactory.createPortViewModelIn(systemViewModel1)
        portViewModel1.visibility = (Visibility.OUTPUT)
        portViewModel1.updateTarget()
        val portViewModel2 = viewModelFactory.createPortViewModelIn(systemViewModel2)
        portViewModel2.visibility = (Visibility.INPUT)
        portViewModel2.updateTarget()
        val systemConnectionViewModel =
            viewModelFactory.createSystemConnectionViewModelIn(rootSystemViewModel!!, portViewModel1, portViewModel2)

        elements = java.util.Set.of(
            stateViewModel1, stateViewModel2, edge, regionViewModel, systemViewModel1, systemViewModel2,
            systemConnectionViewModel
        )

        geckoViewModel!!.switchEditor(rootSystemViewModel!!, true)
    }

    @Test
    fun run() {
        val deleteAction: Action = actionFactory!!.createDeletePositionableViewModelElementAction(elements)
        actionManager!!.run(deleteAction)
        Assertions.assertEquals(geckoViewModel!!.currentEditor.positionableViewModelElements.size, 0)
        geckoViewModel!!.switchEditor(rootSystemViewModel!!, false)
        Assertions.assertEquals(geckoViewModel!!.currentEditor.positionableViewModelElements.size, 0)
    }

    @get:Test
    val undoAction: Unit
        get() {
            val deleteAction: Action = actionFactory!!.createDeletePositionableViewModelElementAction(elements)
            actionManager!!.run(deleteAction)

            Assertions.assertEquals(geckoViewModel!!.currentEditor.positionableViewModelElements.size, 0)
            geckoViewModel!!.switchEditor(rootSystemViewModel!!, false)
            Assertions.assertEquals(geckoViewModel!!.currentEditor.positionableViewModelElements.size, 0)

            actionManager!!.undo()

            Assertions.assertEquals(geckoViewModel!!.currentEditor.positionableViewModelElements.size, 3)
            geckoViewModel!!.switchEditor(rootSystemViewModel!!, false)
            Assertions.assertEquals(geckoViewModel!!.currentEditor.positionableViewModelElements.size, 3)

            actionManager!!.redo()
            Assertions.assertEquals(geckoViewModel!!.currentEditor.positionableViewModelElements.size, 0)
            geckoViewModel!!.switchEditor(rootSystemViewModel!!, false)
            Assertions.assertEquals(geckoViewModel!!.currentEditor.positionableViewModelElements.size, 0)
        }
}
