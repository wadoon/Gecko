package org.gecko.viewmodel

import javafx.beans.property.StringProperty
import org.gecko.io.Mappable
import tornadofx.getValue
import tornadofx.setValue

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
