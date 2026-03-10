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
 * @invar | getAllowedArea().contains(getGeometry().getCenter())
 * @invar | getGeometry() != null
 * @invar | getVelocity() != null
 * @invar | getBehavior() != null
 */
public class Ball
{

    /**
     * Determines shape and position of the ball.
     * 
     * @representationObject
     * @invar | geometry != null
     */
    private Circle geometry;

    /**
     * Expressed in distance per milliseconds.
     * 
     * @representationObject
     * @invar | velocity != null
     */
    private Vector velocity;

    /**
     * Determines how the ball behaves.
     * See BallBehavior.
     * 
     * @invar | behavior != null
     */
    private BallBehavior behavior;

    /**
     * The ball center must at all times fit inside this rectangle.
     * 
     * @representationObject
     * @invar | allowedArea != null
     */
    private final Rectangle allowedArea;
    
    /**
     * Constructor for Ball.
     * 
     * @pre | allowedArea != null
     * @pre | geometry != null
     * @pre | velocity != null
     * @pre | behavior != null
     * @pre | allowedArea.contains(geometry.getCenter())
     * @post | getGeometry().equals(geometry)
     * @post | getVelocity().equals(velocity)
     * @post | getBehavior() == behavior
     * @post | getAllowedArea().equals(allowedArea)
     * @throws IllegalArgumentException | allowedArea == null
     * @throws IllegalArgumentException | geometry == null
     * @throws IllegalArgumentException | velocity == null
     * @throws IllegalArgumentException | behavior == null
     * @throws IllegalArgumentException | !allowedArea.contains(geometry.getCenter())
     */
    public Ball(Rectangle allowedArea, Circle geometry, Vector velocity, BallBehavior behavior)
    {
        if (allowedArea == null) {
            throw new IllegalArgumentException("allowedArea cannot be null");
        }
        if (geometry == null) {
            throw new IllegalArgumentException("geometry cannot be null");
        }
        if (velocity == null) {
            throw new IllegalArgumentException("velocity cannot be null");
        }
        if (behavior == null) {
            throw new IllegalArgumentException("behavior cannot be null");
        }
        if (!allowedArea.contains(geometry.getCenter())) {
            throw new IllegalArgumentException("geometry center must be inside allowedArea");
        }

        this.allowedArea = allowedArea.copy();
        this.geometry = geometry.copy();
        this.velocity = velocity.copy();
        this.behavior = behavior;
    }

    /**
     * Returns the ball's geometry.
     * 
     * @creates | result
     * @post | result != null
     */
    public Circle getGeometry()
    {
        return geometry.copy();
    }

    /**
     * Returns the ball's velocity.
     * 
     * @creates | result
     * @post | result != null
     */
    public Vector getVelocity()
    {
        return velocity.copy();
    }

    /**
     * Returns the ball's allowed area.
     * 
     * @creates | result
     * @post | result != null
     */
    public Rectangle getAllowedArea()
    {
        return allowedArea.copy();
    }

    /**
     * Returns this ball's behavior.
     * 
     * @post | result != null
     */
    public BallBehavior getBehavior()
    {
        return behavior;
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
     * @creates | result
     * @post | result != null
     */
    public Point getCenter()
    {
        return geometry.getCenter().copy();
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
     * @mutates_property | getGeometry()  
     */
    public void move(long elapsedMilliseconds)
    {
        var destination = computeDestination(elapsedMilliseconds);
        setGeometry(destination);
    }

    /**
     * Computes the position the ball would be after elapsedMilleconds time passes.
     * Does not take into account collisions with other elements. 
     * 
     * @pre | elapsedMilliseconds >= 0
     * @creates | result
     * @post | result != null
     */
    public Circle computeDestination(long elapsedMilliseconds)
    {
        var displacement = velocity.scale(elapsedMilliseconds);
        var newCenter = geometry.getCenter().add(displacement);
        return new Circle(newCenter, geometry.getRadius());
    }

    /**
     * Updates the ball's geometry.
     * 
     * @pre | geometry != null
     * @pre | getAllowedArea().contains(geometry)
     * @post | getGeometry().equals(geometry)
     * @mutates_properties | getGeometry()
     * @throws IllegalArgumentException | geometry == null
     * @throws IllegalArgumentException | !getAllowedArea().contains(geometry)
     */
    public void setGeometry(Circle geometry)
    {
        if (geometry == null) {
            throw new IllegalArgumentException("geometry cannot be null");
        }
        if (!allowedArea.contains(geometry.getCenter())) {
            throw new IllegalArgumentException("geometry center must be inside allowedArea");
        }
        this.geometry = geometry.copy();
    }

    /**
     * Updates the ball's velocity.
     * 
     * @pre | velocity != null
     * @post | getVelocity().equals(velocity)
     * @mutates_properties | getVelocity()
     * @throws IllegalArgumentException | velocity == null
     */
    public void setVelocity(Vector velocity)
    {
        if (velocity == null) {
            throw new IllegalArgumentException("velocity cannot be null");
        }
        this.velocity = velocity.copy();
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
     * @post | getBehavior() == behavior
     * @mutates_properties | getBehavior()
     * @throws IllegalArgumentException | behavior == null
     */
    public void setBehavior(BallBehavior behavior)
    {
        if (behavior == null) {
            throw new IllegalArgumentException("behavior cannot be null");
        }
        this.behavior = behavior;
    }
}
