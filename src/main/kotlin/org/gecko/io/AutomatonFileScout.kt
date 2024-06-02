package org.gecko.io

import gecko.parser.SystemDefBaseVisitor
import gecko.parser.SystemDefParser.*

import org.gecko.model.*
import java.util.function.Consumer

/**
 * The AutomatonFileScout is responsible for scanning the parsed automaton file and extracting information about the
 * systems, automata, and contracts. It is used by the [AutomatonFileParser] to give access to information about a
 * sys file that would otherwise be hard to obtain while visiting single elements.
 */
class AutomatonFileScout(ctx: ModelContext) {
    val systems: MutableMap<String, SystemContext> = HashMap()
    val automata: MutableMap<String, AutomataContext> = HashMap()
    val contracts: MutableMap<String, PrepostContext> = HashMap()

    val foundChildren: MutableSet<SystemInfo> = HashSet()
    val rootChildrenIdents: MutableSet<String> = HashSet()


    val rootChildren: MutableSet<SystemContext> = HashSet()
    val parents: MutableMap<SystemContext?, MutableList<SystemContext>> =
        HashMap()

    val scoutVisitor: ScoutVisitor

    init {
        this.scoutVisitor = ScoutVisitor()
        ctx.accept(scoutVisitor)
    }

    fun getSystem(name: String): SystemContext? {
        return systems[name]
    }

    fun getAutomaton(name: String): AutomataContext? {
        return automata[name]
    }

    fun getContract(name: String): PrepostContext? {
        return contracts[name]
    }

    fun getParents(ctx: SystemContext?): List<SystemContext> {
        return parents[ctx]!!
    }

    fun getChildSystemInfos(ctx: SystemContext): List<SystemInfo> {
        return scoutVisitor.getChildSystems(ctx)
    }

    inner class ScoutVisitor : SystemDefBaseVisitor<Unit>() {
        override fun visitModel(ctx: ModelContext) {
            ctx.system().forEach(Consumer { system: SystemContext -> system.accept(this) })
            ctx.system().forEach(Consumer { systemContext: SystemContext -> this.registerParent(systemContext) })
            ctx.contract().forEach(Consumer { contract: ContractContext -> contract.accept(this) })
            val defines = ctx.defines()
            defines?.variable()
                ?.forEach(Consumer { variable: VariableContext ->
                    variable.accept(
                        this
                    )
                })
            foundChildren.stream().map(SystemInfo::type).toList().forEach(
                Consumer { o: String -> rootChildrenIdents.remove(o) })
            rootChildren.addAll(ctx.system()
                .stream()
                .filter { system: SystemContext -> rootChildrenIdents.contains(system.ident().Ident().text) }
                .toList())
        }

        override fun visitSystem(ctx: SystemContext) {
            val sysName = ctx.ident().Ident().text
            systems[sysName] = ctx
            rootChildrenIdents.add(sysName)
            foundChildren.addAll(getChildSystems(ctx))
            parents[ctx] = ArrayList()
        }

        override fun visitContract(ctx: ContractContext) {
            automata[ctx.automata().ident().Ident().text] = ctx.automata()
            for (prepost in ctx.automata().prepost()) {
                contracts[prepost.ident().text] = prepost
            }
        }

        fun getChildSystems(ctx: SystemContext): List<SystemInfo> {
            val children: MutableList<SystemInfo> = ArrayList()
            ctx.io().filter { io -> io.type.type == STATE }
                .forEach { io ->
                    children.addAll(io.variable()
                        .filter { !builtinTypes.contains(it.t.text) }
                        .flatMap { it.n.map { n -> SystemInfo(n.text, it.t.text) } })
                }
            return children
        }

        fun registerParent(systemContext: SystemContext) {
            for ((_, type) in getChildSystems(systemContext)) {
                val childCtx = systems[type] ?: continue
                parents[childCtx]!!.add(systemContext)
            }
        }
    }
}

@JvmRecord
data class SystemInfo(val name: String, val type: String)
