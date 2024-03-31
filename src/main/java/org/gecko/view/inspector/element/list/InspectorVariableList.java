package org.gecko.view.inspector.element.list;

import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.gecko.actions.ActionManager;
import org.gecko.model.Visibility;
import org.gecko.view.inspector.element.InspectorElement;
import org.gecko.view.inspector.element.container.InspectorVariableField;
import org.gecko.viewmodel.PortViewModel;
import org.gecko.viewmodel.SystemViewModel;

/**
 * A concrete representation of an {@link AbstractInspectorList} encapsulating an {@link InspectorVariableField}.
 */
public class InspectorVariableList implements InspectorElement<VBox> {
    private static final double SPACING = 5;
    private static final String STYLE_CLASS = "inspector-list";

    private final ActionManager actionManager;
    private final SystemViewModel viewModel;
    private final Visibility visibility;

    private final TableView<PortViewModel> view = new TableView<>();
    private final VBox box = new VBox(view);

    public InspectorVariableList(ActionManager actionManager,
                                 SystemViewModel viewModel,
                                 Visibility visibility) {
        this.actionManager = actionManager;
        this.viewModel = viewModel;
        this.visibility = visibility;

        FilteredList<PortViewModel> ports = new FilteredList<>(viewModel.getPortsProperty(),
                port -> port.getVisibility() == visibility);
        view.itemsProperty().set(ports);

        //viewModel.getPortsProperty().addListener(this::onPortsListChanged);
        //viewModel.getPorts().stream().filter(port -> port.getVisibility() == visibility).forEach(this::addPortItem);
        //viewModel.getPorts().forEach(port -> port.getVisibilityProperty().addListener(this::onVisibilityChanged));


        TableColumn<PortViewModel, String> colName = new TableColumn<>("Name");
        colName.setSortType(TableColumn.SortType.ASCENDING);
        colName.setCellValueFactory(tcf -> tcf.getValue().getNameProperty());

        TableColumn<PortViewModel, String> colDatatype = new TableColumn<>("Type");
        colDatatype.setSortType(TableColumn.SortType.ASCENDING);
        colDatatype.setCellValueFactory(tcf -> tcf.getValue().getTypeProperty());

        view.getColumns().setAll(colName, colDatatype);

        view.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.DELETE) {
                    keyEvent.consume();

                    final var selected = view.getSelectionModel().getSelectedItems();
                    viewModel.getPortsProperty().removeAll(selected);
                }
            }
        });


        view.getStyleClass().add(STYLE_CLASS);
    }

    @Override
    public VBox getControl() {
        return box;
    }
}
