package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.*
import java.util.stream.Collectors

/**
 * A concrete representation of an [Action] that changes the visibility of a [PortViewModel], which it holds
 * a reference to. Additionally, holds the old and new [Visibilities][Visibility] of the contract for undo/redo
 * purposes.
 */
class ChangeVisibilityPortViewModelAction : Action {
    val geckoViewModel: GeckoViewModel
    val visibility: Visibility
    val oldVisibility: Visibility
    val portViewModel: PortViewModel
    var systemConnectionDeleteActionGroup: Action? = null

    constructor(geckoViewModel: GeckoViewModel, portViewModel: PortViewModel, visibility: Visibility) {
        this.geckoViewModel = geckoViewModel
        this.portViewModel = portViewModel
        this.visibility = visibility
        this.oldVisibility = portViewModel.visibility
    }

    constructor(
        geckoViewModel: GeckoViewModel, portViewModel: PortViewModel, visibility: Visibility,
        systemConnectionDeleteActionGroup: Action?
    ) {
        this.geckoViewModel = geckoViewModel
        this.portViewModel = portViewModel
        this.visibility = visibility
        this.oldVisibility = portViewModel.visibility
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
        portViewModel.visibility = (visibility)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action {
        return actionFactory.changeVisibility(
            portViewModel, oldVisibility,
            systemConnectionDeleteActionGroup!!.getUndoAction(geckoViewModel.actionManager.actionFactory)
        )
    }

    fun getSystemConnectionDeleteActionGroup(): ActionGroup {
        val containingSystem = geckoViewModel.getSystemViewModelWithPort(portViewModel)!!
        val parentSystem = containingSystem.parent
        val deleteActions: MutableList<Action> = ArrayList(getSystemConnectionDeleteActions(containingSystem))

        if (parentSystem != null) {
            deleteActions.addAll(getSystemConnectionDeleteActions(parentSystem))
        }
        return ActionGroup(deleteActions)
    }

    private fun getSystemConnectionViewModels(system: SystemViewModel): Set<SystemConnectionViewModel> =
        system.connections
            .stream()
            .filter { it.source == portViewModel || it.destination == portViewModel }
            .collect(Collectors.toSet())

    private fun getSystemConnectionDeleteActions(system: SystemViewModel): List<Action> =
        getSystemConnectionViewModels(system).stream()
            .map { systemConnectionViewModel: SystemConnectionViewModel? ->
                DeleteSystemConnectionViewModelElementAction(
                    geckoViewModel,
                    systemConnectionViewModel!!, system
                )
            }
            .toList()
}
