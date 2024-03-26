package org.gecko.view.inspector.element.container;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import org.gecko.actions.ActionManager;
import org.gecko.view.ResourceHandler;
import org.gecko.view.inspector.element.InspectorElement;
import org.gecko.view.inspector.element.label.InspectorLabel;
import org.gecko.view.inspector.element.textfield.InspectorCodeSystemField;
import org.gecko.viewmodel.SystemViewModel;

import java.io.File;
import java.nio.file.Files;

public class InspectorCodeSystemContainer extends VBox implements InspectorElement<VBox> {

    public InspectorCodeSystemContainer(ActionManager actionManager, SystemViewModel viewModel) {
        if (!viewModel.getTarget().getChildren().isEmpty()) {
            return;
        }

        getChildren().add(new InspectorLabel(ResourceHandler.getString("Inspector", "code")));
        var codeField = new InspectorCodeSystemField(actionManager, viewModel);
        codeField.prefWidthProperty().bind(widthProperty().subtract(FIELD_OFFSET));

        var openExternally = new Button("Open in Gedit");
        openExternally.setOnAction(new OpenExternallyHandler(actionManager, viewModel, viewModel.getCodeProperty()));
        getChildren().addAll(codeField.getControl(), openExternally);
    }

    @Override
    public VBox getControl() {
        return this;
    }

    private record OpenExternallyHandler(ActionManager actionManager, SystemViewModel viewModel,
                                         StringProperty codeProperty) implements EventHandler<ActionEvent>, Runnable {
        @Override
        public void handle(ActionEvent actionEvent) {
            new Thread(this).start();
        }
        @Override
        @SneakyThrows
        public void run() {
            var temp = File.createTempFile("gecko_system_", ".c").toPath();
            final var csq = codeProperty.get();
            Files.writeString(temp, csq == null ? "" : csq);
            var process = new ProcessBuilder("/usr/bin/gedit", temp.toAbsolutePath().toString())
                    .inheritIO()
                    .start();
            var error = process.waitFor();
            if (error == 0) {
                var text = Files.readString(temp);
                actionManager.run(
                        actionManager.getActionFactory().createChangeCodeSystemViewModelAction(viewModel, text));
            } else {
                //TODO log message
            }
        }
    }
}
