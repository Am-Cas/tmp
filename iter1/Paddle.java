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
 * @invar | getAllowedInterval().isInside(getTopCenter().x() - getHalfWidth())
 * @invar | getAllowedInterval().isInside(getTopCenter().x() + getHalfWidth())
 * @invar | getHalfWidth() > 0
 * @invar | getMotionDirection() != null
 * @invar | getSpeed() > 0
 */
public class Paddle
{
    public static final int HEIGHT = 1000;

    public static final int GROW_FACTOR = 1100;

    public static final int SHRINK_FACTOR = 900;

    /**
     * @representationObject
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
     * @representationObject
     * @invar | allowedInterval != null
     */
    private final Interval allowedInterval;
    
    /**
     * Construct a paddle located around a given center in the field.
     * Note that we specify its half size instead of its full size.
     * This is to avoid the rounding errors that would occur if its size were odd.
     *
     * @pre | allowedInterval != null
     * @pre | topCenter != null
     * @pre | halfWidth > 0
     * @pre | speed > 0
     * @pre | allowedInterval.isInside(topCenter.x() - halfWidth)
     * @pre | allowedInterval.isInside(topCenter.x() + halfWidth)
     * @post | getTopCenter().equals(topCenter)
     * @post | getHalfWidth() == halfWidth
     * @post | getSpeed() == speed
     * @post | getAllowedInterval().equals(allowedInterval)
     * @post | !isInverted()
     * @throws IllegalArgumentException | allowedInterval == null
     * @throws IllegalArgumentException | topCenter == null
     * @throws IllegalArgumentException | halfWidth <= 0
     * @throws IllegalArgumentException | speed <= 0
     * @throws IllegalArgumentException | !allowedInterval.isInside(topCenter.x() - halfWidth)
     * @throws IllegalArgumentException | !allowedInterval.isInside(topCenter.x() + halfWidth)
     */
    public Paddle(Interval allowedInterval, Point topCenter, long halfWidth, long speed)
    {
        if (allowedInterval == null) {
            throw new IllegalArgumentException("allowedInterval cannot be null");
        }
        if (topCenter == null) {
            throw new IllegalArgumentException("topCenter cannot be null");
        }
        if (halfWidth <= 0) {
            throw new IllegalArgumentException("halfWidth must be positive");
        }
        if (speed <= 0) {
            throw new IllegalArgumentException("speed must be positive");
        }
        if (!allowedInterval.isInside(topCenter.x() - halfWidth)) {
            throw new IllegalArgumentException("paddle left edge must be inside allowed interval");
        }
        if (!allowedInterval.isInside(topCenter.x() + halfWidth)) {
            throw new IllegalArgumentException("paddle right edge must be inside allowed interval");
        }

        this.topCenter = topCenter;
        this.halfWidth = halfWidth;
        this.motionDirection = PaddleMotionDirection.STATIONARY;
        this.speed = speed;
        this.allowedInterval = allowedInterval;
        this.invertedFuel = 0;
    }
    
    /**
     * Checks if the paddle is inverted.
     * 
     * @post | result == (invertedFuel > 0)
     */
    public boolean isInverted() {
    	return invertedFuel > 0;
    }

    /**
     * Returns the paddle's motion direction.
     * 
     * @post | result != null
     */
    public PaddleMotionDirection getMotionDirection()
    {
        return motionDirection;
    }

    /**
     * Sets the paddle's motion direction.
     * 
     * @pre | direction != null
     * @post | getMotionDirection() == direction
     * @mutates_properties | getMotionDirection()
     * @throws IllegalArgumentException | direction == null
     */
    public void setMotionDirection(PaddleMotionDirection direction)
    {
        if (direction == null) {
            throw new IllegalArgumentException("direction cannot be null");
        }
        this.motionDirection = direction;
    }

    /**
     * Return the center point of this paddle.
     *
     * @creates | result
     * @post | result != null
     */
    public Point getTopCenter()
    {
        return topCenter.copy();
    }

    /**
     * Returns the allowed interval.
     * 
     * @creates | result
     * @post | result != null
     */
    public Interval getAllowedInterval()
    {
        return allowedInterval.copy();
    }

    /**
     * @post | result > 0
     */
    public long getHalfWidth()
    {
        return this.halfWidth;
    }

    /**
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
     * @post | result.getHeight() == getHeight()
     */
    public Rectangle getGeometry()
    {
    	return new Rectangle(
    		topCenter.x() - halfWidth,
    		topCenter.y(),
    		halfWidth * 2,
    		HEIGHT
    	);
    }

    /**
     * Moves the paddle so that its top center x coordinate equals the given x.
     * Ensures that the paddle does not go outside the allowed area.
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
     * Moves this Paddle a certain distance.
     * Ensures that the paddle remains within the allowed area.
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
     * Computes the new state of the paddle in elapsedMilliseconds, i.e.,
     * moves the paddle a certain distance, determined by its motion direction
     * and speed.

     * @pre | state != null
     * @pre | elapsedMilliseconds >= 0
     * @mutates_properties | getTopCenter()
     * @mutates | state
     */
    public void tick(BreakoutState state, long elapsedMilliseconds)
    {
        var distance = computeMovementDistance(elapsedMilliseconds);
        move(distance);
        
        if (invertedFuel > 0) {
            invertedFuel -= elapsedMilliseconds;
            if (invertedFuel < 0) {
                invertedFuel = 0;
            }
        }
    }

    /**
     * Computes how much distance the paddle travels in the given time.
     * It is dependent on the paddle's motion direction and speed.
     *
     * @pre | elapsedMilliseconds >= 0
     */
    public long computeMovementDistance(long elapsedMilliseconds)
    {
    	int invertedFac = invertedFuel > 0? -1 : 1;
        return elapsedMilliseconds * this.speed * this.motionDirection.getFactor() * invertedFac;
    }

    /**
     * Computes a "corrected" x-coordinate for the paddle's top center.
     * Say we want the paddle's top center to move to position x,
     * this method checks whether this move would be allowed, i.e.,
     * if the paddle will still fit inside its allowed range.
     * If not, this method returns a fixed x so that this is the case.
     *
     * LEGIT
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
        if ( x - halfWidth < allowedInterval.getLowerBound() )
        {
            return allowedInterval.getLowerBound() + halfWidth;
        }

        if ( x + halfWidth > allowedInterval.getUpperBound() )
        {
            return allowedInterval.getUpperBound() - halfWidth;
        }

        return x;
    }

    /**
     * Returns a corrected version of the position so that
     * the paddle fits within the allowed range.
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
     * Private twin of clamp.
     * Its purpose is to be callable while the Paddle object is in state violating its invariant.
     */
    private Point clampPrivate(Point position)
    {
        var x = clampPrivate(position.x());
        var y = position.y();

        return new Point(x, y);
    }

    /**
     * Finds the collision between this paddle and the given ball.
     * Can return null if no such collision occurs.
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

        if ( ballVelocity.y() > 0 && ballPosition.y() < getTopCenter().y() )
        {
            var t = (getTopCenter().y() - ballPosition.y()) / ballVelocity.y();
            var x = ballPosition.x() + t * ballVelocity.x();

            if ( topCenter.x() - halfWidth <= x && x <= topCenter.x() + halfWidth )
            {
                return new Collision(t, getKiloNormal(x));
            }
        }

        return null;
    }

    /**
     * Paints the paddle onto the canvas.
     *
     *
     * @pre | canvas != null
     * @mutates | canvas
     */
    public void paint(Canvas canvas)
    {
    	Color col = (invertedFuel > 0? Color.RED: Color.WHITE);
        canvas.drawFilledRectangle(col, getGeometry());
    }

    /**
     * Changes the paddle's size.
     * The parameter kilofactor is equal to the actual scale factor times 1000,
     * so as to allow more fine grained scaling.
     *
     * If necessary, the paddle's position is updated to ensure that the paddle still lies
     * within its allowed range.
     *
     * The paddle's size cannot exceed the allowed interval's width.
     * If the scaling factor is too high, the paddle's size is set to its maximally allowed value.
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
        if ( halfWidth * 2 > this.allowedInterval.getWidth() )
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
     * Applies the inverted effect to the paddle for 2500ms.
     * 
     * @mutates_properties | this
     */
    public void applyInverted() {
    	invertedFuel = 2500;
    }

    /**
     * Returns normal vector on paddle at position x.
     * Though the paddle is drawn as a rectangle, you have to imagine
     * it has a curved top.
     *
     * LEGIT
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
