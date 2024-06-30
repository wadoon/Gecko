package org.gecko.io

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import org.gecko.viewmodel.*
import org.hildan.fxgson.FxGson
import java.io.File
import java.io.InputStreamReader

var gson = FxGson.fullBuilder().setPrettyPrinting().create()

interface Mappable {
    fun asJson(): JsonElement
}

fun GModel.initFromMap(map: JsonObject) {
    root = map["root"]!!.asJsonObject.initSystemViewModel()
    knownVariantGroups.setAll(map["knownVariantGroups"].mapOfJsonObjects(::initVariantGroup))
    activatedVariants.addAll(map["activatedVariants"].asStringList())
    globalDefines.setAll(map["globalDefines"].mapOfJsonObjects(::initConstants))
    globalCode = map["globalCode"].asString

    // "openedEditors" to openedEditors.map { it.currentSystem.name }
    // "currentEditor" to currentEditor?.currentSystem?.name
}

fun initConstants(obj: JsonObject) = Constant(obj["name"].asString, obj["type"].asString, obj["value"].asString)

fun JsonElement.asStringList(): Collection<String> = asJsonArray.map { it.asString }

fun createSystemViewModel(x: JsonObject) = x.initSystemViewModel()

fun JsonObject.initSystemViewModel(): System = System().also {
    it.code = this["code"].asString
    it.subSystems.setAll(this["subSystems"].mapOfJsonObjects(::createSystemViewModel))
    it.ports.setAll(this["ports"].mapOfJsonObjects(::initPortViewModel))

    val wp = this["connections"].asJsonArray.map { it.asJsonObject }
        .map { it["source"].asString to it["destination"].asString }
        .map { (s, d) -> it.getVariableByName(s) to it.getVariableByName(d) }.map { (s, d) ->
            SystemConnection().also {
                it.source = s
                it.destination = d
            }
        }
    it.connections.setAll(wp)

    initBlockViewElement(it, this)

    it.automaton = this["automaton"].asJsonObject.initAutomaton() ?: error("Entry for 'automaton' missing!")
}

fun <T> JsonElement.mapOfJsonObjects(fn: (JsonObject) -> T) = asJsonArray.map { it.asJsonObject }.map(fn)

fun JsonObject.initAutomaton(): Automaton = Automaton().also {
    initBlockViewElement(it, this)
    it.states.setAll(this["states"].mapOfJsonObjects(::initState))
    it.edges.setAll(this["edges"].mapOfJsonObjects { o -> initEdges(o, it) })
    it.regions.setAll(this["regions"].mapOfJsonObjects(::initRegions))
}

fun initEdges(map: JsonObject, avm: Automaton) = Edge(
    avm.getStateByName(map["source"].asString)!!, avm.getStateByName(map["destination"].asString)!!
).also {
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

fun initState(m: JsonObject) = State().also {
    initBlockViewElement(it, m)
    it.isStartState = m["isStartState"].asBoolean ?: false
    it.contracts.setAll(m["contracts"].mapOfJsonObjects(::initContracts))
}

fun JsonObject.initContract() =
    Contract(this["name"].asString, this["preCondition"].asString, this["postCondition"].asString)

fun initContracts(any: JsonObject) = any.initContract()

fun initPortViewModel(a: JsonObject): Port = Port().also {
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

fun initVariantGroup(it: JsonObject): VariantGroup = it.let {
    val vg = VariantGroup()
    vg.name = it["name"].asString
    vg.variants.setAll(it["variants"].asStringList())
    vg
}

val JsonElement.asJsonObjectOrNull: JsonObject?
    get() = when (this) {
        is JsonObject -> this
        else -> null
    }
