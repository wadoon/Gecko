package org.gecko.view.views.viewelement;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract representation of a {@link Path} type view element, that is a connection in a Gecko project. Contains a
 * list of {@link Point2D path point}s.
 */
@Data
public abstract class ConnectionViewElement {
    private static final int STROKE_WIDTH = 2;
    private static final String STYLE_CLASS = "path";
    private static final double ARROW_HEAD_LENGTH = 25;
    private static final double ARROW_HEAD_ANGLE = 10;
    private static final Point2D ANGLE_OFFSET_HELPER = new Point2D(50, 10);
    private static final int MIN_REQUIRED_PATH_POINTS = 2;

    private final SimpleListProperty<Point2D> pathSource = new SimpleListProperty<>(FXCollections.observableArrayList());

    @Getter(AccessLevel.PROTECTED)
    private final Path line = new Path();
    private final Path arrowHead = createDefaultHead();
    private final Group pane = new Group(line, arrowHead);

    //private MoveTo startElement;
    final BooleanProperty isLoopProperty = new SimpleBooleanProperty(false);
    protected final IntegerProperty orientationProperty = new SimpleIntegerProperty(0);

    private boolean selected;

    /**
     * The first element of the pair is the x property of the point, and the second element is the y property of the
     * point. This list represents the actual points that are drawn on the screen. pathSource is a subset of
     * renderPathSource. In order to draw a loop, extra points are added to renderPathSource.
     */
    protected final List<P2> renderPathSource = new ArrayList<>();

    record P2(double x, double y) {
    }

    protected ConnectionViewElement(List<Point2D> line) {
        this.pathSource.setAll(line);
        pane.setManaged(false);

        updatePathVisualization();

        ListChangeListener<Point2D> pathChangedListener = change -> updatePathVisualization();

        pathSource.addListener(pathChangedListener);
        isLoopProperty.addListener((observable, oldValue, newValue) -> updatePathVisualization());
        orientationProperty.addListener((observable, oldValue, newValue) -> updatePathVisualization());

        this.line.setStrokeWidth(STROKE_WIDTH);
        this.line.getStyleClass().setAll(STYLE_CLASS);

    }

    /**
     * Update the visualization of the path. Path is drawn using the path source points. Path is automatically updated
     * upon change of individual path source points. If this connection view element is a loop, the path will be drawn
     * as a loop by adding extra points to render path source.
     */
    protected void updatePathVisualization() {
        line.getElements().clear();
        renderPathSource.clear();

        // If there are less than two points, there is no path to draw
        if (pathSource.size() < MIN_REQUIRED_PATH_POINTS) {
            return;
        }

        // Start element
        final var first = pathSource.getFirst();
        final var last = pathSource.getLast();

        var startElement = new MoveTo(first.getX(), first.getY());
        line.getElements().add(startElement);
        renderPathSource.add(new P2(startElement.getX(), startElement.getY()));

        if (isLoopProperty.get()) {
            // If source and destination are the same, draw a loop
            ArcTo arcTo = new ArcTo();
            // ArcTo(double radiusX, double radiusY, double xAxisRotation, double x, double y, boolean largeArcFlag, boolean sweepFlag) 

            arcTo.setRadiusX(Math.abs(first.getX() - last.getX()));
            arcTo.setRadiusY(Math.abs(first.getY() - last.getY()));
            arcTo.setX(last.getX());
            arcTo.setY(last.getY());

            arcTo.setLargeArcFlag(true);
            arcTo.setSweepFlag(true);

            line.getElements().add(arcTo);
            renderPathSource.add(new P2(arcTo.getX(), arcTo.getY()));
        } else {
            // Elements in the middle
            for (var point : pathSource) {
                LineTo lineTo = new LineTo(point.getX(), point.getY());
                line.getElements().add(lineTo);
                renderPathSource.add(new P2(lineTo.getX(), lineTo.getY()));
            }
        }

        transformArrowHead(first, last);
        /*
        var lastX = renderPathSource.getLast().x;
        var lastY = renderPathSource.getLast().y;

        if (isLoopProperty.get()) {
            switch (orientationProperty.get()) {
                case 0:
                    secondLastX.bind(lastX.add(ANGLE_OFFSET_HELPER.getX()));
                    secondLastY.bind(lastY.subtract(ANGLE_OFFSET_HELPER.getY()));
                    break;
                case 1:
                    secondLastX.bind(lastX.add(ANGLE_OFFSET_HELPER.getY()));
                    secondLastY.bind(lastY.add(ANGLE_OFFSET_HELPER.getX()));
                    break;
                case 2:
                    secondLastX.bind(lastX.subtract(ANGLE_OFFSET_HELPER.getX()));
                    secondLastY.bind(lastY.add(ANGLE_OFFSET_HELPER.getY()));
                    break;
                case 3:
                    secondLastX.bind(lastX.subtract(ANGLE_OFFSET_HELPER.getY()));
                    secondLastY.bind(lastY.subtract(ANGLE_OFFSET_HELPER.getX()));
                    break;
                default:
                    break;
            }
        }

        // Arrow head
        MoveTo arrowHeadUp = new MoveTo(last.getX(), last.getY());
        path.getElements().add(arrowHeadUp);

        Point2D arrowHeadUpPoint = calculateArrowHeadPosition(secondLastX.getValue(), secondLastY.getValue(), lastX.getValue(), lastY.getValue(), ARROW_HEAD_ANGLE);
        LineTo arrowHeadUpLine = new LineTo(arrowHeadUpPoint.getX(), arrowHeadUpPoint.getY());
        arrowHeadUpLine.xProperty()
                .bind(Bindings.createDoubleBinding(
                        () -> calculateArrowHeadPosition(new Point2D(secondLastX.getValue(), secondLastY.getValue()),
                                new Point2D(lastX.getValue(), lastY.getValue()), ARROW_HEAD_ANGLE).getX(), last,
                        first));
        arrowHeadUpLine.yProperty()
                .bind(Bindings.createDoubleBinding(
                        () -> calculateArrowHeadPosition(new Point2D(secondLastX.getValue(), secondLastY.getValue()),
                                new Point2D(lastX.getValue(), lastY.getValue()), ARROW_HEAD_ANGLE).getY(), last,
                        first));
        getElements().add(arrowHeadUpLine);

        MoveTo arrowHeadDown = new MoveTo(last.getX(), last.getY());
        path.getElements().add(arrowHeadDown);
        Point2D arrowHeadDownPoint =
                calculateArrowHeadPosition(
                        new Point2D(secondLastX.getValue(), secondLastY.getValue()),
                        new Point2D(lastX.getValue(), lastY.getValue()), -ARROW_HEAD_ANGLE);

        LineTo arrowHeadDownLine = new LineTo(arrowHeadDownPoint.getX(), arrowHeadDownPoint.getY());
        arrowHeadDownLine.yProperty()
        path.getElements().add(arrowHeadDownLine);
        */
    }

    private static Path createDefaultHead() {
        var arrowHead = new Path();
        var begin = new MoveTo(0, 0);
        final var x = -50;
        final var y = 25;
        var toP1 = new LineTo(x, y);
        var toP2 = new QuadCurveTo();
        toP2.setX(x);
        toP2.setY(-y);
        toP2.setControlX(x / 2.0);
        toP2.setControlY(0);
        var toStart = new LineTo(0, 0);
        arrowHead.getElements().setAll(
                begin,
                toP1,
                toP2,
                toStart
        );
        return arrowHead;
    }

    private void transformArrowHead(Point2D start, Point2D end) {
        var direction = end.subtract(start).normalize();
        var angle = new Point2D(1, 0).angle(direction);
        if (direction.getY() < 0 || direction.getX() < 0) {
            angle *= -1;
        }


        //System.out.println("ANGLE: " + angle + "  " + direction);
        arrowHead.setFill(Color.BLACK);
        arrowHead.getTransforms().setAll(
                new Translate(end.getX(), end.getY()),
                new Rotate(angle, 0.0, 0.0),
                new Scale(0.5, 0.5)
        );
    }


    private Point2D calculateArrowHeadPosition(Point2D start, Point2D end, double offset) {
        Point2D vector = end.subtract(start).normalize();
        Point2D orthogonalVector = new Point2D(-vector.getY(), vector.getX()).normalize();

        // Arrow head position is calculated by moving from the last point in the path (that is orthogonally shifted) by
        // offset to the first point in the path
        Point2D arrowHeadPosition = end.add(orthogonalVector.multiply(offset));
        return arrowHeadPosition.subtract(vector.multiply(ARROW_HEAD_LENGTH));
    }
}
