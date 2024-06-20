package org.gecko.viewmodel

import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.collections.*
import org.gecko.actions.ActionManager
import tornadofx.asObservable
import tornadofx.getValue
import tornadofx.setValue
import java.util.*
import java.util.function.Consumer

/**
 * Represents the ViewModel component of a Gecko project, which connects the Model and View. Holds a
 * [ViewModelFactory] and a reference to the [GeckoModel], as well as the current [EditorViewModel]
 * and a list of all opened [EditorViewModel]s. Maps all [PositionableViewModelElement]s to their
 * corresponding [Element]s from Model. Contains methods for managing the [EditorViewModel] and the retained
 * [PositionableViewModelElement]s.
 */
class GeckoViewModel(val root: SystemViewModel = SystemViewModel()) {
    val actionManager = ActionManager(this)
    val currentEditorProperty: ObjectProperty<EditorViewModel?> = nullableObjectProperty()
    var currentEditor by currentEditorProperty

    val openedEditorsProperty: SetProperty<EditorViewModel> = setProperty()
    var openedEditors by openedEditorsProperty

    val knownVariantGroupsProperty = listProperty<VariantGroup>()
    var knownVariantGroups by knownVariantGroupsProperty

    val globalDefinesProperty = stringProperty("")
    var globalDefines: String? by globalDefinesProperty

    val globalCodeProperty = stringProperty("")
    var globalCode: String? by globalCodeProperty

    val allSystems: List<SystemViewModel>
        get() = root.subSystems


    val viewModelFactory = ViewModelFactory(actionManager, this)

    init {
        switchEditor(root, false)

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
        if (nextSystemViewModel.parent != null) {
            parent = nextSystemViewModel.parent
        }
        val editorViewModel = viewModelFactory.createEditorViewModel(nextSystemViewModel, parent, isAutomatonEditor)
        openedEditorsProperty.add(editorViewModel)
        currentEditor = editorViewModel
    }

    /**
     * Adds a new [PositionableViewModelElement] to the [GeckoViewModel]. The element is mapped to its
     * corresponding [Element] from the model. The [PositionableViewModelElement] is then added to the
     * correct [EditorViewModel].
     *
     * @param element the [PositionableViewModelElement] to add
     */
    fun addViewModelElement(element: PositionableViewModelElement) {
        updateEditors()
    }

    /**
     * Deletes a [PositionableViewModelElement] from the [GeckoViewModel]. The element is removed from the
     * mapping and from all [EditorViewModel]s. The selection managers of the editors are updated and the element
     * is removed from the editor that displays it.
     *
     * @param element the [PositionableViewModelElement] to delete
     */
    fun deleteViewModelElement(element: PositionableViewModelElement) {
        updateSelectionManagers(element)
        updateEditors()
    }

    fun updateSelectionManagers(removedElement: PositionableViewModelElement) {
        openedEditorsProperty.forEach(
            Consumer { editorViewModel: EditorViewModel ->
                editorViewModel.selectionManager.updateSelections(
                    removedElement
                )
            })
    }

    fun updateEditors() {
        openedEditorsProperty.forEach { this.updateEditor(it) }
        /*val editorViewModelsToDelete = openedEditorsProperty
            .filter { it.currentSystem == }
            .toSet()
        openedEditorsProperty.removeAll(editorViewModelsToDelete)
         */
    }

    fun updateEditor(editorViewModel: EditorViewModel) {
        addPositionableViewModelElementsToEditor(editorViewModel)
    }

    fun addPositionableViewModelElementsToEditor(editorViewModel: EditorViewModel) {
        val currentSystem = editorViewModel.currentSystem
        if (editorViewModel.isAutomatonEditor) {
            editorViewModel.addPositionableViewModelElements(currentSystem.automaton.allElements)
        } else {
            editorViewModel.addPositionableViewModelElements(currentSystem.allElements)
        }
    }

    fun getSystemViewModelWithPort(portViewModel: PortViewModel): SystemViewModel? =
        root.getChildSystemWithVariable(portViewModel)
}

fun booleanProperty(value: Boolean = false): BooleanProperty = SimpleBooleanProperty(value)
fun doubleProperty(value: Double = 0.0): DoubleProperty = SimpleDoubleProperty(value)
fun floatProperty(value: Float = 0F): FloatProperty = SimpleFloatProperty(value)
fun intProperty(value: Int = 0): IntegerProperty = SimpleIntegerProperty(value)
fun <V> listProperty(value: ObservableList<V> = FXCollections.observableArrayList()): ListProperty<V> =
    SimpleListProperty(value)

fun <V> listProperty(vararg values: V): ListProperty<V> = SimpleListProperty(values.toMutableList().asObservable())
fun longProperty(value: Long = 0): LongProperty = SimpleLongProperty(value)
fun <K, V> mapProperty(value: ObservableMap<K, V> = FXCollections.observableHashMap()): MapProperty<K, V> =
    SimpleMapProperty(value)

fun <T> objectProperty(value: T): ObjectProperty<T> = SimpleObjectProperty(value)
fun <T> nullableObjectProperty(value: T? = null): ObjectProperty<T?> = SimpleObjectProperty(value)
fun <V> setProperty(value: ObservableSet<V> = FXCollections.observableSet()): SetProperty<V> = SimpleSetProperty(value)
fun stringProperty(value: String): StringProperty = SimpleStringProperty(value)


fun <T> ObservableValue<T>.onChange(op: (T, T) -> Unit) =
    apply { addListener { _, oldValue, newValue -> op(oldValue, newValue) } }

fun <T> ObservableList<T>.onListChange(op: (ListChangeListener.Change<out T>) -> Unit) = apply {
    addListener(ListChangeListener { op(it) })
}


fun <T> ObservableSet<T>.onChange(op: (SetChangeListener.Change<out T>) -> Unit) = apply {
    addListener(SetChangeListener { op(it) })
}

val builtinTypes: List<String> = listOf(
    "int", "int8", "int16", "int32", "int64", "uint", "uint8", "uint16", "uint32", "uint64", "float",
    "double", "short", "long", "bool"
)
