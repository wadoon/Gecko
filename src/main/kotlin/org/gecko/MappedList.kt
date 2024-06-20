package org.gecko

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.transformation.TransformationList
import java.util.function.Function

/**
 * @author Alexander Weigl
 * @version 1 (23.05.24)
 */
class MappedList<E, F>(observableList: ObservableList<out F>, private val translator: Function<F, E>) :
    TransformationList<E, F>(observableList) {
    private var translated: List<E> = ArrayList()

    init {
        translated = source.stream().map(translator).toList()
    }

    override fun sourceChanged(change: ListChangeListener.Change<out F>) {
        translated = source.stream().map(translator).toList()
    }

    override fun getSourceIndex(i: Int): Int = i

    override fun getViewIndex(i: Int): Int = i

    override val size: Int
        get() = source.size

    override fun get(index: Int): E = translator.apply(source[index])
}

