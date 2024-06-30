package org.gecko.actions

import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.System

/**
 * A concrete representation of an [Action] that restores a deleted [system] in a given [system].
 */
class RestoreSystemAction(val gModel: GModel, val parentSystem: System, val system: System) :
    Action() {
    override fun run(): Boolean {
        parentSystem.subSystems.add(system)
        gModel.updateEditors()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return DeleteSystemAction(gModel, system, system)
    }
}
