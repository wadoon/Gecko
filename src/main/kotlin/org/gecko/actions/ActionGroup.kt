package org.gecko.actions

/**
 * A concrete representation of an [Action], which encapsulates a list of other concrete actions,
 * which are iteratively run or undone.
 */
open class ActionGroup(val actions: List<Action>) : Action() {
    override fun run(): Boolean {
        for (action in actions) {
            if (!action.run()) {
                return false
            }
        }
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action? {
        if (actions.isEmpty() || actions.any { it.getUndoAction(actionFactory) == null }) {
            return null
        }
        return ActionGroup(actions.mapNotNull { it.getUndoAction(actionFactory) }.toMutableList())
    }
}
