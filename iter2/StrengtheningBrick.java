package ogp.bricks;

import java.awt.Color;

import ogp.balls.Ball;
import ogp.balls.StrongBallBehavior;
import ogp.math.Point;
import ogp.math.Rectangle;

/**
 * Represents a brick that disappears after a single hit
 * and causes the ball to be temporarily strengthened.
 *
 * @immutable
 */
public class StrengtheningBrick extends BallModifierBrick
{
    public static final Color COLOR = ColorSet.STRENGTHENING_BALL_BRICK;

    public static final String LABEL = "F";

    /**
     * @throws IllegalArgumentException | geometry == null
     * @throws IllegalArgumentException | gridPosition == null
     * @post | getGeometry().equals(geometry)
     * @post | getGridPosition().equals(gridPosition)
     */
    public StrengtheningBrick(Rectangle geometry, Point gridPosition)
    {
        super(geometry, gridPosition);
    }

    /**
     * @post | result != null
     * @post | result.equals(StrengtheningBrick.COLOR)
     */
    @Override
    public Color getColor()
    {
        return StrengtheningBrick.COLOR;
    }

    /**
     * Sets the ball's behavior to a new StrongBallBehavior.
     *
     * @pre | ball != null
     * @mutates_properties | ball.getBehavior()
     * @post | ball.getBehavior() instanceof StrongBallBehavior
     */
    @Override
    public void modifyBall(Ball ball)
    {
        ball.setBehavior(new StrongBallBehavior());
    }

    /**
     * @post | result != null
     * @post | result.equals(StrengtheningBrick.LABEL)
     */
    @Override
    public String getLabel()
    {
        return StrengtheningBrick.LABEL;
    }
}
