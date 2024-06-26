package org.gecko.view.inspector.element.container

import java.io.File
import java.nio.file.Files
import javafx.beans.property.StringProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.element.InspectorElement
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.view.inspector.element.textfield.InspectorCodeSystemField
import org.gecko.viewmodel.System

class InspectorCodeSystemContainer(actionManager: ActionManager, viewModel: System) :
    VBox(), InspectorElement<VBox> {
    init {
        if (viewModel.subSystems.isEmpty()) {
            children.add(InspectorLabel(ResourceHandler.code))
            val codeField = InspectorCodeSystemField(actionManager, viewModel)
            codeField
                .prefWidthProperty()
                .bind(widthProperty().subtract(InspectorElement.FIELD_OFFSET))

            val openExternally = Button("Open in Gedit")
            openExternally.onAction =
                OpenExternallyHandler(actionManager, viewModel, viewModel.codeProperty)
            children.addAll(codeField.control, openExternally)
        }
    }

    override val control = this

    data class OpenExternallyHandler(
        val actionManager: ActionManager,
        val viewModel: System,
        val codeProperty: StringProperty
    ) : EventHandler<ActionEvent?>, Runnable {
        override fun handle(actionEvent: ActionEvent?) {
            Thread(this).start()
        }

        override fun run() {
            val temp = File.createTempFile("gecko_system_", ".c").toPath()
            val csq = codeProperty.get()
            Files.writeString(temp, csq ?: "")
            val process =
                ProcessBuilder("/usr/bin/gedit", temp.toAbsolutePath().toString())
                    .inheritIO()
                    .start()
            val error = process.waitFor()
            if (error == 0) {
                val text = Files.readString(temp)
                actionManager.run(
                    actionManager.actionFactory.createChangeCodeSystemViewModelAction(
                        viewModel,
                        text
                    )
                )
            } else {
                // TODO log message
            }
        }
    }
}
