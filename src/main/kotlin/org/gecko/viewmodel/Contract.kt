package org.gecko.viewmodel

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import org.gecko.lint.Problem
import tornadofx.getValue
import tornadofx.setValue

/**
 * Represents an abstraction of a [Contract] model element. A [Contract] is described by a name, a
 * pre- and a postcondition. Contains methods for managing the afferent data and updating the
 * target-[Contract].
 */
data class Contract(
    override val nameProperty: StringProperty = SimpleStringProperty(""),
    val preConditionProperty: ObjectProperty<Condition> = objectProperty(Condition()),
    val postConditionProperty: ObjectProperty<Condition> = objectProperty(Condition()),
) : Element(), Renamable {
    override var name: String by nameProperty

    var preCondition: Condition by preConditionProperty
    var postCondition: Condition by postConditionProperty

    override val children: Sequence<Element>
        get() = sequenceOf()

    override fun updateIssues() {
        val i = arrayListOf<Problem>()

        if (name.isEmpty())
            i.report("Pre condition", 1.0)

        if (preCondition.value.isEmpty())
            i.report("Pre condition", 1.0)

        if (postCondition.value.isEmpty())
            i.report("Post condition", 1.0)

        checkName(name, i)

        issues.setAll(i)
    }

    override fun asJson() = withJsonObject {
        addProperty("name", name)
        addProperty("preCondition", preCondition.value)
        addProperty("postCondition", postCondition.value)
    }

    constructor(name: String, pre: String, post: String) : this() {
        this.name = name
        this.preCondition.value = pre
        this.postCondition.value = post
    }

    constructor(name: String, preCondition: Condition, postCondition: Condition) : this() {
        this.name = name
        this.preCondition = preCondition
        this.postCondition = postCondition
    }
}
