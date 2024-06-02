package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.EditorViewModel
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.SystemViewModel

/**
 * A concrete representation of an [Action] that switches between [EditorViewModel]s depending on whether
 * the current [EditorViewModel] is an automaton view.
 */
class ViewSwitchAction internal constructor(
    val geckoViewModel: GeckoViewModel,
    val systemViewModel: SystemViewModel?,
    val isAutomaton: Boolean
) : Action() {
    val oldEditor: EditorViewModel = geckoViewModel.currentEditor!!

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        if (systemViewModel == null) {
            return false
        }
        geckoViewModel.switchEditor(systemViewModel, isAutomaton)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createViewSwitchAction(oldEditor.currentSystem, oldEditor.isAutomatonEditor)
    }
}
