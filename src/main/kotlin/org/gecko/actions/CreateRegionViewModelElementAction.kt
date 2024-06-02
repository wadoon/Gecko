package org.gecko.actions

import javafx.geometry.Point2D
import javafx.scene.paint.Color
import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.RegionViewModel

/**
 * A concrete representation of an [Action] that creates a [RegionViewModel] in the
 * current-[SystemViewModel] through the [org.gecko.viewmodel.ViewModelFactory] of the
 * [GeckoViewModel]. Additionally, holds the current [EditorViewModel][org.gecko.viewmodel.EditorViewModel]
 * for setting the correct size and position for the created region.
 */
class CreateRegionViewModelElementAction internal constructor(
    val geckoViewModel: GeckoViewModel,
    val position: Point2D?,
    val size: Point2D?,
    val color: Color?
) : Action() {
    lateinit var createdRegionViewModel: RegionViewModel

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        val currentParentSystem = geckoViewModel.currentEditor!!.currentSystem
        createdRegionViewModel = geckoViewModel.viewModelFactory.createRegionViewModelIn(currentParentSystem)
        createdRegionViewModel.position = (position!!)
        createdRegionViewModel.size = (size!!)
        if (color != null) {
            createdRegionViewModel.color = (color)
        }
        createdRegionViewModel.updateTarget()
        val actionManager = geckoViewModel.actionManager
        actionManager.run(actionManager.actionFactory.createSelectAction(createdRegionViewModel, true))
        geckoViewModel.currentEditor!!.updateRegions()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action? {
        return actionFactory.createDeletePositionableViewModelElementAction(createdRegionViewModel)
    }
}
