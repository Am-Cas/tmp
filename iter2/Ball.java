package ogp.balls;

import java.awt.Color;

import ogp.BreakoutState;
import ogp.math.Circle;
import ogp.math.Point;
import ogp.math.Rectangle;
import ogp.math.Vector;
import ogp.ui.Canvas;
import ogp.util.MPOOPLegitGenerated;

/**
 * This class represents a ball.
 * A ball has a geometry (represents the shape and position of the ball),
 * a velocity and a behavior.
 *
 * The behavior of a ball defines how it interacts with the game field.
 * See for example StandardBehavior, BlinkingBallBehavior and StrongBallBehavior.
 *
 * @invar | getGeometry() != null
 * @invar | getVelocity() != null
 * @invar | getBehavior() != null
 * @invar | getAllowedArea() != null
 * @invar | getAllowedArea().contains(getGeometry().getCenter())
 */
public class Ball
{
    /**
     * Determines by how much the speed goes up when the ball speeds up.
     * See {@link #speedUp()}.
     */
    public static final int SPEED_UP_FACTOR = 1050;

    /**
     * Determines by how much the speed goes down when the ball slows down.
     * See {@link #slowDown()}.
     */
    public static final int SLOW_DOWN_FACTOR = 950;

    /**
     * Slowdowns (using {@link #slowDown()}) are only applied if the new speed ends up higher than this value.
     * Note that it is allowed for a ball to have a lower speed, e.g., when using the constructor or {@link #setVelocity(Vector)}.
     */
    public static final int MINIMUM_SLOWDOWN_SQUARED_SPEED = 5 * 5;

    /**
     * Speedups are only applied if the new speed ends up lower than this value.
     * Note that it is allowed for a ball to have a higher speed, e.g., when using the constructor or {@link #setVelocity(Vector)}.
     */
    public static final int MAXIMUM_SPEEDUP_SQUARED_SPEED = 100 * 100;

    /**
     * Determines shape and position of the ball.
     *
     * @invar | geometry != null
     */
    private Circle geometry;

    /**
     * Expressed in distance per milliseconds.
     *
     * @invar | velocity != null
     */
    private Vector velocity;

    /**
     * Determines how the ball behaves.
     * See subtypes of BallBehavior.
     *
     * @invar | behavior != null
     */
    private BallBehavior behavior;

    /**
     * The ball center must be contained in this rectangle.
     *
     * @representationObject
     * @invar | allowedArea != null
     */
    private final Rectangle allowedArea;

    private static final int SCALE_INDEX_MIN = -2;
    private static final int SCALE_INDEX_MAX = 10;

    /**
     * @invar | SCALE_INDEX_MIN <= scaleIndex
     * @invar | scaleIndex <= SCALE_INDEX_MAX
     */
    private int scaleIndex = 0;

    /**
     * Constructor.
     * Note that the constructor does not enforce any limitations on the speed of the ball:
     * {@link #MINIMUM_SLOWDOWN_SQUARED_SPEED} and {@link #MAXIMUM_SPEEDUP_SQUARED_SPEED} are not taken into account.
     *
     * @throws IllegalArgumentException | allowedArea == null
     * @throws IllegalArgumentException | geometry == null
     * @throws IllegalArgumentException | velocity == null
     * @throws IllegalArgumentException | behavior == null
     * @post | getGeometry().equals(geometry)
     * @post | getVelocity().equals(velocity)
     * @post | getBehavior() == behavior
     * @post | getAllowedArea() == allowedArea
     */
    public Ball(Rectangle allowedArea, Circle geometry, Vector velocity, BallBehavior behavior)
    {
        if (allowedArea == null) throw new IllegalArgumentException();
        if (geometry == null) throw new IllegalArgumentException();
        if (velocity == null) throw new IllegalArgumentException();
        if (behavior == null) throw new IllegalArgumentException();

        this.allowedArea = allowedArea;
        this.geometry = geometry;
        this.velocity = velocity;
        this.behavior = behavior;
    }

    /**
     * Returns this ball's location.
     *
     * @post | result != null
     */
    public Circle getGeometry()
    {
        return this.geometry;
    }

    /**
     * Returns this ball's velocity.
     *
     * @post | result != null
     */
    public Vector getVelocity()
    {
        return this.velocity;
    }

    /**
     * Returns the ball's allowed area.
     *
     * @post | result != null
     */
    public Rectangle getAllowedArea()
    {
        return this.allowedArea;
    }

    /**
     * Returns this ball's behavior.
     *
     * @post | result != null
     */
    public BallBehavior getBehavior()
    {
        return this.behavior;
    }

    /**
     * @post | result != null
     */
    public Color getColor()
    {
        return Color.WHITE;
    }

    /**
     * Returns this ball's center.
     *
     * @post | result != null
     * @post | result.equals(getGeometry().getCenter())
     */
    public Point getCenter()
    {
        return this.geometry.getCenter();
    }

    /**
     * Updates the ball's state.
     *
     * LEGIT
     *
     * @pre | state != null
     * @pre | elapsedMilliseconds >= 0
     * @mutates | this
     * @mutates | state
     */
    @MPOOPLegitGenerated
    public void tick(BreakoutState state, long elapsedMilliseconds)
    {
        this.behavior.update(state, this, elapsedMilliseconds);
    }

    /**
     * Moves the ball elapsedMilliseconds into the future.
     * This method does not take into account collisions with other elements:
     * it simply moves the ball in a straight line.
     *
     * @pre | elapsedMilliseconds >= 0
     * @mutates_properties | getGeometry()
     * @post | getGeometry().equals(old(getGeometry()).move(getVelocity().multiply(elapsedMilliseconds)))
     */
    public void move(long elapsedMilliseconds)
    {
        this.geometry = this.geometry.move(this.velocity.multiply(elapsedMilliseconds));
    }

    /**
     * Computes the position the ball would be after elapsedMilliseconds time passes.
     * Does not take into account collisions with other elements.
     *
     * @pre | elapsedMilliseconds >= 0
     * @creates | result
     * @post | result != null
     * @post | result.equals(getGeometry().move(getVelocity().multiply(elapsedMilliseconds)))
     */
    public Circle computeDestination(long elapsedMilliseconds)
    {
        return this.geometry.move(this.velocity.multiply(elapsedMilliseconds));
    }

    /**
     * Updates the ball's geometry.
     *
     * @pre | geometry != null
     * @mutates_properties | getGeometry()
     * @post | getGeometry().equals(geometry)
     */
    public void setGeometry(Circle geometry)
    {
        this.geometry = geometry;
    }

    /**
     * Updates the ball's velocity.
     * Note that {@link #MINIMUM_SLOWDOWN_SQUARED_SPEED} and {@link #MAXIMUM_SPEEDUP_SQUARED_SPEED}
     * are not taken into account by this method.
     *
     * @pre | velocity != null
     * @mutates_properties | getVelocity()
     * @post | getVelocity().equals(velocity)
     */
    public void setVelocity(Vector velocity)
    {
        this.velocity = velocity;
    }

    /**
     * Checks that the given {@code velocity} is between {@link #MINIMUM_SLOWDOWN_SQUARED_SPEED} and {@link #MAXIMUM_SPEEDUP_SQUARED_SPEED}.
     *
     * @pre | velocity != null
     * @post | result == (MINIMUM_SLOWDOWN_SQUARED_SPEED <= velocity.getSquaredLength() && velocity.getSquaredLength() <= MAXIMUM_SPEEDUP_SQUARED_SPEED)
     */
    public boolean isValidScaledVelocity(Vector velocity)
    {
        var squaredLength = velocity.getSquaredLength();
        return MINIMUM_SLOWDOWN_SQUARED_SPEED <= squaredLength && squaredLength <= MAXIMUM_SPEEDUP_SQUARED_SPEED;
    }

    /**
     * Paints this ball onto the canvas.
     *
     * LEGIT
     *
     * @pre | canvas != null
     * @mutates | canvas
     */
    @MPOOPLegitGenerated
    public void paint(Canvas canvas)
    {
        this.behavior.paint(canvas, this);
    }

    /**
     * Sets the ball's behavior.
     *
     * @pre | behavior != null
     * @mutates_properties | getBehavior()
     * @post | getBehavior() == behavior
     */
    public void setBehavior(BallBehavior behavior)
    {
        this.behavior = behavior;
    }

    /**
     * Scales the ball's speed.
     * Only has an effect if the new speed would be between
     * {@link #MINIMUM_SLOWDOWN_SQUARED_SPEED} and {@link #MAXIMUM_SPEEDUP_SQUARED_SPEED}.
     *
     * LEGIT
     */
    @MPOOPLegitGenerated
    private void scaleVelocity(int kilofactor)
    {
        var scaledVelocity = this.velocity.multiply(kilofactor).divide(1000);
        if (isValidScaledVelocity(scaledVelocity))
        {
            setVelocity(this.velocity.multiply(kilofactor).divide(1000));
        }
    }

    /**
     * Increases this ball's speed by {@link #SPEED_UP_FACTOR} if the ball's updated speed would not exceed {@link #MAXIMUM_SPEEDUP_SQUARED_SPEED}.
     *
     * LEGIT
     *
     * @post | old(getVelocity().multiply(SPEED_UP_FACTOR).divide(1000).getSquaredLength()) <= MAXIMUM_SPEEDUP_SQUARED_SPEED ? getVelocity().equals(old(getVelocity().multiply(SPEED_UP_FACTOR).divide(1000))) : getVelocity().equals(old(getVelocity()))
     * @mutates_properties | getVelocity()
     */
    @MPOOPLegitGenerated
    public void speedUp()
    {
        scaleVelocity(SPEED_UP_FACTOR);
    }

    /**
     * Decreases this ball's speed by {@link #SLOW_DOWN_FACTOR} if the ball's updated speed would not be lower than {@link #MINIMUM_SLOWDOWN_SQUARED_SPEED}.
     *
     * LEGIT
     *
     * @post | old(getVelocity().multiply(SLOW_DOWN_FACTOR).divide(1000).getSquaredLength()) >= MINIMUM_SLOWDOWN_SQUARED_SPEED ? getVelocity().equals(old(getVelocity().multiply(SLOW_DOWN_FACTOR).divide(1000))) : getVelocity().equals(old(getVelocity()))
     * @mutates_properties | getVelocity()
     */
    @MPOOPLegitGenerated
    public void slowDown()
    {
        scaleVelocity(SLOW_DOWN_FACTOR);
    }

    /**
     * Scales the ball geometry up by a factor of 5/4 if the scale index allows it.
     *
     * @mutates_properties | getGeometry()
     */
    public void scaleUp()
    {
        boolean incremented = incrementScaleIndex();
        if (incremented)
        {
            this.geometry = this.geometry.scaleUp();
        }
    }

    /**
     * Scales the ball geometry down by a factor of 4/5 if the scale index allows it.
     *
     * @mutates_properties | getGeometry()
     */
    public void scaleDown()
    {
        boolean decremented = decrementScaleIndex();
        if (decremented)
        {
            geometry = geometry.scaleDown();
        }
    }

    /**
     * Increments the scale index field, if possible. Returns true if increment happened.
     */
    private boolean incrementScaleIndex()
    {
        int temp = scaleIndex;
        scaleIndex = Math.min(temp + 1, SCALE_INDEX_MAX);
        return temp < scaleIndex;
    }

    /**
     * Decrements the scale index field, if possible. Returns true if decrement happened.
     */
    private boolean decrementScaleIndex()
    {
        int temp = scaleIndex;
        scaleIndex = Math.max(temp - 1, SCALE_INDEX_MIN);
        return temp > scaleIndex;
    }
}
