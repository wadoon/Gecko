package org.gecko.viewmodel

import javafx.beans.property.SimpleStringProperty
import tornadofx.getValue
import tornadofx.setValue

/**
 *
 * @author Alexander Weigl
 * @version 1 (20.06.24)
 */
data class Condition(val valueProperty: SimpleStringProperty = SimpleStringProperty("")) {
    infix fun and(other: Condition): Condition = Condition("($value & ${other.value})")
    fun not(): Condition = Condition("!($value)")

    constructor(value: String) : this() {
        this.value = value
    }

    var value: String by valueProperty
}