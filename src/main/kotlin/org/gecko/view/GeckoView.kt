package org.gecko.view

import javafx.application.Platform
import javafx.beans.Observable
import javafx.beans.property.ListProperty
import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableSet
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Dialog
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TabPane.TabClosingPolicy
import javafx.scene.input.KeyCombination
import javafx.scene.input.Mnemonic
import javafx.scene.layout.BorderPane
import org.gecko.actions.Action
import org.gecko.view.menubar.ToolbarBuilder
import org.gecko.view.views.EditorView
import org.gecko.view.views.ViewFactory
import org.gecko.viewmodel.*
import tornadofx.plus

/**
 * Represents the View component of a Gecko project. Holds a [ViewFactory], a current [EditorView] and a
 * reference to the [GeckoViewModel]. Contains methods for managing the [EditorView] shown in the graphic
 * editor.
 */
class GeckoView(var viewModel: GeckoViewModel) {
    val mainPane: BorderPane
    val centerPane: TabPane
    val viewFactory: ViewFactory

    val currentViewProperty: Property<EditorView?> = nullableObjectProperty()

    val openedViews: MutableList<EditorView?>

    val darkModeProperty = booleanProperty(false)

    var hasBeenFocused = false
    val mnemonicsProperty = listProperty<Mnemonic>()

    init {
        this.mainPane = BorderPane()
        this.centerPane = TabPane()
        this.viewFactory = ViewFactory(viewModel.actionManager, this)
        this.openedViews = ArrayList()

        val geckoCss = GeckoView::class.java.getResource(STYLE_SHEET_LIGHT)?.toString()
            ?: error("Could not find $STYLE_SHEET_LIGHT in resources")
        mainPane.stylesheets.add(geckoCss)

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

        // Menubar
        // MenuBar menuBar = new MenuBarBuilder(this, viewModel.getActionManager()).build();
        val menuBar = ToolbarBuilder(this, viewModel, viewModel.actionManager).build()
        mainPane.top = menuBar
        mainPane.center = centerPane

        // Initial view
        currentViewProperty.value = viewModel.currentEditor!!.editor(viewFactory.actionManager, viewFactory.geckoView)
        constructTab(currentViewProperty.value, viewModel.currentEditor!!)
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
        currentViewProperty.value = openedViews.find { it!!.viewModel == newValue }
        refreshView()
    }

    fun onOpenedEditorChanged(newValue: ObservableSet<EditorViewModel>?) {
        if (newValue != null) {
            for (editorViewModel in newValue) {
                if (openedViews.stream()
                        .anyMatch { editorView: EditorView? -> editorView!!.viewModel == editorViewModel }
                ) {
                    continue
                }

                val newEditorView = editorViewModel.editor(viewFactory.actionManager, this)

                if (!openedViews.contains(newEditorView)) {
                    handleUserTabChange(constructTab(newEditorView, editorViewModel))
                    Platform.runLater { focusCenter(editorViewModel) }
                }
            }

            val editorViewModelsToRemove = openedViews
                .map { obj -> obj!!.viewModel }
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
            .filter { editorViewModelsToRemove.contains(it!!.viewModel) }
        editorViewsToRemove.forEach { centerPane.tabs.remove(it!!.currentView) }
        openedViews.removeAll(editorViewsToRemove)
    }

    fun constructTab(editorView: EditorView?, editorViewModel: EditorViewModel): Tab {
        openedViews.add(editorView)
        val tab = editorView!!.currentView
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

        val splitPane = SplitPane(
            currentViewProperty.value!!.drawToolbar(),
            OutlineView(viewModel).root
        )
        splitPane.orientation = Orientation.VERTICAL
        splitPane.setDividerPositions(0.3)
        mainPane.left = splitPane

        mainPane.right = currentViewProperty.value!!.drawInspector()
        currentViewProperty.value!!.currentInspector.addListener { observable: Observable? ->
            mainPane.right = currentViewProperty.value!!.drawInspector()
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
        if (editorViewModel.positionableViewModelElements.isEmpty()) {
            editorViewModel.pivot = Point2D(0.0, 0.0)
            return
        }

        val center = editorViewModel.positionableViewModelElements
            .map { it.center }
            .reduce { a, b -> a + b }
            .multiply(1.0 / editorViewModel.positionableViewModelElements.size)

        editorViewModel.pivot = center
    }

    val allDisplayedElements: Set<PositionableViewModelElement>
        /**
         * Returns all displayed elements in the current view.
         *
         * @return a set of all displayed elements in the current view
         */
        get() = viewModel.currentEditor!!.positionableViewModelElements

    fun toggleAppearance() {
        isDarkMode = !isDarkMode
        //mainPane.getStylesheets().clear();
        /*mainPane.getStylesheets()
            .add(Objects.requireNonNull(GeckoView.class.getResource(darkMode ? STYLE_SHEET_DARK : STYLE_SHEET_LIGHT))
                .toString());*/
    }

    var isDarkMode: Boolean
        get() = darkModeProperty.get()
        set(dark) {
            darkModeProperty.set(dark)
        }

    fun getView(tab: Tab?): EditorView? {
        return openedViews.stream().filter { editorView: EditorView? -> editorView!!.currentView === tab }
            .findFirst().orElseThrow()
    }

    val currentView: EditorView?
        get() = currentViewProperty.value

    fun addMnemonic(node: Node?, kc: KeyCombination?): Mnemonic {
        val mn = Mnemonic(node, kc)
        mnemonicsProperty.add(mn)
        return mn
    }

    fun mnemonicsProperty(): ListProperty<Mnemonic> {
        return mnemonicsProperty
    }


    fun showVersionManager() {
        val dialog = Dialog<Void>()
        dialog.title = "Version Manager"
        dialog.dialogPane = VersionManagerPane()
        dialog.show()
    }

    companion object {
        const val STYLE_SHEET_LIGHT = "/styles/gecko.css"
        const val STYLE_SHEET_DARK = "/styles/gecko-dark.css"
    }
}
