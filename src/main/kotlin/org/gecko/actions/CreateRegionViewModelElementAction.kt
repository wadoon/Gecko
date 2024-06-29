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
    val position: Point2D,
    val size: Point2D,
    val color: Color?
) : Action() {
    lateinit var createdRegion: RegionViewModel

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        val currentParentSystem = geckoViewModel.currentEditor!!.currentSystem
        createdRegion = geckoViewModel.viewModelFactory.createRegion(currentParentSystem)
        createdRegion.position = position
        createdRegion.size = size
        if (color != null) {
            createdRegion.color = (color)
        }
        val actionManager = geckoViewModel.actionManager
        actionManager.run(actionManager.actionFactory.createSelectAction(createdRegion, true))
        geckoViewModel.currentEditor!!.updateRegions()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createDeleteAction(createdRegion)
    }
}
