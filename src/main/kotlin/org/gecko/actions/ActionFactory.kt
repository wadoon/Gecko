package org.gecko.actions

import javafx.geometry.Point2D
import javafx.scene.paint.Color
import org.gecko.tools.ToolType
import org.gecko.view.views.viewelement.decorator.ElementScalerBlock
import org.gecko.viewmodel.*

/**
 * Represents a factory for actions. Provides a method for the creation of each subtype of [Action].
 */
class ActionFactory(val gModel: GModel) {
    fun createChangeColorRegion(Region: Region, color: Color?) =
        ChangeColorRegionViewModelElementAction(Region, color)

    fun createChangeContractEdge(viewModel: Edge, contract: Contract?) =
        ChangeContractEdgeViewModelAction(viewModel, contract)

    fun createChangeInvariantViewModelElementAction(
        Region: Region, newInvariant: String
    ) = ChangeInvariantViewModelElementAction(Region, newInvariant)

    fun createChangeKindAction(Edge: Edge, kind: Kind?) =
        ChangeKindEdgeViewModelAction(Edge, kind)

    fun createChangePreconditionViewModelElementAction(Contract: Contract, newPrecondition: String) =
        ChangePreconditionViewModelElementAction(Contract, newPrecondition)

    fun createChangePostconditionViewModelElementAction(
        Contract: Contract, newPostcondition: String
    ) = ChangePostconditionViewModelElementAction(Contract, newPostcondition)

    fun createChangeTypePortViewModelElementAction(
        Port: Port, newType: String
    ) = ChangeTypePortViewModelElementAction(Port, newType)

    fun createChangeVariableValuePortViewModelAction(
        Port: Port, newValue: String?
    ) = ChangeVariableValuePortViewModelAction(Port, newValue)

    fun changeVisibility(
        Port: Port, visibility: Visibility
    ) = ChangeVisibilityPortViewModelAction(gModel, Port, visibility)

    fun changeVisibility(
        Port: Port, visibility: Visibility, systemConnectionDeleteActionGroup: Action?
    ): ChangeVisibilityPortViewModelAction {
        return ChangeVisibilityPortViewModelAction(
            gModel, Port, visibility, systemConnectionDeleteActionGroup
        )
    }

    /*
    fun createCopyPositionableViewModelElementAction(): CopyPositionableViewModelElementAction {
        return CopyPositionableViewModelElementAction(geckoViewModel)
    }*/

    fun createCreateContractViewModelElementAction(
        state: State
    ) = CreateContractViewModelElementAction(gModel, state)

    fun createCreateEdgeViewModelElementAction(
        source: State?, destination: State?
    ) = CreateEdgeViewModelElementAction(gModel, source!!, destination!!)

    fun createCreatePortViewModelElementAction(parentSystem: System) =
        CreatePortViewModelElementAction(gModel, parentSystem)

    fun createRegion(position: Point2D, size: Point2D, color: Color?) =
        CreateRegionViewModelElementAction(gModel, position, size, color)

    fun createState(position: Point2D) =
        CreateStateViewModelElementAction(gModel, gModel.currentEditor!!, position)

    fun createCreateSystemConnection(source: Port, destination: Port) =
        CreateSystemConnectionViewModelElementAction(gModel, source, destination)

    fun createSystem(position: Point2D) =
        CreateSystemViewModelElementAction(gModel, position)

    fun createVariable(position: Point2D) = CreateVariableAction(gModel, position)

    fun createDeleteContractViewModelAction(parent: State, Contract: Contract?) =
        DeleteContractViewModelAction(parent, Contract)

    fun createDeleteAction(
        element: PositionableElement
    ) = DeletePositionableViewModelElementAction(gModel, element)

    fun createDeleteAction(elements: Set<PositionableElement>) =
        DeletePositionableViewModelElementAction(gModel, elements)

    fun createDeleteAction() = DeletePositionableViewModelElementAction(
        gModel,
        gModel.currentEditor!!.selectionManager.currentSelection
    )

    fun createMoveBlockViewModelElementAction(delta: Point2D) =
        MoveBlockViewModelElementAction(gModel.currentEditor!!, delta)

    fun createMoveBlockViewModelElementAction(
        elementsToMove: Set<PositionableElement>?, delta: Point2D
    ) = MoveBlockViewModelElementAction(gModel.currentEditor!!, elementsToMove, delta)

    fun createMoveEdgeViewModelElementAction(
        Edge: Edge, elementScalerBlock: ElementScalerBlock, delta: Point2D?
    ) = MoveEdgeViewModelElementAction(gModel, Edge, elementScalerBlock, delta)

    fun createMoveEdgeViewModelElementAction(
        Edge: Edge,
        elementScalerBlock: ElementScalerBlock,
        state: State?,
        Contract: Contract?
    ) = MoveEdgeViewModelElementAction(
        gModel, Edge, elementScalerBlock, state, Contract
    )

    fun createMoveSystemConnectionViewModelElementAction(
        systemConnectionViewModel: SystemConnection?, elementScalerBlock: ElementScalerBlock?, delta: Point2D?
    ): MoveSystemConnectionViewModelElementAction {
        return MoveSystemConnectionViewModelElementAction(
            gModel, systemConnectionViewModel, elementScalerBlock!!, delta
        )
    }

    fun createMoveSystemConnectionViewModelElementAction(
        systemConnectionViewModel: SystemConnection?,
        elementScalerBlock: ElementScalerBlock?,
        Port: Port?,
        isVariableBlock: Boolean
    ): MoveSystemConnectionViewModelElementAction {
        return MoveSystemConnectionViewModelElementAction(
            gModel, systemConnectionViewModel, elementScalerBlock!!, Port, isVariableBlock
        )
    }

    /*fun createPastePositionableViewModelElementAction(center: Point2D?): PastePositionableViewModelElementAction {
        return PastePositionableViewModelElementAction(geckoViewModel, center!!)
    }*/

    fun createRenameViewModelElementAction(renamable: Renamable, name: String) =
        RenameViewModelElementAction(renamable, name)

    fun createRestoreContractViewModelElementAction(
        parent: State, Contract: Contract?, edgesWithContract: Set<Edge>?
    ) = RestoreContractViewModelElementAction(parent, Contract, edgesWithContract)

    fun createScaleBlockViewModelElementAction(
        blockViewModelElement: BlockElement,
        elementScalerBlock: ElementScalerBlock?,
        position: Point2D?,
        size: Point2D?,
        isPreviousScale: Boolean
    ) = ScaleBlockViewModelElementAction(
        gModel.currentEditor!!, blockViewModelElement, elementScalerBlock, position, size, isPreviousScale
    )

    fun createScaleBlockViewModelElementAction(
        blockViewModelElement: BlockElement, elementScalerBlock: ElementScalerBlock?) = ScaleBlockViewModelElementAction(
        gModel.currentEditor!!, blockViewModelElement, elementScalerBlock
    )

    fun createFocusPositionableViewModelElementAction(
        element: PositionableElement
    ) = FocusPositionableViewModelElementAction(gModel.currentEditor!!, element)

    fun createModifyEdgeViewModelPriorityAction(
        Edge: Edge, priority: Int
    ) = ModifyEdgeViewModelPriorityAction(Edge, priority)

    fun createSelectAction(element: PositionableElement, newSelection: Boolean) =
        createSelectAction(setOf(element), newSelection)

    fun createSelectAction(elements: Iterable<PositionableElement>, newSelection: Boolean) =
        SelectAction(gModel.currentEditor!!, elements, newSelection)

    fun createDeselectAction() = DeselectAction(gModel.currentEditor!!)

    fun createSelectionHistoryBackAction() = SelectionHistoryBackAction(gModel.currentEditor!!.selectionManager)

    fun createSelectionHistoryForwardAction() =
        SelectionHistoryForwardAction(gModel.currentEditor!!.selectionManager)

    fun createSelectToolAction(tool: ToolType) = SelectToolAction(gModel.currentEditor!!, tool)
    fun createSetStartStateViewModelElementAction(state: State) =
        SetStartStateViewModelElementAction(gModel, state, true)

    fun createViewSwitchAction(System: System?, isAutomaton: Boolean) =
        ViewSwitchAction(gModel, System, isAutomaton)

    fun createZoomAction(pivot: Point2D, factor: Double) = ZoomAction(gModel.currentEditor!!, pivot, factor)
    fun createZoomCenterAction(factor: Double) = ZoomCenterAction(gModel.currentEditor!!, factor)
    fun createChangeCodeSystemViewModelAction(System: System, newCode: String) =
        ChangeCodeSystemViewModelAction(System, newCode)

    /*fun createCutPositionableViewModelElementAction(): CutPositionableViewModelElementAction {
        return CutPositionableViewModelElementAction(geckoViewModel)
    }*/
}
