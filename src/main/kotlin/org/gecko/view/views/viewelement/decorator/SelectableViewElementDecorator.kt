package org.gecko.view.views.viewelement.decorator

import javafx.geometry.Point2D
import javafx.scene.Node
import org.gecko.view.views.viewelement.ViewElement
import org.gecko.view.views.viewelement.ViewElementVisitor

/**
 * A concrete representation of a [ViewElementDecorator] following the decorator pattern for
 * selection purposes. It holds a reference to a Group containing the drawn target and borderline
 * Path
 */
class SelectableViewElementDecorator(decoratorTarget: ViewElement<*>) :
    ViewElementDecorator(decoratorTarget) {
    override fun drawElement(): Node {
        return decoratorTarget.drawElement()
    }

    override var isSelected: Boolean
        get() = super.isSelected
        set(value) {
            if (value) {
                decoratorTarget.drawElement().styleClass.add(SELECTED_BORDER_STYLE_CLASS)
            } else {
                decoratorTarget.drawElement().styleClass.remove(SELECTED_BORDER_STYLE_CLASS)
            }
            super.isSelected = value
            decoratorTarget.isSelected = value
        }

    override val position: Point2D
        get() = decoratorTarget.position

    override fun accept(visitor: ViewElementVisitor) {
        visitor.visit(this)
        decoratorTarget.accept(visitor)
    }

    companion object {
        const val SELECTED_BORDER_STYLE_CLASS = "selected-border"
    }
}
