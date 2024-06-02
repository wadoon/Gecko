package org.gecko.actions

import javafx.geometry.Point2D
import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.EditorViewModel

/**
 * A concrete representation of an [Action] that zooms in or out in the given [EditorViewModel] from a given
 * {link Point2D pivot} by a given zoom factor.
 */
class ZoomAction internal constructor(
    val editorViewModel: EditorViewModel,
    val pivot: Point2D,
    val factor: Double
) : Action() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        editorViewModel.zoom(factor, pivot)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action? {
        return null
    }
}
