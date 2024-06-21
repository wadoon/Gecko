package org.gecko.lint

import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.layout.BorderPane
import org.gecko.viewmodel.GeckoViewModel
import tornadofx.*

/**
 *
 * @author Alexander Weigl
 * @version 1 (21.06.24)
 */
class IssuesView(val geckoViewModel: GeckoViewModel) {
    val listProblems = TableView<Problem>().apply {
        column("#", Problem::severity.getter)
        column("#", Problem::message.getter)
    }
    val problemsProperty = listProblems.itemsProperty()
    val root = BorderPane().apply {
        top = Label("Problems View")
        center = listProblems
    }
}

data class Problem(val message: String, val severity: Double)
