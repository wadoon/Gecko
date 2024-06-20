package org.gecko.actions

import org.gecko.viewmodel.GeckoViewModel

class CutPositionableViewModelElementAction internal constructor(var geckoViewModel: GeckoViewModel) :
    ActionGroup(ArrayList()) {
    var delete: Action

    init {
        val copy = CopyPositionableViewModelElementAction(geckoViewModel)
        actions.add(copy)
        delete = DeletePositionableViewModelElementAction(
            geckoViewModel,
            geckoViewModel.currentEditor.selectionManager.currentSelection
        )
        actions.add(delete)
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return delete.getUndoAction(actionFactory)
    }
}
