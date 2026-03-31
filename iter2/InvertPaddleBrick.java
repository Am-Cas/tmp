package ogp.bricks;

import java.awt.Color;

import ogp.math.Point;
import ogp.math.Rectangle;
import ogp.paddles.Paddle;

/**
 * Represents a brick that disappears on hit and inverts the paddle controls for 2500ms.
 *
 * @immutable
 */
public class InvertPaddleBrick extends PaddleModifierBrick
{
    public static final Color COLOR = ColorSet.INVERT_PADDLE_BRICK;

    public static final String LABEL = "I";

    /**
     * @throws IllegalArgumentException | geometry == null
     * @throws IllegalArgumentException | gridPosition == null
     * @post | getGeometry().equals(geometry)
     * @post | getGridPosition().equals(gridPosition)
     */
    public InvertPaddleBrick(Rectangle geometry, Point gridPosition)
    {
        super(geometry, gridPosition);
    }

    /**
     * @post | result != null
     * @post | result.equals(InvertPaddleBrick.COLOR)
     */
    @Override
    public Color getColor()
    {
        return InvertPaddleBrick.COLOR;
    }

    /**
     * Applies inverted controls to the paddle.
     *
     * @pre | paddle != null
     * @mutates | paddle
     */
    @Override
    public void modifyPaddle(Paddle paddle)
    {
        paddle.applyInverted();
    }

    /**
     * @post | result != null
     * @post | result.equals(InvertPaddleBrick.LABEL)
     */
    @Override
    public String getLabel()
    {
        return InvertPaddleBrick.LABEL;
    }
}
