package ogp.paddles;

import java.awt.Color;

import ogp.BreakoutState;
import ogp.Collision;
import ogp.balls.Ball;
import ogp.math.Interval;
import ogp.math.Point;
import ogp.math.Rectangle;
import ogp.math.Vector;
import ogp.ui.Canvas;
import ogp.util.MPOOPLegitGenerated;

/**
 * Represents the paddle in the breakout game.
 *
 * @invar | getTopCenter() != null
 * @invar | getHalfWidth() > 0
 * @invar | getSpeed() > 0
 * @invar | getAllowedInterval() != null
 * @invar | getMotionDirection() != null
 * @invar | getAllowedInterval().isInside(getTopCenter().x() - getHalfWidth())
 * @invar | getAllowedInterval().isInside(getTopCenter().x() + getHalfWidth())
 */
public class Paddle
{
    public static final int HEIGHT = 1000;

    public static final int GROW_FACTOR = 1100;

    public static final int SHRINK_FACTOR = 900;

    /**
     * @invar | topCenter != null
     */
    private Point topCenter;

    /**
     * @invar | halfWidth > 0
     */
    private long halfWidth;

    /**
     * @invar | motionDirection != null
     */
    private PaddleMotionDirection motionDirection;

    /**
     * Remaining milliseconds the paddle controls are inverted.
     *
     * @invar | invertedFuel >= 0
     */
    private int invertedFuel;

    /**
     * Speed at which paddle moves.
     * Whether the paddle actually moves is determined by motionDirection,
     * but if the paddle moves, it is at this speed.
     *
     * @invar | speed > 0
     */
    private final long speed;

    /**
     * The entire width of the paddle must fit inside this interval.
     *
     * @invar | allowedInterval != null
     * @invar | allowedInterval.isInside(topCenter.x() - halfWidth)
     * @invar | allowedInterval.isInside(topCenter.x() + halfWidth)
     */
    private final Interval allowedInterval;

    /**
     * Constructs a paddle located around a given center in the field.
     * Note that we specify its half size instead of its full size.
     * This is to avoid rounding errors that would occur if its size were odd.
     *
     * @throws IllegalArgumentException | allowedInterval == null
     * @throws IllegalArgumentException | topCenter == null
     * @throws IllegalArgumentException | halfWidth <= 0
     * @throws IllegalArgumentException | speed <= 0
     * @inspects | topCenter
     * @post | getTopCenter().equals(clamp(topCenter))
     * @post | getHalfWidth() == halfWidth
     * @post | getSpeed() == speed
     * @post | getAllowedInterval() == allowedInterval
     * @post | getMotionDirection() == PaddleMotionDirection.STATIONARY
     * @post | !isInverted()
     */
    public Paddle(Interval allowedInterval, Point topCenter, long halfWidth, long speed)
    {
        if (allowedInterval == null) throw new IllegalArgumentException();
        if (topCenter == null) throw new IllegalArgumentException();
        if (halfWidth <= 0) throw new IllegalArgumentException();
        if (speed <= 0) throw new IllegalArgumentException();

        this.topCenter = topCenter;
        this.halfWidth = halfWidth;
        this.motionDirection = PaddleMotionDirection.STATIONARY;
        this.speed = speed;
        this.allowedInterval = allowedInterval;
        this.invertedFuel = 0;
    }

    /**
     * Returns whether the paddle is currently inverted.
     *
     * @post | result == (invertedFuel > 0)
     */
    public boolean isInverted()
    {
        return invertedFuel > 0;
    }

    /**
     * Returns the current motion direction of the paddle.
     *
     * @post | result != null
     */
    public PaddleMotionDirection getMotionDirection()
    {
        return this.motionDirection;
    }

    /**
     * Sets the motion direction of the paddle.
     *
     * @pre | direction != null
     * @mutates_properties | getMotionDirection()
     * @post | getMotionDirection() == direction
     */
    public void setMotionDirection(PaddleMotionDirection direction)
    {
        this.motionDirection = direction;
    }

    /**
     * Returns the top-center point of this paddle.
     *
     * @post | result != null
     */
    public Point getTopCenter()
    {
        return topCenter;
    }

    /**
     * Returns the interval within which the paddle is allowed to move.
     *
     * @post | result != null
     */
    public Interval getAllowedInterval()
    {
        return this.allowedInterval;
    }

    /**
     * Returns half the width of the paddle.
     *
     * @post | result > 0
     */
    public long getHalfWidth()
    {
        return this.halfWidth;
    }

    /**
     * Returns the full width of the paddle.
     *
     * @post | result == 2 * getHalfWidth()
     */
    public long getWidth()
    {
        return this.halfWidth * 2;
    }

    /**
     * Returns the paddle's height.
     *
     * @post | result == Paddle.HEIGHT
     */
    public long getHeight()
    {
        return HEIGHT;
    }

    /**
     * Returns the paddle's speed.
     *
     * @post | result > 0
     */
    public long getSpeed()
    {
        return speed;
    }

    /**
     * Returns a rectangle representing the paddle's shape.
     *
     * @creates | result
     * @post | result != null
     * @post | result.getLeft() == getTopCenter().x() - getHalfWidth()
     * @post | result.getTop() == getTopCenter().y()
     * @post | result.getWidth() == getWidth()
     * @post | result.getHeight() == Paddle.HEIGHT
     */
    public Rectangle getGeometry()
    {
        var left = topCenter.x() - halfWidth;
        var top = topCenter.y();
        var width = halfWidth * 2;
        var height = HEIGHT;

        return new Rectangle(left, top, width, height);
    }

    /**
     * Moves the paddle so that its top-center x coordinate equals the given x,
     * clamped to fit within the allowed interval.
     *
     * @creates | getTopCenter()
     * @mutates_properties | getTopCenter()
     * @post | getTopCenter().equals(clamp(new Point(x, old(getTopCenter().y()))))
     */
    public void setTopCenterX(long x)
    {
        this.topCenter = clamp(new Point(x, topCenter.y()));
    }

    /**
     * Moves this paddle by the given distance, clamping to the allowed interval.
     *
     * @creates | getTopCenter()
     * @mutates_properties | getTopCenter()
     * @post | getTopCenter().equals(clamp(new Point(old(getTopCenter().x()) + distance, old(getTopCenter().y()))))
     */
    public void move(long distance)
    {
        setTopCenterX(topCenter.x() + distance);
    }

    /**
     * Computes the new state of the paddle after elapsedMilliseconds,
     * moving it according to its current motion direction and speed.
     * Also decrements invertedFuel if active.
     *
     * @pre | state != null
     * @pre | elapsedMilliseconds >= 0
     * @pre | state.getPaddle() == this
     * @mutates_properties | getTopCenter()
     * @post | getTopCenter().y() == old(getTopCenter().y())
     */
    public void tick(BreakoutState state, long elapsedMilliseconds)
    {
        var distance = computeMovementDistance(elapsedMilliseconds);
        this.move(distance);

        if (invertedFuel > 0)
        {
            invertedFuel = (int) Math.max(0L, (long) invertedFuel - elapsedMilliseconds);
        }
    }

    /**
     * Computes how much distance the paddle travels in the given time,
     * taking into account the motion direction (and inversion if active).
     *
     * @pre | elapsedMilliseconds >= 0
     * @post | result == elapsedMilliseconds * getSpeed() * getMotionDirection().getFactor() * (isInverted() ? -1 : 1)
     */
    public long computeMovementDistance(long elapsedMilliseconds)
    {
        int invertedFac = invertedFuel > 0 ? -1 : 1;
        return elapsedMilliseconds * this.speed * this.motionDirection.getFactor() * invertedFac;
    }

    /**
     * Returns a corrected x-coordinate for the paddle's top-center,
     * ensuring the paddle stays within its allowed range.
     *
     * LEGIT
     *
     * @post | result == (x - getHalfWidth() < getAllowedInterval().getLowerBound() ? getAllowedInterval().getLowerBound() + getHalfWidth() : (x + getHalfWidth() > getAllowedInterval().getUpperBound() ? getAllowedInterval().getUpperBound() - getHalfWidth() : x))
     */
    @MPOOPLegitGenerated
    public long clamp(long x)
    {
        return clampPrivate(x);
    }

    /**
     * Private twin of clamp.
     * Its purpose is to be callable while the Paddle object is in state violating its invariant.
     *
     * LEGIT
     */
    @MPOOPLegitGenerated
    private long clampPrivate(long x)
    {
        if (x - halfWidth < allowedInterval.getLowerBound())
        {
            return allowedInterval.getLowerBound() + halfWidth;
        }

        if (x + halfWidth > allowedInterval.getUpperBound())
        {
            return allowedInterval.getUpperBound() - halfWidth;
        }

        return x;
    }

    /**
     * Returns a corrected Point so that the paddle fits within the allowed range.
     *
     * @inspects | position
     * @pre | position != null
     * @creates | result
     * @post | result != null
     * @post | result.x() == clamp(position.x())
     * @post | result.y() == position.y()
     */
    public Point clamp(Point position)
    {
        return clampPrivate(position);
    }

    /**
     * Private twin of clamp(Point).
     */
    private Point clampPrivate(Point position)
    {
        var x = clampPrivate(position.x());
        var y = position.y();

        return new Point(x, y);
    }

    /**
     * Finds the collision between this paddle and the given ball.
     * Returns null if no collision occurs.
     *
     * LEGIT
     *
     * @pre | ball != null
     * @inspects | ball
     */
    @MPOOPLegitGenerated
    public Collision findCollision(Ball ball)
    {
        var ballVelocity = ball.getVelocity();
        var ballPosition = ball.getGeometry().getBottommostPoint();

        if (ballVelocity.y() > 0 && ballPosition.y() < getTopCenter().y())
        {
            var t = (getTopCenter().y() - ballPosition.y()) / ballVelocity.y();
            var x = ballPosition.x() + t * ballVelocity.x();

            if (topCenter.x() - halfWidth <= x && x <= topCenter.x() + halfWidth)
            {
                return new Collision(t, getKiloNormal(x));
            }
        }

        return null;
    }

    /**
     * Paints the paddle onto the canvas.
     * The paddle is red when inverted, white otherwise.
     *
     * LEGIT
     *
     * @pre | canvas != null
     * @mutates | canvas
     */
    @MPOOPLegitGenerated
    public void paint(Canvas canvas)
    {
        Color col = (invertedFuel > 0 ? Color.RED : Color.WHITE);
        canvas.drawFilledRectangle(col, getGeometry());
    }

    /**
     * Changes the paddle's size by the given kilofactor (actual factor × 1000).
     * The paddle's width cannot exceed the allowed interval's width.
     * The paddle's position is clamped after scaling.
     *
     * @pre | kilofactor > 0
     * @mutates_properties | getTopCenter(), getHalfWidth()
     * @post | getWidth() == Math.min(getAllowedInterval().getWidth(), old(getWidth()) * kilofactor / 1000)
     * @post | getTopCenter().equals(clamp(old(getTopCenter())))
     */
    public void scale(long kilofactor)
    {
        this.halfWidth = clampHalfWidth(this.halfWidth * kilofactor / 1000);
        this.topCenter = clampPrivate(this.topCenter);
    }

    private long clampHalfWidth(long halfWidth)
    {
        if (halfWidth * 2 > this.allowedInterval.getWidth())
        {
            return this.allowedInterval.getWidth() / 2;
        }
        else
        {
            return halfWidth;
        }
    }

    /**
     * Grows the paddle by a factor GROW_FACTOR.
     *
     * LEGIT
     */
    @MPOOPLegitGenerated
    public void grow()
    {
        scale(GROW_FACTOR);
    }

    /**
     * Shrinks the paddle by a factor SHRINK_FACTOR.
     *
     * LEGIT
     */
    @MPOOPLegitGenerated
    public void shrink()
    {
        scale(SHRINK_FACTOR);
    }

    /**
     * Applies inverted controls to the paddle for 2500 milliseconds.
     *
     * @mutates_properties | isInverted()
     * @post | isInverted()
     */
    public void applyInverted()
    {
        invertedFuel = 2500;
    }

    /**
     * Returns the kilo-normal vector on the paddle at the given x position.
     * The paddle is modelled as having a curved top for more interesting ball angles.
     *
     * LEGIT
     *
     * @pre | getTopCenter().x() - getHalfWidth() <= x && x <= getTopCenter().x() + getHalfWidth()
     * @post | result != null
     * @post | result.isKiloUnitVector()
     */
    @MPOOPLegitGenerated
    public Vector getKiloNormal(long x)
    {
        var relativePosition = (x - this.getTopCenter().x()) * 1000 / halfWidth;

        assert -1000 <= relativePosition;
        assert relativePosition <= 1000;

        return new Vector(relativePosition / 3, -1000).rescale(1000);
    }
}
