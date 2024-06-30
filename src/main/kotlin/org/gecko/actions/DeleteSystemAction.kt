package org.gecko.actions

import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.PositionableElement
import org.gecko.viewmodel.System

/**
 * A concrete representation of an [Action] that removes a [System] from a given
 * [System].
 */
class DeleteSystemAction(
    val gModel: GModel, val system: System, val parentSystem: System
) : AbstractPositionableViewModelElementAction() {

    override fun run(): Boolean {
        parentSystem.subSystems.remove(system)
        gModel.deleteViewModelElement(system)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return RestoreSystemAction(gModel, parentSystem, system)
    }

    override val target: PositionableElement
        get() = system
}
