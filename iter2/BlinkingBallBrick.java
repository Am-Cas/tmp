package ogp.bricks;

import java.awt.Color;

import ogp.balls.Ball;
import ogp.balls.BlinkingBallBehavior;
import ogp.math.Point;
import ogp.math.Rectangle;

/**
 * Represents a brick that disappears after a single hit
 * and causes the ball to be temporarily "blinking".
 *
 * @immutable
 */
public class BlinkingBallBrick extends BallModifierBrick
{
    public static final Color COLOR = ColorSet.BLINKING_BALL_BRICK;

    public static final String LABEL = "B";

    /**
     * @throws IllegalArgumentException | geometry == null
     * @throws IllegalArgumentException | gridPosition == null
     * @post | getGeometry().equals(geometry)
     * @post | getGridPosition().equals(gridPosition)
     */
    public BlinkingBallBrick(Rectangle geometry, Point gridPosition)
    {
        super(geometry, gridPosition);
    }

    /**
     * @post | result != null
     * @post | result.equals(BlinkingBallBrick.COLOR)
     */
    @Override
    public Color getColor()
    {
        return BlinkingBallBrick.COLOR;
    }

    /**
     * Sets the ball's behavior to a new BlinkingBallBehavior.
     *
     * @pre | ball != null
     * @mutates_properties | ball.getBehavior()
     * @post | ball.getBehavior() instanceof BlinkingBallBehavior
     */
    @Override
    public void modifyBall(Ball ball)
    {
        ball.setBehavior(new BlinkingBallBehavior());
    }

    /**
     * @post | result != null
     * @post | result.equals(BlinkingBallBrick.LABEL)
     */
    @Override
    public String getLabel()
    {
        return BlinkingBallBrick.LABEL;
    }
}
