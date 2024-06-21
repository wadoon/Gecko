package org.gecko.viewmodel

import com.sun.javafx.collections.NonIterableChange
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.ObservableListBase
import org.gecko.actions.Action
import org.gecko.lint.Problem
import tornadofx.getValue


/**
 * Represents an abstraction of a view model element of a Gecko project. An [Element] has an id
 * and a target-[Element], the data of which it can update.
 */
abstract class Element {
    val issuesProperty = listProperty<Problem>()
    val issues: ObservableList<Problem> by issuesProperty
    abstract val children: Sequence<Element>

    fun allIssues(): Sequence<Problem> =
        issues.asSequence() + children.flatMap { it.allIssues() }
}


class ConcatList(vararg seqs: ObservableList<Problem>) : ObservableListBase<Problem>() {
    private val lists = seqs.toList()

    init {
        lists.forEach { it.onListChange { this.sourceChanged() } }
    }

    private fun sourceChanged() {
        //fireChange(NonIterableChange.GenericAddRemoveChange())
    }

    override fun get(index: Int): Problem {
        var idx = index
        lists.forEach {
            if (idx < it.size) {
                return it[idx]
            } else {
                idx -= it.size
            }
        }
        throw IndexOutOfBoundsException()
    }

    override val size: Int
        get() = lists.sumOf { it.size }
}