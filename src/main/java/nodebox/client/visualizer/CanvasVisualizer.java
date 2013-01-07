package nodebox.client.visualizer;

import com.google.common.collect.Iterables;
import nodebox.graphics.Canvas;
import nodebox.graphics.Grob;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class CanvasVisualizer implements Visualizer {

    public static final CanvasVisualizer INSTANCE = new CanvasVisualizer();

    private CanvasVisualizer() {
    }

    public boolean accepts(Iterable<?> objects, Class listClass) {
        return nodebox.graphics.Canvas.class.isAssignableFrom(listClass);
    }

    public Rectangle2D getBounds(Iterable<?> objects) {
        Rectangle2D r = new Rectangle2D.Double();
        for (Object o : objects) {
            if (o instanceof Grob) {
                r.add(((Grob) o).getBounds().getRectangle2D());
            }
        }
        return r;
    }

    public Point2D getOffset(Iterable<?> objects, Dimension2D size) {
        return new Point2D.Double(size.getWidth() / 2, size.getHeight() / 2);
    }

    @Override
    public Grob visualize(Iterable<?> objects) {
        return getFirstCanvas(objects);
    }

    private Canvas getFirstCanvas(Iterable<?> objects) {
        Canvas c = (Canvas) Iterables.getFirst(objects, null);
        return c != null ? c : new Canvas();
    }

}
