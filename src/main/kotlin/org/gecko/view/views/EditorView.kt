package org.gecko.view.views

import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.input.ContextMenuEvent
import javafx.scene.input.KeyEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import org.gecko.actions.ActionManager
import org.gecko.tools.Tool
import org.gecko.view.GeckoView
import org.gecko.view.ResourceHandler
import org.gecko.view.ToolBarView
import org.gecko.view.contextmenu.ViewContextMenuBuilder
import org.gecko.view.inspector.Inspector
import org.gecko.view.inspector.InspectorFactory
import org.gecko.view.views.shortcuts.ShortcutHandler
import org.gecko.view.views.viewelement.ViewElement
import org.gecko.viewmodel.*

/**
 * Represents a displayable view in the Gecko Graphic Editor, holding a collection of displayed
 * [ViewElement]s and other items specific to their visualisation.
 */
class EditorView(
    val actionManager: ActionManager,
    val viewModel: EditorViewModel,
    val geckoView: GeckoView
) {
    val currentView: Tab
    val currentViewPane: StackPane = StackPane()

    val toolBar = ToolBarView(actionManager, this, viewModel).root
    val inspectorFactory: InspectorFactory = InspectorFactory(actionManager, viewModel)
    val emptyInspector: Inspector = Inspector(ArrayList(), actionManager)
    val searchWindow: Node?

    val viewElementPane: ViewElementPane

    var currentInspector: ObjectProperty<Inspector?> = nullableObjectProperty()

    var shortcutHandler: ShortcutHandler? = null
        /**
         * Set the shortcut handler for the editor view. This will be used to handle keyboard
         * shortcuts.
         */
        set(value) {
            field = value
            currentViewPane.addEventHandler(KeyEvent.ANY, shortcutHandler)
            toolBar.addEventHandler(KeyEvent.ANY, shortcutHandler)
            if (currentInspector.get() != null) {
                currentInspector.get()!!.addEventHandler(KeyEvent.ANY, shortcutHandler)
            }
        }

    var contextMenu: ContextMenu?

    init {
        this.currentInspector = SimpleObjectProperty(emptyInspector)
        this.viewElementPane = ViewElementPane(viewModel)

        this.currentView = Tab("", currentViewPane)

        val tabName: StringProperty = SimpleStringProperty("Error_Name")
        tabName.bind(
            Bindings.createStringBinding(
                {
                    val name = viewModel.currentSystem.name
                    name +
                            (if (viewModel.isAutomatonEditor) " (${ResourceHandler.automaton})"
                            else " (${ResourceHandler.system})")
                },
                viewModel.currentSystem.nameProperty
            )
        )

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
        viewModel.viewableElementsProperty.onListChange { this.onUpdateViewElements() }

        // Inspector creator listener
        viewModel.focusedElementProperty.onChange { oldValue, newValue ->
            this.focusedElementChanged(newValue)
        }

        viewModel.selectionManager.currentSelectionProperty.onChange { oldValue, newValue ->
            this.selectionChanged(oldValue, newValue)
        }

        // Set current tool
        viewModel.currentToolProperty.addListener { observable: ObservableValue<out Tool>,
                                                    oldValue: Tool?,
                                                    newValue: Tool? ->
            this.onToolChanged(newValue)
        }

        val contextMenuBuilder = ViewContextMenuBuilder(viewModel.actionManager, viewModel, this)
        this.contextMenu = contextMenuBuilder.build()
        currentViewPane.onContextMenuRequested =
            EventHandler<ContextMenuEvent> { event: ContextMenuEvent ->
                changeContextMenu(contextMenuBuilder.contextMenu)
                contextMenu!!.show(currentViewPane, event.screenX, event.screenY)
                event.consume()
            }

        initializeViewElements()
        acceptTool(viewModel.currentTool)
        focus()
    }

    /** Focus the center pane of the editor view. */
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
            currentInspector.get()!!.addEventHandler(KeyEvent.ANY, shortcutHandler)
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

    fun onUpdateViewElements() {
        viewElementPane.elements.setAll(
            viewModel.viewableElements.map { findViewElement(it) ?: addElement(it) }
        )
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
        viewModel.viewableElementsProperty.forEach { element: PositionableElement? ->
            this.addElement(element)
        }
        postUpdate()
    }

    fun addElement(element: PositionableElement?) =
        element?.view(actionManager, geckoView)?.let { viewElement ->
            // Add view element to current view elements
            viewElementPane.addElement(viewElement)
            viewElement.accept(viewModel.currentTool)
            viewElement
        }

    fun findViewElement(element: PositionableElement) = viewElementPane.findViewElement(element)

    fun onToolChanged(newValue: Tool?) {
        acceptTool(newValue)
    }

    fun acceptTool(tool: Tool?) {
        tool!!.visitView(viewElementPane)
        viewElementPane.elements.forEach { element -> element.accept(tool) }
    }

    fun focusedElementChanged(newValue: PositionableElement?) {
        val newInspector = inspectorFactory.createInspector(newValue)
        currentInspector.set(if ((newInspector != null)) newInspector else emptyInspector)
        if (shortcutHandler != null) {
            currentInspector.get()!!.addEventHandler(KeyEvent.ANY, shortcutHandler)
        }
    }

    fun selectionChanged(
        oldValue: MutableSet<PositionableElement>?,
        newValue: Set<PositionableElement>?
    ) {
        val toRemove: MutableList<PositionableElement> = ArrayList()
        for (element in oldValue!!) {
            val viewElement = findViewElement(element)
            if (viewElement == null) {
                toRemove.add(element)
            }
        }
        toRemove.forEach { o: PositionableElement -> oldValue.remove(o) }

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

    val currentViewElements
        get() = viewElementPane.elements

    companion object {
        const val DEFAULT_ANCHOR_VALUE = 18.0
        const val LEFT_ANCHOR_VALUE = 15.0
    }
}
