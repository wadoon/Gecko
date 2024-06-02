package org.gecko.actions

import javafx.geometry.Point2D
import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.PortViewModel
import org.gecko.viewmodel.SystemViewModel

/**
 * A concrete representation of an [Action] that creates a [PortViewModel] in a given
 * [SystemViewModel] through the [ViewModelFactory][org.gecko.viewmodel.ViewModelFactory] of the
 * [GeckoViewModel].
 */
class CreatePortViewModelElementAction internal constructor(
    val geckoViewModel: GeckoViewModel,
    parentSystem: SystemViewModel
) : Action() {
    val systemViewModel = parentSystem
    lateinit var createdPortViewModel: PortViewModel

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        createdPortViewModel = geckoViewModel.viewModelFactory.createPortViewModelIn(systemViewModel)
        val offset = createdPortViewModel.size.y * (systemViewModel.ports.size - 1)
        createdPortViewModel.position = (Point2D(MARGIN.toDouble(), MARGIN + offset))
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory) =
        actionFactory.createDeletePositionableViewModelElementAction(createdPortViewModel)

    companion object {
        const val MARGIN = 2
    }
}
