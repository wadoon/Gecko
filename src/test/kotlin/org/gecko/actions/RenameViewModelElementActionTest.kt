package org.gecko.actions

import org.gecko.util.TestHelper
import org.gecko.viewmodel.State
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RenameViewModelElementActionTest {
    private var actionManager: ActionManager
    private var actionFactory: ActionFactory
    private var state: State

    init {
        val geckoViewModel = TestHelper.createGeckoViewModel()
        actionManager = ActionManager(geckoViewModel)
        actionFactory = ActionFactory(geckoViewModel)
        val viewModelFactory = geckoViewModel.viewModelFactory
        val systemViewModel = geckoViewModel.root
        state = viewModelFactory.createState(systemViewModel)
    }

    @Test
    fun run() {
        val renameAction: Action = actionFactory.createRenameViewModelElementAction(
            state, NEW_NAME
        )
        actionManager.run(renameAction)
        Assertions.assertEquals(NEW_NAME, state.name)
    }

    @Test
    fun undoAction() {
        val renameAction: Action = actionFactory.createRenameViewModelElementAction(
            state, NEW_NAME_2
        )
        val beforeChangeName = state.name
        actionManager.run(renameAction)
        Assertions.assertEquals(NEW_NAME_2, state.name)
        actionManager.undo()
        Assertions.assertEquals(beforeChangeName, state.name)
    }

    companion object {
        const val NEW_NAME_2: String = "newName2"
        const val NEW_NAME: String = "newName"
    }
}
