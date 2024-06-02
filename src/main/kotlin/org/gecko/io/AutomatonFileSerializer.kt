package org.gecko.io

import org.gecko.exceptions.ModelException
import org.gecko.model.*
import java.io.File
import java.io.IOException
import java.lang.System
import java.nio.file.Files
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function

/**
 * The AutomatonFileSerializer is used to export a project to a sys file. When exporting, it transforms features unique
 * to Gecko, such as regions, kinds and priorities, to be compatible with the sys file format.
 */
class AutomatonFileSerializer(val model: GeckoModel) : FileSerializer {
    @Throws(IOException::class)
    override fun writeToFile(file: File) {
        val joiner = StringJoiner(System.lineSeparator())
        if (model.globalDefines != null) {
            joiner.add(model.globalDefines)
            joiner.add("")
        }
        if (model.globalCode != null) {
            joiner.add(serializeCode(model.globalCode!!))
            joiner.add("")
        }
        joiner.add(serializeAutomata(model))
        joiner.add(serializeSystems(model))
        Files.writeString(file.toPath(), joiner.toString())
    }

    fun serializeAutomata(model: GeckoModel): String {
        val relevantSystems =
            model.allSystems.stream().filter { system: org.gecko.model.System? -> !system!!.automaton.isEmpty }
                .toList()
        return serializeCollectionWithMapping(relevantSystems) { system: org.gecko.model.System? ->
            this.serializeAutomaton(system!!)
        }
    }

    fun serializeSystems(model: GeckoModel): String {
        return serializeCollectionWithMapping(model.allSystems) { system: org.gecko.model.System? ->
            this.serializeSystem(
                system
            )
        }
    }

    fun serializeAutomaton(system: org.gecko.model.System): String {
        val automaton = system.automaton
        val joiner = StringJoiner(System.lineSeparator())
        joiner.add(AUTOMATON_SERIALIZATION_AS_SYSTEM_CONTRACT.format(system.name))
        if (system.variables.isNotEmpty()) {
            joiner.add(serializeIo(system))
            joiner.add("")
        }
        joiner.add(
            serializeCollectionWithMapping(
                automaton.states,
                { state, automaton -> this.serializeStateContracts(state, automaton) },
                automaton
            )
        )
        joiner.add("")
        val relevantEdges = automaton.edges.stream().filter { edge: Edge -> edge.contract != null }.toList()
        joiner.add(serializeCollectionWithMapping(relevantEdges) { edge: Edge -> this.serializeTransition(edge) })
        joiner.add("}")
        joiner.add("")
        return joiner.toString()
    }

    fun serializeStateContracts(state: State, automaton: Automaton): String {
        //Edges are used so much here because contracts don't have priorities or kinds and only states can be in regions
        val relevantRegions = automaton.getRegionsWithState(state)
        val edges = automaton.getOutgoingEdges(state).filter { it.contract != null }.toList()
        if (edges.isEmpty()) {
            return ""
        }
        //Creating new contracts to not alter the model
        val newContracts: MutableMap<Edge, Contract> = HashMap()
        for (edge in edges) {
            val newContract = applyRegionsToContract(relevantRegions, edge.contract)
            try {
                applyKindToContract(newContract, edge.kind)
                newContract.name = getContractName(edge)
            } catch (e: ModelException) {
                throw RuntimeException("Failed to apply kind to contract", e)
            }
            newContracts[edge] = newContract
        }

        //Building the conditions for the priorities
        val groupedEdges = edges.groupBy { obj -> obj!!.priority }.values.reversed()
        val preConditionsByPrio: MutableList<Condition?> = ArrayList()
        for (edgeGroup in groupedEdges) {
            //OrElseThrow because validity needs to be ensured by model
            val newPre = edgeGroup!!
                .map { key -> newContracts[key]!! }
                .map { obj -> obj.preCondition }
                .reduce { obj: Condition?, other: Condition? -> obj!!.and(other!!) }
            preConditionsByPrio.add(newPre)
        }
        //and the specific condition for a prio with all conditions with lower prio
        val allLowerPrioPreConditions: MutableList<Condition?> = ArrayList()
        for (i in 0 until preConditionsByPrio.size - 1) {
            allLowerPrioPreConditions.add(
                allLowerPrioPreConditions.stream()
                    .reduce(preConditionsByPrio[i]) { obj: Condition?, other: Condition? -> obj!!.and(other!!) })
        }

        //applying priorites
        var prioIndex = 0
        for (edgeGroup in groupedEdges) {
            for (edge in edgeGroup!!) {
                if (prioIndex == 0) {
                    continue  //Highest prio doesn't need to be altered
                }
                val contractWithPrio = newContracts[edge]!!
                contractWithPrio.preCondition = (
                        contractWithPrio.preCondition.and(allLowerPrioPreConditions[prioIndex - 1]!!.not())
                        )
                newContracts[edge] = contractWithPrio
            }
            prioIndex++
        }
        return serializeCollectionWithMapping(newContracts.values) { contract: Contract? ->
            this.serializeContract(
                contract
            )
        }
    }

    @Throws(ModelException::class)
    fun applyKindToContract(contract: Contract, kind: Kind) {
        when (kind) {
            Kind.MISS -> {
                contract.preCondition = contract.preCondition.not()
                contract.postCondition = Condition.Companion.trueCondition()
            }

            Kind.FAIL -> contract.postCondition = contract.postCondition.not()
            Kind.HIT -> {
            }

            else -> throw IllegalArgumentException("Unknown kind: $kind")
        }
    }

    fun applyRegionsToContract(relevantRegions: List<Region?>?, contract: Contract): Contract {
        val newContract: Contract
        try {
            newContract = Contract(0u, contract.name, contract.preCondition, contract.postCondition)
        } catch (e: ModelException) {
            throw RuntimeException("Failed to build contract out of other valid contracts", e)
        }
        if (relevantRegions!!.isEmpty()) {
            return newContract
        }
        val newConditions = andConditions(relevantRegions)
        newContract.preCondition = newConditions.first()!!.and(newContract.preCondition)
        newContract.postCondition = newConditions[1]!!.and(newContract.postCondition)
        return newContract
    }

    fun andConditions(regions: List<Region?>?): List<Condition?> {
        val first = regions!!.first()!!

        var newPre: Condition?
        var newPost: Condition?
        try {
            newPre = Condition(first.preAndPostCondition.preCondition.condition)
            newPost = Condition(first.preAndPostCondition.postCondition.condition)
        } catch (e: ModelException) {
            throw RuntimeException("Failed to build conditions out of other valid conditions", e)
        }
        newPre = newPre.and(first.invariant)
        newPost = newPost.and(first.invariant)

        for (i in 1 until regions.size) {
            val region = regions[i]!!
            newPre = newPre!!.and(region.preAndPostCondition.preCondition)
            newPre = newPre!!.and(region.invariant)
            newPost = newPost!!.and(region.preAndPostCondition.postCondition)
            newPost = newPost!!.and(region.invariant)
        }
        return java.util.List.of(newPre, newPost)
    }

    fun serializeContract(contract: Contract?): String {
        return INDENT + SERIALIZED_CONTRACT.format(
            contract?.name, contract!!.preCondition, contract.postCondition
        )
    }

    fun serializeTransition(edge: Edge): String {
        return INDENT + SERIALIZED_TRANSITION.format(
            edge.source.name, edge.destination.name,
            getContractName(edge)
        )
    }

    fun serializeSystem(system: org.gecko.model.System?): String {
        val joiner = StringJoiner(System.lineSeparator())
        joiner.add(SERIALIZED_SYSTEM.format(system!!.name))

        if (system.variables.isNotEmpty()) {
            joiner.add(serializeIo(system))
            joiner.add("")
        }
        if (system.children.isNotEmpty()) {
            joiner.add(serializeChildren(system))
            joiner.add("")
        }
        if (system.automaton != null && !system.automaton.isEmpty) {
            joiner.add(INDENT + SERIALIZED_CONTRACT_NAME.format(system.name))
            joiner.add("")
        }
        if (system.connections.isNotEmpty()) {
            joiner.add(serializeConnections(system))
            joiner.add("")
        }
        if (system.code != null) {
            joiner.add(serializeCode(system.code))
        }
        joiner.add("}")
        joiner.add("")
        return joiner.toString()
    }

    fun serializeConnections(system: org.gecko.model.System?): String {
        return serializeCollectionWithMapping(
            system!!.connections,
            { connection: SystemConnection, parent: org.gecko.model.System? ->
                this.serializeConnection(
                    connection,
                    parent
                )
            },
            system
        )
    }

    fun serializeConnection(connection: SystemConnection, parent: org.gecko.model.System?): String {
        val startSystem = serializeSystemReference(parent, connection.source)
        val startPort = connection.source.name
        val endSystem = serializeSystemReference(parent, connection.destination)
        val endPort = connection.destination.name
        String.format(return INDENT + SERIALIZED_CONNECTION, startSystem, startPort, endSystem, endPort)
    }

    fun serializeSystemReference(parent: org.gecko.model.System?, v: Variable): String? {
        return if (parent!!.variables.contains(v)) {
            AutomatonFileVisitor.Companion.SELF_REFERENCE_TOKEN
        } else {
            parent!!.getChildSystemWithVariable(v)!!.name
        }
    }

    fun serializeIo(system: org.gecko.model.System): String {
        val orderedVariables =
            system.variables.stream().sorted(Comparator.comparing { obj: Variable -> obj.visibility }).toList()
        return serializeCollectionWithMapping(orderedVariables) { variable: Variable -> this.serializeVariable(variable) }
    }

    fun serializeChildren(system: org.gecko.model.System): String {
        return serializeCollectionWithMapping(system.children) { system: org.gecko.model.System ->
            this.serializeChild(
                system
            )
        }
    }

    fun serializeChild(system: org.gecko.model.System): String {
        String.format(return INDENT + SERIALIZED_STATE, system.name, system.name)
    }

    fun serializeVariable(variable: Variable): String {
        var output = ""
        output += INDENT + when (variable.visibility) {
            Visibility.INPUT -> SERIALIZED_INPUT
            Visibility.OUTPUT -> SERIALIZED_OUTPUT
            Visibility.STATE -> SERIALIZED_STATE_VISIBILITY
            else -> throw IllegalArgumentException("Unknown visibility: " + variable.visibility)
        }
        output += VARIABLE_ATTRIBUTES.format(variable.name, variable.type)
        if (variable.value != null) {
            output += " := " + variable.value
        }
        return output
    }

    fun serializeCode(code: String): String {
        return INDENT + AutomatonFileVisitor.Companion.CODE_BEGIN + code + AutomatonFileVisitor.Companion.CODE_END
    }

    fun <T> serializeCollectionWithMapping(collection: Collection<T>?, mapping: Function<T, String>): String {
        val joiner = StringJoiner(System.lineSeparator())
        for (item in collection!!) {
            val result = mapping.apply(item)
            if (result == null || result.isBlank()) {
                continue
            }
            joiner.add(result)
        }
        return joiner.toString()
    }

    fun <T, S> serializeCollectionWithMapping(
        collection: Collection<T>, mapping: BiFunction<T, S, String>, additionalArg: S
    ): String {
        val joiner = StringJoiner(System.lineSeparator())
        for (item in collection) {
            val result = mapping.apply(item, additionalArg)
            if (result == null || result.isBlank()) {
                continue
            }
            joiner.add(result)
        }
        return joiner.toString()
    }

    fun getContractName(edge: Edge): String {
        val name = edge.contract.name
        if (edge.kind == Kind.HIT) {
            return name!!
        }
        return makeNameUnique("%s %s".format(name, edge.kind.name))
    }

    fun makeNameUnique(baseName: String): String {
        var name = baseName
        var i = 1
        while (!model.isNameUnique(name)) {
            name = "%s_%d".format(baseName, i++)
        }
        return name
    }

    companion object {
        const val INDENT = "    "
        const val SERIALIZED_CONTRACT_NAME = "contract %s"
        const val AUTOMATON_SERIALIZATION_AS_SYSTEM_CONTRACT = SERIALIZED_CONTRACT_NAME + " {"
        const val SERIALIZED_CONTRACT = SERIALIZED_CONTRACT_NAME + " := %s ==> %s"
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
