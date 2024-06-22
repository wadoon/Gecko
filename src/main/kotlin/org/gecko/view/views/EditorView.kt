package org.gecko.view.views

import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.collections.SetChangeListener
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.*
import javafx.scene.layout.*
import org.gecko.actions.*
import org.gecko.tools.*
import org.gecko.view.GeckoView
import org.gecko.view.ResourceHandler
import org.gecko.view.contextmenu.ViewContextMenuBuilder
import org.gecko.view.inspector.Inspector
import org.gecko.view.inspector.InspectorFactory
import org.gecko.view.toolbar.ToolBarBuilder
import org.gecko.view.views.shortcuts.ShortcutHandler
import org.gecko.view.views.viewelement.ViewElement
import org.gecko.viewmodel.EditorViewModel
import org.gecko.viewmodel.PositionableViewModelElement
import org.gecko.viewmodel.onChange
import java.util.function.Consumer

/**
 * Represents a displayable view in the Gecko Graphic Editor, holding a collection of displayed [ViewElement]s and
 * other items specific to their visualisation.
 */
class EditorView(val actionManager: ActionManager, val viewModel: EditorViewModel, val geckoView: GeckoView) {
    val currentView: Tab
    val currentViewPane: StackPane

    val toolBar = ToolBarBuilder(actionManager, this, viewModel).build()
    val inspectorFactory: InspectorFactory
    val emptyInspector: Inspector
    val searchWindow: Node?

    val viewElementPane: ViewElementPane

    var currentInspector: ObjectProperty<Inspector?>

    var shortcutHandler: ShortcutHandler? = null
        /**
         * Set the shortcut handler for the editor view. This will be used to handle keyboard shortcuts.
         *
         * @param shortcutHandler the shortcut handler
         */
        set(value) {
            field = value
            currentViewPane.addEventHandler(KeyEvent.ANY, shortcutHandler)
            toolBar.addEventHandler(KeyEvent.ANY, shortcutHandler)
            if (currentInspector.get() != null) {
                currentInspector.get()!!
                    .addEventHandler(KeyEvent.ANY, shortcutHandler)
            }
        }


    var contextMenu: ContextMenu?

    init {
        this.inspectorFactory = InspectorFactory(actionManager, viewModel)

        this.currentViewPane = StackPane()
        this.currentInspector = SimpleObjectProperty(null)

        this.emptyInspector = Inspector(ArrayList(), actionManager)
        this.currentInspector = SimpleObjectProperty(emptyInspector)
        this.viewElementPane = ViewElementPane(viewModel)

        this.currentView = Tab("", currentViewPane)

        val tabName: StringProperty = SimpleStringProperty("Error_Name")
        tabName.bind(Bindings.createStringBinding({
            val name = viewModel.currentSystem.name
            name + (if (viewModel.isAutomatonEditor) " (${ResourceHandler.automaton})" else " (${ResourceHandler.system})")
        }, viewModel.currentSystem.nameProperty))

        val tabLabel = Label()
        tabLabel.textProperty().bind(tabName)
        currentView.graphic = tabLabel

        // Floating UI
        val floatingUIBuilder = FloatingUIBuilder(actionManager, viewModel)
        val zoomButtons = floatingUIBuilder.buildZoomButtons()
        AnchorPane.setBottomAnchor(zoomButtons, DEFAULT_ANCHOR_VALUE)
        AnchorPane.setRightAnchor(zoomButtons, DEFAULT_ANCHOR_VALUE)

        val currentViewLabel = floatingUIBuilder.buildCurrentViewLabel()
        AnchorPane.setTopAnchor(currentViewLabel, DEFAULT_ANCHOR_VALUE)
        AnchorPane.setLeftAnchor(currentViewLabel, LEFT_ANCHOR_VALUE)

        val viewSwitchButton = floatingUIBuilder.buildViewSwitchButtons()
        AnchorPane.setTopAnchor(viewSwitchButton, DEFAULT_ANCHOR_VALUE)
        AnchorPane.setRightAnchor(viewSwitchButton, DEFAULT_ANCHOR_VALUE)

        searchWindow = floatingUIBuilder.buildSearchWindow(this)
        activateSearchWindow(false)

        val floatingUI = AnchorPane()
        floatingUI.children.addAll(zoomButtons, currentViewLabel, viewSwitchButton)
        floatingUI.isPickOnBounds = false

        // Build stack pane
        currentViewPane.children.addAll(viewElementPane.draw(), floatingUI, searchWindow)
        StackPane.setAlignment(searchWindow, Pos.TOP_CENTER)

        // View element creator listener
        viewModel.containedPositionableViewModelElementsProperty.onChange { this.onUpdateViewElements(it) }

        // Inspector creator listener
        viewModel.focusedElementProperty.onChange { oldValue, newValue ->
            this.focusedElementChanged(newValue)
        }

        viewModel.selectionManager.currentSelectionProperty.onChange { oldValue, newValue ->
            this.selectionChanged(oldValue, newValue)
        }

        // Set current tool
        viewModel.currentToolProperty.addListener { observable: ObservableValue<out Tool>, oldValue: Tool?, newValue: Tool? ->
            this.onToolChanged(newValue)
        }

        val contextMenuBuilder =
            ViewContextMenuBuilder(viewModel.actionManager, viewModel, this)
        this.contextMenu = contextMenuBuilder.build()
        currentViewPane.onContextMenuRequested = EventHandler<ContextMenuEvent> { event: ContextMenuEvent ->
            changeContextMenu(contextMenuBuilder.contextMenu)
            contextMenu!!.show(currentViewPane, event.screenX, event.screenY)
            event.consume()
        }

        initializeViewElements()
        acceptTool(viewModel.currentTool)
        focus()
    }


    /**
     * Focus the center pane of the editor view.
     */
    fun focus() {
        currentViewPane.requestFocus()
    }

    /**
     * Draw the toolbar of the editor view.
     *
     * @return the toolbar
     */
    fun drawToolbar(): Node {
        toolBar.addEventHandler(KeyEvent.ANY, shortcutHandler)
        return toolBar
    }

    /**
     * Draw the inspector of the editor view.
     *
     * @return the inspector
     */
    fun drawInspector(): Node? {
        if (!viewModel.isAutomatonEditor) {
            currentInspector.get()!!
                .addEventHandler(KeyEvent.ANY, shortcutHandler)
            return currentInspector.get()
        } else {
            val vbox = VBox()
            vbox.addEventHandler(KeyEvent.ANY, shortcutHandler)
            val currentInspectorNode = currentInspector.get()
            val inspectorBox = VBox(currentInspectorNode)
            VBox.setVgrow(inspectorBox, Priority.ALWAYS)

            val automatonVariablePane = inspectorFactory.createAutomatonVariablePane()
            vbox.children.addAll(inspectorBox, automatonVariablePane)
            return vbox
        }
    }

    /**
     * Activate or deactivate the floating search window.
     *
     * @param activate true if the search window should be activated, false otherwise
     */
    fun activateSearchWindow(activate: Boolean) {
        searchWindow!!.isVisible = activate
        searchWindow.requestFocus()
    }

    fun toggleSearchWindow() {
        activateSearchWindow(!searchWindow!!.isVisible)
    }

    fun onUpdateViewElements(change: SetChangeListener.Change<out PositionableViewModelElement?>) {
        if (change.wasAdded()) {
            addElement(change.elementAdded)
        } else if (change.wasRemoved()) {
            // Find corresponding view element and remove it
            val viewElement = findViewElement(change.elementRemoved)
            if (viewElement != null) {
                viewElementPane.removeElement(viewElement)
            }
        }
        postUpdate()
    }

    fun postUpdate() {
        if (viewModel.currentToolType != null) {
            for (viewElement in viewElementPane.elements) {
                viewElement.accept(viewModel.currentTool)
            }
        }
    }

    fun initializeViewElements() {
        viewModel.containedPositionableViewModelElementsProperty.forEach { element: PositionableViewModelElement? ->
            this.addElement(
                element
            )
        }
        postUpdate()
    }

    fun addElement(element: PositionableViewModelElement?) {
        element?.view(actionManager, geckoView)?.let { viewElement ->
            // Add view element to current view elements
            viewElementPane.addElement(viewElement)
            if (viewModel.currentToolType != null) {
                viewElement.accept(viewModel.currentTool)
            }
        }
    }

    fun findViewElement(element: PositionableViewModelElement?): ViewElement<*>? {
        return viewElementPane.findViewElement(element)
    }

    fun onToolChanged(newValue: Tool?) {
        acceptTool(newValue)
    }

    fun acceptTool(tool: Tool?) {
        tool!!.visitView(viewElementPane)
        viewElementPane.elements.forEach { element -> element.accept(tool) }

    }

    fun focusedElementChanged(newValue: PositionableViewModelElement?) {
        val newInspector = inspectorFactory.createInspector(newValue)
        currentInspector.set(if ((newInspector != null)) newInspector else emptyInspector)
        if (shortcutHandler != null) {
            currentInspector.get()!!
                .addEventHandler(KeyEvent.ANY, shortcutHandler)
        }
    }

    fun selectionChanged(
        oldValue: MutableSet<PositionableViewModelElement>?,
        newValue: Set<PositionableViewModelElement>?
    ) {
        val toRemove: MutableList<PositionableViewModelElement> = ArrayList()
        for (element in oldValue!!) {
            val viewElement = findViewElement(element)
            if (viewElement == null) {
                toRemove.add(element)
            }
        }
        toRemove.forEach { o: PositionableViewModelElement -> oldValue.remove(o) }

        oldValue
            .map { this.findViewElement(it) }
            .forEach { viewElement -> viewElement!!.isSelected = false }
        newValue
            ?.map { this.findViewElement(it) }
            ?.forEach { viewElement -> viewElement!!.isSelected = true }
        viewElementPane.onSelectionChanged()
    }

    fun changeContextMenu(contextMenu: ContextMenu?) {
        this.contextMenu?.hide()
        this.contextMenu = contextMenu
    }

    val currentViewElements: Set<ViewElement<*>>
        get() = viewElementPane.elements

    companion object {
        const val DEFAULT_ANCHOR_VALUE = 18.0
        const val LEFT_ANCHOR_VALUE = 15.0
    }
}
