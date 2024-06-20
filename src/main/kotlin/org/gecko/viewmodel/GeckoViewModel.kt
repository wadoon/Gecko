package org.gecko.viewmodel

import javafx.beans.property.Property
import javafx.beans.property.SetProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleSetProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableSet
import javafx.collections.SetChangeListener
import org.gecko.actions.ActionManager
import org.gecko.model.Element
import org.gecko.model.GeckoModel
import tornadofx.getValue
import tornadofx.setValue
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

/**
 * Represents the ViewModel component of a Gecko project, which connects the Model and View. Holds a
 * [ViewModelFactory] and a reference to the [GeckoModel], as well as the current [EditorViewModel]
 * and a list of all opened [EditorViewModel]s. Maps all [PositionableViewModelElement]s to their
 * corresponding [Element]s from Model. Contains methods for managing the [EditorViewModel] and the retained
 * [PositionableViewModelElement]s.
 */
class GeckoViewModel(val geckoModel: GeckoModel) {
    val actionManager = ActionManager(this)
    val modelToViewModel = HashMap<Element, PositionableViewModelElement<*>>()
    val viewModelFactory: ViewModelFactory = ViewModelFactory(actionManager, this, geckoModel.modelFactory)
    val currentEditorProperty: Property<EditorViewModel> = SimpleObjectProperty()
    val openedEditorsProperty: SetProperty<EditorViewModel> = SimpleSetProperty(FXCollections.observableSet())

    var currentEditor by currentEditorProperty

    init {
        // Create root system view model
        val rootSystemViewModel = viewModelFactory.createSystemViewModelFrom(geckoModel.root)
        switchEditor(rootSystemViewModel, false)

        currentEditorProperty.onChange { old, _ ->
            old?.selectionManager?.deselectAll()
            updateEditors()
        }
    }

    /**
     * Switches the current [EditorViewModel] to the one that contains the given [SystemViewModel] and has
     * the correct type (automaton  or system editor). If the [EditorViewModel] does not exist, a new one is
     * created.
     *
     * @param nextSystemViewModel the [SystemViewModel] that should be displayed in the editor
     * @param isAutomatonEditor   true if the editor should be an automaton editor, false if it should be a system
     * editor
     */
    fun switchEditor(nextSystemViewModel: SystemViewModel, isAutomatonEditor: Boolean) {
        openedEditorsProperty.stream()
            .filter { editorViewModel: EditorViewModel -> (editorViewModel.currentSystem == nextSystemViewModel && editorViewModel.isAutomatonEditor == isAutomatonEditor) }
            .findFirst()
            .ifPresentOrElse(
                { editorViewModel: EditorViewModel -> this.currentEditor = editorViewModel },
                { setupNewEditorViewModel(nextSystemViewModel, isAutomatonEditor) })
    }

    fun setupNewEditorViewModel(nextSystemViewModel: SystemViewModel, isAutomatonEditor: Boolean) {
        var parent: SystemViewModel? = null
        if (nextSystemViewModel.target.parent != null) {
            parent = getViewModelElement(nextSystemViewModel.target.parent!!) as SystemViewModel
        }
        val editorViewModel = viewModelFactory.createEditorViewModel(nextSystemViewModel, parent, isAutomatonEditor)
        openedEditorsProperty.add(editorViewModel)
        currentEditor = editorViewModel
    }

    fun getViewModelElement(element: Element): PositionableViewModelElement<*> {
        return modelToViewModel[element]!!
    }

    fun getViewModelElements(elements: Set<Element>): MutableSet<PositionableViewModelElement<*>> {
        val positionableViewModelElements: MutableSet<PositionableViewModelElement<*>> = HashSet()
        elements.forEach { positionableViewModelElements.add(getViewModelElement(it)) }

        positionableViewModelElements.removeIf { obj: PositionableViewModelElement<*>? -> Objects.isNull(obj) }

        return positionableViewModelElements
    }

    /**
     * Adds a new [PositionableViewModelElement] to the [GeckoViewModel]. The element is mapped to its
     * corresponding [Element] from the model. The [PositionableViewModelElement] is then added to the
     * correct [EditorViewModel].
     *
     * @param element the [PositionableViewModelElement] to add
     */
    fun addViewModelElement(element: PositionableViewModelElement<*>) {
        modelToViewModel[element.target] = element
        updateEditors()
    }

    /**
     * Deletes a [PositionableViewModelElement] from the [GeckoViewModel]. The element is removed from the
     * mapping and from all [EditorViewModel]s. The selection managers of the editors are updated and the element
     * is removed from the editor that displays it.
     *
     * @param element the [PositionableViewModelElement] to delete
     */
    fun deleteViewModelElement(element: PositionableViewModelElement<*>) {
        modelToViewModel.remove(element.target)
        updateSelectionManagers(element)
        updateEditors()
    }

    fun updateSelectionManagers(removedElement: PositionableViewModelElement<*>) {
        openedEditorsProperty.forEach(
            Consumer { editorViewModel: EditorViewModel ->
                editorViewModel.selectionManager.updateSelections(
                    removedElement
                )
            })
    }

    fun updateEditors() {
        openedEditorsProperty.forEach(Consumer { editorViewModel: EditorViewModel -> this.updateEditor(editorViewModel) })
        val editorViewModelsToDelete = openedEditorsProperty.stream()
            .filter { editorViewModel: EditorViewModel -> !modelToViewModel.containsValue(editorViewModel.currentSystem) }
            .collect(Collectors.toSet())
        openedEditorsProperty.removeAll(editorViewModelsToDelete)
    }

    fun updateEditor(editorViewModel: EditorViewModel) {
        editorViewModel.removePositionableViewModelElements(
            editorViewModel.containedPositionableViewModelElementsProperty
                .filter { element: PositionableViewModelElement<*> -> !modelToViewModel.containsKey(element.target) }
                .toSet())

        addPositionableViewModelElementsToEditor(editorViewModel)
    }

    fun addPositionableViewModelElementsToEditor(editorViewModel: EditorViewModel) {
        val currentSystem = editorViewModel.currentSystem.target
        if (editorViewModel.isAutomatonEditor) {
            editorViewModel.addPositionableViewModelElements(
                getViewModelElements(currentSystem.automaton.allElements)
            )
        } else {
            editorViewModel.addPositionableViewModelElements(getViewModelElements(currentSystem.allElements))
        }
    }

    fun getSystemViewModelWithPort(portViewModel: PortViewModel): SystemViewModel? {
        val system = geckoModel.getSystemWithVariable(portViewModel.target) ?: return null
        return getViewModelElement(system) as SystemViewModel
    }
}


fun <T> ObservableValue<T>.onChange(op: (T, T) -> Unit) =
    apply { addListener { o, oldValue, newValue -> op(oldValue, newValue) } }


fun <T> ObservableSet<T>.onChange(op: (SetChangeListener.Change<out T>) -> Unit) = apply {
    addListener(SetChangeListener { op(it) })
}