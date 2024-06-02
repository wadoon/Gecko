package org.gecko.model

import kotlinx.serialization.Serializable

import org.gecko.exceptions.MissingViewModelElementException
import org.gecko.exceptions.ModelException

/**
 * Represents a state in the domain model of a Gecko project. A [State] has a name and a set of [Contract]s.
 * Contains methods for managing the afferent data.
 */
@Serializable
data class State(
    override val id: UInt, override var name: String?,
    val contracts: MutableSet<Contract> = mutableSetOf()
) : Element(), Renamable {
    fun addContract(contract: Contract) {
        contracts.add(contract)
    }

    fun addContracts(contracts: Set<Contract>) {
        for (contract in contracts) {
            addContract(contract)
        }
    }

    fun removeContract(contract: Contract) {
        contracts.remove(contract)
    }

    fun removeContracts(contracts: Set<Contract>) {
        for (contract in contracts) {
            removeContract(contract)
        }
    }

    @Throws(ModelException::class, MissingViewModelElementException::class)
    override fun accept(visitor: ElementVisitor) {
        visitor.visit(this)
    }
}
