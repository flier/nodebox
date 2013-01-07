package nodebox.client.visualizer;

import nodebox.graphics.Color;
import nodebox.graphics.*;
import nodebox.graphics.Point;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class PointVisualizer implements Visualizer {

    public static final PointVisualizer INSTANCE = new PointVisualizer();
    public static final double HALF_POINT_SIZE = 2;
    public static final double POINT_SIZE = 4;
    public static final Color ON_CURVE_COLOR = new Color(0, 0, 1);
    public static final Color OFF_CURVE_COLOR = new Color(1, 0, 0);

    private PointVisualizer() {
    }

    public boolean accepts(Iterable<?> objects, Class listClass) {
        return listClass == Point.class;
    }

    public Rectangle2D getBounds(Iterable<?> objects) {
        Rectangle2D.Double bounds = new Rectangle2D.Double();
        for (Object o : objects) {
            if (o instanceof Point) {
                Point pt = (Point) o;
                bounds.add(pt.toPoint2D());
            } else if (o instanceof Iterable) {
                bounds.add(getBounds((Iterable<?>) o));
            }
        }
        bounds.x -= 5;
        bounds.y -= 5;
        bounds.width += 10;
        bounds.height += 10;
        return bounds;
    }

    public Point2D getOffset(Iterable<?> objects, Dimension2D size) {
        return new Point2D.Double(size.getWidth() / 2, size.getHeight() / 2);
    }

    @Override
    public Grob visualize(Iterable<?> objects) {
        Geometry geo = new Geometry();
        Path onCurves = new Path();
        onCurves.setFillColor(ON_CURVE_COLOR);
        Path offCurves = new Path();
        offCurves.setFillColor(OFF_CURVE_COLOR);
        geo.add(onCurves);
        geo.add(offCurves);
        visualizePoints(objects, onCurves, offCurves);
        return geo;
    }

    private void visualizePoints(Iterable<?> points, Path onCurves, Path offCurves) {
        for (Object o : points) {
            if (o instanceof Point) {
                Point point = (Point) o;
                Shape s = new Ellipse2D.Double(point.x - HALF_POINT_SIZE, point.y - HALF_POINT_SIZE, POINT_SIZE, POINT_SIZE);
                if (point.isOnCurve()) {
                    onCurves.extend(s);
                } else {
                    offCurves.extend(s);
                }
            } else if (o instanceof Iterable) {
                visualizePoints((Iterable<?>) o, onCurves, offCurves);
            }
        }
    }

}
