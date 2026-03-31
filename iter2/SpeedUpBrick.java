package ogp.bricks;

import java.awt.Color;

import ogp.balls.Ball;
import ogp.math.Point;
import ogp.math.Rectangle;

/**
 * Bricks of this type disappear after a single hit,
 * cause the ball to speed up once and scale down once.
 *
 * @immutable
 */
public class SpeedUpBrick extends BallModifierBrick
{
    public static final Color COLOR = ColorSet.SPEEDUP_BALL_BRICK;

    public static final String LABEL = ">";

    /**
     * @throws IllegalArgumentException | geometry == null
     * @throws IllegalArgumentException | gridPosition == null
     * @post | getGeometry().equals(geometry)
     * @post | getGridPosition().equals(gridPosition)
     */
    public SpeedUpBrick(Rectangle geometry, Point gridPosition)
    {
        super(geometry, gridPosition);
    }

    /**
     * @post | result != null
     * @post | result.equals(SpeedUpBrick.COLOR)
     */
    @Override
    public Color getColor()
    {
        return SpeedUpBrick.COLOR;
    }

    /**
     * Speeds up the ball and scales it down.
     *
     * @pre | ball != null
     * @mutates | ball
     */
    @Override
    public void modifyBall(Ball ball)
    {
        ball.speedUp();
        ball.scaleDown();
    }

    /**
     * @post | result != null
     * @post | result.equals(SpeedUpBrick.LABEL)
     */
    @Override
    public String getLabel()
    {
        return SpeedUpBrick.LABEL;
    }
}
