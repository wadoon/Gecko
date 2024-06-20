package org.gecko.viewmodel

import javafx.beans.property.*
import javafx.collections.*
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.geometry.Point2D
import org.gecko.actions.*
import org.gecko.tools.*
import org.gecko.view.views.ViewElementSearchVisitor
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import kotlin.math.max
import kotlin.math.min

/**
 * Represents the view model correspondent to an [EditorView][org.gecko.view.views.EditorView], holding relevant
 * items like the [ActionManager], the current- and parent-[SystemsViewModel][SystemViewModel]s, the
 * contained [PositionableViewModelElement]s, the [SelectionManager] and others, updating the view model of
 * the Gecko project.
 */

class EditorViewModel(
    val actionManager: ActionManager,
    val currentSystem: SystemViewModel,
    val parentSystem: SystemViewModel?,
    val isAutomatonEditor: Boolean,
) {
    val containedPositionableViewModelElementsProperty: ObservableSet<PositionableViewModelElement> = FXCollections.observableSet()
    val tools: MutableList<List<Tool>> = FXCollections.observableArrayList()
    val selectionManager = SelectionManager()
    val pivotProperty: Property<Point2D> = SimpleObjectProperty(Point2D.ZERO)
    val zoomScaleProperty: DoubleProperty = SimpleDoubleProperty(DEFAULT_ZOOM_SCALE)
    val needsRefocusProperty: BooleanProperty = SimpleBooleanProperty(false)
    val currentToolProperty = SimpleObjectProperty<Tool>()
    val focusedElementProperty = SimpleObjectProperty<PositionableViewModelElement>()

    init {
        initializeTools()

        selectionManager.currentSelectionProperty.onChange { old, new ->
            focusedElement = new.firstOrNull()
        }

        setCurrentTool(ToolType.CURSOR)
    }

    fun updateRegions() {
//        val automaton:  = currentSystem.target.automaton
//        val regionViewModels = containedPositionableViewModelElementsProperty
//            .filter { it.target in automaton.regions }
//            .map { it as RegionViewModel }
//            .toSet()
//        val stateViewModels = containedPositionableViewModelElementsProperty
//            .filter { it.target in automaton.states }
//            .map { it as StateViewModel? }
//            .toSet()
//        for (regionViewModel in regionViewModels) {
//            regionViewModel.clearStates()
//            for (stateViewModel in stateViewModels) {
//                regionViewModel.checkStateInRegion(stateViewModel!!)
//                regionViewModel.updateTarget()
//            }
//        }
    }

    /**
     * Returns the [RegionViewModel]s that contain the given [StateViewModel] by checking if the state is in
     * set of states of the region.
     *
     * @param stateViewModel the [StateViewModel] to get the containing [RegionViewModel]s for
     * @return the [RegionViewModel]s that contain the given [StateViewModel]
     */
    fun getRegionViewModels(stateViewModel: StateViewModel): ObservableList<RegionViewModel> {
        val regionViewModels = FXCollections.observableArrayList<RegionViewModel>()
        val regions = currentSystem.automaton.regions
        val containingStateRegions =
            regions.filter { it.states.contains(stateViewModel) }.toList()
        val containedRegionViewModels = containedPositionableViewModelElementsProperty.stream()
            .filter { containingStateRegions.contains(it) }
            .map { it as RegionViewModel }
            .toList()
        regionViewModels.addAll(containedRegionViewModels)

        for (region in containedPositionableViewModelElementsProperty.stream()
            .filter { element: PositionableViewModelElement -> regions.contains(element) }
            .map { element: PositionableViewModelElement -> element as RegionViewModel }
            .toList()) {
            region.statesProperty.addListener { change: ListChangeListener.Change<out StateViewModel> ->
                updateStateRegionList(stateViewModel, region, change, regionViewModels)
            }
        }

        return regionViewModels
    }

    val currentToolType: ToolType?
        get() = currentToolProperty.value.toolType

    val currentTool: Tool
        get() = currentToolProperty.value

    fun setCurrentTool(currentToolType: ToolType) {
        val tool = getTool(currentToolType)
        if (tool != null) {
            currentToolProperty.value = tool
        }
    }

    fun getTool(toolType: ToolType): Tool? {
        return tools
            .flatten()
            .find { it.toolType == toolType }
    }

    var focusedElement: PositionableViewModelElement?
        get() = focusedElementProperty.value
        set(focusedElement) {
            focusedElementProperty.value = focusedElement
        }

    /**
     * Adds the given elements to the current [EditorViewModel]. They will then be displayed in the view.
     *
     * @param elements the elements to add
     */
    fun addPositionableViewModelElements(elements: MutableList<PositionableViewModelElement>) {
        elements.removeAll(containedPositionableViewModelElementsProperty)
        containedPositionableViewModelElementsProperty.addAll(elements)
    }

    /**
     * Removes the given elements from the current [EditorViewModel]. They will then no longer be displayed in the
     * view.
     *
     * @param elements the elements to remove
     */
    fun removePositionableViewModelElements(elements: Set<PositionableViewModelElement>) {
        elements.forEach { containedPositionableViewModelElementsProperty.remove(it) }
    }

    val positionableViewModelElements: Set<PositionableViewModelElement>
        get() = containedPositionableViewModelElementsProperty

    fun initializeTools() {
        tools.add(
            listOf(
                CursorTool(actionManager, selectionManager, this), MarqueeTool(actionManager, this),
                PanTool(actionManager), ZoomTool(actionManager)
            )
        )
        if (isAutomatonEditor) {
            tools.add(
                listOf(
                    StateCreatorTool(actionManager), EdgeCreatorTool(actionManager),
                    RegionCreatorTool(actionManager)
                )
            )
        } else {
            tools.add(
                listOf(
                    SystemCreatorTool(actionManager), SystemConnectionCreatorTool(actionManager),
                    VariableBlockCreatorTool(actionManager)
                )
            )
        }
    }

    fun moveToFocusedElement() {
        if (focusedElementProperty.value != null) {
            pivot = focusedElementProperty.value!!.center!!
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
        this.pivot = this.pivot.add(pivot.subtract(this.pivot).multiply(zoomScaleProperty.get() / oldScale - 1))
    }

    fun zoomCenter(factor: Double) {
        zoom(factor, pivotProperty.value)
    }

    fun getElementsByName(name: String): List<PositionableViewModelElement> {
        val matches: MutableList<PositionableViewModelElement> = ArrayList()
        val visitor: PositionableViewModelElementVisitor<*> = ViewElementSearchVisitor(name)
        containedPositionableViewModelElementsProperty.forEach(Consumer { element: PositionableViewModelElement ->
            val searchResult = element.accept(visitor) as PositionableViewModelElement?
            if (searchResult != null) {
                matches.add(searchResult)
            }
        })
        return matches
    }

    /**
     * Returns the elements that are in the given area.
     *
     * @param bound the area in world coordinates
     * @return the elements that are in the given area
     */
    fun getElementsInArea(bound: Bounds): Set<PositionableViewModelElement> {
        return containedPositionableViewModelElementsProperty.stream()
            .filter { element: PositionableViewModelElement ->
                if (element.size == Point2D.ZERO) {
                    return@filter false
                }
                val elementBound: Bounds =
                    BoundingBox(
                        element.position.x, element.position.y, element.size.x,
                        element.size.y
                    )
                bound.intersects(elementBound)
            }.collect(Collectors.toSet())
    }

    companion object {
        const val MAX_ZOOM_SCALE = 2.5
        const val MIN_ZOOM_SCALE = 0.1
        const val DEFAULT_ZOOM_SCALE = 1.0
        const val DEFAULT_ZOOM_STEP = 1.1
        fun updateStateRegionList(
            stateViewModel: StateViewModel, region: RegionViewModel,
            change: ListChangeListener.Change<out StateViewModel>, regionViewModels: ObservableList<RegionViewModel>
        ) {
            while (change.next()) {
                if (change.wasAdded()) {
                    if (change.addedSubList.contains(stateViewModel)) {
                        regionViewModels.add(region)
                    }
                }
                if (change.wasRemoved()) {
                    if (change.removed.contains(stateViewModel)) {
                        regionViewModels.remove(region)
                    }
                }
            }
        }

        val defaultZoomStep: Double
            get() = DEFAULT_ZOOM_STEP
    }
}
