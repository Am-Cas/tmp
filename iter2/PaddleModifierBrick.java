package ogp.bricks;

import ogp.BreakoutState;
import ogp.balls.Ball;
import ogp.math.Point;
import ogp.math.Rectangle;
import ogp.paddles.Paddle;

/**
 * Superclass for all bricks that modify the paddle (e.g., make it wider) when the brick is hit.
 * These bricks are also single-life, i.e., they disappear after one hit.
 */
public abstract class PaddleModifierBrick extends LabeledBrick
{
    /**
     * @throws IllegalArgumentException | geometry == null
     * @throws IllegalArgumentException | gridPosition == null
     * @post | getGeometry().equals(geometry)
     * @post | getGridPosition().equals(gridPosition)
     */
    public PaddleModifierBrick(Rectangle geometry, Point gridPosition)
    {
        super(geometry, gridPosition);
    }

    /**
     * Removes the brick and calls modifyPaddle on the paddle.
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
        modifyPaddle(state.getPaddle());
    }

    /**
     * Called when a ball hits this brick.
     * Subclasses can override this method to specify what should happen with the paddle.
     *
     * @pre | paddle != null
     * @mutates | paddle
     */
    public abstract void modifyPaddle(Paddle paddle);
}
