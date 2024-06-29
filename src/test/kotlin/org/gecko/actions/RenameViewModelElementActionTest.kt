package org.gecko.actions

import org.gecko.util.TestHelper
import org.gecko.viewmodel.StateViewModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RenameViewModelElementActionTest {
    private var actionManager: ActionManager
    private var actionFactory: ActionFactory
    private var stateViewModel: StateViewModel

    init {
        val geckoViewModel = TestHelper.createGeckoViewModel()
        actionManager = ActionManager(geckoViewModel)
        actionFactory = ActionFactory(geckoViewModel)
        val viewModelFactory = geckoViewModel.viewModelFactory
        val systemViewModel = geckoViewModel.root
        stateViewModel = viewModelFactory.createState(systemViewModel)
    }

    @Test
    fun run() {
        val renameAction: Action = actionFactory.createRenameViewModelElementAction(
            stateViewModel, NEW_NAME
        )
        actionManager.run(renameAction)
        Assertions.assertEquals(NEW_NAME, stateViewModel.name)
    }

    @Test
    fun undoAction() {
        val renameAction: Action = actionFactory.createRenameViewModelElementAction(
            stateViewModel, NEW_NAME_2
        )
        val beforeChangeName = stateViewModel.name
        actionManager.run(renameAction)
        Assertions.assertEquals(NEW_NAME_2, stateViewModel.name)
        actionManager.undo()
        Assertions.assertEquals(beforeChangeName, stateViewModel.name)
    }

    companion object {
        const val NEW_NAME_2: String = "newName2"
        const val NEW_NAME: String = "newName"
    }
}
