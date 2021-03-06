package nodebox.client.visualizer;

import com.google.common.collect.Iterables;
import nodebox.graphics.Grob;

import java.awt.*;
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

    @SuppressWarnings("unchecked")
    public void draw(Graphics2D g, Iterable<?> objects) {
        Object firstObject = Iterables.getFirst(objects, null);
        if (firstObject instanceof Grob)
            drawGrobs(g, (Iterable<Grob>) objects);
        else if (firstObject instanceof Iterable) {
            for (Object o : objects)
                draw(g, (Iterable<?>) o);
        }
    }

    public static void drawGrobs(Graphics2D g, Iterable<Grob> objects) {
        for (Grob grob : objects) {
            Shape oldClip = g.getClip();
            grob.draw(g);
            g.setClip(oldClip);
        }
    }

}
