package ogp.bricks;

import ogp.BreakoutState;
import ogp.balls.Ball;
import ogp.math.Point;
import ogp.math.Rectangle;

/**
 * Superclass for all bricks that modify the ball (e.g., make it go faster) when the brick is hit.
 * These bricks are also single-life, i.e., they disappear after one hit.
 */
public abstract class BallModifierBrick extends LabeledBrick
{
    /**
     * @throws IllegalArgumentException | geometry == null
     * @throws IllegalArgumentException | gridPosition == null
     */
    public BallModifierBrick(Rectangle geometry, Point gridPosition)
    {
        super(geometry, gridPosition);
    }

    /**
     * Removes the brick from the game state and calls the modifyBall method.
     *
     * @pre | state != null
     * @pre | ball != null
     * @mutates | state
     * @mutates | ball
     * @post | !state.getBricks().contains(this)
     */
    @Override
    public void hit(BreakoutState state, Ball ball)
    {
        state.getBrickGrid().removeBrick(this);
        modifyBall(ball);
    }

    /**
     * Called when a ball hits this brick.
     * Subclasses can override this method to specify what should happen with the ball.
     *
     * @pre | ball != null
     * @mutates | ball
     */
    public abstract void modifyBall(Ball ball);
}
