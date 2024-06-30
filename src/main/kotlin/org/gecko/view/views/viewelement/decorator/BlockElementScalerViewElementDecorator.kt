package org.gecko.view.views.viewelement.decorator

import org.gecko.view.views.viewelement.ViewElement
import org.gecko.view.views.viewelement.ViewElementVisitor

/**
 * A concrete representation of a [ViewElementDecorator] following the decorator pattern for scaling
 * purposes. It holds a reference to a Group containing the drawn target and a list of
 * [ElementScalerBlock]s, one for each edge point of the decorator.
 */
class BlockElementScalerViewElementDecorator(decoratorTarget: ViewElement<*>) :
    ElementScalerViewElementDecorator(decoratorTarget) {
    override fun accept(visitor: ViewElementVisitor) {
        visitor.visit(this)
        decoratorTarget.accept(visitor)
    }
}
