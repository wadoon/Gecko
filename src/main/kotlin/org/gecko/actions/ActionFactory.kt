package org.gecko.actions

import javafx.geometry.Point2D
import javafx.scene.paint.Color
import org.gecko.tools.ToolType
import org.gecko.view.views.viewelement.decorator.ElementScalerBlock
import org.gecko.viewmodel.*

/**
 * Represents a factory for actions. Provides a method for the creation of each subtype of [Action].
 */
class ActionFactory(val geckoViewModel: GeckoViewModel) {
    fun createChangeColorRegion(regionViewModel: RegionViewModel, color: Color?) =
        ChangeColorRegionViewModelElementAction(regionViewModel, color)

    fun createChangeContractEdge(viewModel: EdgeViewModel, contract: ContractViewModel?) =
        ChangeContractEdgeViewModelAction(viewModel, contract)

    fun createChangeInvariantViewModelElementAction(
        regionViewModel: RegionViewModel, newInvariant: String
    ) = ChangeInvariantViewModelElementAction(regionViewModel, newInvariant)

    fun createChangeKindAction(edgeViewModel: EdgeViewModel, kind: Kind?) =
        ChangeKindEdgeViewModelAction(edgeViewModel, kind)

    fun createChangePreconditionViewModelElementAction(contractViewModel: ContractViewModel, newPrecondition: String) =
        ChangePreconditionViewModelElementAction(contractViewModel, newPrecondition)

    fun createChangePostconditionViewModelElementAction(
        contractViewModel: ContractViewModel, newPostcondition: String
    ) = ChangePostconditionViewModelElementAction(contractViewModel, newPostcondition)

    fun createChangeTypePortViewModelElementAction(
        portViewModel: PortViewModel, newType: String
    ) = ChangeTypePortViewModelElementAction(portViewModel, newType)

    fun createChangeVariableValuePortViewModelAction(
        portViewModel: PortViewModel, newValue: String?
    ) = ChangeVariableValuePortViewModelAction(portViewModel, newValue)

    fun changeVisibility(
        portViewModel: PortViewModel, visibility: Visibility
    ) = ChangeVisibilityPortViewModelAction(geckoViewModel, portViewModel, visibility)

    fun changeVisibility(
        portViewModel: PortViewModel, visibility: Visibility, systemConnectionDeleteActionGroup: Action?
    ): ChangeVisibilityPortViewModelAction {
        return ChangeVisibilityPortViewModelAction(
            geckoViewModel, portViewModel, visibility, systemConnectionDeleteActionGroup
        )
    }

    /*
    fun createCopyPositionableViewModelElementAction(): CopyPositionableViewModelElementAction {
        return CopyPositionableViewModelElementAction(geckoViewModel)
    }*/

    fun createCreateContractViewModelElementAction(
        stateViewModel: StateViewModel
    ) = CreateContractViewModelElementAction(geckoViewModel, stateViewModel)

    fun createCreateEdgeViewModelElementAction(
        source: StateViewModel?, destination: StateViewModel?
    ) = CreateEdgeViewModelElementAction(geckoViewModel, source!!, destination!!)

    fun createCreatePortViewModelElementAction(parentSystem: SystemViewModel) =
        CreatePortViewModelElementAction(geckoViewModel, parentSystem)

    fun createRegion(position: Point2D, size: Point2D, color: Color?) =
        CreateRegionViewModelElementAction(geckoViewModel, position, size, color)

    fun createState(position: Point2D) =
        CreateStateViewModelElementAction(geckoViewModel, geckoViewModel.currentEditor!!, position)

    fun createCreateSystemConnection(source: PortViewModel, destination: PortViewModel) =
        CreateSystemConnectionViewModelElementAction(geckoViewModel, source, destination)

    fun createCreateSystemViewModelElementAction(position: Point2D) =
        CreateSystemViewModelElementAction(geckoViewModel, position)

    fun createVariable(position: Point2D) = CreateVariableAction(geckoViewModel, position)

    fun createDeleteContractViewModelAction(parent: StateViewModel, contractViewModel: ContractViewModel?) =
        DeleteContractViewModelAction(parent, contractViewModel)

    fun createDeleteAction(
        element: PositionableViewModelElement
    ) = DeletePositionableViewModelElementAction(geckoViewModel, element)

    fun createDeleteAction(elements: Set<PositionableViewModelElement>) =
        DeletePositionableViewModelElementAction(geckoViewModel, elements)

    fun createDeleteAction() = DeletePositionableViewModelElementAction(
        geckoViewModel,
        geckoViewModel.currentEditor!!.selectionManager.currentSelection
    )

    fun createMoveBlockViewModelElementAction(delta: Point2D) =
        MoveBlockViewModelElementAction(geckoViewModel.currentEditor!!, delta)

    fun createMoveBlockViewModelElementAction(
        elementsToMove: Set<PositionableViewModelElement>?, delta: Point2D
    ) = MoveBlockViewModelElementAction(geckoViewModel.currentEditor!!, elementsToMove, delta)

    fun createMoveEdgeViewModelElementAction(
        edgeViewModel: EdgeViewModel, elementScalerBlock: ElementScalerBlock, delta: Point2D?
    ) = MoveEdgeViewModelElementAction(geckoViewModel, edgeViewModel, elementScalerBlock, delta)

    fun createMoveEdgeViewModelElementAction(
        edgeViewModel: EdgeViewModel,
        elementScalerBlock: ElementScalerBlock,
        stateViewModel: StateViewModel?,
        contractViewModel: ContractViewModel?
    ) = MoveEdgeViewModelElementAction(
        geckoViewModel, edgeViewModel, elementScalerBlock, stateViewModel, contractViewModel
    )

    fun createMoveSystemConnectionViewModelElementAction(
        systemConnectionViewModel: SystemConnectionViewModel?, elementScalerBlock: ElementScalerBlock?, delta: Point2D?
    ): MoveSystemConnectionViewModelElementAction {
        return MoveSystemConnectionViewModelElementAction(
            geckoViewModel, systemConnectionViewModel, elementScalerBlock!!, delta
        )
    }

    fun createMoveSystemConnectionViewModelElementAction(
        systemConnectionViewModel: SystemConnectionViewModel?,
        elementScalerBlock: ElementScalerBlock?,
        portViewModel: PortViewModel?,
        isVariableBlock: Boolean
    ): MoveSystemConnectionViewModelElementAction {
        return MoveSystemConnectionViewModelElementAction(
            geckoViewModel, systemConnectionViewModel, elementScalerBlock!!, portViewModel, isVariableBlock
        )
    }

    /*fun createPastePositionableViewModelElementAction(center: Point2D?): PastePositionableViewModelElementAction {
        return PastePositionableViewModelElementAction(geckoViewModel, center!!)
    }*/

    fun createRenameViewModelElementAction(renamable: Renamable, name: String) =
        RenameViewModelElementAction(renamable, name)

    fun createRestoreContractViewModelElementAction(
        parent: StateViewModel, contractViewModel: ContractViewModel?, edgesWithContract: Set<EdgeViewModel>?
    ) = RestoreContractViewModelElementAction(parent, contractViewModel, edgesWithContract)

    fun createScaleBlockViewModelElementAction(
        blockViewModelElement: BlockViewModelElement,
        elementScalerBlock: ElementScalerBlock?,
        position: Point2D?,
        size: Point2D?,
        isPreviousScale: Boolean
    ) = ScaleBlockViewModelElementAction(
        geckoViewModel.currentEditor!!, blockViewModelElement, elementScalerBlock, position, size, isPreviousScale
    )

    fun createScaleBlockViewModelElementAction(
        blockViewModelElement: BlockViewModelElement, elementScalerBlock: ElementScalerBlock?) = ScaleBlockViewModelElementAction(
        geckoViewModel.currentEditor!!, blockViewModelElement, elementScalerBlock
    )

    fun createFocusPositionableViewModelElementAction(
        element: PositionableViewModelElement
    ) = FocusPositionableViewModelElementAction(geckoViewModel.currentEditor!!, element)

    fun createModifyEdgeViewModelPriorityAction(
        edgeViewModel: EdgeViewModel, priority: Int
    ) = ModifyEdgeViewModelPriorityAction(edgeViewModel, priority)

    fun createSelectAction(element: PositionableViewModelElement, newSelection: Boolean) =
        createSelectAction(setOf(element), newSelection)

    fun createSelectAction(elements: Set<PositionableViewModelElement>?, newSelection: Boolean) =
        SelectAction(geckoViewModel.currentEditor!!, elements, newSelection)

    fun createDeselectAction() = DeselectAction(geckoViewModel.currentEditor!!)

    fun createSelectionHistoryBackAction() = SelectionHistoryBackAction(geckoViewModel.currentEditor!!.selectionManager)

    fun createSelectionHistoryForwardAction() =
        SelectionHistoryForwardAction(geckoViewModel.currentEditor!!.selectionManager)

    fun createSelectToolAction(tool: ToolType) = SelectToolAction(geckoViewModel.currentEditor!!, tool)
    fun createSetStartStateViewModelElementAction(stateViewModel: StateViewModel) =
        SetStartStateViewModelElementAction(geckoViewModel, stateViewModel, true)

    fun createViewSwitchAction(systemViewModel: SystemViewModel?, isAutomaton: Boolean) =
        ViewSwitchAction(geckoViewModel, systemViewModel, isAutomaton)

    fun createZoomAction(pivot: Point2D, factor: Double) = ZoomAction(geckoViewModel.currentEditor!!, pivot, factor)
    fun createZoomCenterAction(factor: Double) = ZoomCenterAction(geckoViewModel.currentEditor!!, factor)
    fun createChangeCodeSystemViewModelAction(systemViewModel: SystemViewModel, newCode: String) =
        ChangeCodeSystemViewModelAction(systemViewModel, newCode)

    /*fun createCutPositionableViewModelElementAction(): CutPositionableViewModelElementAction {
        return CutPositionableViewModelElementAction(geckoViewModel)
    }*/
}
