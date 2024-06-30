package org.gecko.viewmodel

import com.google.gson.JsonObject
import javafx.beans.property.*
import javafx.collections.*
import org.gecko.actions.ActionManager
import org.gecko.io.*
import org.gecko.lint.Problem
import tornadofx.getValue
import tornadofx.setValue
import java.util.*
import java.util.function.Consumer

/**
 * Represents the ViewModel component of a Gecko project, which connects the Model and View. Holds a
 * [ViewModelFactory] and a reference to the [GeckoModel], as well as the current [EditorViewModel]
 * and a list of all opened [EditorViewModel]s. Maps all [PositionableElement]s to their
 * corresponding [Element]s from Model. Contains methods for managing the [EditorViewModel] and the
 * retained [PositionableElement]s.
 */
class GModel(var root: System = System()) : Element() {
    val actionManager = ActionManager(this)
    val currentEditorProperty: ObjectProperty<EditorViewModel> = SimpleObjectProperty()
    var currentEditor: EditorViewModel by currentEditorProperty

    val openedEditorsProperty: ListProperty<EditorViewModel> = listProperty()
    val openedEditors by openedEditorsProperty

    val knownVariantGroupsProperty = listProperty<VariantGroup>()
    val knownVariantGroups: ObservableList<VariantGroup> by knownVariantGroupsProperty

    val activatedVariantsProperty = setProperty<String>()
    var activatedVariants by activatedVariantsProperty

    val globalDefinesProperty = listProperty<Constant>()
    var globalDefines by globalDefinesProperty

    val globalCodeProperty = stringProperty("")
    var globalCode: String? by globalCodeProperty

    val allSystems: List<System>
        get() {
            fun list(s: System): List<System> = listOf(s) + s.subSystems.flatMap { list(it) }
            return list(root)
        }

    val viewModelFactory = ViewModelFactory(actionManager, this)

    init {
        switchEditor(root, false)

        currentEditorProperty.onChange { old, _ ->
            old?.selectionManager?.deselectAll()
            updateEditors()
        }
    }

    /**
     * Switches the current [EditorViewModel] to the one that contains the given [System] and has
     * the correct type (automaton or system editor). If the [EditorViewModel] does not exist, a new
     * one is created.
     *
     * @param nextSystem the [System] that should be displayed in the editor
     * @param isAutomatonEditor true if the editor should be an automaton editor, false if it should
     *   be a system editor
     */
    fun switchEditor(nextSystem: System, isAutomatonEditor: Boolean): EditorViewModel {
        return openedEditorsProperty.firstOrNull {
            (it.currentSystem == nextSystem && it.isAutomatonEditor == isAutomatonEditor)
        }?.let {
            this.currentEditor = it
            it
        } ?: setupNewEditorViewModel(nextSystem, isAutomatonEditor)
    }

    fun setupNewEditorViewModel(nextSystem: System, isAutomatonEditor: Boolean): EditorViewModel {
        var parent: System? = null
        if (nextSystem.parent != null) {
            parent = nextSystem.parent
        }
        val editorModel = viewModelFactory.createEditorViewModel(nextSystem, parent, isAutomatonEditor)
        openedEditorsProperty.add(editorModel)
        currentEditor = editorModel
        return editorModel
    }

    /**
     * Adds a new [PositionableElement] to the [GModel]. The element is mapped to its corresponding
     * [Element] from the model. The [PositionableElement] is then added to the correct
     * [EditorViewModel].
     *
     * @param element the [PositionableElement] to add
     */
    fun addViewModelElement(element: PositionableElement) {
        updateEditors()
    }

    /**
     * Deletes a [PositionableElement] from the [GModel]. The element is removed from the mapping
     * and from all [EditorViewModel]s. The selection managers of the editors are updated and the
     * element is removed from the editor that displays it.
     *
     * @param element the [PositionableElement] to delete
     */
    fun deleteViewModelElement(element: PositionableElement) {
        updateSelectionManagers(element)
        updateEditors()
    }

    fun updateSelectionManagers(removedElement: PositionableElement) {
        openedEditorsProperty.forEach(Consumer { editorViewModel: EditorViewModel ->
            editorViewModel.selectionManager.updateSelections(removedElement)
        })
    }

    fun updateEditors() {
        openedEditors.forEach { addPositionableViewModelElementsToEditor(it) }
        // TODO
        // val editorViewModelsToDelete = openedEditorsProperty.filter { it.currentSystem == }
        // openedEditorsProperty.removeAll(editorViewModelsToDelete)
    }

    fun addPositionableViewModelElementsToEditor(editorViewModel: EditorViewModel) {
        val currentSystem = editorViewModel.currentSystem
        editorViewModel.viewableElementsProperty.clear()

        if (editorViewModel.isAutomatonEditor) {
            editorViewModel.viewableElements.setAll(currentSystem.automaton.allElements)
        } else {
            editorViewModel.viewableElements.setAll(currentSystem.allElements)
        }
    }

    fun getSystemViewModelWithPort(Port: Port): System? = root.getChildSystemWithVariable(Port)

    override val children: Sequence<Element>
        get() = sequenceOf(root)

    override fun updateIssues() {
        val i = arrayListOf<Problem>()
        val kg = knownVariantGroups.flatMap { it.variants }.groupBy { it }

        kg.filter { (k, v) -> v.size > 1 }
            .forEach { (k, v) -> i.report("Variant $k declared twice: $v") }

        activatedVariants.forEach { av ->
            val g = knownVariantGroups.find { g -> av in g.variants }
            if (g?.variants?.filter { it != av }?.any { it in activatedVariants } == true) {
                i.report("Too many variants activated in group $g")
            }
        }

        issues.setAll(i)
    }

    override fun asJson() = withJsonObject {
        add("root", root.asJson())
        add("knownVariantGroups", knownVariantGroups.asJsonArray())
        add("activatedVariants", activatedVariants.asJsonArray())
        add("globalDefines", globalDefines.asJsonArray())
        addProperty("globalCode", globalCode)
        add("openedEditors", openedEditors.map { it.currentSystem.name }.asJsonArray())
        addProperty("currentEditor", currentEditor.currentSystem.name)
    }
}

val builtinTypes: List<String> = listOf(
    "int",
    "int8",
    "int16",
    "int32",
    "int64",
    "uint",
    "uint8",
    "uint16",
    "uint32",
    "uint64",
    "float",
    "double",
    "short",
    "long",
    "bool"
)
