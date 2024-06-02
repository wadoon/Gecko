package org.gecko.actions

import org.gecko.exceptions.ModelException

import org.gecko.util.TestHelper
import org.gecko.viewmodel.StateViewModel
import org.junit.jupiter.api.*

class RenameViewModelElementActionTest {
    @Test
    fun run() {
        val renameAction: Action = actionFactory!!.createRenameViewModelElementAction(
            stateViewModel!!, NEW_NAME
        )
        actionManager!!.run(renameAction)
        Assertions.assertEquals(NEW_NAME, stateViewModel!!.name)
    }

    @get:Test
    val undoAction: Unit
        get() {
            val renameAction: Action = actionFactory!!.createRenameViewModelElementAction(
                stateViewModel!!, NEW_NAME_2
            )
            val beforeChangeName = stateViewModel!!.name
            actionManager!!.run(renameAction)
            Assertions.assertEquals(NEW_NAME_2, stateViewModel!!.name)
            actionManager!!.undo()
            Assertions.assertEquals(beforeChangeName, stateViewModel!!.name)
        }

    companion object {
        const val NEW_NAME_2: String = "newName2"
        const val NEW_NAME: String = "newName"
        private var actionManager: ActionManager? = null
        private var actionFactory: ActionFactory? = null
        private var stateViewModel: StateViewModel? = null

        @BeforeAll
        @Throws(ModelException::class)
        fun setUp() {
            val geckoViewModel = TestHelper.createGeckoViewModel()
            actionManager = ActionManager(geckoViewModel!!)
            actionFactory = ActionFactory(geckoViewModel)
            val viewModelFactory = geckoViewModel.viewModelFactory
            val systemViewModel =
                viewModelFactory.createSystemViewModelFrom(geckoViewModel.geckoModel.root)
            stateViewModel = viewModelFactory.createStateViewModelIn(systemViewModel)
        }
    }
}
