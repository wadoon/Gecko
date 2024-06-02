package org.gecko.actions

import javafx.geometry.Point2D
import org.gecko.exceptions.GeckoException
import org.gecko.model.*
import org.gecko.view.views.viewelement.decorator.ElementScalerBlock
import org.gecko.viewmodel.*

/**
 * A concrete representation of an [Action] that moves a [SystemConnectionViewModel] with a given
 * [delta value][Point2D].
 */
class MoveSystemConnectionViewModelElementAction : Action {
    val geckoViewModel: GeckoViewModel
    val editorViewModel: EditorViewModel
    val systemConnectionViewModel: SystemConnectionViewModel?
    val elementScalerBlock: ElementScalerBlock
    var delta: Point2D? = null
    var portViewModel: PortViewModel? = null
    var previousPortViewModel: PortViewModel? = null
    var isVariableBlock = false
    var wasVariableBlock = false

    internal constructor(
        geckoViewModel: GeckoViewModel,
        systemConnectionViewModel: SystemConnectionViewModel?,
        elementScalerBlock: ElementScalerBlock,
        delta: Point2D?
    ) {
        this.geckoViewModel = geckoViewModel
        this.editorViewModel = geckoViewModel.currentEditor!!
        this.systemConnectionViewModel = systemConnectionViewModel
        this.elementScalerBlock = elementScalerBlock
        this.delta = delta
    }

    internal constructor(
        geckoViewModel: GeckoViewModel,
        systemConnectionViewModel: SystemConnectionViewModel?,
        elementScalerBlock: ElementScalerBlock,
        portViewModel: PortViewModel?,
        isVariableBlock: Boolean
    ) {
        this.geckoViewModel = geckoViewModel
        this.editorViewModel = geckoViewModel.currentEditor!!
        this.systemConnectionViewModel = systemConnectionViewModel
        this.elementScalerBlock = elementScalerBlock
        this.portViewModel = portViewModel
        this.isVariableBlock = isVariableBlock
    }

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        if (portViewModel == null) {
            portViewModel = portViewModelAt
            if (portViewModel == null) {
                return false
            }
        }

        val parentSystem = geckoViewModel.currentEditor!!.currentSystem
        var sourcePortViewModel = systemConnectionViewModel!!.source
        var destinationPortViewModel = systemConnectionViewModel.destination

        if (isSource) {
            if (portViewModel == sourcePortViewModel) {
                return false
            }
            wasVariableBlock = geckoViewModel.getSystemViewModelWithPort(sourcePortViewModel) == parentSystem
            sourcePortViewModel = portViewModel!!
        } else {
            if (portViewModel == destinationPortViewModel) {
                return false
            }
            wasVariableBlock = geckoViewModel.getSystemViewModelWithPort(destinationPortViewModel) == parentSystem
            destinationPortViewModel = portViewModel!!
        }

        val sourceSystem = geckoViewModel.getSystemViewModelWithPort(sourcePortViewModel)
        val destinationSystem = geckoViewModel.getSystemViewModelWithPort(destinationPortViewModel)

        if (!isConnectingAllowed(
                sourcePortViewModel,
                destinationPortViewModel,
                sourceSystem,
                destinationSystem,
                parentSystem,
                systemConnectionViewModel
            )
        ) {
            return false
        }

        val newPositionProperty = if (isVariableBlock) {
            calculateEndPortPosition(portViewModel!!.position, portViewModel!!.size, portViewModel!!.visibility, false)

            /*portViewModel.getPositionProperty().addListener((observable, oldValue, newValue) ->
                    newPositionProperty.setValue(
                            calculateEndPortPosition(portViewModel.getPosition(), portViewModel.getSize(),
                                    portViewModel.getVisibility(), false)));*/
        } else {
            calculateEndPortPosition(
                portViewModel!!.systemPortPositionProperty.value,
                portViewModel!!.systemPortSizeProperty.value,
                portViewModel!!.visibility,
                true
            )

            /*            portViewModel.getSystemPortPositionProperty()
                    .addListener((observable, oldValue, newValue) -> newPositionProperty.setValue(
                            calculateEndPortPosition(portViewModel.getSystemPortPositionProperty().getValue(),
                                    portViewModel.getSystemPortSizeProperty().getValue(), portViewModel.getVisibility(), true)));*/
        }

        systemConnectionViewModel.edgePoints[elementScalerBlock.index] = newPositionProperty

        if (isSource) {
            previousPortViewModel = systemConnectionViewModel.source
            systemConnectionViewModel.source = sourcePortViewModel
        } else {
            previousPortViewModel = systemConnectionViewModel.destination
            systemConnectionViewModel.destination = destinationPortViewModel
        }

        elementScalerBlock.updatePosition()
        systemConnectionViewModel.updateTarget()

        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createMoveSystemConnectionViewModelElementAction(
            systemConnectionViewModel,
            elementScalerBlock,
            previousPortViewModel,
            wasVariableBlock
        )
    }

    val portViewModelAt: PortViewModel?
        get() {
            if (systemConnectionViewModel == null) {
                return null
            }

            return getPortViewModelAt(elementScalerBlock.layoutPosition.add(delta))
        }

    fun getPortViewModelAt(point: Point2D): PortViewModel? {
        // Check for variable blocks in the current system
        for (variable in editorViewModel.currentSystem.target!!.variables) {
            val portViewModel = geckoViewModel.getViewModelElement(variable) as PortViewModel
            if (point.x > portViewModel.position.x && point.x < portViewModel.position.x + portViewModel.size.x && point.y > portViewModel.position.y && point.y < portViewModel.position.y + portViewModel.size.y) {
                isVariableBlock = true
                return portViewModel
            }
        }

        // Check for ports in the children systems
        for (system in editorViewModel.currentSystem.target.children) {
            for (variable in system.variables) {
                val portViewModel = geckoViewModel.getViewModelElement(variable) as PortViewModel
                if (point.x > portViewModel.systemPortPositionProperty.value.x && point.x < portViewModel.systemPortPositionProperty.value.x + portViewModel.systemPortSizeProperty.value.x && point.y > portViewModel.systemPortPositionProperty.value.y && point.y < portViewModel.systemPortPositionProperty.value.y + portViewModel.systemPortSizeProperty.value.y) {
                    isVariableBlock = false
                    return portViewModel
                }
            }
        }
        return null
    }

    fun calculateEndPortPosition(
        position: Point2D,
        size: Point2D,
        visibility: Visibility,
        isPort: Boolean
    ): Point2D {
        val sign = if (isPort) 1 else -1
        return position.add(size.multiply(0.5))
            .subtract((if (visibility == Visibility.INPUT) 1 else -1) * sign * size.x / 2, 0.0)
    }

    val isSource: Boolean
        get() = elementScalerBlock.index == 0
}
