package org.gecko.actions

import org.gecko.exceptions.ModelException
import org.gecko.viewmodel.Automaton
import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.Region

/**
 * A concrete representation of an [Action] that restores a deleted [Region] in a given [Automaton].
 */
class RestoreRegionViewModelElementAction
internal constructor(val gModel: GModel, val Region: Region, val automaton: Automaton) : Action() {
    @Throws(ModelException::class)
    override fun run(): Boolean {
        automaton.addRegion(Region)
        gModel.addViewModelElement(Region)
        gModel.currentEditor.updateRegions()
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return DeleteRegionAction(gModel, Region, automaton)
    }
}
