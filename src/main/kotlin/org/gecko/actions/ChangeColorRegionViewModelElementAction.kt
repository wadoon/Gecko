package org.gecko.actions

import javafx.scene.paint.Color
import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.Region

/**
 * A concrete representation of an [Action] that changes the color of a [Region]. Additionally,
 * holds the old and new [Color]s of the region for undo/redo purposes.
 */
class ChangeColorRegionViewModelElementAction
internal constructor(val Region: Region, color: Color?) : Action() {
    val newColor = color

    val oldColor: Color = Region.color

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        Region.color = newColor!!
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createChangeColorRegion(Region, oldColor)
    }
}
