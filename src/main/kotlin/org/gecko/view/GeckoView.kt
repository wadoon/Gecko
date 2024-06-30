package org.gecko.view

import javafx.application.Platform
import javafx.beans.Observable
import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.ObservableSet
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TabPane.TabClosingPolicy
import javafx.scene.input.KeyCombination
import javafx.scene.input.Mnemonic
import org.gecko.actions.Action
import org.gecko.application.GeckoManager
import org.gecko.lint.IssuesView
import org.gecko.lint.ModelIssuesView
import org.gecko.view.menubar.ToolbarController
import org.gecko.view.views.EditorView
import org.gecko.view.views.ViewFactory
import org.gecko.viewmodel.*
import org.gecko.viewmodel.booleanProperty
import tornadofx.*
import kotlin.error

/**
 * Represents the View component of a Gecko project. Holds a [ViewFactory], a current [EditorView] and a
 * reference to the [GModel]. Contains methods for managing the [EditorView] shown in the graphic
 * editor.
 */
class GeckoView(val manager: GeckoManager, var viewModel: GModel) : View() {
    val viewFactory: ViewFactory = ViewFactory(viewModel.actionManager, this)
    val currentViewProperty: Property<EditorView?> = nullableObjectProperty()
    val openedViews: MutableList<EditorView> = ArrayList()
    val darkModeProperty = booleanProperty(false)
    var hasBeenFocused = false
    val mnemonicsProperty = listProperty<Mnemonic>(FXCollections.observableArrayList())


    val outlineView = OutlineView(viewModel)
    val versionView = VersionManagerPane(viewModel)
    val modelIssuesView = ModelIssuesView(viewModel)
    val importIssuesView = IssuesView()
    var inspectorItem by singleAssign<DrawerItem>()

    val drawerRight = drawer(Side.RIGHT, floatingContent = false) {
        inspectorItem = item("Inspector") { }
        item(outlineView, true, false)
        item(versionView, false, false)
    }

    val drawerBottom = drawer(Side.BOTTOM, floatingContent = false) {
        item(modelIssuesView, false, false)
        item(importIssuesView, false, false)
    }

    val centerPane: TabPane = TabPane()

    val toolbar = ToolbarController(manager, this, viewModel, viewModel.actionManager)

    override val root = borderpane {
        top = toolbar.root
        center = centerPane
        right = drawerRight
        bottom = drawerBottom

        //importIssuesView.problems.add(Problem("Test ERROR", 10.0))
    }

    init {
        val geckoCss = GeckoView::class.java.getResource(STYLE_SHEET_LIGHT)?.toString()
            ?: error("Could not find $STYLE_SHEET_LIGHT in resources")
        root.stylesheets.add(geckoCss)

        //CSSFX.start(mainPane)

        // Listener for current editor
        viewModel.currentEditorProperty.addListener { observable, oldValue, newValue ->
            this.onUpdateCurrentEditorFromViewModel(
                newValue
            )
        }
        viewModel.openedEditorsProperty.addListener { observable, oldValue, newValue ->
            this.onOpenedEditorChanged(newValue)
        }

        centerPane.isPickOnBounds = false
        centerPane.isFocusTraversable = false
        centerPane.selectionModel.selectedItemProperty()
            .addListener { observable, oldValue, newValue ->
                this.onUpdateCurrentEditorToViewModel(
                    newValue
                )
            }

        // Initial view
        val x = viewModel.currentEditor!!.editor(viewFactory.actionManager, viewFactory.geckoView)
        currentViewProperty.value = x
        constructTab(x, viewModel.currentEditor!!)
        centerPane.tabClosingPolicy = TabClosingPolicy.UNAVAILABLE

        centerPane.isPickOnBounds = false
        refreshView()

        centerPane.focusedProperty()
            .addListener { observable: ObservableValue<out Boolean>?, _: Boolean?, newValue: Boolean ->
                if (newValue) {
                    currentViewProperty.value!!.focus()
                }
            }

        centerPane.sceneProperty()
            .addListener { observable: ObservableValue<out Scene?>?, oldValue: Scene?, newValue: Scene? ->
                if (newValue == null) {
                    return@addListener
                }
                newValue.focusOwnerProperty()
                    .addListener { observable1: ObservableValue<out Node?>?, oldValue1: Node?, newValue1: Node? ->
                        if (currentViewProperty.value!!.currentViewElements.isNotEmpty() && !hasBeenFocused) {
                            focusCenter(currentViewProperty.value!!.viewModel)
                        }
                        hasBeenFocused = true
                    }
            }
    }

    fun onUpdateCurrentEditorFromViewModel(newValue: EditorViewModel?) {
        currentViewProperty.value = openedViews.find { it.viewModel == newValue }
        refreshView()
    }

    fun onOpenedEditorChanged(newValue: ObservableSet<EditorViewModel>?) {
        if (newValue != null) {
            for (editorViewModel in newValue) {
                if (openedViews.any { it.viewModel == editorViewModel }) {
                    continue
                }

                val newEditorView = editorViewModel.editor(viewFactory.actionManager, this)

                if (!openedViews.contains(newEditorView)) {
                    handleUserTabChange(constructTab(newEditorView, editorViewModel))
                    Platform.runLater { focusCenter(editorViewModel) }
                }
            }

            val editorViewModelsToRemove = openedViews
                .map { obj -> obj.viewModel }
                .filter { editorViewModel: EditorViewModel -> !newValue.contains(editorViewModel) }
            if (editorViewModelsToRemove.isNotEmpty()) {
                removeEditorViews(editorViewModelsToRemove)
            }
        }
        if (openedViews.size == 1) {
            centerPane.tabClosingPolicy = TabClosingPolicy.UNAVAILABLE
        } else {
            centerPane.tabClosingPolicy = TabClosingPolicy.ALL_TABS
        }
    }

    fun removeEditorViews(editorViewModelsToRemove: List<EditorViewModel>) {
        val editorViewsToRemove = openedViews
            .filter { editorViewModelsToRemove.contains(it.viewModel) }
        editorViewsToRemove.forEach { centerPane.tabs.remove(it.currentView) }
        openedViews.removeAll(editorViewsToRemove)
    }

    fun constructTab(editorView: EditorView, editorViewModel: EditorViewModel): Tab {
        openedViews.add(editorView)
        val tab = editorView.currentView
        val graphic = tab.graphic
        graphic.onMouseClicked = EventHandler { _ -> handleUserTabChange(tab) }
        centerPane.tabs.add(tab)

        editorView.currentView.onClosed = EventHandler { event ->
            openedViews.remove(editorView)
            viewModel.openedEditorsProperty.remove(editorViewModel)
        }
        return tab
    }

    fun handleUserTabChange(tab: Tab) {
        if (getView(tab)!!.viewModel == viewModel.currentEditor) {
            return
        }
        currentViewProperty.value = getView(tab)
        val next = currentViewProperty.value!!.viewModel.currentSystem
        val switchAction: Action = viewModel.actionManager
            .actionFactory
            .createViewSwitchAction(next, currentViewProperty.value!!.viewModel.isAutomatonEditor)
        viewModel.actionManager.run(switchAction)
    }

    fun refreshView() {
        centerPane.selectionModel.select(currentViewProperty.value!!.currentView)
        root.left = currentViewProperty.value!!.drawToolbar()
        inspectorItem.children.setAll(currentViewProperty.value!!.drawInspector())

        currentViewProperty.value!!.currentInspector.addListener { _: Observable? ->
            inspectorItem.children.setAll(currentViewProperty.value!!.drawInspector())
        }

        currentViewProperty.value!!.focus()
    }

    fun onUpdateCurrentEditorToViewModel(newValue: Tab?) {
        if (getView(newValue)!!.viewModel == viewModel.currentEditor) {
            return
        }
        val switchAction: Action = viewModel.actionManager
            .actionFactory
            .createViewSwitchAction(
                getView(newValue)!!.viewModel.currentSystem,
                getView(newValue)!!.viewModel.isAutomatonEditor
            )
        viewModel.actionManager.run(switchAction)
        refreshView()
    }

    fun focusCenter(editorViewModel: EditorViewModel) {
        // Evaluate the center of all elements by calculating the average position
        if (editorViewModel.viewableElementsProperty.isEmpty()) {
            editorViewModel.pivot = Point2D(0.0, 0.0)
            return
        }

        val center = editorViewModel.viewableElements
            .map { it.center }
            .reduce { a, b -> a + b }
            .multiply(1.0 / editorViewModel.viewableElements.size)

        editorViewModel.pivot = center
    }

    val allDisplayedElements: ObservableList<PositionableViewModelElement>
        /**
         * Returns all displayed elements in the current view.
         *
         * @return a set of all displayed elements in the current view
         */
        get() = viewModel.currentEditor!!.viewableElements

    fun toggleAppearance() {
        isDarkMode = !isDarkMode
        //mainPane.getStylesheets().clear();
        /*mainPane.getStylesheets()
            .add(Objects.requireNonNull(GeckoView.class.getResource(darkMode ? STYLE_SHEET_DARK : STYLE_SHEET_LIGHT))
                .toString());*/
    }

    var isDarkMode: Boolean by darkModeProperty

    fun getView(tab: Tab?) =
        openedViews.firstOrNull { editorView: EditorView? -> editorView!!.currentView === tab }

    val currentView: EditorView?
        get() = currentViewProperty.value

    fun addMnemonic(node: Node?, kc: KeyCombination?): Mnemonic {
        val mn = Mnemonic(node, kc)
        mnemonicsProperty.add(mn)
        return mn
    }

    companion object {
        const val STYLE_SHEET_LIGHT = "/styles/gecko.css"
        const val STYLE_SHEET_DARK = "/styles/gecko-dark.css"
    }
}

