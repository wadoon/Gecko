package org.gecko.io

import org.gecko.exceptions.ModelException
import org.gecko.viewmodel.*
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function

/**
 * The AutomatonFileSerializer is used to export a project to a sys file. When exporting, it transforms features unique
 * to Gecko, such as regions, kinds and priorities, to be compatible with the sys file format.
 */
class AutomatonFileSerializer(val model: GeckoViewModel) : FileSerializer {
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

    fun serializeAutomata(model: GeckoViewModel): String {
        val relevantSystems = model.allSystems
        return serializeCollectionWithMapping(relevantSystems) { this.serializeAutomaton(it) }
    }

    fun serializeSystems(model: GeckoViewModel): String {
        return serializeCollectionWithMapping(model.allSystems) { system: SystemViewModel? ->
            this.serializeSystem(
                system
            )
        }
    }

    fun serializeAutomaton(system: SystemViewModel): String {
        val automaton = system.automaton
        val joiner = StringJoiner(System.lineSeparator())
        joiner.add(AUTOMATON_SERIALIZATION_AS_SYSTEM_CONTRACT.format(system.name))
        if (system.ports.isNotEmpty()) {
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
        val relevantEdges = automaton.edges.filter { edge: EdgeViewModel -> edge.contract != null }
        joiner.add(serializeCollectionWithMapping(relevantEdges) { edge: EdgeViewModel -> this.serializeTransition(edge) })
        joiner.add("}")
        joiner.add("")
        return joiner.toString()
    }

    fun serializeStateContracts(state: StateViewModel, automaton: AutomatonViewModel): String {
        //Edges are used so much here because contracts don't have priorities or kinds and only states can be in regions
        val relevantRegions = automaton.getRegionsWithState(state)
        val edges = automaton.getOutgoingEdges(state).filter { it.contract != null }
        if (edges.isEmpty()) {
            return ""
        }
        //Creating new contracts to not alter the model
        val newContracts: MutableMap<EdgeViewModel, ContractViewModel> = HashMap()
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

        //Building the conditions for the priorities
        val groupedEdges = edges.groupBy { it!!.priority }.values.reversed()
        val preConditionsByPrio: MutableList<Condition> = arrayListOf()
        for (edgeGroup in groupedEdges) {
            //OrElseThrow because validity needs to be ensured by model
            val newPre = edgeGroup
                .map { key -> newContracts[key]!! }
                .map { obj -> obj.preCondition }
                .reduce { obj, other -> Condition("$obj & $other") }
            preConditionsByPrio.add(newPre)
        }
        //and the specific condition for a prio with all conditions with lower prio
        val allLowerPrioPreConditions = arrayListOf<Condition>()
        for (i in 0 until preConditionsByPrio.size - 1) {
            allLowerPrioPreConditions.add(
                allLowerPrioPreConditions.reduce { obj, other -> obj.and(other) })
        }

        //applying priorites
        var prioIndex = 0
        for (edgeGroup in groupedEdges) {
            for (edge in edgeGroup) {
                if (prioIndex == 0) {
                    continue  //Highest prio doesn't need to be altered
                }
                val contractWithPrio = newContracts[edge]!!
                contractWithPrio.preCondition.value =
                    contractWithPrio.preCondition.and(allLowerPrioPreConditions[prioIndex - 1].not()).value
                newContracts[edge] = contractWithPrio
            }
            prioIndex++
        }
        return serializeCollectionWithMapping(newContracts.values) { contract: ContractViewModel ->
            this.serializeContract(contract)
        }
    }

    @Throws(ModelException::class)
    fun applyKindToContract(contract: ContractViewModel, kind: Kind) {
        when (kind) {
            Kind.MISS -> {
                contract.preCondition.value = contract.preCondition.not().value
                contract.postCondition.value = Condition("true").value
            }

            Kind.FAIL -> contract.postCondition.value = contract.postCondition.not().value
            Kind.HIT -> {}
        }
    }

    fun applyRegionsToContract(relevantRegions: List<RegionViewModel>, contract: ContractViewModel): ContractViewModel {
        val newContract: ContractViewModel
        try {
            newContract = ContractViewModel(contract.name, contract.preCondition, contract.postCondition)
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

    fun andConditions(regions: List<RegionViewModel>): Pair<Condition, Condition> {
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
            val c = region.contract
            newPre = newPre.and(c.preCondition)
            newPre = newPre.and(region.invariant)
            newPost = newPost.and(c.postCondition)
            newPost = newPost.and(region.invariant)
        }
        return newPre to newPost
    }

    fun serializeContract(contract: ContractViewModel?): String {
        return INDENT + SERIALIZED_CONTRACT.format(
            contract?.name, contract!!.preCondition, contract.postCondition
        )
    }

    fun serializeTransition(edge: EdgeViewModel): String {
        return INDENT + SERIALIZED_TRANSITION.format(
            edge.source?.name, edge.destination?.name,
            getContractName(edge)
        )
    }

    fun serializeSystem(system: SystemViewModel?): String {
        val joiner = StringJoiner(System.lineSeparator())
        joiner.add(SERIALIZED_SYSTEM.format(system!!.name))

        if (system.ports.isNotEmpty()) {
            joiner.add(serializeIo(system))
            joiner.add("")
        }
        if (system.subSystems.isNotEmpty()) {
            joiner.add(serializeChildren(system))
            joiner.add("")
        }
        if (system.automaton != null && system.automaton != null) {
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

    fun serializeConnections(system: SystemViewModel?): String {
        return serializeCollectionWithMapping(
            system!!.connections, { connection, parent ->
                this.serializeConnection(
                    connection,
                    parent
                )
            },
            system
        )
    }

    fun serializeConnection(connection: SystemConnectionViewModel, parent: SystemViewModel?): String {
        val startSystem = serializeSystemReference(parent, connection.source!!)
        val startPort = connection.source?.name
        val endSystem = serializeSystemReference(parent, connection.destination!!)
        val endPort = connection.destination?.name
        return String.format(INDENT + SERIALIZED_CONNECTION, startSystem, startPort, endSystem, endPort)
    }

    fun serializeSystemReference(parent: SystemViewModel?, v: PortViewModel): String {
        return if (parent!!.ports.contains(v)) {
            AutomatonFileVisitor.SELF_REFERENCE_TOKEN
        } else {
            parent.getChildSystemWithVariable(v)!!.name
        }
    }

    fun serializeIo(system: SystemViewModel): String {
        val orderedVariables = system.ports.sorted(Comparator.comparing { it.visibility })
        return serializeCollectionWithMapping(orderedVariables) { this.serializeVariable(it) }
    }

    fun serializeChildren(system: SystemViewModel): String {
        return serializeCollectionWithMapping(system.subSystems) { this.serializeChild(it) }
    }

    fun serializeChild(system: SystemViewModel): String {
        return String.format(INDENT + SERIALIZED_STATE, system.name, system.name)
    }

    fun serializeVariable(variable: PortViewModel): String {
        var output = ""
        output += INDENT + when (variable.visibility) {
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

    fun serializeCode(code: String): String {
        return INDENT + AutomatonFileVisitor.CODE_BEGIN + code + AutomatonFileVisitor.CODE_END
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

    fun getContractName(edge: EdgeViewModel): String {
        val name = edge.contract?.name
        if (edge.kind == Kind.HIT) {
            return name!!
        }
        return makeNameUnique("%s %s".format(name, edge.kind.name))
    }

    fun makeNameUnique(baseName: String): String {
        return baseName
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

