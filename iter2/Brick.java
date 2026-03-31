package ogp.bricks;

import java.awt.Color;

import ogp.BreakoutState;
import ogp.balls.Ball;
import ogp.math.Point;
import ogp.math.Rectangle;
import ogp.ui.Canvas;
import ogp.util.MPOOPLegitGenerated;

/**
 * Superclass for all bricks.
 *
 * All bricks have two properties:
 * - a rectangle (geometry), representing their position in the game world.
 * - a grid position, representing the position in the block grid
 *
 * @invar | getGeometry() != null
 * @invar | getGridPosition() != null
 */
public abstract class Brick
{
    /**
     * @representationObject
     * @invar | geometry != null
     */
    private final Rectangle geometry;

    /**
     * @invar | gridPosition != null
     */
    private final Point gridPosition;

    /**
     * Constructor.
     *
     * @throws IllegalArgumentException | geometry == null
     * @throws IllegalArgumentException | gridPosition == null
     * @post | getGeometry().equals(geometry)
     * @post | getGridPosition().equals(gridPosition)
     */
    public Brick(Rectangle geometry, Point gridPosition)
    {
        if (geometry == null)
        {
            throw new IllegalArgumentException();
        }

        if (gridPosition == null)
        {
            throw new IllegalArgumentException();
        }

        this.geometry = geometry;
        this.gridPosition = gridPosition;
    }

    /**
     * Returns the grid position of this brick.
     *
     * @post | result != null
     */
    public Point getGridPosition()
    {
        return this.gridPosition;
    }

    /**
     * Returns the rectangle occupied by this brick in the game world.
     *
     * @post | result != null
     */
    public Rectangle getGeometry()
    {
        return this.geometry;
    }

    /**
     * Returns whether this brick is indestructible.
     *
     * @post | result == false || result == true
     */
    public boolean isIndestructible()
    {
        return false; // default: false
    }

    /**
     * Paints the brick using the canvas.
     *
     * LEGIT
     *
     * @pre | canvas != null
     * @mutates | canvas
     */
    @MPOOPLegitGenerated
    public void paint(Canvas canvas)
    {
        canvas.drawRectangle(getColor(), getGeometry());
    }

    /**
     * Used in the paint method to determine the color of the rectangle on screen.
     * Can be overridden in subclasses to give each brick their own color.
     *
     * @post | result != null
     */
    public Color getColor()
    {
        return Color.WHITE;
    }

    /**
     * Called when this brick has been hit by a ball.
     * It is given the full BreakoutState and the Ball which has hit the brick.
     * This method should update the state and/or ball,
     * e.g., remove the brick from the state, change the paddle's size, etc.
     *
     * @pre | state != null
     * @pre | ball != null
     * @pre | state.getBalls().contains(ball)
     * @mutates | this
     * @mutates | state
     * @mutates | ball
     */
    public abstract void hit(BreakoutState state, Ball ball);

    /**
     * Called when this brick has been hit by a strong ball.
     * Like the hit method, it should update the state/ball to reflect what
     * happens when this brick is hit by a strong ball.
     *
     * Returns true if brick survives, false otherwise.
     *
     * @pre | state != null
     * @pre | ball != null
     * @post | result == true || result == false
     */
    public boolean strongHit(BreakoutState state, Ball ball)
    {
        hit(state, ball);
        return false;
    }
}
