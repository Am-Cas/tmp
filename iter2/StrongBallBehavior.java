package ogp.balls;

import java.awt.Color;

import ogp.BreakoutState;
import ogp.BrickCollision;
import ogp.Collision;

/**
 * Strong ball behavior: when hitting a wall, the player loses 1 life.
 * When hitting a brick that is destroyed, the ball continues without velocity reflection
 * and scales up. When hitting a brick that survives, velocity is reflected normally.
 * The behavior lasts 5 seconds before reverting to standard.
 *
 * @invar | getTimeLeft() >= 0
 */
public class StrongBallBehavior extends TemporaryBehavior
{
    /**
     * Color of balls with strong behavior.
     */
    public static final Color COLOR = Color.yellow;

    /**
     * How long strong behavior stays active in milliseconds.
     */
    public static final int DURATION = 5000;

    /**
     * Constructor.
     *
     * @post | getTimeLeft() == StrongBallBehavior.DURATION
     */
    public StrongBallBehavior()
    {
        super(DURATION);
    }

    /**
     * Moves the ball to the point of impact.
     * Calls {@link ogp.bricks.Brick#strongHit} on the brick.
     * If the brick survives, velocity is reflected.
     * If the brick is destroyed, velocity is NOT reflected and the ball scales up.
     *
     * @pre | state != null
     * @pre | ball != null
     * @pre | collision != null
     * @mutates | ball
     * @mutates | state
     */
    @Override
    public void bounceOffBrick(BreakoutState state, Ball ball, BrickCollision collision)
    {
        ball.move(collision.getMillisecondsUntilCollision());
        boolean survived = collision.getBrick().strongHit(state, ball);
        if (survived)
        {
            // Brick survived: reflect velocity normally
            var newVelocity = ball.getVelocity().kiloBounce(collision.getKiloNormal());
            ball.setVelocity(newVelocity);
        }
        else
        {
            // Brick destroyed: no reflection, scale up ball
            ball.scaleUp();
        }
    }

    /**
     * Moves the ball to the wall, loses 1 player life, then reflects velocity.
     *
     * @pre | state != null
     * @pre | ball != null
     * @pre | collision != null
     * @mutates | ball
     * @mutates | state
     */
    @Override
    public void bounceOffWall(BreakoutState state, Ball ball, Collision collision)
    {
        ball.move(collision.getMillisecondsUntilCollision());
        state.lose1Life();
        var newVelocity = ball.getVelocity().kiloBounce(collision.getKiloNormal());
        ball.setVelocity(newVelocity);
    }

    /**
     * @post | result != null
     * @post | result.equals(StrongBallBehavior.COLOR)
     */
    @Override
    public Color getColor()
    {
        return StrongBallBehavior.COLOR;
    }
}
