package org.gecko.lint

import javafx.scene.control.Label
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.onListChange
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign2.MaterialDesignA
import tornadofx.*

/**
 *
 * @author Alexander Weigl
 * @version 1 (21.06.24)
 */
open class IssuesView : UIComponent("IssuesView") {
    val listProblems = tableview<Problem> {
        readonlyColumn("#", Problem::severity)
        readonlyColumn("Message", Problem::message)
    }
    val problemsProperty = org.gecko.viewmodel.listProperty<Problem>()
    val problems by problemsProperty


    override val root = borderpane {
        top = label("Problems View") {}
        center = listProblems
    }

    init {
        listProblems.itemsProperty().bind(problemsProperty)
        icon = FontIcon(MaterialDesignA.ALERT_BOX)

        problemsProperty.onListChange { c ->
            if (problems.any { it.severity >= 10.0 })
                icon.style = "-fx-icon-color:red"
            else if (problems.any { it.severity > 0.0 })
                icon.style = "-fx-icon-color:orange"
            else icon.style = "-fx-icon-color:black"

        }


        listProblems.prefWidth = 100.0
        listProblems.prefHeight = 150.0
        listProblems.isTableMenuButtonVisible = true
        listProblems.isEditable = false
        listProblems.placeholder = Label("No issues found")
    }
}

class ModelIssuesView(val geckoViewModel: GeckoViewModel) : UIComponent("Model Issues") {
    val issuesView = IssuesView()
    override val root = issuesView.root
}


data class Problem(val message: String, val severity: Double)
