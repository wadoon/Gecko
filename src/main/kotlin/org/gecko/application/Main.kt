package org.gecko.application

import javafx.application.Application

/**
 * The main class of the Gecko Graphic Editor for Contract Automata, serving as the entry point of
 * the [Application]. Provides the start method to configure and display the primary
 * [javafx.stage.Stage] of the application.
 */
object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        Application.launch(GeckoManager::class.java, *args)
    }
}
