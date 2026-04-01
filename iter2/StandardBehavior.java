package ogp.balls;

import java.awt.Color;

/**
 * Implements the standard (default) behavior of a ball.
 * A standard ball bounces off walls, the paddle and bricks normally.
 * It is painted white.
 */
public class StandardBehavior extends BallBehavior
{
    /** The color of a ball with standard behavior. */
    public final static Color COLOR = Color.WHITE;

    /**
     * Returns the color of the standard ball (white).
     *
     * @post | result != null
     * @post | result.equals(StandardBehavior.COLOR)
     */
    @Override
    public Color getColor()
    {
        return COLOR;
    }
}
