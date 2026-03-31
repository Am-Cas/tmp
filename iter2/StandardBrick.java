package ogp.bricks;

import java.awt.Color;

import ogp.BreakoutState;
import ogp.balls.Ball;
import ogp.math.Point;
import ogp.math.Rectangle;

/**
 * Represents bricks that disappear after a single hit and have no kind of effect other than that,
 * e.g., they don't change the paddle or ball in any way.
 *
 * @immutable
 */
public class StandardBrick extends Brick
{
    public static final Color COLOR = new Color(200, 200, 200);

    /**
     * @throws IllegalArgumentException | geometry == null
     * @throws IllegalArgumentException | gridPosition == null
     * @post | getGeometry().equals(geometry)
     * @post | getGridPosition().equals(gridPosition)
     */
    public StandardBrick(Rectangle geometry, Point gridPosition)
    {
        super(geometry, gridPosition);
    }

    /**
     * @post | result != null
     * @post | result.equals(StandardBrick.COLOR)
     */
    @Override
    public Color getColor()
    {
        return StandardBrick.COLOR;
    }

    /**
     * Called when a ball hits this brick. Removes the brick from the state.
     *
     * @pre | state != null
     * @pre | ball != null
     * @mutates | state
     * @post | !state.getBricks().contains(this)
     */
    @Override
    public void hit(BreakoutState state, Ball ball)
    {
        state.getBrickGrid().removeBrick(this);
    }
}
