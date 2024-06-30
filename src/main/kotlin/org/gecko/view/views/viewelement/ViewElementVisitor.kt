package org.gecko.view.views.viewelement

import org.gecko.view.views.viewelement.decorator.BlockElementScalerViewElementDecorator
import org.gecko.view.views.viewelement.decorator.ConnectionElementScalerViewElementDecorator
import org.gecko.view.views.viewelement.decorator.ElementScalerViewElementDecorator
import org.gecko.view.views.viewelement.decorator.SelectableViewElementDecorator

/**
 * Represents a visitor pattern for performing operations on [ViewElement]s. Concrete visitors must
 * implement this interface to define specific behavior for each [ViewElement] and their decorators.
 */
interface ViewElementVisitor {
    fun visit(stateViewElement: StateViewElement)

    fun visit(edgeViewElement: EdgeViewElement)

    fun visit(regionViewElement: RegionViewElement)

    fun visit(systemViewElement: SystemViewElement)

    fun visit(portViewElement: PortViewElement)

    fun visit(systemConnectionViewElement: SystemConnectionViewElement)

    fun visit(variableBlockViewElement: VariableBlockViewElement)

    fun visit(elementScalarViewElementDecorator: ElementScalerViewElementDecorator)

    fun visit(selectableViewElementDecorator: SelectableViewElementDecorator)

    fun visit(
        connectionElementScalerViewElementDecorator: ConnectionElementScalerViewElementDecorator
    )

    fun visit(blockElementScalerViewElementDecorator: BlockElementScalerViewElementDecorator)
}
