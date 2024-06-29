package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.EditorViewModel
import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.System

/**
 * A concrete representation of an [Action] that switches between [EditorViewModel]s depending on whether
 * the current [EditorViewModel] is an automaton view.
 */
class ViewSwitchAction internal constructor(
    val gModel: GModel,
    val System: System?,
    val isAutomaton: Boolean
) : Action() {
    val oldEditor: EditorViewModel = gModel.currentEditor!!

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        if (System == null) {
            return false
        }
        gModel.switchEditor(System, isAutomaton)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.createViewSwitchAction(oldEditor.currentSystem, oldEditor.isAutomatonEditor)
    }
}
