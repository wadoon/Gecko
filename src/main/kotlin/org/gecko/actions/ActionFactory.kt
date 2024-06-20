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
    fun createChangeColorRegionViewModelElementAction(
        regionViewModel: RegionViewModel, color: Color?
    ): ChangeColorRegionViewModelElementAction {
        return ChangeColorRegionViewModelElementAction(regionViewModel, color)
    }

    fun createChangeContractEdgeViewModelAction(viewModel: EdgeViewModel, contract: ContractViewModel?): Action {
        return ChangeContractEdgeViewModelAction(viewModel, contract)
    }

    fun createChangeInvariantViewModelElementAction(
        regionViewModel: RegionViewModel, newInvariant: String
    ): ChangeInvariantViewModelElementAction {
        return ChangeInvariantViewModelElementAction(regionViewModel, newInvariant)
    }

    fun createChangeKindAction(edgeViewModel: EdgeViewModel, kind: Kind?): ChangeKindEdgeViewModelAction {
        return ChangeKindEdgeViewModelAction(edgeViewModel, kind)
    }

    fun createChangePreconditionViewModelElementAction(
        contractViewModel: ContractViewModel, newPrecondition: String
    ): ChangePreconditionViewModelElementAction {
        return ChangePreconditionViewModelElementAction(contractViewModel, newPrecondition)
    }

    fun createChangePostconditionViewModelElementAction(
        contractViewModel: ContractViewModel, newPostcondition: String
    ): ChangePostconditionViewModelElementAction {
        return ChangePostconditionViewModelElementAction(contractViewModel, newPostcondition)
    }

    fun createChangeTypePortViewModelElementAction(
        portViewModel: PortViewModel, newType: String
    ): ChangeTypePortViewModelElementAction {
        return ChangeTypePortViewModelElementAction(portViewModel, newType)
    }

    fun createChangeVariableValuePortViewModelAction(
        portViewModel: PortViewModel, newValue: String?
    ): ChangeVariableValuePortViewModelAction {
        return ChangeVariableValuePortViewModelAction(portViewModel, newValue)
    }

    fun createChangeVisibilityPortViewModelAction(
        portViewModel: PortViewModel, visibility: Visibility
    ): ChangeVisibilityPortViewModelAction {
        return ChangeVisibilityPortViewModelAction(geckoViewModel, portViewModel, visibility)
    }

    fun createChangeVisibilityPortViewModelAction(
        portViewModel: PortViewModel, visibility: Visibility, systemConnectionDeleteActionGroup: Action?
    ): ChangeVisibilityPortViewModelAction {
        return ChangeVisibilityPortViewModelAction(
            geckoViewModel, portViewModel, visibility,
            systemConnectionDeleteActionGroup
        )
    }

    /*
    fun createCopyPositionableViewModelElementAction(): CopyPositionableViewModelElementAction {
        return CopyPositionableViewModelElementAction(geckoViewModel)
    }*/

    fun createCreateContractViewModelElementAction(
        stateViewModel: StateViewModel
    ): CreateContractViewModelElementAction {
        return CreateContractViewModelElementAction(geckoViewModel, stateViewModel)
    }

    fun createCreateEdgeViewModelElementAction(
        source: StateViewModel?, destination: StateViewModel?
    ): CreateEdgeViewModelElementAction {
        return CreateEdgeViewModelElementAction(geckoViewModel, source!!, destination!!)
    }

    fun createCreatePortViewModelElementAction(parentSystem: SystemViewModel): CreatePortViewModelElementAction {
        return CreatePortViewModelElementAction(geckoViewModel, parentSystem)
    }

    fun createCreateRegionViewModelElementAction(
        position: Point2D?, size: Point2D?, color: Color?
    ): CreateRegionViewModelElementAction {
        return CreateRegionViewModelElementAction(geckoViewModel, position, size, color)
    }

    fun createCreateStateViewModelElementAction(position: Point2D): CreateStateViewModelElementAction {
        return CreateStateViewModelElementAction(geckoViewModel, geckoViewModel.currentEditor!!, position)
    }

    fun createCreateSystemConnectionViewModelElementAction(
        source: PortViewModel, destination: PortViewModel
    ): CreateSystemConnectionViewModelElementAction {
        return CreateSystemConnectionViewModelElementAction(geckoViewModel, source, destination)
    }

    fun createCreateSystemViewModelElementAction(position: Point2D): CreateSystemViewModelElementAction {
        return CreateSystemViewModelElementAction(geckoViewModel, position)
    }

    fun createCreateVariableAction(position: Point2D?): CreateVariableAction {
        return CreateVariableAction(geckoViewModel, position)
    }

    fun createDeleteContractViewModelAction(
        parent: StateViewModel, contractViewModel: ContractViewModel?
    ): DeleteContractViewModelAction {
        return DeleteContractViewModelAction(parent, contractViewModel)
    }

    fun createDeletePositionableViewModelElementAction(
        element: PositionableViewModelElement
    ): DeletePositionableViewModelElementAction {
        return DeletePositionableViewModelElementAction(geckoViewModel, element)
    }

    fun createDeletePositionableViewModelElementAction(
        elements: Set<PositionableViewModelElement>?
    ): DeletePositionableViewModelElementAction {
        return DeletePositionableViewModelElementAction(geckoViewModel, elements)
    }

    fun createDeletePositionableViewModelElementAction(): DeletePositionableViewModelElementAction {
        return DeletePositionableViewModelElementAction(
            geckoViewModel,
            geckoViewModel.currentEditor!!.selectionManager.currentSelection
        )
    }

    fun createMoveBlockViewModelElementAction(delta: Point2D): MoveBlockViewModelElementAction {
        return MoveBlockViewModelElementAction(geckoViewModel.currentEditor!!, delta)
    }

    fun createMoveBlockViewModelElementAction(
        elementsToMove: Set<PositionableViewModelElement>?, delta: Point2D
    ): MoveBlockViewModelElementAction {
        return MoveBlockViewModelElementAction(geckoViewModel.currentEditor!!, elementsToMove, delta)
    }

    fun createMoveEdgeViewModelElementAction(
        edgeViewModel: EdgeViewModel, elementScalerBlock: ElementScalerBlock, delta: Point2D?
    ): MoveEdgeViewModelElementAction {
        return MoveEdgeViewModelElementAction(geckoViewModel, edgeViewModel, elementScalerBlock, delta)
    }

    fun createMoveEdgeViewModelElementAction(
        edgeViewModel: EdgeViewModel, elementScalerBlock: ElementScalerBlock, stateViewModel: StateViewModel?,
        contractViewModel: ContractViewModel?
    ): MoveEdgeViewModelElementAction {
        return MoveEdgeViewModelElementAction(
            geckoViewModel, edgeViewModel, elementScalerBlock, stateViewModel,
            contractViewModel
        )
    }

    fun createMoveSystemConnectionViewModelElementAction(
        systemConnectionViewModel: SystemConnectionViewModel?, elementScalerBlock: ElementScalerBlock?, delta: Point2D?
    ): MoveSystemConnectionViewModelElementAction {
        return MoveSystemConnectionViewModelElementAction(
            geckoViewModel, systemConnectionViewModel,
            elementScalerBlock!!, delta
        )
    }

    fun createMoveSystemConnectionViewModelElementAction(
        systemConnectionViewModel: SystemConnectionViewModel?, elementScalerBlock: ElementScalerBlock?,
        portViewModel: PortViewModel?, isVariableBlock: Boolean
    ): MoveSystemConnectionViewModelElementAction {
        return MoveSystemConnectionViewModelElementAction(
            geckoViewModel, systemConnectionViewModel,
            elementScalerBlock!!, portViewModel, isVariableBlock
        )
    }

    /*fun createPastePositionableViewModelElementAction(center: Point2D?): PastePositionableViewModelElementAction {
        return PastePositionableViewModelElementAction(geckoViewModel, center!!)
    }*/

    fun createRenameViewModelElementAction(renamable: Renamable, name: String): RenameViewModelElementAction {
        return RenameViewModelElementAction(renamable, name)
    }

    fun createRestoreContractViewModelElementAction(
        parent: StateViewModel, contractViewModel: ContractViewModel?, edgesWithContract: Set<EdgeViewModel>?
    ): RestoreContractViewModelElementAction {
        return RestoreContractViewModelElementAction(parent, contractViewModel, edgesWithContract)
    }

    fun createScaleBlockViewModelElementAction(
        blockViewModelElement: BlockViewModelElement, elementScalerBlock: ElementScalerBlock?, position: Point2D?,
        size: Point2D?, isPreviousScale: Boolean
    ): ScaleBlockViewModelElementAction {
        return ScaleBlockViewModelElementAction(
            geckoViewModel.currentEditor!!, blockViewModelElement,
            elementScalerBlock, position, size, isPreviousScale
        )
    }

    fun createScaleBlockViewModelElementAction(
        blockViewModelElement: BlockViewModelElement, elementScalerBlock: ElementScalerBlock?
    ): ScaleBlockViewModelElementAction {
        return ScaleBlockViewModelElementAction(
            geckoViewModel.currentEditor!!, blockViewModelElement,
            elementScalerBlock
        )
    }

    fun createFocusPositionableViewModelElementAction(
        element: PositionableViewModelElement
    ): FocusPositionableViewModelElementAction {
        return FocusPositionableViewModelElementAction(geckoViewModel.currentEditor!!, element)
    }

    fun createModifyEdgeViewModelPriorityAction(
        edgeViewModel: EdgeViewModel, priority: Int
    ): ModifyEdgeViewModelPriorityAction {
        return ModifyEdgeViewModelPriorityAction(edgeViewModel, priority)
    }

    fun createSelectAction(element: PositionableViewModelElement, newSelection: Boolean): SelectAction {
        return createSelectAction(setOf(element), newSelection)
    }

    fun createSelectAction(elements: Set<PositionableViewModelElement>?, newSelection: Boolean): SelectAction {
        return SelectAction(geckoViewModel.currentEditor!!, elements, newSelection)
    }

    fun createDeselectAction(): DeselectAction {
        return DeselectAction(geckoViewModel.currentEditor!!)
    }

    fun createSelectionHistoryBackAction(): SelectionHistoryBackAction {
        return SelectionHistoryBackAction(geckoViewModel.currentEditor!!.selectionManager)
    }

    fun createSelectionHistoryForwardAction(): SelectionHistoryForwardAction {
        return SelectionHistoryForwardAction(geckoViewModel.currentEditor!!.selectionManager)
    }

    fun createSelectToolAction(tool: ToolType): SelectToolAction {
        return SelectToolAction(geckoViewModel.currentEditor!!, tool)
    }

    fun createSetStartStateViewModelElementAction(stateViewModel: StateViewModel): SetStartStateViewModelElementAction {
        return SetStartStateViewModelElementAction(geckoViewModel, stateViewModel, true)
    }

    fun createViewSwitchAction(systemViewModel: SystemViewModel?, isAutomaton: Boolean): ViewSwitchAction {
        return ViewSwitchAction(geckoViewModel, systemViewModel, isAutomaton)
    }

    fun createZoomAction(pivot: Point2D, factor: Double): ZoomAction {
        return ZoomAction(geckoViewModel.currentEditor!!, pivot, factor)
    }

    fun createZoomCenterAction(factor: Double): ZoomCenterAction {
        return ZoomCenterAction(geckoViewModel.currentEditor!!, factor)
    }

    fun createChangeCodeSystemViewModelAction(
        systemViewModel: SystemViewModel, newCode: String
    ): ChangeCodeSystemViewModelAction {
        return ChangeCodeSystemViewModelAction(systemViewModel, newCode)
    }

    /*fun createCutPositionableViewModelElementAction(): CutPositionableViewModelElementAction {
        return CutPositionableViewModelElementAction(geckoViewModel)
    }*/
}
