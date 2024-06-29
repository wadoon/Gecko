package org.gecko.actions

import javafx.geometry.Point2D
import javafx.scene.paint.Color
import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.Region

/**
 * A concrete representation of an [Action] that creates a [Region] in the
 * current-[SystemViewModel] through the [org.gecko.viewmodel.ViewModelFactory] of the
 * [GModel]. Additionally, holds the current [EditorViewModel][org.gecko.viewmodel.EditorViewModel]
 * for setting the correct size and position for the created region.
 */
class CreateRegionViewModelElementAction internal constructor(
    val gModel: GModel,
    val position: Point2D,
    val size: Point2D,
    val color: Color?
) : Action() {
    lateinit var createdRegion: Region

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        val currentParentSystem = gModel.currentEditor!!.currentSystem
        createdRegion = gModel.viewModelFactory.createRegion(currentParentSystem)
        createdRegion.position = position
        createdRegion.size = size
        if (color != null) {
            createdRegion.color = (color)
        }
        val actionManager = gModel.actionManager
        actionManager.run(actionManager.actionFactory.createSelectAction(createdRegion, true))
        gModel.currentEditor!!.updateRegions()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createDeleteAction(createdRegion)
    }
}
