package org.gecko.view.views.viewelement

import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.shape.*
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
import javafx.scene.transform.Translate
import org.gecko.viewmodel.listProperty
import kotlin.math.abs

/**
 * An abstract representation of a [Path] type view element, that is a connection in a Gecko project. Contains a
 * list of [path point][Point2D]s.
 */

abstract class ConnectionViewElement(line: List<Point2D>) {
    val pathSource = listProperty<Point2D>()
    val line = Path()
    val arrowHead = createDefaultHead()
    val pane = Group(this.line, arrowHead)

    //MoveTo startElement;
    val isLoopProperty: BooleanProperty = SimpleBooleanProperty(false)
    protected val orientationProperty: IntegerProperty = SimpleIntegerProperty(0)

    val isSelected = false

    /**
     * The first element of the pair is the x property of the point, and the second element is the y property of the
     * point. This list represents the actual points that are drawn on the screen. pathSource is a subset of
     * renderPathSource. In order to draw a loop, extra points are added to renderPathSource.
     */
    protected val renderPathSource: MutableList<Point2D> = ArrayList()

    init {
        pathSource.setAll(line)
        pane.isManaged = false

        updatePathVisualization()

        val pathChangedListener =
            ListChangeListener { change: ListChangeListener.Change<out Point2D>? -> updatePathVisualization() }

        pathSource.addListener(pathChangedListener)
        isLoopProperty.addListener { observable: ObservableValue<out Boolean?>?, oldValue: Boolean?, newValue: Boolean? -> updatePathVisualization() }
        orientationProperty.addListener { observable: ObservableValue<out Number?>?, oldValue: Number?, newValue: Number? -> updatePathVisualization() }

        this.line.strokeWidth = STROKE_WIDTH.toDouble()
        this.line.styleClass.setAll(STYLE_CLASS)
    }

    /**
     * Update the visualization of the path. Path is drawn using the path source points. Path is automatically updated
     * upon change of individual path source points. If this connection view element is a loop, the path will be drawn
     * as a loop by adding extra points to render path source.
     */
    protected fun updatePathVisualization() {
        line.elements.clear()
        renderPathSource.clear()

        // If there are less than two points, there is no path to draw
        if (pathSource.size < MIN_REQUIRED_PATH_POINTS) {
            return
        }

        // Start element
        val first = pathSource.first()
        val last = pathSource.last()

        val startElement = MoveTo(first.x, first.y)
        line.elements.add(startElement)
        renderPathSource.add(Point2D(startElement.x, startElement.y))

        if (isLoopProperty.get()) {
            // If source and destination are the same, draw a loop
            val arcTo = ArcTo()

            // ArcTo(double radiusX, double radiusY, double xAxisRotation, double x, double y, boolean largeArcFlag, boolean sweepFlag) 
            arcTo.radiusX = abs(first.x - last.x)
            arcTo.radiusY = abs(first.y - last.y)
            arcTo.x = last.x
            arcTo.y = last.y

            arcTo.isLargeArcFlag = true
            arcTo.isSweepFlag = true

            line.elements.add(arcTo)
            renderPathSource.add(Point2D(arcTo.x, arcTo.y))
        } else {
            // Elements in the middle
            for (point in pathSource) {
                val lineTo = LineTo(point.x, point.y)
                line.elements.add(lineTo)
                renderPathSource.add(Point2D(lineTo.x, lineTo.y))
            }
        }

        transformArrowHead(first, last)
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

    fun transformArrowHead(start: Point2D, end: Point2D) {
        val direction = end.subtract(start).normalize()
        var angle = Point2D(1.0, 0.0).angle(direction)
        if (direction.y < 0 || direction.x < 0) {
            angle *= -1.0
        }


        //System.out.println("ANGLE: " + angle + "  " + direction);
        arrowHead.fill = Color.BLACK
        arrowHead.transforms.setAll(
            Translate(end.x, end.y),
            Rotate(angle, 0.0, 0.0),
            Scale(0.5, 0.5)
        )
    }


    fun calculateArrowHeadPosition(start: Point2D, end: Point2D, offset: Double): Point2D {
        val vector = end.subtract(start).normalize()
        val orthogonalVector = Point2D(-vector.y, vector.x).normalize()

        // Arrow head position is calculated by moving from the last point in the path (that is orthogonally shifted) by
        // offset to the first point in the path
        val arrowHeadPosition = end.add(orthogonalVector.multiply(offset))
        return arrowHeadPosition.subtract(vector.multiply(ARROW_HEAD_LENGTH))
    }

    companion object {
        const val STROKE_WIDTH = 2
        const val STYLE_CLASS = "path"
        const val ARROW_HEAD_LENGTH = 25.0
        const val ARROW_HEAD_ANGLE = 10.0
        val ANGLE_OFFSET_HELPER = Point2D(50.0, 10.0)
        const val MIN_REQUIRED_PATH_POINTS = 2

        fun createDefaultHead(): Path {
            val arrowHead = Path()
            val begin = MoveTo(0.0, 0.0)
            val x = -50
            val y = 25
            val toP1 = LineTo(x.toDouble(), y.toDouble())
            val toP2 = QuadCurveTo()
            toP2.x = x.toDouble()
            toP2.y = -y.toDouble()
            toP2.controlX = x / 2.0
            toP2.controlY = 0.0
            val toStart = LineTo(0.0, 0.0)
            arrowHead.elements.setAll(
                begin,
                toP1,
                toP2,
                toStart
            )
            return arrowHead
        }
    }
}

