package org.gecko.viewmodel

import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.geometry.Point2D
import kotlin.math.max
import kotlin.math.min
import org.gecko.actions.ActionManager
import org.gecko.tools.*
import org.gecko.view.GeckoView
import org.gecko.view.views.EditorView
import org.gecko.view.views.shortcuts.AutomatonEditorViewShortcutHandler
import org.gecko.view.views.shortcuts.SystemEditorViewShortcutHandler
import tornadofx.getValue
import tornadofx.setValue

/**
 * Represents the view model correspondent to an [EditorView][org.gecko.view.views.EditorView],
 * holding relevant items like the [ActionManager], the current- and
 * parent-[SystemsViewModel][System]s, the contained [PositionableElement]s, the [SelectionManager]
 * and others, updating the view model of the Gecko project.
 */
class EditorViewModel(
    val actionManager: ActionManager,
    val currentSystem: System,
    val parentSystem: System?,
    val isAutomatonEditor: Boolean,
) : Openable {
    val viewableElementsProperty = listProperty<PositionableElement>()
    val viewableElements: ObservableList<PositionableElement> by viewableElementsProperty

    val tools: MutableList<List<Tool>> = FXCollections.observableArrayList()
    val selectionManager = SelectionManager()
    val pivotProperty: Property<Point2D> = objectProperty(Point2D.ZERO)
    val zoomScaleProperty: DoubleProperty = doubleProperty(DEFAULT_ZOOM_SCALE)
    val needsRefocusProperty: BooleanProperty = booleanProperty(false)
    val currentToolProperty = SimpleObjectProperty<Tool>()
    val focusedElementProperty = SimpleObjectProperty<PositionableElement>()

    init {
        initializeTools()

        selectionManager.currentSelectionProperty.onChange { _, new ->
            focusedElement = new.firstOrNull()
        }

        setCurrentTool(ToolType.CURSOR)
    }

    fun updateRegions() {
        val regions = currentSystem.automaton.regions
        val states = currentSystem.automaton.states
        for (r in regions) {
            r.states.clear()
            for (stateViewModel in states) {
                r.checkStateInRegion(stateViewModel)
            }
        }
    }

    /**
     * Returns the [Region]s that contain the given [State] by checking if the state is in set of
     * states of the region.
     *
     * @param state the [State] to get the containing [Region]s for
     * @return the [Region]s that contain the given [State]
     */
    fun getRegions(state: State): List<Region> {
        val regions = currentSystem.automaton.regions
        return regions.filter { it.checkStateInRegion(state) }
    }

    var currentTool: Tool by currentToolProperty
    val currentToolType: ToolType
        get() = currentTool.toolType

    fun setCurrentTool(currentToolType: ToolType) {
        getTool(currentToolType)?.let { currentTool = it }
    }

    fun getTool(toolType: ToolType): Tool? {
        return tools.flatten().find { it.toolType == toolType }
    }

    var focusedElement: PositionableElement?
        get() = focusedElementProperty.value
        set(focusedElement) {
            focusedElementProperty.value = focusedElement
        }

    fun initializeTools() {
        tools.add(
            listOf(
                CursorTool(actionManager, selectionManager, this),
                MarqueeTool(actionManager, this),
                PanTool(actionManager),
                ZoomTool(actionManager)
            )
        )
        if (isAutomatonEditor) {
            tools.add(
                listOf(
                    StateCreatorTool(actionManager),
                    EdgeCreatorTool(actionManager),
                    RegionCreatorTool(actionManager)
                )
            )
        } else {
            tools.add(
                listOf(
                    SystemCreatorTool(actionManager),
                    SystemConnectionCreatorTool(actionManager),
                    VariableBlockCreatorTool(actionManager)
                )
            )
        }
    }

    fun moveToFocusedElement() {
        if (focusedElementProperty.value != null) {
            pivot = focusedElementProperty.value!!.center
        }
    }

    var pivot: Point2D
        get() = pivotProperty.value
        set(pivot) {
            pivotProperty.value = pivot
            needsRefocus()
        }

    fun updatePivot(pivot: Point2D) {
        pivotProperty.value = pivot
    }

    fun needsRefocus() {
        needsRefocusProperty.set(true)
    }

    val zoomScale: Double
        get() = zoomScaleProperty.get()

    fun zoom(factor: Double, pivot: Point2D) {
        require(!(factor < 0)) { "Zoom factor must be positive" }
        val oldScale = zoomScaleProperty.get()
        zoomScaleProperty.set(
            min(max(zoomScaleProperty.get() * factor, MIN_ZOOM_SCALE), MAX_ZOOM_SCALE)
        )
        this.pivot =
            this.pivot.add(
                pivot.subtract(this.pivot).multiply(zoomScaleProperty.get() / oldScale - 1)
            )
    }

    fun zoomCenter(factor: Double) {
        zoom(factor, pivotProperty.value)
    }

    fun getElementsByName(name: String): List<PositionableElement> {
        val matches: MutableList<PositionableElement> = ArrayList()
        // val visitor: PositionableViewModelElementVisitor<*> = ViewElementSearchVisitor(name)
        /*containedPositionableViewModelElementsProperty.forEach( { element: PositionableViewModelElement ->
            val searchResult = element.accept(visitor) as PositionableViewModelElement?
            if (searchResult != null) {
                matches.add(searchResult)
            }
        })*/
        return matches
    }

    /**
     * Returns the elements that are in the given area.
     *
     * @param bound the area in world coordinates
     * @return the elements that are in the given area
     */
    fun getElementsInArea(bound: Bounds): Set<PositionableElement> {
        return viewableElementsProperty
            .filter { element: PositionableElement ->
                if (element.size == Point2D.ZERO) {
                    return@filter false
                }
                val elementBound =
                    BoundingBox(
                        element.position.x,
                        element.position.y,
                        element.size.x,
                        element.size.y
                    )
                bound.intersects(elementBound)
            }
            .toSet()
    }

    companion object {
        const val MAX_ZOOM_SCALE = 2.5
        const val MIN_ZOOM_SCALE = 0.1
        const val DEFAULT_ZOOM_SCALE = 1.0
        const val DEFAULT_ZOOM_STEP = 1.1

        fun updateStateRegionList(
            state: State,
            region: Region,
            change: ListChangeListener.Change<out State>,
            Regions: ObservableList<Region>
        ) {
            while (change.next()) {
                if (change.wasAdded()) {
                    if (change.addedSubList.contains(state)) {
                        Regions.add(region)
                    }
                }
                if (change.wasRemoved()) {
                    if (change.removed.contains(state)) {
                        Regions.remove(region)
                    }
                }
            }
        }

        val defaultZoomStep: Double
            get() = DEFAULT_ZOOM_STEP
    }

    override fun editor(actionManager: ActionManager, geckoView: GeckoView): EditorView {
        val editorView = EditorView(actionManager, this, geckoView)
        editorView.shortcutHandler =
            if (isAutomatonEditor) AutomatonEditorViewShortcutHandler(actionManager, editorView)
            else SystemEditorViewShortcutHandler(actionManager, editorView)
        return editorView
    }
}
