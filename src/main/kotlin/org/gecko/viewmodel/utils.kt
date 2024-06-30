package org.gecko.viewmodel

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import javafx.scene.paint.Color
import org.gecko.io.Mappable

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
