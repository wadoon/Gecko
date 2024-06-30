package org.gecko.viewmodel

import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import org.gecko.io.Mappable
import tornadofx.getValue
import tornadofx.setValue

/**
 * @author Alexander Weigl
 * @version 1 (23.05.24)
 */
class VariantGroup(name: String = "", vararg variants: String) : Mappable {
    val nameProperty: StringProperty = SimpleStringProperty(name)
    var name by nameProperty

    val variantsProperty: ListProperty<String> =
        SimpleListProperty(FXCollections.observableArrayList(*variants))
    var variants by variantsProperty

    override fun asJson() = withJsonObject {
        addProperty("name", name)
        add("variants", variants.asJsonArray())
    }
}
