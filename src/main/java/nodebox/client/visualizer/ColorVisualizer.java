package nodebox.client.visualizer;

import com.google.common.collect.Iterables;
import nodebox.graphics.*;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Visualizes Color objects.
 */
public final class ColorVisualizer implements Visualizer {

    public static final ColorVisualizer INSTANCE = new ColorVisualizer();
    public static final int COLOR_MARGIN = 10;
    public static final int MAX_WIDTH = 500;
    public static final int COLOR_SIZE = 30;
    public static final int COLORS_PER_ROW = (MAX_WIDTH / (COLOR_SIZE + COLOR_MARGIN)) + 1;
    public static final int COLOR_TOTAL_SIZE = COLOR_SIZE + COLOR_MARGIN;

    private ColorVisualizer() {
    }

    public boolean accepts(Iterable<?> objects, Class listClass) {
        return Color.class.isAssignableFrom(listClass);
    }

    public Rectangle2D getBounds(Iterable<?> objects) {
        int size = Iterables.size(objects);
        int h = ((size / COLORS_PER_ROW) + 1) * COLOR_TOTAL_SIZE;
        return new Rectangle2D.Double(0, 0, MAX_WIDTH, h);
    }

    public Point2D getOffset(Iterable<?> objects, Dimension2D size) {
        return new Point2D.Double(10, 10);
    }

    @Override
    public Grob visualize(Iterable<?> objects) {
        Geometry geo = new Geometry();
        Path borders = new Path();
        borders.setFill(Color.WHITE);
        borders.setStroke(new Color(200, 200, 200));
        geo.add(borders);

        int x = 0;
        int y = 0;

        for (Object o : objects) {
            Color c = (Color) o;
            borders.roundedRect(new Rect(x, y, COLOR_SIZE + 6, COLOR_SIZE + 6), 3);
            Path p = new Path();
            p.rect(new Rect(x + 3, y + 3, COLOR_SIZE, COLOR_SIZE));
            p.setFill(c);
            geo.add(p);

            x += COLOR_TOTAL_SIZE;
            if (x > MAX_WIDTH) {
                x = 0;
                y += COLOR_TOTAL_SIZE;
            }
        }

        return geo;
    }

}
