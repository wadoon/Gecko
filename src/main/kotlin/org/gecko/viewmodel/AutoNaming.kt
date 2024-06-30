package org.gecko.viewmodel

import java.util.concurrent.atomic.AtomicInteger

/**
 * @author Alexander Weigl
 * @version 1 (22.06.24)
 */
object AutoNaming {
    private val counter = AtomicInteger()

    fun name(prefix: String) = "$prefix${counter.incrementAndGet()}"
}
