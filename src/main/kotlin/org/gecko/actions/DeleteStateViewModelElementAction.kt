package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.*

/**
 * A concrete representation of an [Action] that removes a [State] from the [GModel] and its
 * target-[State] from the given [Automaton].
 */
class DeleteStateViewModelElementAction
internal constructor(val gModel: GModel, val state: State, val System: System) :
    AbstractPositionableViewModelElementAction() {
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
        val regionViewModels =
            editorViewModel.viewableElementsProperty
                .filter { element: PositionableElement -> automaton.regions.contains(element) }
                .map { element: PositionableElement -> element as Region }
                .toSet()

        regionViewModels.forEach { it.states.remove(state) }

        automaton.removeState(state)
        gModel.deleteViewModelElement(state)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return RestoreStateAction(gModel, state, System)
    }

    override val target
        get() = state
}
