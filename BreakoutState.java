package ogp;

import java.util.ArrayList;
import java.util.Arrays;

import ogp.balls.Ball;
import ogp.balls.BallBehavior;
import ogp.bricks.Brick;
import ogp.math.Circle;
import ogp.math.Interval;
import ogp.math.Rectangle;
import ogp.math.Vector;
import ogp.paddles.Paddle;
import ogp.util.MPOOPLegitGenerated;
import ogp.util.SpecUtil;
import ogp.walls.Wall;
import ogp.walls.WallKind;

/**
 * Represents the current state of a breakout game.
 * 
 * @invar | getBalls() != null
 * @invar | getBalls().stream().allMatch(b -> b != null)
 * @invar | getBrickGrid() != null
 * @invar | getPaddle() != null
 * @invar | getWalls() != null
 * @invar | getWalls().stream().allMatch(w -> w != null)
 * @invar | getHps() >= 0
 */
public class BreakoutState
{
    public static int MAXIMUM_TIME_DELTA = 20;

    /**
     * List of all balls.
     * 
     * @invar | balls != null
     * @invar | balls.stream().allMatch(b -> b != null && getBoundingRectanglePrivate().thickening().contains(b.getGeometry().getCenter()))
     * @invar | !SpecUtil.containsDuplicateObjects(balls)
     * @representationObject
     */
    private ArrayList<Ball> balls;

    /**
     * @invar | bricks != null
     */
    private final BrickGrid bricks;

    /**
     * @invar | paddle != null
     * @representationObject
     */
    private Paddle paddle;

    /**
     * @invar | walls != null
     * @invar | walls.stream().allMatch(w -> w != null)
     * @representationObject
     */
    private final ArrayList<Wall> walls;
    
    /**
     * Health points.
     * @invar | hps >= 0
     */
    private int hps;
    
    /**
     * Returns the health points.
     * 
     * @post | result >= 0
     */
    public int getHps() {
    	return hps;
    }

    /**
	 * Returns the list of balls.
	 *
	 * @creates | result
	 * @post | result != null
	 * @post | result.stream().allMatch(b -> b != null)
	 */
	public ArrayList<Ball> getBalls()
	{
	    return new ArrayList<>(balls);
	}

	/**
	 * Return the paddle of this BreakoutState.
	 * 
	 * @post | result != null
	 */
	public Paddle getPaddle()
	{
	    return paddle;
	}

	/**
	 * Returns a list of bricks.
	 * 
	 * LEGIT
	 * 
	 * @post | result != null
	 * @post | result.stream().allMatch(brick -> brick != null)
	 * @post | !SpecUtil.containsDuplicateObjects(result)
	 */
    @MPOOPLegitGenerated
	public ArrayList<Brick> getBricks()
	{
	    return this.bricks.getBricks();
	}

    /**
     * Returns the brick grid.
     * 
     * @post | result != null
     */
	public BrickGrid getBrickGrid()
	{
	    return bricks;
	}

	/**
	 * Return a rectangle representing the game field.
	 *
	 * LEGIT
	 *
	 * @post | result != null
	 */
    @MPOOPLegitGenerated
	public Rectangle getBoundingRectangle()
	{
	    return getBoundingRectanglePrivate();
	}

	/**
	 * Returns a list of walls.
	 * 
	 * @creates | result
	 * @post | result != null
	 * @post | result.stream().allMatch(w -> w != null)
	 */
	public ArrayList<Wall> getWalls()
	{
	    return new ArrayList<>(walls);
	}

	/**
     * Construct a new BreakoutState.
     * 
     * @pre | brickGrid != null
     * @pre | initialPaddleHalfWidth > 0
     * @pre | paddleSpeed > 0
     * @pre | initHP >= 0
     * @post | getPaddle() != null
     * @post | getBrickGrid() != null
     * @post | getBalls().isEmpty()
     * @post | getWalls().size() == 2
     * @post | getHps() == initHP
     * @throws IllegalArgumentException | brickGrid == null
     * @throws IllegalArgumentException | initialPaddleHalfWidth <= 0
     * @throws IllegalArgumentException | paddleSpeed <= 0
     * @throws IllegalArgumentException | initHP < 0
     */
    public BreakoutState(BrickGrid brickGrid, long initialPaddleHalfWidth, long paddleSpeed, int initHP)
    {
        if (brickGrid == null) {
            throw new IllegalArgumentException("brickGrid cannot be null");
        }
        if (initialPaddleHalfWidth <= 0) {
            throw new IllegalArgumentException("initialPaddleHalfWidth must be positive");
        }
        if (paddleSpeed <= 0) {
            throw new IllegalArgumentException("paddleSpeed must be positive");
        }
        if (initHP < 0) {
            throw new IllegalArgumentException("initHP cannot be negative");
        }

        this.hps = initHP;
        this.balls = new ArrayList<>();
        this.bricks = brickGrid;
        this.paddle = createPaddle(brickGrid, initialPaddleHalfWidth, paddleSpeed);
        this.walls = createWalls(brickGrid);
    }

    /**
     * Move all moving objects one step forward.
     * Cuts large elapsedMilliseconds in multiple smaller values.
     * 
     * LEGIT
     *
     * @pre | elapsedMilliseconds >= 0
     *
     * @mutates | this
     * @mutates | ...getBalls()
     */
    @MPOOPLegitGenerated
    public void tick(long elapsedMilliseconds)
    {
        while ( elapsedMilliseconds > 0 )
        {
            var dt = Math.min(MAXIMUM_TIME_DELTA, elapsedMilliseconds);
            atomicTick(dt);
            elapsedMilliseconds -= dt;
        }
    }

    /**
     * Checks if the game has ended.
     * 
     * @post | result == (isGameWon() || isGameLost())
     */
    public boolean isGameEnded()
    {
        return isGameWon() || isGameLost();
    }

    /**
     * Checks whether the game has been won.
     * The game is won when the bricks that remain are indestructible.
     *
     * @post | result == bricks.getBricks().stream().allMatch(b -> b.isIndestructible())
     */
    public boolean isGameWon()
    {
    	return bricks.getBricks().stream().allMatch(b -> b.isIndestructible());
    }

    /**
     * Checks whether the game has been lost.
     * The game is lost when there are no more balls left or we have <= 0 hps
     *
     * @post | result == (getBalls().isEmpty() || getHps() <= 0)
     */
    public boolean isGameLost()
    {
        return balls.isEmpty() || hps <= 0;
    }

    /**
     * Removes the given ball from the game.
     * 
     * @pre | ball != null
     * @pre | getBalls().contains(ball)
     * @post | !getBalls().contains(ball)
     * @mutates | this
     */
    public void removeBall(Ball ball)
    {
        balls.remove(ball);
    }

    /**
     * Checks if the ball is lost.
     * 
     * LEGIT
     * 
     * @pre | ball != null
     */
    @MPOOPLegitGenerated
    public boolean isBallLost(Ball ball)
    {
        return !getBoundingRectangle().contains(ball.getCenter());
    }

    /**
     * Adds new ball to the game and returns the reference.
     * 
     * @pre | geometry != null
     * @pre | velocity != null
     * @pre | behavior != null
     * @pre | getBoundingRectangle().thickening().contains(geometry.getCenter())
     * @post | getBalls().contains(result)
     * @creates | result
     * @post | result != null
     * @mutates | this
     */
    public Ball addBall(Circle geometry, Vector velocity, BallBehavior behavior)
    {
        var ball = new Ball(getBoundingRectangle().thickening(), geometry, velocity, behavior);
        balls.add(ball);
        return ball;
    }
    
    /**
     * Decreases health points by 1.
     * 
     * @post | getHps() == old(getHps()) - 1
     * @mutates_properties | getHps()
     */
    public void lose1Life() {
        if (hps > 0) {
            hps--;
        }
    }

	/**
	 * LEGIT
	 */
    @MPOOPLegitGenerated
	private void atomicTick(long elapsedTime)
	{
	    paddle.tick(this, elapsedTime);
	
	    for ( var ball : new ArrayList<>(this.balls) )
	    {
	        ball.tick(this, elapsedTime);
	    }
	}

	/**
	 * Private twin of getBoundingRectangle() so as to be usable internally.
	 * 
	 * LEGIT
	 */
    @MPOOPLegitGenerated
	private Rectangle getBoundingRectanglePrivate()
	{
	    var left = 0;
	    var top = 0;
	    var width = this.bricks.getWidth();
	    var height = this.bricks.getHeight() + this.paddle.getHeight();
	
	    return new Rectangle(left, top, width, height);
	}

    /**
     * Returns a list containing the top right and left walls.
     * 
     * @pre | brickGrid != null
     * @creates | result
     * @post | result != null
     * @post | result.size() == 2
     */
	private static ArrayList<Wall> createWalls(BrickGrid brickGrid)
	{
	    var walls = new ArrayList<Wall>();
	    var bounds = brickGrid.getBoundingRectangle();
	    
	    // Left wall
	    walls.add(new Wall(new Rectangle(
	        bounds.getLeft() - 1000,
	        bounds.getTop(),
	        1000,
	        bounds.getHeight()
	    ), WallKind.LEFT));
	    
	    // Right wall
	    walls.add(new Wall(new Rectangle(
	        bounds.getRight(),
	        bounds.getTop(),
	        1000,
	        bounds.getHeight()
	    ), WallKind.RIGHT));
	    
	    return walls;
	}

	/**
	 * LEGIT
	 */
    @MPOOPLegitGenerated
	private static Paddle createPaddle(BrickGrid brickGrid, long initialPaddleHalfWidth, long speed)
	{
	    var allowedInterval = new Interval(0, brickGrid.getWidth());
	    var topCenter = brickGrid.getBoundingRectangle().getBottomCenter();
	
	    return new Paddle(allowedInterval, topCenter, initialPaddleHalfWidth, speed);
	}
}
