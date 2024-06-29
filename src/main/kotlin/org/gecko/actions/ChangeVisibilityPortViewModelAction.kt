package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.*
import java.util.stream.Collectors

/**
 * A concrete representation of an [Action] that changes the visibility of a [Port], which it holds
 * a reference to. Additionally, holds the old and new [Visibilities][Visibility] of the contract for undo/redo
 * purposes.
 */
class ChangeVisibilityPortViewModelAction : Action {
    val gModel: GModel
    val visibility: Visibility
    val oldVisibility: Visibility
    val Port: Port
    var systemConnectionDeleteActionGroup: Action? = null

    constructor(gModel: GModel, Port: Port, visibility: Visibility) {
        this.gModel = gModel
        this.Port = Port
        this.visibility = visibility
        this.oldVisibility = Port.visibility
    }

    constructor(
        gModel: GModel, Port: Port, visibility: Visibility,
        systemConnectionDeleteActionGroup: Action?
    ) {
        this.gModel = gModel
        this.Port = Port
        this.visibility = visibility
        this.oldVisibility = Port.visibility
        this.systemConnectionDeleteActionGroup = systemConnectionDeleteActionGroup
    }

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        if (visibility == oldVisibility) {
            return false
        }

        if (systemConnectionDeleteActionGroup == null) {
            systemConnectionDeleteActionGroup = getSystemConnectionDeleteActionGroup()
        }

        systemConnectionDeleteActionGroup!!.run()
        Port.visibility = (visibility)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.changeVisibility(
            Port, oldVisibility,
            systemConnectionDeleteActionGroup!!.getUndoAction(gModel.actionManager.actionFactory)
        )
    }

    fun getSystemConnectionDeleteActionGroup(): ActionGroup {
        val containingSystem = gModel.getSystemViewModelWithPort(Port)!!
        val parentSystem = containingSystem.parent
        val deleteActions: MutableList<Action> = ArrayList(getSystemConnectionDeleteActions(containingSystem))

        if (parentSystem != null) {
            deleteActions.addAll(getSystemConnectionDeleteActions(parentSystem))
        }
        return ActionGroup(deleteActions)
    }

    private fun getSystemConnectionViewModels(system: System): Set<SystemConnectionViewModel> =
        system.connections
            .stream()
            .filter { it.source == Port || it.destination == Port }
            .collect(Collectors.toSet())

    private fun getSystemConnectionDeleteActions(system: System): List<Action> =
        getSystemConnectionViewModels(system).stream()
            .map { systemConnectionViewModel: SystemConnectionViewModel? ->
                DeleteSystemConnectionViewModelElementAction(
                    gModel,
                    systemConnectionViewModel!!, system
                )
            }
            .toList()
}
