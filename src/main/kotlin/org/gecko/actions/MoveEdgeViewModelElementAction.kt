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
    val gModel: GModel
    val editorViewModel: EditorViewModel
    val Edge: Edge
    val elementScalerBlock: ElementScalerBlock
    var delta: Point2D? = null
    var state: State? = null
    var previousState: State? = null
    var Contract: Contract? = null
    var previousContract: Contract? = null

    internal constructor(
        gModel: GModel,
        Edge: Edge,
        elementScalerBlock: ElementScalerBlock,
        delta: Point2D?
    ) {
        this.gModel = gModel
        this.editorViewModel = gModel.currentEditor!!
        this.Edge = Edge
        this.elementScalerBlock = elementScalerBlock
        this.delta = delta
    }

    internal constructor(
        gModel: GModel,
        Edge: Edge,
        elementScalerBlock: ElementScalerBlock,
        state: State?,
        Contract: Contract?
    ) {
        this.gModel = gModel
        this.editorViewModel = gModel.currentEditor!!
        this.Edge = Edge
        this.elementScalerBlock = elementScalerBlock
        this.state = state
        this.Contract = Contract
    }

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        previousState = if (elementScalerBlock.index == 0) Edge.source else Edge.destination
        if (state == null) {
            state = attemptRelocation()
            if (state == null || state == previousState) {
                Edge.setBindings()
                return false
            }
        }

        if (elementScalerBlock.index == 0) {
            Edge.source = (state!!)
            previousContract = Edge.contract
            Edge.contract = Contract
        } else {
            Edge.destination = (state!!)
        }

        elementScalerBlock.updatePosition()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createMoveEdgeViewModelElementAction(
            Edge,
            elementScalerBlock,
            previousState,
            previousContract
        )
    }

    fun attemptRelocation(): State? {
        return getStateViewModelAt(elementScalerBlock.layoutPosition.add(delta))
    }

    fun getStateViewModelAt(point: Point2D): State? {
        for (state in editorViewModel.currentSystem.automaton.states) {
            if (
                point.x > state.position.x &&
                    point.x < state.position.x + state.size.x &&
                    point.y > state.position.y &&
                    point.y < state.position.y + state.size.y
            ) {
                return state
            }
        }
        return null
    }
}
