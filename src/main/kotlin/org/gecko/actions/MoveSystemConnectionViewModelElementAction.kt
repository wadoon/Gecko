package org.gecko.actions

import javafx.geometry.Point2D
import org.gecko.exceptions.GeckoException
import org.gecko.view.views.viewelement.decorator.ElementScalerBlock
import org.gecko.viewmodel.*

/**
 * A concrete representation of an [Action] that moves a [SystemConnection] with a given
 * [delta value][Point2D].
 */
class MoveSystemConnectionViewModelElementAction : Action {
    val gModel: GModel
    val editorViewModel: EditorViewModel
    val systemConnectionViewModel: SystemConnection?
    val elementScalerBlock: ElementScalerBlock
    var delta: Point2D? = null
    var Port: Port? = null
    var previousPort: Port? = null
    var isVariableBlock = false
    var wasVariableBlock = false

    internal constructor(
        gModel: GModel,
        systemConnectionViewModel: SystemConnection?,
        elementScalerBlock: ElementScalerBlock,
        delta: Point2D?
    ) {
        this.gModel = gModel
        this.editorViewModel = gModel.currentEditor!!
        this.systemConnectionViewModel = systemConnectionViewModel
        this.elementScalerBlock = elementScalerBlock
        this.delta = delta
    }

    internal constructor(
        gModel: GModel,
        systemConnectionViewModel: SystemConnection?,
        elementScalerBlock: ElementScalerBlock,
        Port: Port?,
        isVariableBlock: Boolean
    ) {
        this.gModel = gModel
        this.editorViewModel = gModel.currentEditor!!
        this.systemConnectionViewModel = systemConnectionViewModel
        this.elementScalerBlock = elementScalerBlock
        this.Port = Port
        this.isVariableBlock = isVariableBlock
    }

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        if (Port == null) {
            Port = PortAt
            if (Port == null) {
                return false
            }
        }

        val parentSystem = gModel.currentEditor!!.currentSystem
        var sourcePortViewModel = systemConnectionViewModel!!.source!!
        var destinationPortViewModel = systemConnectionViewModel.destination!!

        if (isSource) {
            if (Port == sourcePortViewModel) {
                return false
            }
            wasVariableBlock =
                gModel.getSystemViewModelWithPort(sourcePortViewModel) == parentSystem
            sourcePortViewModel = Port!!
        } else {
            if (Port == destinationPortViewModel) {
                return false
            }
            wasVariableBlock =
                gModel.getSystemViewModelWithPort(destinationPortViewModel) == parentSystem
            destinationPortViewModel = Port!!
        }

        val sourceSystem = gModel.getSystemViewModelWithPort(sourcePortViewModel)
        val destinationSystem = gModel.getSystemViewModelWithPort(destinationPortViewModel)

        if (
            !isConnectingAllowed(
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

        val newPositionProperty =
            if (isVariableBlock) {
                calculateEndPortPosition(Port!!.position, Port!!.size, Port!!.visibility, false)

                /*portViewModel.getPositionProperty().addListener((observable, oldValue, newValue) ->
                newPositionProperty.setValue(
                        calculateEndPortPosition(portViewModel.getPosition(), portViewModel.getSize(),
                                portViewModel.getVisibility(), false)));*/
            } else {
                calculateEndPortPosition(
                    Port!!.systemPortPositionProperty.value,
                    Port!!.systemPortSizeProperty.value,
                    Port!!.visibility,
                    true
                )

                /*            portViewModel.getSystemPortPositionProperty()
                .addListener((observable, oldValue, newValue) -> newPositionProperty.setValue(
                        calculateEndPortPosition(portViewModel.getSystemPortPositionProperty().getValue(),
                                portViewModel.getSystemPortSizeProperty().getValue(), portViewModel.getVisibility(), true)));*/
            }

        systemConnectionViewModel.edgePoints[elementScalerBlock.index] = newPositionProperty

        if (isSource) {
            previousPort = systemConnectionViewModel.source
            systemConnectionViewModel.source = sourcePortViewModel
        } else {
            previousPort = systemConnectionViewModel.destination
            systemConnectionViewModel.destination = destinationPortViewModel
        }
        elementScalerBlock.updatePosition()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createMoveSystemConnectionViewModelElementAction(
            systemConnectionViewModel,
            elementScalerBlock,
            previousPort,
            wasVariableBlock
        )
    }

    val PortAt: Port?
        get() {
            if (systemConnectionViewModel == null) {
                return null
            }

            return getPortViewModelAt(elementScalerBlock.layoutPosition.add(delta))
        }

    fun getPortViewModelAt(point: Point2D): Port? {
        // Check for variable blocks in the current system
        for (variable in editorViewModel.currentSystem.ports) {
            if (
                point.x > variable.position.x &&
                    point.x < variable.position.x + variable.size.x &&
                    point.y > variable.position.y &&
                    point.y < variable.position.y + variable.size.y
            ) {
                isVariableBlock = true
                return variable
            }
        }

        // Check for ports in the children systems
        for (system in editorViewModel.currentSystem.subSystems) {
            for (variable in system.ports) {
                if (
                    point.x > variable.systemPortPositionProperty.value.x &&
                        point.x <
                            variable.systemPortPositionProperty.value.x +
                                variable.systemPortSizeProperty.value.x &&
                        point.y > variable.systemPortPositionProperty.value.y &&
                        point.y <
                            variable.systemPortPositionProperty.value.y +
                                variable.systemPortSizeProperty.value.y
                ) {
                    isVariableBlock = false
                    return variable
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
        return position
            .add(size.multiply(0.5))
            .subtract((if (visibility == Visibility.INPUT) 1 else -1) * sign * size.x / 2, 0.0)
    }

    val isSource: Boolean
        get() = elementScalerBlock.index == 0
}
