package org.gecko.view.views.viewelement;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;
import org.gecko.model.Element;
import org.gecko.viewmodel.PositionableViewModelElement;

/**
 * An abstract representation of a {@link Pane} view element, that is an element with a rectangular shape in a Gecko
 * project. Contains a list of {@link Point2D edge point}s.
 */
@Getter
public abstract class BlockViewElement extends Pane {
    protected static final int BACKGROUND_ROUNDING = 15;

    private final SimpleListProperty<Point2D> edgePoints = new SimpleListProperty<>(FXCollections.observableArrayList());

    @Setter
    private boolean selected;


    protected BlockViewElement(PositionableViewModelElement<? extends Element> positionableViewModelElement) {
        // Initialize edge points for a rectangular shaped block
        for (int i = 0; i < 4; i++) {
            edgePoints.add(Point2D.ZERO);
        }

        // Auto calculate new edge points on size and position changes
        positionableViewModelElement.getSizeProperty().addListener((observable, oldValue, newValue) -> calculateEdgePoints(positionableViewModelElement));
        positionableViewModelElement.getPositionProperty().addListener((observable, oldValue, newValue) -> calculateEdgePoints(positionableViewModelElement));

        calculateEdgePoints(positionableViewModelElement);
    }

    private void calculateEdgePoints(PositionableViewModelElement<?> target) {
        Point2D position = target.getPosition();
        double width = target.getSize().getX();
        double height = target.getSize().getY();
        edgePoints.setAll(
                position.add(Point2D.ZERO),
                position.add(new Point2D(width, 0)),
                position.add(new Point2D(width, height)),
                position.add(new Point2D(0, height)));
    }
}
