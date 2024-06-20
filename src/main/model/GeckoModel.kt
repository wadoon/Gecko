package org.gecko.model

import org.gecko.viewmodel.VariantGroup
import tornadofx.getValue
import tornadofx.setValue

/**
 * Represents the Model component of a [Gecko][org.gecko.application.Gecko]. Holds the root-[System] and a
 * [ModelFactory], which allow the creation of and access to Gecko's elements, the data and dependencies of which
 * are required for their eventual graphic representation.
 */
class GeckoModel(val root: System = System()) {
    val modelFactory: ModelFactory = ModelFactory(this)

    var globalCode: String? = null
    var globalDefines: String? = null

    val allSystems: List<System?>
        get() {
            val result: MutableList<System?> = ArrayList()
            result.add(root)
            result.addAll(root.allChildren)
            return result
        }

    fun getSystemWithVariable(variable: Variable): System? {
        return findSystemWithVariable(root!!, variable)
    }

    fun findSystemWithVariable(system: System, variable: Variable): System? {
        if (system.variables.contains(variable)) {
            return system
        }
        for (child in system.children) {
            val result = findSystemWithVariable(child, variable)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun isNameUnique(name: String): Boolean {
        if (root == null) {
            return true
        }
        return isNameUnique(root, name)
    }

    fun isNameUnique(system: System, name: String): Boolean {
        if (system.name == name) {
            return false
        }
        if (system.variables.any { it?.name == name }) {
            return false
        }

        val automaton = system.automaton
        if (automaton.regions
                .any { it?.name == name || it?.preAndPostCondition?.name == name }
        ) {
            return false
        }
        for (state in automaton.states) {
            if (state?.name == name) {
                return false
            }
            if (state?.contracts?.any { contract: Contract -> contract.name == name } == true) {
                return false
            }
        }

        for (child in system.children) {
            if (!isNameUnique(child, name)) {
                return false
            }
        }
        return true
    }
}
