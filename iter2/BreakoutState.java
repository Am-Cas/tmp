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
import ogp.walls.EastWall;
import ogp.walls.NorthWall;
import ogp.walls.Wall;
import ogp.walls.WestWall;

/**
 * Represents the current state of a breakout game.
 *
 * @invar | getBalls() != null
 * @invar | getBalls().stream().allMatch(b -> b != null)
 * @invar | !SpecUtil.containsDuplicateObjects(getBalls())
 * @invar | getBrickGrid() != null
 * @invar | getPaddle() != null
 * @invar | getWalls() != null
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
     * @invar | getBoundingRectanglePrivate().contains(paddle.getGeometry())
     */
    private Paddle paddle;

    /**
     * @representationObject
     * @invar | walls != null
     * @invar | SpecUtil.containsNoNulls(walls)
     */
    private final ArrayList<Wall> walls;

    /**
     * Health points.
     * @invar | hps >= 0
     */
    private int hps;


    /**
     * Returns current health points.
     *
     * @post | result >= 0
     */
    public int getHps()
    {
        return hps;
    }

    /**
     * Returns a copy of the list of balls.
     *
     * @creates | result
     * @post | result != null
     * @post | result.stream().allMatch(b -> b != null)
     * @post | !SpecUtil.containsDuplicateObjects(result)
     */
    public ArrayList<Ball> getBalls()
    {
        return new ArrayList<>(balls);
    }

    /**
     * Returns the paddle of this BreakoutState.
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
     * @post | result != null
     */
    public BrickGrid getBrickGrid()
    {
        return this.bricks;
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
     */
    public ArrayList<Wall> getWalls()
    {
        return new ArrayList<>(this.walls);
    }

    /**
     * Construct a new BreakoutState.
     *
     * LEGIT
     *
     * @post | getBrickGrid() == brickGrid
     * @post | getBalls().isEmpty()
     * @post | getWalls().size() == 3
     * @post | getPaddle() != null
     * @post | getHps() == initHP
     *
     * @throws IllegalArgumentException | brickGrid == null
     * @throws IllegalArgumentException | initialPaddleHalfWidth <= 0
     * @throws IllegalArgumentException | paddleSpeed <= 0
     * @throws IllegalArgumentException | initHP < 0
     */
    @MPOOPLegitGenerated
    public BreakoutState(BrickGrid brickGrid, long initialPaddleHalfWidth, long paddleSpeed, int initHP)
    {
        if (brickGrid == null)
        {
            throw new IllegalArgumentException();
        }

        if (initHP < 0) { throw new IllegalArgumentException(); }
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
        while (elapsedMilliseconds > 0)
        {
            var dt = Math.min(MAXIMUM_TIME_DELTA, elapsedMilliseconds);
            atomicTick(dt);
            elapsedMilliseconds -= dt;
        }
    }

    /**
     * Checks if the game has ended (won or lost).
     *
     * @post | result == (isGameWon() || isGameLost())
     */
    public boolean isGameOver()
    {
        return isGameWon() || isGameLost();
    }

    /**
     * Checks whether the game has been won.
     * The game is won when all remaining bricks are indestructible.
     *
     * @post | result == getBricks().stream().allMatch(b -> b.isIndestructible())
     */
    public boolean isGameWon()
    {
        return getBricks().stream().allMatch(b -> b.isIndestructible());
    }

    /**
     * Checks whether the game has been lost.
     * The game is lost when there are no more balls left or hps <= 0.
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
     * @mutates | this
     * @post | !getBalls().contains(ball)
     */
    public void removeBall(Ball ball)
    {
        this.balls.remove(ball);
    }

    /**
     * Checks if the ball is lost.
     *
     * LEGIT
     *
     * @pre | ball != null
     * @post | result == !getBoundingRectangle().contains(ball.getCenter())
     */
    @MPOOPLegitGenerated
    public boolean isBallLost(Ball ball)
    {
        return !getBoundingRectangle().contains(ball.getCenter());
    }

    /**
     * Adds new ball to the game.
     *
     * @post | SpecUtil.sameListsWithElementRemoved(getBalls(), old(new ArrayList<>(getBalls())), result)
     */
    public Ball addBall(Circle geometry, Vector velocity, BallBehavior behavior)
    {
        var allowedArea = this.getBoundingRectangle().thickening();
        var ball = new Ball(allowedArea, geometry, velocity, behavior);

        this.balls.add(ball);

        return ball;
    }

    /**
     * Decrements the player's health points by 1 (minimum 0).
     *
     * @mutates_properties | getHps()
     * @post | getHps() == Math.max(0, old(getHps()) - 1)
     */
    public void lose1Life()
    {
        this.hps = Math.max(0, this.hps - 1);
    }


    /**
     * LEGIT
     */
    @MPOOPLegitGenerated
    private void atomicTick(long elapsedTime)
    {
        paddle.tick(this, elapsedTime);

        for (var ball : new ArrayList<>(this.balls))
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
     * LEGIT
     */
    @MPOOPLegitGenerated
    private static ArrayList<Wall> createWalls(BrickGrid brickGrid)
    {
        var right = brickGrid.getWidth();
        var topWall = new NorthWall(0);
        var rightWall = new EastWall(right);
        var leftWall = new WestWall(0);

        return new ArrayList<>(Arrays.asList(topWall, rightWall, leftWall));
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
