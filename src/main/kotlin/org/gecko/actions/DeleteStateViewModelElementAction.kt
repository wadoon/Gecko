package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.model.*
import org.gecko.viewmodel.*
import java.util.function.Consumer
import java.util.stream.Collectors

/**
 * A concrete representation of an [Action] that removes a [StateViewModel] from the [GeckoViewModel]
 * and its target-[State] from the given [Automaton].
 */
class DeleteStateViewModelElementAction internal constructor(
    val geckoViewModel: GeckoViewModel,
    val stateViewModel: StateViewModel,
    val systemViewModel: SystemViewModel
) : AbstractPositionableViewModelElementAction() {
    val editorViewModel: EditorViewModel = geckoViewModel.currentEditor!!
    val automaton: Automaton = systemViewModel.target?.automaton!!
    var wasStartState = false

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        wasStartState = stateViewModel.target == automaton.startState
        if (wasStartState && automaton.states.size > 1) {
            val states = automaton.states
            val newStartState = states.find { it != stateViewModel.target }
            val newStartStateViewModel = geckoViewModel.getViewModelElement(newStartState!!) as StateViewModel
            systemViewModel.startState = newStartStateViewModel
            systemViewModel.updateTarget()
        }

        // remove from region if it is in one
        val regionViewModels = editorViewModel.containedPositionableViewModelElementsProperty
            .stream()
            .filter { element: PositionableViewModelElement<*> -> automaton.regions.contains(element.target) }
            .map { element: PositionableViewModelElement<*> -> element as RegionViewModel }
            .collect(Collectors.toSet())

        regionViewModels.forEach(Consumer { regionViewModel: RegionViewModel ->
            regionViewModel.removeState(
                stateViewModel
            )
        })

        if (automaton.states.size == 1 && wasStartState) {
            systemViewModel.startState = null
        }
        systemViewModel.updateTarget()
        automaton.removeState(stateViewModel.target!!)
        geckoViewModel.deleteViewModelElement(stateViewModel)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return RestoreStateViewModelElementAction(geckoViewModel, stateViewModel, systemViewModel, wasStartState)
    }


    override val target
        get() = stateViewModel
}
