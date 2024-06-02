package org.gecko.view

import fr.brouillard.oss.cssfx.CSSFX
import javafx.application.Platform
import javafx.beans.Observable
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableSet
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.TabPane.TabClosingPolicy
import javafx.scene.input.*
import javafx.scene.layout.BorderPane
import org.gecko.actions.*
import org.gecko.view.menubar.RibbonBuilder
import org.gecko.view.views.*
import org.gecko.viewmodel.EditorViewModel
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.PositionableViewModelElement
import tornadofx.plus
import java.util.*

/**
 * Represents the View component of a Gecko project. Holds a [ViewFactory], a current [EditorView] and a
 * reference to the [GeckoViewModel]. Contains methods for managing the [EditorView] shown in the graphic
 * editor.
 */
class GeckoView(var viewModel: GeckoViewModel) {
    val mainPane: BorderPane
    val centerPane: TabPane
    val viewFactory: ViewFactory

    val currentViewProperty: Property<EditorView?>

    val openedViews: MutableList<EditorView?>

    val darkModeProperty = SimpleBooleanProperty(false)

    var hasBeenFocused = false
    val mnemonicsProperty = SimpleListProperty(FXCollections.observableArrayList<Mnemonic>())

    init {
        this.viewModel = viewModel
        this.mainPane = BorderPane()
        this.centerPane = TabPane()
        this.viewFactory = ViewFactory(viewModel.actionManager, this)
        this.openedViews = ArrayList()
        this.currentViewProperty = SimpleObjectProperty()

        mainPane.stylesheets
            .add(Objects.requireNonNull(GeckoView::class.java.getResource(STYLE_SHEET_LIGHT)).toString())

        CSSFX.start(mainPane)

        // Listener for current editor
        viewModel.currentEditorProperty.addListener { observable, oldValue, newValue ->
            this.onUpdateCurrentEditorFromViewModel(
                observable, oldValue, newValue
            )
        }
        viewModel.openedEditorsProperty.addListener { observable, oldValue, newValue ->
            this.onOpenedEditorChanged(observable, oldValue, newValue)
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
        val menuBar = RibbonBuilder(this, viewModel.actionManager).build()
        mainPane.top = menuBar
        mainPane.center = centerPane

        // Initial view
        currentViewProperty.value = viewFactory.createEditorView(
            viewModel.currentEditor!!,
            viewModel.currentEditor!!.isAutomatonEditor
        )
        constructTab(currentViewProperty.value, viewModel.currentEditor!!)
        centerPane.tabClosingPolicy = TabClosingPolicy.UNAVAILABLE

        centerPane.isPickOnBounds = false
        refreshView()

        centerPane.focusedProperty()
            .addListener { observable: ObservableValue<out Boolean>?, oldValue: Boolean?, newValue: Boolean ->
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

    fun onUpdateCurrentEditorFromViewModel(
        observable: ObservableValue<out EditorViewModel?>, oldValue: EditorViewModel?, newValue: EditorViewModel?
    ) {
        currentViewProperty.setValue(openedViews.find { it!!.viewModel == newValue })
        refreshView()
    }

    fun onOpenedEditorChanged(
        observable: ObservableValue<out ObservableSet<EditorViewModel>?>, oldValue: ObservableSet<EditorViewModel>?,
        newValue: ObservableSet<EditorViewModel>?
    ) {
        if (newValue != null) {
            for (editorViewModel in newValue) {
                if (openedViews.stream()
                        .anyMatch { editorView: EditorView? -> editorView!!.viewModel == editorViewModel }
                ) {
                    continue
                }

                val newEditorView =
                    viewFactory.createEditorView(editorViewModel, editorViewModel.isAutomatonEditor)

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

        mainPane.left = currentViewProperty.value!!.drawToolbar()
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
            .mapNotNull { it.center }
            .reduce { a, b -> a + b }
            .multiply(1.0 / editorViewModel.positionableViewModelElements.size)

        editorViewModel.pivot = center
    }

    val allDisplayedElements: Set<PositionableViewModelElement<*>>
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

    fun darkModeProperty(): SimpleBooleanProperty {
        return darkModeProperty
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

    companion object {
        const val STYLE_SHEET_LIGHT = "/styles/gecko.css"
        const val STYLE_SHEET_DARK = "/styles/gecko-dark.css"
    }
}
