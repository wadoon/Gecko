package org.gecko.viewmodel

import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.geometry.Point2D
import org.gecko.exceptions.ModelException
import org.gecko.model.System
import tornadofx.getValue
import tornadofx.setValue
import java.util.stream.Collectors

/**
 * Represents an abstraction of a [System] model element. A [SystemViewModel] is described by a code snippet
 * and a set of [PortViewModel]s. Contains methods for managing the afferent data and updating the
 * target-[System].
 */
class SystemViewModel(id: Int, target: System) : BlockViewModelElement<System>(id, target) {
    val codeProperty = SimpleStringProperty(target.code)
    val portsProperty: ListProperty<PortViewModel> = SimpleListProperty(FXCollections.observableArrayList())
    var startState: StateViewModel? = null
        /**
         * Sets the start state of the automaton of the system.
         *
         * @param value the new start state
         */
        set(value) {
            field?.let { it.isStartState = false }
            value?.isStartState = true
            field = value
        }

    init {
        size = DEFAULT_SYSTEM_SIZE
    }

    val ports: List<PortViewModel>
        get() = ArrayList(portsProperty)

    var code: String by codeProperty

    @Throws(ModelException::class)
    override fun updateTarget() {
        super.updateTarget()
        target!!.code = this.code
        target.variables.clear()
        target.addVariables(portsProperty.stream().map { obj: PortViewModel -> obj.target }.collect(Collectors.toSet()))
        target.automaton.startState = if (startState != null) startState!!.target else null
    }

    fun addPort(port: PortViewModel) {
        portsProperty.add(port)
        port.systemPositionProperty.bind(positionProperty)
    }

    fun removePort(port: PortViewModel) {
        portsProperty.remove(port)
        port.systemPositionProperty.unbind()
    }


    override fun <S> accept(visitor: PositionableViewModelElementVisitor<S>): S {
        return visitor.visit(this)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is SystemViewModel) {
            return false
        }
        return id == o.id
    }

    companion object {
        val DEFAULT_SYSTEM_SIZE = Point2D(300.0, 300.0)
    }
}
