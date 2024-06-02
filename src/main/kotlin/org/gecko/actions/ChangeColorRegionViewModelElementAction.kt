package org.gecko.actions

import javafx.scene.paint.Color
import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.RegionViewModel

/**
 * A concrete representation of an [Action] that changes the color of a [RegionViewModel]. Additionally,
 * holds the old and new [Color]s of the region for undo/redo purposes.
 */
class ChangeColorRegionViewModelElementAction internal constructor(
    val regionViewModel: RegionViewModel,
    color: Color?
) : Action() {
    val newColor = color

    val oldColor: Color = regionViewModel.color

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        regionViewModel.color = newColor!!
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createChangeColorRegionViewModelElementAction(regionViewModel, oldColor)
    }
}
