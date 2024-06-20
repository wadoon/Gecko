package org.gecko.viewmodel


import javafx.beans.property.*

import tornadofx.getValue
import tornadofx.setValue

/**
 * Represents an abstraction of a [Contract] model element. A [ContractViewModel] is described by a name, a
 * pre- and a postcondition. Contains methods for managing the afferent data and updating the target-[Contract].
 */
data class ContractViewModel(
    override val nameProperty: StringProperty = SimpleStringProperty(""),
    val preConditionProperty: SimpleObjectProperty<Condition> = SimpleObjectProperty<Condition>(),
    val postConditionProperty: SimpleObjectProperty<Condition> = SimpleObjectProperty<Condition>(),
) : Element(), Renamable {
    override var name: String by nameProperty

    var preCondition: Condition by preConditionProperty
    var postCondition: Condition by postConditionProperty

    constructor(name: String, pre: String, post: String) : this() {
        this.name = name
        this.preCondition.value = pre
        this.postCondition.value = post
    }

    constructor(name: String, preCondition: Condition, postCondition: Condition) : this(){
        this.name = name
        this.preCondition = preCondition
        this.postCondition = postCondition
    }
}