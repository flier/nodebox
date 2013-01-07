package nodebox.client.visualizer;

import com.google.common.base.Joiner;
import nodebox.graphics.Grob;
import nodebox.graphics.Text;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * The objects visualizer can visualize everything.
 */
public final class LastResortVisualizer implements Visualizer {

    public static final LastResortVisualizer INSTANCE = new LastResortVisualizer();
    private static final Rectangle2D BIG_RECTANGLE = new Rectangle2D.Double(0, 0, Double.MAX_VALUE, Double.MAX_VALUE);
    private static final Point2D ZERO_POINT = new Point2D.Double(0, 0);

    private LastResortVisualizer() {
    }

    public boolean accepts(Iterable<?> objects, Class listClass) {
        return true;
    }

    public Rectangle2D getBounds(Iterable<?> objects) {
        return BIG_RECTANGLE;
    }

    public Point2D getOffset(Iterable<?> objects, Dimension2D size) {
        return ZERO_POINT;
    }

    @Override
    public Grob visualize(Iterable<?> objects) {
        String allStrings = Joiner.on("\n").join(objects);
        Text t = new Text(allStrings, 5, 15);
        t.setFontSize(11);
        t.setFontName(Font.MONOSPACED);
        t.setAlign(Text.Align.LEFT);
        t.setLineHeight(1.5);
        return t;
    }

}
