package org.gecko.actions

import org.gecko.exceptions.GeckoException

import org.gecko.viewmodel.*

/**
 * A concrete representation of an [Action] that removes a [StateViewModel] from the [GModel]
 * and its target-[State] from the given [Automaton].
 */
class DeleteStateViewModelElementAction internal constructor(
    val gModel: GModel,
    val stateViewModel: StateViewModel,
    val System: System
) : AbstractPositionableViewModelElementAction() {
    val editorViewModel: EditorViewModel = gModel.currentEditor!!
    val automaton: Automaton = System.automaton
    var wasStartState = false

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        /*wasStartState = stateViewModel == automaton.startState
        if (wasStartState && automaton.states.size > 1) {
            val states = automaton.states
            val newStartState = states.find { it != stateViewModel }
        }*/

        // remove from region if it is in one
        val regionViewModels = editorViewModel.viewableElementsProperty
            .filter { element: PositionableViewModelElement -> automaton.regions.contains(element) }
            .map { element: PositionableViewModelElement -> element as Region }
            .toSet()

        regionViewModels.forEach { it.removeState(stateViewModel) }

        automaton.removeState(stateViewModel)
        gModel.deleteViewModelElement(stateViewModel)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return RestoreStateViewModelElementAction(gModel, stateViewModel, System, wasStartState)
    }


    override val target
        get() = stateViewModel
}
