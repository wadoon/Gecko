package org.gecko.io

import java.io.PrintWriter
import java.io.Writer
import java.util.*
import org.gecko.exceptions.ModelException
import org.gecko.viewmodel.*

/**
 * The AutomatonFileSerializer is used to export a project to a sys file. When exporting, it
 * transforms features unique to Gecko, such as regions, kinds and priorities, to be compatible with
 * the sys file format.
 */
class AutomatonFileSerializer(val model: GModel) : FileSerializer {
    lateinit var out: PrintWriter

    override fun writeToStream(w: Writer) {
        out = PrintWriter(w)

        if (model.globalDefines.isNotEmpty()) {
            out.write("defines {")
            model.globalDefines.forEach {
                out.format("\t%s : %s := %s\n", it.name, it.type, it.value)
            }
            out.write("}\n\n")
        }

        model.globalCode?.let {
            if (it.isNotBlank()) {
                serializeCode(it)
                out.write("\n")
            }
        }

        serializeAutomata(model)
        serializeSystems(model)
    }

    private fun serializeAutomata(model: GModel) =
        model.allSystems.forEach { serializeAutomaton(it) }

    private fun serializeSystems(model: GModel) = model.allSystems.forEach { serializeSystem(it) }

    private fun serializeAutomaton(system: System) {
        val automaton = system.automaton

        out.format(AUTOMATON_SERIALIZATION_AS_SYSTEM_CONTRACT, system.name)

        if (system.ports.isNotEmpty()) {
            serializeIo(system)
            out.write("\n")
        }
        automaton.states.forEach { state -> this.serializeStateContracts(state, automaton) }
        out.write("\n")
        val relevantEdges = automaton.edges.filter { edge: Edge -> edge.contract != null }
        relevantEdges.forEach { edge -> this.serializeTransition(edge) }
        out.write("}")
        out.write("\n")
    }

    private fun serializeStateContracts(state: State, automaton: Automaton) {
        // Edges are used so much here because contracts don't have priorities or kinds and only
        // states can be in regions
        val relevantRegions = automaton.getRegionsWithState(state)
        val edges = automaton.getOutgoingEdges(state).filter { it.contract != null }
        if (edges.isEmpty()) {
            return
        }
        // Creating new contracts to not alter the model
        val newContracts: MutableMap<Edge, Contract> = HashMap()
        for (edge in edges) {
            val newContract = applyRegionsToContract(relevantRegions, edge.contract!!)
            try {
                applyKindToContract(newContract, edge.kind)
                newContract.name = getContractName(edge)
            } catch (e: ModelException) {
                throw RuntimeException("Failed to apply kind to contract", e)
            }
            newContracts[edge] = newContract
        }

        // Building the conditions for the priorities
        val groupedEdges = edges.groupBy { it!!.priority }.values.reversed()
        val preConditionsByPrio: MutableList<Condition> = arrayListOf()
        for (edgeGroup in groupedEdges) {
            // OrElseThrow because validity needs to be ensured by model
            val newPre =
                edgeGroup
                    .map { key -> newContracts[key]!! }
                    .map { obj -> obj.preCondition }
                    .reduce { obj, other -> Condition("$obj & $other") }
            preConditionsByPrio.add(newPre)
        }
        // and the specific condition for a prio with all conditions with lower prio
        val allLowerPrioPreConditions = arrayListOf<Condition>()
        for (i in 0 until preConditionsByPrio.size - 1) {
            allLowerPrioPreConditions.add(
                allLowerPrioPreConditions.reduce { obj, other -> obj.and(other) }
            )
        }

        // applying priorites
        for ((prioIndex, edgeGroup) in groupedEdges.withIndex()) {
            for (edge in edgeGroup) {
                if (prioIndex == 0) {
                    continue // Highest prio doesn't need to be altered
                }
                val contractWithPrio = newContracts[edge]!!
                contractWithPrio.preCondition.value =
                    contractWithPrio.preCondition
                        .and(allLowerPrioPreConditions[prioIndex - 1].not())
                        .value
                newContracts[edge] = contractWithPrio
            }
        }
        newContracts.values.forEach { this.serializeContract(it) }
    }

    @Throws(ModelException::class)
    fun applyKindToContract(contract: Contract, kind: Kind) {
        when (kind) {
            Kind.MISS -> {
                contract.preCondition.value = contract.preCondition.not().value
                contract.postCondition.value = Condition("true").value
            }
            Kind.FAIL -> contract.postCondition.value = contract.postCondition.not().value
            Kind.HIT -> {}
        }
    }

    private fun applyRegionsToContract(
        relevantRegions: List<Region>,
        contract: Contract
    ): Contract {
        val newContract: Contract
        try {
            newContract = Contract(contract.name, contract.preCondition, contract.postCondition)
        } catch (e: ModelException) {
            throw RuntimeException("Failed to build contract out of other valid contracts", e)
        }
        if (relevantRegions.isEmpty()) {
            return newContract
        }
        val (pre, post) = andConditions(relevantRegions)
        newContract.preCondition = pre and newContract.preCondition
        newContract.postCondition = post and newContract.postCondition
        return newContract
    }

    private fun andConditions(regions: List<Region>): Pair<Condition, Condition> {
        val c = regions.first()
        val first = c.contract

        var newPre: Condition
        var newPost: Condition
        try {
            newPre = first.preCondition
            newPost = first.postCondition
        } catch (e: ModelException) {
            throw RuntimeException("Failed to build conditions out of other valid conditions", e)
        }
        newPre = newPre.and(c.invariant)
        newPost = newPost.and(c.invariant)

        for (i in 1 until regions.size) {
            val region = regions[i]
            val con = region.contract
            newPre = newPre.and(con.preCondition)
            newPre = newPre.and(region.invariant)
            newPost = newPost.and(con.postCondition)
            newPost = newPost.and(region.invariant)
        }
        return newPre to newPost
    }

    private fun serializeContract(contract: Contract) {
        out.write(INDENT)
        out.format(
            SERIALIZED_CONTRACT,
            contract.name,
            contract.preCondition,
            contract.postCondition
        )
    }

    private fun serializeTransition(edge: Edge) {
        out.write(INDENT)
        out.format(
            SERIALIZED_TRANSITION,
            edge.source.name,
            edge.destination.name,
            getContractName(edge)
        )
    }

    private fun serializeSystem(system: System) {
        out.format(SERIALIZED_SYSTEM, system.name)

        if (system.ports.isNotEmpty()) {
            serializeIo(system)
            out.write("\n")
        }
        if (system.subSystems.isNotEmpty()) {
            serializeChildren(system)
            out.write("\n")
        }
        out.write(INDENT)
        out.format(SERIALIZED_CONTRACT_NAME, system.name)
        out.write("\n")
        if (system.connections.isNotEmpty()) {
            serializeConnections(system)
            out.write("\n")
        }
        serializeCode(system.code)
        out.write("}")
        out.write("\n")
    }

    private fun serializeConnections(system: System) =
        system.connections.forEach { this.serializeConnection(it, system) }

    private fun serializeConnection(connection: SystemConnection, parent: System) {
        val startSystem = serializeSystemReference(parent, connection.source!!)
        val startPort = connection.source?.name
        val endSystem = serializeSystemReference(parent, connection.destination!!)
        val endPort = connection.destination?.name
        out.write(INDENT)
        out.format(SERIALIZED_CONNECTION, startSystem, startPort, endSystem, endPort)
    }

    private fun serializeSystemReference(parent: System?, v: Port): String {
        return if (parent!!.ports.contains(v)) {
            AutomatonFileVisitor.SELF_REFERENCE_TOKEN
        } else {
            parent.getChildSystemWithVariable(v)!!.name
        }
    }

    private fun serializeIo(system: System) {
        val orderedVariables = system.ports.sorted(Comparator.comparing { it.visibility })
        orderedVariables.forEach { this.serializeVariable(it) }
    }

    private fun serializeChildren(system: System) =
        system.subSystems.forEach { this.serializeChild(it) }

    private fun serializeChild(system: System): String =
        String.format(INDENT + SERIALIZED_STATE, system.name, system.name)

    private fun serializeVariable(variable: Port): String {
        var output = ""
        output +=
            INDENT +
                when (variable.visibility) {
                    Visibility.INPUT -> SERIALIZED_INPUT
                    Visibility.OUTPUT -> SERIALIZED_OUTPUT
                    Visibility.STATE -> SERIALIZED_STATE_VISIBILITY
                }
        output += VARIABLE_ATTRIBUTES.format(variable.name, variable.type)
        if (variable.value != null) {
            output += " := " + variable.value
        }
        return output
    }

    private fun serializeCode(code: String) =
        out.write(INDENT + AutomatonFileVisitor.CODE_BEGIN + code + AutomatonFileVisitor.CODE_END)

    private fun getContractName(edge: Edge): String {
        val name = edge.contract?.name
        if (edge.kind == Kind.HIT) {
            return name!!
        }
        return makeNameUnique("%s %s".format(name, edge.kind.name))
    }

    private fun makeNameUnique(baseName: String): String = baseName

    companion object {
        const val INDENT = "    "
        const val SERIALIZED_CONTRACT_NAME = "contract %s"
        const val AUTOMATON_SERIALIZATION_AS_SYSTEM_CONTRACT = "$SERIALIZED_CONTRACT_NAME {"
        const val SERIALIZED_CONTRACT = "$SERIALIZED_CONTRACT_NAME := %s ==> %s"
        const val SERIALIZED_TRANSITION = "%s -> %s :: %s"
        const val SERIALIZED_SYSTEM = "reactor %s {"
        const val SERIALIZED_CONNECTION = "%s.%s -> %s.%s"
        const val SERIALIZED_STATE = "state %s: %s"
        const val VARIABLE_ATTRIBUTES = " %s: %s"
        const val SERIALIZED_INPUT = "input"
        const val SERIALIZED_OUTPUT = "output"
        const val SERIALIZED_STATE_VISIBILITY = "state"
    }
}
