package org.gecko.view.inspector.element.textfield;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;
import org.gecko.actions.Action;
import org.gecko.actions.ActionManager;
import org.gecko.view.inspector.element.InspectorElement;

public abstract class InspectorContractField extends TextArea implements InspectorElement<TextArea> {
    private static final int MAX_HEIGHT = 30;
    private static final int EXPANDED_MAX_HEIGHT = 90;

    protected InspectorContractField(ActionManager actionManager, StringProperty stringProperty) {
        setText(stringProperty.get());
        stringProperty.addListener((observable, oldValue, newValue) -> setText(newValue));
        setPrefHeight(MAX_HEIGHT);
        setWrapText(true);

        setOnMouseExited(event -> {
            if (getText().equals(stringProperty.get())) {
                return;
            }

            if (getText().isEmpty()) {
                setText("true");
            }

            actionManager.run(getAction());
        });
    }

    protected abstract Action getAction();

    public void toggleExpand() {
        setPrefHeight(getPrefHeight() == MAX_HEIGHT ? EXPANDED_MAX_HEIGHT : MAX_HEIGHT);
    }

    @Override
    public TextArea getControl() {
        return this;
    }
}
