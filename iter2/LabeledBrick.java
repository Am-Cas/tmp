package ogp.bricks;

import java.awt.Color;

import ogp.math.Point;
import ogp.math.Rectangle;
import ogp.ui.Canvas;
import ogp.util.MPOOPLegitGenerated;

/**
 * Convenience class for helping implement Brick classes that are
 * visually represented by a rectangle surrounding a label.
 *
 * This class provides a template to simplify defining such classes.
 */
public abstract class LabeledBrick extends Brick
{
    /**
     * @throws IllegalArgumentException | geometry == null
     * @throws IllegalArgumentException | gridPosition == null
     * @post | getGeometry().equals(geometry)
     * @post | getGridPosition().equals(gridPosition)
     */
    public LabeledBrick(Rectangle geometry, Point gridPosition)
    {
        super(geometry, gridPosition);
    }

    /**
     * Draws a rectangle with a label inside of it.
     * The label is determined by the getLabel method.
     * The label's color is determined by the getLabelColor method.
     *
     * LEGIT
     *
     * @pre | canvas != null
     * @mutates | canvas
     */
    @MPOOPLegitGenerated
    @Override
    public void paint(Canvas canvas)
    {
        super.paint(canvas);
        canvas.drawLabel(getLabelColor(), getLabel(), getGeometry().getCenter());
    }

    /**
     * Returns the color used to paint the label.
     * By default, returns the same color as the brick itself.
     *
     * @post | result != null
     */
    public Color getLabelColor()
    {
        return getColor();
    }

    /**
     * Returns the label string displayed on the brick when rendered.
     *
     * @post | result != null
     */
    public abstract String getLabel();
}
