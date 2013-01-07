package nodebox.client.visualizer;

import nodebox.graphics.Canvas;
import nodebox.graphics.Grob;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Visualizes NodeBox graphics objects.
 */
public final class GrobVisualizer implements Visualizer {

    public static final GrobVisualizer INSTANCE = new GrobVisualizer();

    private GrobVisualizer() {
    }

    public boolean accepts(Iterable<?> objects, Class listClass) {
        return Grob.class.isAssignableFrom(listClass);
    }

    public Rectangle2D getBounds(Iterable<?> objects) {
        Rectangle2D.Double bounds = new Rectangle2D.Double();
        for (Object o : objects) {
            if (o instanceof Grob) {
                Grob grob = (Grob) o;
                bounds.add(grob.getBounds().getRectangle2D());
            } else if (o instanceof Iterable) {
                bounds.add(getBounds((Iterable<?>) o));
            }
        }
        // Give a bit of extra padding. This is used so objects with zero-width bounds (e.g. a vertical line)
        // actually display.
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
        // TODO Find out why we can't use Float.MAX_VALUE here.
        Canvas c = new Canvas(100000000, 100000000);
        c.setBackground(null);
        for (Object o : objects) {
            if (o instanceof Grob) {
                c.add((Grob) o);
            } else if (o instanceof Iterable) {
                c.add(visualize((Iterable) o));
            }
        }
        return c;
    }

}
