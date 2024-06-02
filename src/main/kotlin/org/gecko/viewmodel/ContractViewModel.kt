package org.gecko.viewmodel

import javafx.beans.property.*


import org.gecko.exceptions.ModelException
import org.gecko.model.*
import tornadofx.getValue
import tornadofx.setValue

/**
 * Represents an abstraction of a [Contract] model element. A [ContractViewModel] is described by a name, a
 * pre- and a postcondition. Contains methods for managing the afferent data and updating the target-[Contract].
 */
class ContractViewModel(id: Int, target: Contract) : AbstractViewModelElement<Contract>(id, target), Renamable {
    val preConditionProperty: StringProperty = SimpleStringProperty(target.preCondition.condition)
    val postConditionProperty: StringProperty = SimpleStringProperty(target.postCondition.condition)
    override val nameProperty: StringProperty = SimpleStringProperty(target.name ?: "")
    override var name: String by nameProperty

    var precondition: String by preConditionProperty
    var postcondition: String by postConditionProperty

    @Throws(ModelException::class)
    override fun updateTarget() {
        target.name = name
        target.preCondition.condition = precondition
        target.postCondition.condition = postcondition
    }


    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is ContractViewModel) {
            return false
        }
        return id == o.id
    }
}