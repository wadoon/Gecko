package org.gecko.view.views.viewelement.decorator;

import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import org.gecko.view.views.viewelement.ViewElement;
import org.gecko.view.views.viewelement.ViewElementVisitor;

public class SelectableViewElementDecorator extends ViewElementDecorator {

    private static final double STROKE_WIDTH = 3;

    private final Group decoratedNode;

    public SelectableViewElementDecorator(ViewElement<?> decoratorTarget) {
        super(decoratorTarget);
        decoratedNode = new Group();

        List<Property<Point2D>> borderPoints = getDecoratorTarget().getEdgePoints();
        Path borderLine = new Path();

        // Start at the first point
        MoveTo startPoint = new MoveTo(decoratorTarget.getPosition().getX() + borderPoints.get(0).getValue().getX(),
            decoratorTarget.getPosition().getY() + borderPoints.get(0).getValue().getY());
        startPoint.xProperty()
                  .bind(Bindings.createDoubleBinding(() -> decoratorTarget.getPosition().getX() + borderPoints.get(0).getValue().getX(),
                      borderPoints.get(0)));
        startPoint.yProperty()
                  .bind(Bindings.createDoubleBinding(() -> decoratorTarget.getPosition().getY() + borderPoints.get(0).getValue().getY(),
                      borderPoints.get(0)));

        borderLine.getElements().add(startPoint);

        for (int i = 1; i < borderPoints.size(); i++) {
            LineTo connectingPoint = new LineTo(decoratorTarget.getPosition().getX() + borderPoints.get(i).getValue().getX(),
                decoratorTarget.getPosition().getY() + borderPoints.get(i).getValue().getY());
            int finalI = i;
            connectingPoint.xProperty()
                           .bind(Bindings.createDoubleBinding(() -> decoratorTarget.getPosition().getX() + borderPoints.get(finalI).getValue().getX(),
                               borderPoints.get(i)));
            connectingPoint.yProperty()
                           .bind(Bindings.createDoubleBinding(() -> decoratorTarget.getPosition().getY() + borderPoints.get(finalI).getValue().getY(),
                               borderPoints.get(i)));

            borderLine.getElements().add(connectingPoint);
        }

        // End at the last point
        ClosePath endPoint = new ClosePath();
        borderLine.getElements().add(endPoint);

        borderLine.setStrokeWidth(STROKE_WIDTH);
        borderLine.setStroke(Color.BLUE);

        decoratedNode.getChildren().addAll(borderLine, getDecoratorTarget().drawElement());
        decoratedNode.getChildren().get(0).setVisible(false);
    }

    @Override
    public Node drawElement() {
        return decoratedNode;
    }

    @Override
    public void setSelected(boolean selected) {
        decoratedNode.getChildren().get(0).setVisible(selected);
    }

    @Override
    public Point2D getPosition() {
        return getDecoratorTarget().getPosition();
    }


    @Override
    public void accept(ViewElementVisitor visitor) {
        visitor.visit(this);
        getDecoratorTarget().accept(visitor);
    }
}
