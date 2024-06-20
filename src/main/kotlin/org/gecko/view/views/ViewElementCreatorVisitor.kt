package org.gecko.view.views

import org.gecko.view.views.viewelement.ViewElement
import org.gecko.viewmodel.*

/**
 * Follows the visitor pattern, implementing the [PositionableViewModelElementVisitor] interface. Creates each
 * type of [ViewElement] through the [ViewFactory] by visiting a type of
 * [PositionableViewModelElement][org.gecko.viewmodel.PositionableViewModelElement].
 */
class ViewElementCreatorVisitor(val viewFactory: ViewFactory) :
    PositionableViewModelElementVisitor<ViewElement<*>?> {
    override fun visit(systemViewModel: SystemViewModel): ViewElement<*>? {
        return viewFactory.createViewElementFrom(systemViewModel)
    }

    override fun visit(regionViewModel: RegionViewModel): ViewElement<*>? {
        return viewFactory.createViewElementFrom(regionViewModel)
    }

    override fun visit(systemConnectionViewModel: SystemConnectionViewModel): ViewElement<*>? {
        return viewFactory.createViewElementFrom(systemConnectionViewModel)
    }

    override fun visit(edgeViewModel: EdgeViewModel): ViewElement<*>? {
        return viewFactory.createViewElementFrom(edgeViewModel)
    }

    override fun visit(stateViewModel: StateViewModel): ViewElement<*>? {
        return viewFactory.createViewElementFrom(stateViewModel)
    }

    override fun visit(portViewModel: PortViewModel): ViewElement<*>? {
        return viewFactory.createViewElementFrom(portViewModel)
    }

    override fun visit(automatonViewModel: AutomatonViewModel): ViewElement<*>? {
        TODO("Not yet implemented")
    }
}
