package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.EditorViewModel
import org.gecko.viewmodel.PositionableViewModelElement
import org.gecko.viewmodel.SelectionManager

/**
 * A concrete representation of an [Action] that selects a set of [PositionableViewModelElement]s.
 */
class SelectAction internal constructor(
    editorViewModel: EditorViewModel,
    elements: Set<PositionableViewModelElement>?,
    val newSelection: Boolean
) : Action() {
    val selectionManager: SelectionManager = editorViewModel.selectionManager
    val elementsToSelect: MutableSet<PositionableViewModelElement> = elements!!.toMutableSet()

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        if (elementsToSelect.isEmpty()) {
            selectionManager.deselectAll()
        }
        if (!newSelection) {
            elementsToSelect.addAll(selectionManager.currentSelection)
        }
        selectionManager.select(elementsToSelect)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action? {
        return null
    }
}
