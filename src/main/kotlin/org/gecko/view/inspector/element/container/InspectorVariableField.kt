package org.gecko.view.inspector.element.container

import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.gecko.actions.ActionManager
import org.gecko.view.ResourceHandler
import org.gecko.view.inspector.element.InspectorElement
import org.gecko.view.inspector.element.button.InspectorRemoveVariableButton
import org.gecko.view.inspector.element.combobox.InspectorTypeComboBox
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.view.inspector.element.textfield.InspectorRenameField
import org.gecko.viewmodel.Port

/**
 * Represents a type of [VBox] implementing the [InspectorElement] interface. Holds a reference to a
 * [Port] and contains an [InspectorRenameField], an [InspectorRemoveVariableButton] and an
 * {link InspectorTypeField}.
 */
class InspectorVariableField(actionManager: ActionManager, val viewModel: Port) : VBox(),
    InspectorElement<VBox> {

    init {
        val nameAndDeleteContainer = HBox()
        val spacer = HBox()
        HBox.setHgrow(spacer, Priority.ALWAYS)
        val typeContainer = HBox()


        val nameLabel = InspectorLabel(ResourceHandler.name)
        val variableNameField = InspectorRenameField(actionManager, viewModel)
        val deleteButton = InspectorRemoveVariableButton(actionManager, viewModel)
        nameAndDeleteContainer.children.addAll(
            nameLabel.control, variableNameField.control,
            spacer, deleteButton.control
        )

        val typeLabel = InspectorLabel(ResourceHandler.type)
        val typeField = InspectorTypeComboBox(actionManager, viewModel)

        typeContainer.children.addAll(typeLabel.control, typeField.control)
        children.addAll(nameAndDeleteContainer, typeContainer)
    }

    override val control
        get() = this

}
