package ogp.bricks;

import java.awt.Color;

import ogp.BreakoutState;
import ogp.balls.Ball;
import ogp.balls.BallBehavior;
import ogp.math.Circle;
import ogp.math.Point;
import ogp.math.Rectangle;
import ogp.math.Vector;
import ogp.ui.Canvas;

/**
 * All bricks have two properties:
 * - a rectangle (geometry), representing their position in the game world.
 * - a grid position, representing the position in the block grid
 * 
 * Also any brick is of some "kind", see BrickKind and how to do a case analysis on BrickKind values.
 * 
 * @invar | getGeometry() != null
 * @invar | getGridPosition() != null
 * @invar | getBrickKind() != null
 */
public class Brick
{
	
    /**
     * @invar | geometry != null
     * @representationObject
     */
    private final Rectangle geometry;

    /**
     * @invar | gridPosition != null
     */
    private final Point gridPosition;
    
    /**
     * @invar | brickKind != null
     */
    private final BrickKind brickKind;
    
    /**
     * Constructor for Brick.
     * 
     * @pre | geometry != null
     * @pre | gridPosition != null
     * @pre | brickKind != null
     * @post | getGeometry().equals(geometry)
     * @post | getGridPosition().equals(gridPosition)
     * @post | getBrickKind() == brickKind
     * @throws IllegalArgumentException | geometry == null
     * @throws IllegalArgumentException | gridPosition == null
     * @throws IllegalArgumentException | brickKind == null
     */
    public Brick(Rectangle geometry, Point gridPosition, BrickKind brickKind)
    {
        if (geometry == null) {
            throw new IllegalArgumentException("geometry cannot be null");
        }
        if (gridPosition == null) {
            throw new IllegalArgumentException("gridPosition cannot be null");
        }
        if (brickKind == null) {
            throw new IllegalArgumentException("brickKind cannot be null");
        }

        this.geometry = geometry.copy();
        this.gridPosition = gridPosition;
        this.brickKind = brickKind;
    }
    
    /**
     * Returns the brick kind.
     * 
     * @post | result != null
     */
    public BrickKind getBrickKind() { return this.brickKind; }

    /**
     * Returns the grid position.
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
     * @creates | result
     * @post | result != null
     */
    public Rectangle getGeometry()
    {
        return geometry.copy();
    }
    
    /**
     * Returns the label for this brick type.
     * 
     * @post | result != null
     */
    public String getLabel() {
    	return switch (brickKind) {
    		case STANDARD -> "";
    		case SPAWNBALL -> "o";
    		case SPIKEY -> "S";
    		case SHRINKPADDLE -> "-";
    		case INVERTPADDLE -> "I";
    	};
    }
    
    /**
     * Returns the color of the label.
     * 
     * @post | result != null
     */
    public Color getLabelColor() {
    	return getColorPriv();
    }

    /**
     * Checks if this brick is indestructible.
     * 
     * @post | result == (getBrickKind() == BrickKind.SPIKEY)
     */
    public boolean isIndestructible() {
		return brickKind == BrickKind.SPIKEY;
	}

	/**
     * Paints the brick using the canvas, including its label.
     * 
     * NOSPEC
     * 
     * @pre | canvas != null
     * @mutates | canvas
     */
    public void paint(Canvas canvas)
    {
    	canvas.drawFilledRectangle(getColorPriv(), geometry);
    	if (!getLabel().isEmpty()) {
    		canvas.drawText(geometry.getCenter(), getLabelColor(), getLabel());
    	}
    }

    /**
     * Used in the paint method to determine the color of the rectangle on screen.
     * See ColorSet as well.
     * 
     * @post | result != null
     */
    private Color getColorPriv()
    {
        return switch (brickKind) {
    		case STANDARD -> ColorSet.STANDARD_BRICK;
    		case SPAWNBALL -> ColorSet.SPAWN_BALL_BRICK;
    		case SPIKEY -> ColorSet.SPIKEY_BRICK;
    		case SHRINKPADDLE -> ColorSet.SHRINK_PADDLE_BRICK;
    		case INVERTPADDLE -> ColorSet.INVERT_PADDLE_BRICK;
    	};
    }
    
    /**
     * Returns the color of the brick.
     * 
     * @post | result != null
     */
    public Color getColor() {
    	return getColorPriv();
    }

    /**
     * Called when this brick has been hit by a ball.
     * It is given the full BreakoutState and the Ball which has hit the brick.
     * This method should update the state and/or ball,
     * e.g., remove the brick from the state, change the paddle's size, etc.
     * 
     * Speed reflection of the ball is not handled here.
     * 
     * @pre | state != null
     * @pre | ball != null
     * @pre | state.getBricks().contains(this)
     * @mutates | state
     */
    public void hit(BreakoutState state, Ball ball) {
    	
    	switch (this.brickKind) {
    	
    		case STANDARD -> {
    			state.getBrickGrid().removeBrick(this);
    		}
    		
    		case SPAWNBALL -> {
    			state.getBrickGrid().removeBrick(this);
    			Point c = state.getBrickGrid().getBoundingRectangle().getBottomCenter();
    			Vector up = new Vector(0, -350);
    			BallBehavior behavior = new BallBehavior();
    			state.addBall(
    					new Circle(c, 500),
    					up,
    					behavior);
    		}
    		
    		case SPIKEY -> {
    			state.lose1Life();
    		}
    		
    		case SHRINKPADDLE -> {
    			state.getBrickGrid().removeBrick(this);
    			state.getPaddle().shrink();
    		}
    		
    		case INVERTPADDLE -> {
    			state.getBrickGrid().removeBrick(this);
    			state.getPaddle().applyInverted();
    		}
    	}
    }
}
