package org.gecko.actions

import org.gecko.exceptions.ModelException


import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.StateViewModel
import org.gecko.viewmodel.SystemViewModel
import org.junit.jupiter.api.*
import java.util.Set

internal class DeleteRestoreActionTest {
    @Test
    fun deleteElement() {
        geckoViewModel!!.switchEditor(rootSystemViewModel!!, true)
        actionManager!!.run(
            actionManager!!.actionFactory.createDeletePositionableViewModelElementAction(Set.of(stateViewModel))
        )
        Assertions.assertTrue(geckoViewModel!!.currentEditor.containedPositionableViewModelElementsProperty.isEmpty())
    }

    @Test
    fun restoreElementCheckViewModel() {
        geckoViewModel!!.switchEditor(rootSystemViewModel!!, true)
        actionManager!!.run(
            actionManager!!.actionFactory.createDeletePositionableViewModelElementAction(Set.of(stateViewModel))
        )
        actionManager!!.undo()
        Assertions.assertTrue(
            geckoViewModel!!.currentEditor
                .containedPositionableViewModelElementsProperty
                .contains(stateViewModel)
        )
    }

    @Test
    fun restoreElementCheckModel() {
        geckoViewModel!!.switchEditor(rootSystemViewModel!!, true)
        actionManager!!.run(
            actionManager!!.actionFactory.createDeletePositionableViewModelElementAction(Set.of(stateViewModel))
        )
        actionManager!!.undo()
        Assertions.assertTrue(
            geckoModel!!.root.automaton.states.contains(
                stateViewModel!!.target
            )
        )
    }

    @Test
    fun deleteElementInChildSystem() {
        geckoViewModel!!.switchEditor(childSystemViewModel1!!, true)
        actionManager!!.run(
            actionManager!!.actionFactory.createDeletePositionableViewModelElementAction(Set.of(stateViewModel2))
        )
        Assertions.assertTrue(geckoViewModel!!.currentEditor.containedPositionableViewModelElementsProperty.isEmpty())
    }

    @Test
    fun restoreElementCheckChildSystemViewModel() {
        geckoViewModel!!.switchEditor(childSystemViewModel1!!, true)
        actionManager!!.run(
            actionManager!!.actionFactory.createDeletePositionableViewModelElementAction(Set.of(stateViewModel2))
        )
        actionManager!!.undo()
        Assertions.assertTrue(
            geckoViewModel!!.currentEditor
                .containedPositionableViewModelElementsProperty
                .contains(stateViewModel2)
        )
    }

    @Test
    fun restoreElementCheckChildSystemModel() {
        geckoViewModel!!.switchEditor(childSystemViewModel1!!, true)
        actionManager!!.run(
            actionManager!!.actionFactory.createDeletePositionableViewModelElementAction(Set.of(stateViewModel2))
        )
        actionManager!!.undo()
        Assertions.assertTrue(
            childSystemViewModel1!!.target.automaton.states.contains(
                stateViewModel2!!.target
            )
        )
    }

    companion object {
        private var actionManager: ActionManager? = null
        private var geckoModel: GeckoModel? = null
        private var geckoViewModel: GeckoViewModel? = null
        private var rootSystemViewModel: SystemViewModel? = null
        private var childSystemViewModel1: SystemViewModel? = null
        private var stateViewModel: StateViewModel? = null
        private var stateViewModel2: StateViewModel? = null

        @BeforeAll
        @Throws(ModelException::class)
        fun setUp() {
            geckoModel = GeckoModel()
            geckoViewModel = GeckoViewModel(geckoModel!!)
            val viewModelFactory = geckoViewModel!!.viewModelFactory
            actionManager = ActionManager(geckoViewModel!!)
            rootSystemViewModel = geckoViewModel!!.currentEditor.currentSystem
            childSystemViewModel1 = viewModelFactory.createSystemViewModelIn(
                rootSystemViewModel!!
            )
            try {
                stateViewModel = viewModelFactory.createStateViewModelIn(
                    rootSystemViewModel!!
                )
                stateViewModel2 = viewModelFactory.createStateViewModelIn(
                    childSystemViewModel1!!
                )
            } catch (e: Exception) {
                Assertions.fail<Any>()
            }
        }
    }
}
