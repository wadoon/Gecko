package org.gecko.actions

import org.gecko.exceptions.GeckoException

import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.System

/**
 * A concrete representation of an [Action] that restores a deleted [System] in a given
 * [System].
 */
class RestoreSystemViewModelElementAction internal constructor(
    val gModel: GModel,
    val System: System,
) : Action() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        gModel.addViewModelElement(System)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return DeleteSystemAction(gModel, System, System)
    }
}
