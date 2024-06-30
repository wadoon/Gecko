package org.gecko.viewmodel

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.collections.*
import javafx.scene.paint.Color
import org.gecko.io.Mappable
import org.gecko.lint.Problem
import tornadofx.asObservable

fun <E : Mappable> Iterable<E>.asJsonArray() =
    JsonArray().also { a -> forEach { a.add(it.asJson()) } }

@JvmName("asJsonStringArray")
fun Iterable<String>.asJsonArray() = JsonArray().also { a -> forEach { a.add(it) } }

fun withJsonObject(fn: JsonObject.() -> Unit): JsonObject = JsonObject().also(fn)

fun Color.asJson() =
    JsonArray().apply {
        add(this@asJson.red)
        add(this@asJson.green)
        add(this@asJson.blue)
        add(this@asJson.opacity)
    }

val NAME_REGEX = "[a-zA-Z][a-zA-Z0-9_]*".toRegex()
fun checkName(s: String, seq: MutableList<Problem>) {
    if (NAME_REGEX.matchEntire(s) == null)
        seq.report("Illegal name \"$s\"", 1.0)
}

fun MutableList<Problem>.report(message: String, level: Double = 1.0) = add(Problem(message, level))


fun booleanProperty(value: Boolean = false): BooleanProperty = SimpleBooleanProperty(value)

fun doubleProperty(value: Double = 0.0): DoubleProperty = SimpleDoubleProperty(value)

fun floatProperty(value: Float = 0F): FloatProperty = SimpleFloatProperty(value)

fun intProperty(value: Int = 0): IntegerProperty = SimpleIntegerProperty(value)

fun <V> listProperty(
    value: ObservableList<V> = FXCollections.observableArrayList()
): ListProperty<V> = SimpleListProperty(value)

fun <V> listProperty(vararg values: V): ListProperty<V> = SimpleListProperty(values.toMutableList().asObservable())

fun longProperty(value: Long = 0): LongProperty = SimpleLongProperty(value)

fun <K, V> mapProperty(
    value: ObservableMap<K, V> = FXCollections.observableHashMap()
): MapProperty<K, V> = SimpleMapProperty(value)

fun <T> objectProperty(value: T): ObjectProperty<T> = SimpleObjectProperty(value)

fun <T> nullableObjectProperty(value: T? = null): ObjectProperty<T?> = SimpleObjectProperty(value)

fun <V> setProperty(value: ObservableSet<V> = FXCollections.observableSet()): SetProperty<V> = SimpleSetProperty(value)

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