package org.gecko.view.inspector.element.container;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import org.gecko.actions.ActionManager;
import org.gecko.view.ResourceHandler;
import org.gecko.view.inspector.element.InspectorElement;
import org.gecko.view.inspector.element.button.InspectorRemoveVariableButton;
import org.gecko.view.inspector.element.combobox.InspectorTypeComboBox;
import org.gecko.view.inspector.element.label.InspectorLabel;
import org.gecko.view.inspector.element.textfield.InspectorRenameField;
import org.gecko.viewmodel.PortViewModel;

/**
 * Represents a type of {@link VBox} implementing the {@link InspectorElement} interface. Holds a reference to a
 * {@link PortViewModel} and contains an {@link InspectorRenameField}, an {@link InspectorRemoveVariableButton} and an
 * {link InspectorTypeField}.
 */
@Getter
public class InspectorVariableField extends VBox implements InspectorElement<VBox> {

    private final PortViewModel viewModel;

    public InspectorVariableField(ActionManager actionManager, PortViewModel portViewModel) {
        this.viewModel = portViewModel;

        HBox nameAndDeleteContainer = new HBox();
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox typeContainer = new HBox();


        var nameLabel = new InspectorLabel(ResourceHandler.getString("Inspector", "name"));
        var variableNameField = new InspectorRenameField(actionManager, portViewModel);
        var deleteButton = new InspectorRemoveVariableButton(actionManager, portViewModel);
        nameAndDeleteContainer.getChildren().addAll(
                nameLabel.getControl(), variableNameField.getControl(),
                spacer, deleteButton.getControl());

        var typeLabel = new InspectorLabel(ResourceHandler.getString("Inspector", "type"));
        var typeField = new InspectorTypeComboBox(actionManager, portViewModel);

        typeContainer.getChildren().addAll(typeLabel.getControl(), typeField.getControl());
        getChildren().addAll(nameAndDeleteContainer, typeContainer);
    }

    @Override
    public VBox getControl() {
        return this;
    }
}
