package org.gecko.view.views

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.ContextMenuEvent
import org.gecko.actions.ActionManager
import org.gecko.tools.ToolType
import org.gecko.view.GeckoView
import org.gecko.view.contextmenu.*
import org.gecko.view.views.shortcuts.AutomatonEditorViewShortcutHandler
import org.gecko.view.views.shortcuts.SystemEditorViewShortcutHandler
import org.gecko.view.views.viewelement.*
import org.gecko.view.views.viewelement.decorator.BlockElementScalerViewElementDecorator
import org.gecko.view.views.viewelement.decorator.ConnectionElementScalerViewElementDecorator
import org.gecko.view.views.viewelement.decorator.SelectableViewElementDecorator
import org.gecko.viewmodel.*

/**
 * Represents a factory for the view elements of a Gecko project. Provides a method for the creation of each element.
 */
class ViewFactory(val actionManager: ActionManager, val geckoView: GeckoView) {
    fun createEditorView(editorViewModel: EditorViewModel, isAutomatonEditor: Boolean): EditorView {
        return if (isAutomatonEditor) createAutomatonEditorView(editorViewModel) else createSystemEditorView(
            editorViewModel
        )
    }

    fun createViewElementFrom(stateViewModel: StateViewModel): ViewElement<*> {
        val newStateViewElement = StateViewElement(stateViewModel)

        val contextMenuBuilder: ViewContextMenuBuilder =
            StateViewElementContextMenuBuilder(actionManager, stateViewModel, geckoView)
        setContextMenu(newStateViewElement, contextMenuBuilder)
        return SelectableViewElementDecorator(newStateViewElement)
    }

    fun createViewElementFrom(regionViewModel: RegionViewModel): ViewElement<*> {
        val newRegionViewElement = RegionViewElement(regionViewModel)

        val contextMenuBuilder: ViewContextMenuBuilder =
            RegionViewElementContextMenuBuilder(actionManager, regionViewModel, geckoView)
        setContextMenu(newRegionViewElement, contextMenuBuilder)
        return BlockElementScalerViewElementDecorator(SelectableViewElementDecorator(newRegionViewElement))
    }

    fun createViewElementFrom(portViewModel: PortViewModel): ViewElement<*> {
        val newVariableBlockViewElement = VariableBlockViewElement(portViewModel)

        val contextMenuBuilder: ViewContextMenuBuilder =
            VariableBlockViewElementContextMenuBuilder(actionManager, portViewModel, geckoView)
        setContextMenu(newVariableBlockViewElement, contextMenuBuilder)

        return SelectableViewElementDecorator(newVariableBlockViewElement)
    }

    fun createViewElementFrom(edgeViewModel: EdgeViewModel): ViewElement<*> {
        val newEdgeViewElement = EdgeViewElement(edgeViewModel)

        val contextMenuBuilder: ViewContextMenuBuilder =
            EdgeViewElementContextMenuBuilder(actionManager, edgeViewModel, geckoView)
        setContextMenu(newEdgeViewElement.pane, contextMenuBuilder)

        return ConnectionElementScalerViewElementDecorator(newEdgeViewElement)
    }

    fun createViewElementFrom(systemConnectionViewModel: SystemConnectionViewModel): ViewElement<*> {
        val newSystemConnectionViewElement =
            SystemConnectionViewElement(systemConnectionViewModel)

        val contextMenuBuilder: ViewContextMenuBuilder =
            SystemConnectionViewElementContextMenuBuilder(actionManager, systemConnectionViewModel, geckoView)
        setContextMenu(newSystemConnectionViewElement.pane, contextMenuBuilder)

        return ConnectionElementScalerViewElementDecorator(newSystemConnectionViewElement)
    }

    fun createViewElementFrom(systemViewModel: SystemViewModel): ViewElement<*> {
        val newSystemViewElement = SystemViewElement(systemViewModel)

        val contextMenuBuilder: ViewContextMenuBuilder =
            SystemViewElementContextMenuBuilder(actionManager, systemViewModel, geckoView)
        setContextMenu(newSystemViewElement, contextMenuBuilder)

        return SelectableViewElementDecorator(newSystemViewElement)
    }

    fun setContextMenu(newViewElement: Node, contextMenuBuilder: ViewContextMenuBuilder) {
        newViewElement.onContextMenuRequested = EventHandler { event: ContextMenuEvent ->
            actionManager.run(actionManager.actionFactory.createSelectToolAction(ToolType.CURSOR))
            geckoView.currentView!!.changeContextMenu(contextMenuBuilder.build())
            contextMenuBuilder.contextMenu!!.show(newViewElement, event.screenX, event.screenY)
            event.consume()
        }
    }

    fun createAutomatonEditorView(editorViewModel: EditorViewModel): EditorView {
        val editorView = EditorView(this, actionManager, editorViewModel)
        editorView.shortcutHandler = AutomatonEditorViewShortcutHandler(actionManager, editorView)
        return editorView
    }

    fun createSystemEditorView(editorViewModel: EditorViewModel): EditorView {
        val editorView = EditorView(this, actionManager, editorViewModel)
        editorView.shortcutHandler = SystemEditorViewShortcutHandler(actionManager, editorView)
        return editorView
    }
}
