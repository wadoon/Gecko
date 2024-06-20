package org.gecko.view.inspector.annotation

import javafx.beans.property.Property
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.gecko.viewmodel.SystemViewModel
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

interface InspectorBuilder<D> {
    fun build(annotation: Builder, value: Property<D>): Node
}

annotation class Builder(
    val label: String,
    val description: String = "",
    val builder: KClass<InspectorBuilder<*>> = TextFieldBuilder::class
)

class TextFieldBuilder : InspectorBuilder<String> {
    override fun build(annotation: Builder, value: Property<String>): Node {
        val txt = TextField()
        txt.textProperty().bind(value)
        return HBox().apply {
            children.setAll(Label(annotation.label), txt)
        }
    }
}

fun constructInspector(viewModel: SystemViewModel) {
    val v = VBox()
    v.children.setAll(viewModel::class.memberProperties.map {
        it.findAnnotation<Builder>()?.let { builder ->
            builder.builder.createInstance().build(builder, it.get(viewModel))
        }
    })
}

