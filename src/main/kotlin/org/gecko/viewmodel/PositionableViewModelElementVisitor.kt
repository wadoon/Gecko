package org.gecko.viewmodel

/**
 * Represents a visitor pattern for performing operations on [PositionableViewModelElement]s. Concrete visitors
 * must implement this interface to define specific behavior for each [PositionableViewModelElement].
 *
 */
@Deprecated(message = "stupid idea eva!!")
interface PositionableViewModelElementVisitor<T> {
    fun visit(systemViewModel: SystemViewModel): T
    fun visit(regionViewModel: RegionViewModel): T

    fun visit(systemConnectionViewModel: SystemConnectionViewModel): T

    fun visit(edgeViewModel: EdgeViewModel): T

    fun visit(stateViewModel: StateViewModel): T

    fun visit(portViewModel: PortViewModel): T
    fun visit(automatonViewModel: AutomatonViewModel): T
}
