package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.EditorViewModel
import org.gecko.viewmodel.PositionableViewModelElement

/**
 * A concrete representation of an [Action] that selects and focuses on a {link PositionableViewModelElement}.
 */
class FocusPositionableViewModelElementAction internal constructor(
    val editorViewModel: EditorViewModel,
    positionableViewModelElement: PositionableViewModelElement
) : Action() {
    val element = positionableViewModelElement

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        editorViewModel.selectionManager.select(element)
        editorViewModel.moveToFocusedElement()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action? {
        return null
    }
}
