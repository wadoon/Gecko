package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.EditorViewModel

/**
 * A concrete representation of an [Action] that zooms in or out from the center of a given
 * [EditorViewModel] by a given zoom factor.
 */
class ZoomCenterAction internal constructor(val editorViewModel: EditorViewModel, val factor: Double) :
    Action() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        editorViewModel.zoomCenter(factor)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action? {
        return null
    }
}
