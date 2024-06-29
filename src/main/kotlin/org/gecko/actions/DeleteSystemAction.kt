package org.gecko.actions

import org.gecko.exceptions.GeckoException

import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.PositionableViewModelElement
import org.gecko.viewmodel.System

/**
 * A concrete representation of an [Action] that removes a [System] from a given
 * [System].
 */
class DeleteSystemAction(
    val gModel: GModel, val system: System, val parentSystem: System
) : AbstractPositionableViewModelElementAction() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        parentSystem.subSystems.remove(system)
        gModel.deleteViewModelElement(system)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return RestoreSystemViewModelElementAction(gModel, system)
    }

    override val target: PositionableViewModelElement
        get() = system
}
