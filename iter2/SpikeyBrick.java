package ogp.bricks;

import java.awt.Color;

import ogp.BreakoutState;
import ogp.balls.Ball;
import ogp.math.Point;
import ogp.math.Rectangle;

/**
 * Represents an indestructible spikey brick.
 * When hit, the player loses 1 hp. The brick is never removed.
 *
 * @immutable
 */
public class SpikeyBrick extends LabeledBrick
{
    public static final Color COLOR = ColorSet.SPIKEY_BRICK;

    public static final String LABEL = "S";

    /**
     * @throws IllegalArgumentException | geometry == null
     * @throws IllegalArgumentException | gridPosition == null
     * @post | getGeometry().equals(geometry)
     * @post | getGridPosition().equals(gridPosition)
     */
    public SpikeyBrick(Rectangle geometry, Point gridPosition)
    {
        super(geometry, gridPosition);
    }

    /**
     * @post | result == true
     */
    @Override
    public boolean isIndestructible()
    {
        return true;
    }

    /**
     * @post | result != null
     * @post | result.equals(SpikeyBrick.COLOR)
     */
    @Override
    public Color getColor()
    {
        return COLOR;
    }

    /**
     * Player loses 1 hp. Brick is NOT removed (it is indestructible).
     *
     * @pre | state != null
     * @pre | ball != null
     * @mutates | state
     */
    @Override
    public void hit(BreakoutState state, Ball ball)
    {
        state.lose1Life();
    }

    /**
     * Strong ball hits a spikey brick: player loses 1 hp, brick survives.
     *
     * @pre | state != null
     * @pre | ball != null
     * @post | result == true
     * @mutates | state
     */
    @Override
    public boolean strongHit(BreakoutState state, Ball ball)
    {
        state.lose1Life();
        return true; // survives (indestructible)
    }

    /**
     * @post | result != null
     * @post | result.equals(SpikeyBrick.LABEL)
     */
    @Override
    public String getLabel()
    {
        return SpikeyBrick.LABEL;
    }
}
