package org.gecko.viewmodel

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.*
import java.util.function.Consumer
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.collections.*
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import org.gecko.actions.ActionManager
import org.gecko.io.Mappable
import tornadofx.asObservable
import tornadofx.getValue
import tornadofx.setValue

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
        return openedEditorsProperty
            .firstOrNull {
                (it.currentSystem == nextSystem && it.isAutomatonEditor == isAutomatonEditor)
            }
            ?.let {
                this.currentEditor = it
                it
            } ?: setupNewEditorViewModel(nextSystem, isAutomatonEditor)
    }

    fun setupNewEditorViewModel(nextSystem: System, isAutomatonEditor: Boolean): EditorViewModel {
        var parent: System? = null
        if (nextSystem.parent != null) {
            parent = nextSystem.parent
        }
        val editorModel =
            viewModelFactory.createEditorViewModel(nextSystem, parent, isAutomatonEditor)
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
        openedEditorsProperty.forEach(
            Consumer { editorViewModel: EditorViewModel ->
                editorViewModel.selectionManager.updateSelections(removedElement)
            }
        )
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

    override fun asJson() = withJsonObject {
        add("root", root.asJson())
        add("knownVariantGroups", knownVariantGroups.asJsonArray())
        add("activatedVariants", activatedVariants.asJsonArray())
        add("globalDefines", globalDefines.asJsonArray())
        addProperty("globalCode", globalCode)
        add("openedEditors", openedEditors.map { it.currentSystem.name }.asJsonArray())
        addProperty("currentEditor", currentEditor.currentSystem.name)
    }

    fun initFromMap(map: JsonObject) {
        root = map["root"]!!.asJsonObject.initSystemViewModel()
        knownVariantGroups.setAll(map["knownVariantGroups"].mapOfJsonObjects(::initVariantGroup))
        activatedVariants.addAll(map["activatedVariants"].asStringList())
        globalDefines.setAll(map["globalDefines"].mapOfJsonObjects(::initConstants))
        globalCode = map["globalCode"].asString

        // "openedEditors" to openedEditors.map { it.currentSystem.name }
        // "currentEditor" to currentEditor?.currentSystem?.name
    }
}

fun initConstants(obj: JsonObject) =
    Constant(obj["name"].asString, obj["type"].asString, obj["value"].asString)

fun JsonElement.asStringList(): Collection<String> = asJsonArray.map { it.asString }

fun createSystemViewModel(x: JsonObject) = x.initSystemViewModel()

fun JsonObject.initSystemViewModel(): System =
    System().also {
        it.code = this["code"].asString
        it.subSystems.setAll(this["subSystems"].mapOfJsonObjects(::createSystemViewModel))
        it.ports.setAll(this["ports"].mapOfJsonObjects(::initPortViewModel))

        val wp =
            this["connections"]
                .asJsonArray
                .map { it.asJsonObject }
                .map { it["source"].asString to it["destination"].asString }
                .map { (s, d) -> it.getVariableByName(s) to it.getVariableByName(d) }
                .map { (s, d) ->
                    SystemConnection().also {
                        it.source = s
                        it.destination = d
                    }
                }
        it.connections.setAll(wp)

        initBlockViewElement(it, this)

        it.automaton =
            this["automaton"].asJsonObject.initAutomaton()
                ?: error("Entry for 'automaton' missing!")
    }

fun <T> JsonElement.mapOfJsonObjects(fn: (JsonObject) -> T) =
    asJsonArray.map { it.asJsonObject }.map(fn)

fun JsonObject.initAutomaton(): Automaton =
    Automaton().also {
        initBlockViewElement(it, this)
        it.states.setAll(this["states"].mapOfJsonObjects(::initState))
        it.edges.setAll(this["edges"].mapOfJsonObjects { o -> initEdges(o, it) })
        it.regions.setAll(this["regions"].mapOfJsonObjects(::initRegions))
    }

fun initEdges(map: JsonObject, avm: Automaton) =
    Edge(
            avm.getStateByName(map["source"].asString)!!,
            avm.getStateByName(map["destination"].asString)!!
        )
        .also {
            it.priority = map["priority"].asInt
            it.kind = Kind.valueOf(map["kind"].asString ?: "HIT")
            it.contract = map["contract"]?.asJsonObjectOrNull?.initContract()
        }

fun initRegions(m: JsonObject): Region {
    val contract: Contract = m["contract"].asJsonObject.initContract()
    return Region(contract).also {
        initBlockViewElement(it, m)
        val (r, g, b, a) = m["color"].asJsonArray.map { it.asDouble }
        it.color = Color(r, g, b, a)
        it.invariant = Condition(m["invariant"].asString)
    }
}

fun initState(m: JsonObject) =
    State().also {
        initBlockViewElement(it, m)
        it.isStartState = m["isStartState"].asBoolean ?: false
        it.contracts.setAll(m["contracts"].mapOfJsonObjects(::initContracts))
    }

fun JsonObject.initContract() =
    Contract(this["name"].asString, this["preCondition"].asString, this["postCondition"].asString)

fun initContracts(any: JsonObject) = any.initContract()

fun initPortViewModel(a: JsonObject): Port =
    Port().also {
        initPositionableViewElement(it, a)
        it.value = a["value"].asString
        it.type = a["type"].asString
        it.visibility = Visibility.valueOf(a["visibility"].asString)
    }

fun initBlockViewElement(it: BlockElement, m: JsonObject) {
    it.name = m["name"].asString
    initPositionableViewElement(it, m)
}

fun initPositionableViewElement(it: PositionableElement, m: JsonObject) {
    fun JsonElement.asPoint2D() = asJsonObject.let { Point2D(it["x"].asDouble, it["y"].asDouble) }
    it.size = m["size"].asPoint2D()
    it.position = m["position"].asPoint2D()
}

fun initVariantGroup(it: JsonObject): VariantGroup =
    it.let {
        val vg = VariantGroup()
        vg.name = it["name"].asString
        vg.variants.setAll(it["variants"].asStringList())
        vg
    }

class Constant(name: String, type: String, value: String) : Mappable {
    val nameProperty: StringProperty = stringProperty(name)
    val typeProperty: StringProperty = stringProperty(type)
    val valueProperty: StringProperty = stringProperty(value)

    var name by nameProperty
    var type by typeProperty
    var value by valueProperty

    override fun asJson() = withJsonObject {
        addProperty("name", name)
        addProperty("type", type)
        addProperty("value", value)
    }
}

fun booleanProperty(value: Boolean = false): BooleanProperty = SimpleBooleanProperty(value)

fun doubleProperty(value: Double = 0.0): DoubleProperty = SimpleDoubleProperty(value)

fun floatProperty(value: Float = 0F): FloatProperty = SimpleFloatProperty(value)

fun intProperty(value: Int = 0): IntegerProperty = SimpleIntegerProperty(value)

fun <V> listProperty(
    value: ObservableList<V> = FXCollections.observableArrayList()
): ListProperty<V> = SimpleListProperty(value)

fun <V> listProperty(vararg values: V): ListProperty<V> =
    SimpleListProperty(values.toMutableList().asObservable())

fun longProperty(value: Long = 0): LongProperty = SimpleLongProperty(value)

fun <K, V> mapProperty(
    value: ObservableMap<K, V> = FXCollections.observableHashMap()
): MapProperty<K, V> = SimpleMapProperty(value)

fun <T> objectProperty(value: T): ObjectProperty<T> = SimpleObjectProperty(value)

fun <T> nullableObjectProperty(value: T? = null): ObjectProperty<T?> = SimpleObjectProperty(value)

fun <V> setProperty(value: ObservableSet<V> = FXCollections.observableSet()): SetProperty<V> =
    SimpleSetProperty(value)

fun stringProperty(value: String): StringProperty = SimpleStringProperty(value)

fun <T> ObservableValue<T>.onChange(op: (T, T) -> Unit) = apply {
    addListener { _, oldValue, newValue -> op(oldValue, newValue) }
}

fun <T> ObservableList<T>.onListChange(op: (ListChangeListener.Change<out T>) -> Unit) = apply {
    addListener(ListChangeListener { op(it) })
}

fun <T> ObservableSet<T>.onChange(op: (SetChangeListener.Change<out T>) -> Unit) = apply {
    addListener(SetChangeListener { op(it) })
}

private val JsonElement.asJsonObjectOrNull: JsonObject?
    get() =
        when (this) {
            is JsonObject -> this
            else -> null
        }

val builtinTypes: List<String> =
    listOf(
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
