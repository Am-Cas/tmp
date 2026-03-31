package ogp.bricks;

import java.awt.Color;

import ogp.math.Point;
import ogp.math.Rectangle;
import ogp.paddles.Paddle;

/**
 * This class represents bricks that disappear after a single hit
 * and cause the paddle to shrink.
 *
 * @immutable
 */
public class ShrinkPaddleBrick extends PaddleModifierBrick
{
    public static final Color COLOR = ColorSet.SHRINK_PADDLE_BRICK;

    public static final String LABEL = "-";

    /**
     * @throws IllegalArgumentException | geometry == null
     * @throws IllegalArgumentException | gridPosition == null
     * @post | getGeometry().equals(geometry)
     * @post | getGridPosition().equals(gridPosition)
     */
    public ShrinkPaddleBrick(Rectangle geometry, Point gridPosition)
    {
        super(geometry, gridPosition);
    }

    /**
     * @post | result != null
     * @post | result.equals(ShrinkPaddleBrick.COLOR)
     */
    @Override
    public Color getColor()
    {
        return ShrinkPaddleBrick.COLOR;
    }

    /**
     * Shrinks the paddle.
     *
     * @pre | paddle != null
     * @mutates | paddle
     */
    @Override
    public void modifyPaddle(Paddle paddle)
    {
        paddle.shrink();
    }

    /**
     * @post | result != null
     * @post | result.equals(ShrinkPaddleBrick.LABEL)
     */
    @Override
    public String getLabel()
    {
        return ShrinkPaddleBrick.LABEL;
    }
}
