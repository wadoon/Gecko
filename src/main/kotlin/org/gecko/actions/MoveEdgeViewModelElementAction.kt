package org.gecko.actions

import javafx.geometry.Point2D
import org.gecko.exceptions.GeckoException
import org.gecko.view.views.viewelement.decorator.ElementScalerBlock
import org.gecko.viewmodel.*

/**
 * A concrete representation of an [Action] that moves an {link EdgeViewModelElement} with a given
 * [delta value][Point2D].
 */
class MoveEdgeViewModelElementAction : Action {
    val geckoViewModel: GeckoViewModel
    val editorViewModel: EditorViewModel
    val edgeViewModel: EdgeViewModel
    val elementScalerBlock: ElementScalerBlock
    var delta: Point2D? = null
    var stateViewModel: StateViewModel? = null
    var previousStateViewModel: StateViewModel? = null
    var contractViewModel: ContractViewModel? = null
    var previousContractViewModel: ContractViewModel? = null

    internal constructor(
        geckoViewModel: GeckoViewModel, edgeViewModel: EdgeViewModel, elementScalerBlock: ElementScalerBlock,
        delta: Point2D?
    ) {
        this.geckoViewModel = geckoViewModel
        this.editorViewModel = geckoViewModel.currentEditor!!
        this.edgeViewModel = edgeViewModel
        this.elementScalerBlock = elementScalerBlock
        this.delta = delta
    }

    internal constructor(
        geckoViewModel: GeckoViewModel, edgeViewModel: EdgeViewModel, elementScalerBlock: ElementScalerBlock,
        stateViewModel: StateViewModel?, contractViewModel: ContractViewModel?
    ) {
        this.geckoViewModel = geckoViewModel
        this.editorViewModel = geckoViewModel.currentEditor!!
        this.edgeViewModel = edgeViewModel
        this.elementScalerBlock = elementScalerBlock
        this.stateViewModel = stateViewModel
        this.contractViewModel = contractViewModel
    }


    @Throws(GeckoException::class)
    override fun run(): Boolean {
        previousStateViewModel =
            if (elementScalerBlock.index == 0) edgeViewModel.source else edgeViewModel.destination
        if (stateViewModel == null) {
            stateViewModel = attemptRelocation()
            if (stateViewModel == null || stateViewModel == previousStateViewModel) {
                edgeViewModel.setBindings()
                return false
            }
        }

        if (elementScalerBlock.index == 0) {
            edgeViewModel.source = (stateViewModel!!)
            previousContractViewModel = edgeViewModel.contract
            edgeViewModel.contract = contractViewModel
        } else {
            edgeViewModel.destination = (stateViewModel!!)
        }

        elementScalerBlock.updatePosition()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createMoveEdgeViewModelElementAction(
            edgeViewModel, elementScalerBlock,
            previousStateViewModel, previousContractViewModel
        )
    }

    fun attemptRelocation(): StateViewModel? {
        return getStateViewModelAt(elementScalerBlock.layoutPosition.add(delta))
    }

    fun getStateViewModelAt(point: Point2D): StateViewModel? {
        for (state in editorViewModel.currentSystem.automaton.states) {
            if (point.x > state.position.x && point.x < state.position.x + state.size.x && point.y > state.position.y && point.y < state.position.y + state.size.y) {
                return state
            }
        }
        return null
    }
}
