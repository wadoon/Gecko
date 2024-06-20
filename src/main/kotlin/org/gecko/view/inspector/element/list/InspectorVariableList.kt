package org.gecko.view.inspector.element.list

import javafx.collections.transformation.FilteredList
import javafx.event.EventHandler
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.input.KeyCode
import javafx.scene.layout.VBox
import org.gecko.actions.ActionManager
import org.gecko.viewmodel.Visibility
import org.gecko.view.inspector.element.InspectorElement
import org.gecko.viewmodel.PortViewModel
import org.gecko.viewmodel.SystemViewModel

/**
 * A concrete representation of an [AbstractInspectorList] encapsulating an [InspectorVariableField].
 */
class InspectorVariableList(
    val actionManager: ActionManager,
    val viewModel: SystemViewModel,
    val visibility: Visibility
) : InspectorElement<VBox> {
    val view = TableView<PortViewModel>()
    override val control: VBox = VBox(view)

    init {
        val ports = FilteredList(
            viewModel.portsProperty
        ) { port: PortViewModel -> port.visibility == visibility }
        view.itemsProperty().set(ports)


        //viewModel.getPortsProperty().addListener(this::onPortsListChanged);
        //viewModel.getPorts().stream().filter(port -> port.getVisibility() == visibility).forEach(this::addPortItem);
        //viewModel.getPorts().forEach(port -> port.getVisibilityProperty().addListener(this::onVisibilityChanged));
        val colName = TableColumn<PortViewModel, String>("Name")
        colName.sortType = TableColumn.SortType.ASCENDING
        colName.setCellValueFactory { tcf: TableColumn.CellDataFeatures<PortViewModel, String> -> tcf.value.nameProperty }

        val colDatatype = TableColumn<PortViewModel, String>("Type")
        colDatatype.sortType = TableColumn.SortType.ASCENDING
        colDatatype.setCellValueFactory { tcf: TableColumn.CellDataFeatures<PortViewModel, String> -> tcf.value.typeProperty }

        view.columns.setAll(colName, colDatatype)

        view.onKeyTyped = EventHandler { keyEvent ->
            if (keyEvent.code == KeyCode.DELETE) {
                keyEvent.consume()

                val selected = view.selectionModel.selectedItems
                viewModel.portsProperty.removeAll(selected)
            }
        }


        view.styleClass.add(STYLE_CLASS)
    }

    companion object {
        const val SPACING = 5.0
        const val STYLE_CLASS = "inspector-list"
    }
}
