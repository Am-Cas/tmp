package ogp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ogp.balls.Ball;
import ogp.balls.BlinkingBallBehavior;
import ogp.balls.StandardBehavior;
import ogp.balls.StrongBallBehavior;
import ogp.bricks.BlinkingBallBrick;
import ogp.bricks.InvertPaddleBrick;
import ogp.bricks.ShrinkPaddleBrick;
import ogp.bricks.SpawnBallBrick;
import ogp.bricks.SpeedUpBrick;
import ogp.bricks.SpikeyBrick;
import ogp.bricks.StandardBrick;
import ogp.bricks.StrengtheningBrick;
import ogp.math.Circle;
import ogp.math.Interval;
import ogp.math.Point;
import ogp.math.Rectangle;
import ogp.math.Vector;
import ogp.paddles.Paddle;
import ogp.paddles.PaddleMotionDirection;

/**
 * Unit tests for the Breakout project, Iteration 2.
 *
 * Tests are divided into:
 *   - Regression tests (regrXxx): fail on the original flawed code, pass after fixing.
 *   - Normal unit tests: verify correct behaviour as specified in the assignment.
 *
 * Naming convention: one scenario per test method.
 */
public class BreakoutTest
{
    // -----------------------------------------------------------------------
    // Shared factory helpers
    // -----------------------------------------------------------------------

    private BrickGrid emptyGrid()
    {
        return new BrickGrid(7, 8, 100, 30);
    }

    /** State with no balls added, backed by the default map. */
    private BreakoutState defaultState()
    {
        return GameMapParser.parseNoBalls(GameMapParser.DEFAULT_MAP, 100, 30);
    }

    /** State built on a fresh empty grid, with no balls. */
    private BreakoutState freshState()
    {
        return new BreakoutState(emptyGrid(), 100, 10, 70);
    }

    /** Adds a standard ball at the centre of the bounding rectangle. */
    private Ball addCentreBall(BreakoutState state)
    {
        var centre = state.getBoundingRectangle().getCenter();
        return state.addBall(new Circle(centre, 5), new Vector(1, -1), new StandardBehavior());
    }

    // =======================================================================
    // REGRESSION TESTS
    // Each test name starts with "regr" and targets one specific original flaw.
    // =======================================================================

    // --- Collision ---

    /** REGRESSION: Collision constructor stored 0 instead of the given time. */
    @Test
    public void regrCollisionConstructorStoresTime()
    {
        var col = new Collision(42L, Vector.KILO_DOWN);
        assertEquals(42L, col.getMillisecondsUntilCollision());
    }

    /** REGRESSION: Collision constructor stored null instead of the given kiloNormal. */
    @Test
    public void regrCollisionConstructorStoresKiloNormal()
    {
        var col = new Collision(0L, Vector.KILO_UP);
        assertEquals(Vector.KILO_UP, col.getKiloNormal());
    }

    // --- BrickCollision ---

    /** REGRESSION: BrickCollision stored null in brick field; getBrick() returned null. */
    @Test
    public void regrBrickCollisionGetBrickNotNull()
    {
        var grid = emptyGrid();
        var brick = grid.addStandardBrick(new Point(0, 0));
        var col = new BrickCollision(0L, Vector.KILO_DOWN, brick);
        assertNotNull(col.getBrick());
    }

    /** REGRESSION: BrickCollision stored null; getBrick() did not return the correct brick. */
    @Test
    public void regrBrickCollisionGetBrickIsCorrectObject()
    {
        var grid = emptyGrid();
        var brick = grid.addStandardBrick(new Point(1, 1));
        var col = new BrickCollision(10L, Vector.KILO_LEFT, brick);
        assertSame(brick, col.getBrick());
    }

    // --- BrickGrid ---

    /** REGRESSION: BrickGrid constructor set brickWidth to 0. */
    @Test
    public void regrBrickGridBrickWidthStoredCorrectly()
    {
        var grid = new BrickGrid(3, 4, 50, 20);
        assertEquals(50, grid.getBrickWidth());
    }

    /** REGRESSION: BrickGrid constructor set brickHeight to 0. */
    @Test
    public void regrBrickGridBrickHeightStoredCorrectly()
    {
        var grid = new BrickGrid(3, 4, 50, 20);
        assertEquals(20, grid.getBrickHeight());
    }

    /** REGRESSION: BrickGrid constructor set grid to null, causing NPE on column-count access. */
    @Test
    public void regrBrickGridColumnCountAccessibleAfterConstruction()
    {
        var grid = new BrickGrid(3, 4, 50, 20);
        assertEquals(3, grid.getColumnCount());
    }

    /** REGRESSION: BrickGrid constructor set grid to null, causing NPE on row-count access. */
    @Test
    public void regrBrickGridRowCountAccessibleAfterConstruction()
    {
        var grid = new BrickGrid(3, 4, 50, 20);
        assertEquals(4, grid.getRowCount());
    }

    /** REGRESSION: BrickGrid.getHeight() returned 0 instead of rowCount * brickHeight. */
    @Test
    public void regrBrickGridGetHeightCorrect()
    {
        var grid = new BrickGrid(3, 4, 50, 20);
        assertEquals(4 * 20, grid.getHeight());
    }

    /** REGRESSION: BrickGrid.getBrickAt() returned null even when a brick was present. */
    @Test
    public void regrBrickGridGetBrickAtReturnsAddedBrick()
    {
        var grid = new BrickGrid(5, 5, 100, 30);
        var pos = new Point(2, 2);
        var brick = grid.addStandardBrick(pos);
        assertSame(brick, grid.getBrickAt(pos));
    }

    /** REGRESSION: BrickGrid.addSpawnBallBrick() returned null instead of the new brick. */
    @Test
    public void regrBrickGridAddSpawnBallBrickNotNull()
    {
        var grid = new BrickGrid(5, 5, 100, 30);
        assertNotNull(grid.addSpawnBallBrick(new Point(0, 0)));
    }

    /** REGRESSION: BrickGrid.addSpawnBallBrick() did not store the brick in the grid. */
    @Test
    public void regrBrickGridAddSpawnBallBrickStoredInGrid()
    {
        var grid = new BrickGrid(5, 5, 100, 30);
        var pos = new Point(0, 0);
        var brick = grid.addSpawnBallBrick(pos);
        assertSame(brick, grid.getBrickAt(pos));
    }

    // --- Ball ---

    /** REGRESSION: Ball constructor stored null in all fields; getGeometry() returned null. */
    @Test
    public void regrBallGetGeometryNotNull()
    {
        assertNotNull(addCentreBall(defaultState()).getGeometry());
    }

    /** REGRESSION: Ball constructor stored null; getVelocity() returned null. */
    @Test
    public void regrBallGetVelocityNotNull()
    {
        assertNotNull(addCentreBall(defaultState()).getVelocity());
    }

    /** REGRESSION: Ball constructor stored null; getBehavior() returned null. */
    @Test
    public void regrBallGetBehaviorNotNull()
    {
        assertNotNull(addCentreBall(defaultState()).getBehavior());
    }

    /** REGRESSION: Ball constructor stored null; getAllowedArea() returned null. */
    @Test
    public void regrBallGetAllowedAreaNotNull()
    {
        assertNotNull(addCentreBall(defaultState()).getAllowedArea());
    }

    /** REGRESSION: Ball.getCenter() delegated to null geometry and returned null. */
    @Test
    public void regrBallGetCenterNotNull()
    {
        assertNotNull(addCentreBall(defaultState()).getCenter());
    }

    /** REGRESSION: Ball.move() did nothing; x-position unchanged. */
    @Test
    public void regrBallMoveChangesPositionX()
    {
        var state = freshState();
        var centre = state.getBoundingRectangle().getCenter();
        var ball = state.addBall(new Circle(centre, 5), new Vector(3, 0), new StandardBehavior());
        long xBefore = ball.getCenter().x();
        ball.move(10);
        assertEquals(xBefore + 30, ball.getCenter().x());
    }

    /** REGRESSION: Ball.move() did nothing; y-position unchanged. */
    @Test
    public void regrBallMoveChangesPositionY()
    {
        var state = freshState();
        var centre = state.getBoundingRectangle().getCenter();
        var ball = state.addBall(new Circle(centre, 5), new Vector(0, -4), new StandardBehavior());
        long yBefore = ball.getCenter().y();
        ball.move(5);
        assertEquals(yBefore - 20, ball.getCenter().y());
    }

    /** REGRESSION: Ball.computeDestination() returned null. */
    @Test
    public void regrBallComputeDestinationNotNull()
    {
        assertNotNull(addCentreBall(freshState()).computeDestination(5));
    }

    /** REGRESSION: Ball.computeDestination() returned wrong coordinates. */
    @Test
    public void regrBallComputeDestinationCorrect()
    {
        var state = freshState();
        var centre = state.getBoundingRectangle().getCenter();
        var ball = state.addBall(new Circle(centre, 5), new Vector(2, -3), new StandardBehavior());
        var dest = ball.computeDestination(10);
        assertEquals(centre.x() + 20, dest.getCenter().x());
        assertEquals(centre.y() - 30, dest.getCenter().y());
    }

    /** REGRESSION: Ball.setVelocity() did nothing; velocity was unchanged. */
    @Test
    public void regrBallSetVelocityActuallyUpdatesVelocity()
    {
        var state = freshState();
        var ball = addCentreBall(state);
        var newVel = new Vector(7, -3);
        ball.setVelocity(newVel);
        assertEquals(newVel, ball.getVelocity());
    }

    // --- TemporaryBehavior ---

    /** REGRESSION: TemporaryBehavior stored timeLeft = duration * 0 = 0; should be DURATION. */
    @Test
    public void regrStrongBallBehaviorTimeLeftInitialisedToDuration()
    {
        assertEquals(StrongBallBehavior.DURATION, new StrongBallBehavior().getTimeLeft());
    }

    /** REGRESSION: Same issue for BlinkingBallBehavior. */
    @Test
    public void regrBlinkingBallBehaviorTimeLeftInitialisedToDuration()
    {
        assertEquals(BlinkingBallBehavior.DURATION, new BlinkingBallBehavior().getTimeLeft());
    }

    // --- Brick.getGridPosition ---

    /** REGRESSION: Brick.getGridPosition() returned null instead of the stored position. */
    @Test
    public void regrBrickGetGridPositionNotNull()
    {
        assertNotNull(emptyGrid().addStandardBrick(new Point(1, 2)).getGridPosition());
    }

    /** REGRESSION: Brick.getGridPosition() returned null so could not equal expected position. */
    @Test
    public void regrBrickGetGridPositionEqualsConstructorArgument()
    {
        var pos = new Point(3, 1);
        assertEquals(pos, emptyGrid().addStandardBrick(pos).getGridPosition());
    }

    // --- Paddle speed guard ---

    /** REGRESSION: Paddle constructor threw for speed == 10 (a perfectly valid speed). */
    @Test
    public void regrPaddleAcceptsSpeedOfTen()
    {
        assertDoesNotThrow(
                () -> new Paddle(new Interval(0, 1000), new Point(500, 0), 100, 10));
    }

    /** REGRESSION: With the wrong guard, speed == 0 was accepted instead of rejected. */
    @Test
    public void regrPaddleRejectsZeroSpeed()
    {
        assertThrows(IllegalArgumentException.class,
                () -> new Paddle(new Interval(0, 1000), new Point(500, 0), 100, 0));
    }

    /** REGRESSION: With the wrong guard, negative speed was also accepted. */
    @Test
    public void regrPaddleRejectsNegativeSpeed()
    {
        assertThrows(IllegalArgumentException.class,
                () -> new Paddle(new Interval(0, 1000), new Point(500, 0), 100, -1));
    }

    // =======================================================================
    // NORMAL UNIT TESTS
    // =======================================================================

    // --- Collision ---

    @Test
    public void collisionZeroTimeIsAllowed()
    {
        assertEquals(0L, new Collision(0L, Vector.KILO_RIGHT).getMillisecondsUntilCollision());
    }

    @Test
    public void collisionThrowsOnNullKiloNormal()
    {
        assertThrows(IllegalArgumentException.class, () -> new Collision(0L, null));
    }

    @Test
    public void collisionThrowsOnNegativeTime()
    {
        assertThrows(IllegalArgumentException.class, () -> new Collision(-1L, Vector.KILO_DOWN));
    }

    @Test
    public void getEarliestCollisionBothNull()
    {
        assertNull(Collision.getEarliestCollision(null, null));
    }

    @Test
    public void getEarliestCollisionFirstNull()
    {
        var c = new Collision(5L, Vector.KILO_DOWN);
        assertSame(c, Collision.getEarliestCollision(null, c));
    }

    @Test
    public void getEarliestCollisionSecondNull()
    {
        var c = new Collision(5L, Vector.KILO_DOWN);
        assertSame(c, Collision.getEarliestCollision(c, null));
    }

    @Test
    public void getEarliestCollisionPicksLowerTime()
    {
        var c1 = new Collision(3L, Vector.KILO_DOWN);
        var c2 = new Collision(7L, Vector.KILO_UP);
        assertSame(c1, Collision.getEarliestCollision(c1, c2));
    }

    @Test
    public void getEarliestCollisionPicksSecondWhenSmaller()
    {
        var c1 = new Collision(10L, Vector.KILO_DOWN);
        var c2 = new Collision(2L, Vector.KILO_UP);
        assertSame(c2, Collision.getEarliestCollision(c1, c2));
    }

    @Test
    public void getEarliestCollisionEqualTimePrefersFirst()
    {
        var c1 = new Collision(5L, Vector.KILO_DOWN);
        var c2 = new Collision(5L, Vector.KILO_UP);
        assertSame(c1, Collision.getEarliestCollision(c1, c2));
    }

    // --- BrickCollision ---

    @Test
    public void brickCollisionThrowsOnNullBrick()
    {
        assertThrows(IllegalArgumentException.class,
                () -> new BrickCollision(0L, Vector.KILO_DOWN, null));
    }

    @Test
    public void brickCollisionStoresTime()
    {
        var brick = emptyGrid().addStandardBrick(new Point(0, 0));
        assertEquals(77L, new BrickCollision(77L, Vector.KILO_DOWN, brick).getMillisecondsUntilCollision());
    }

    // --- BrickGrid ---

    @Test
    public void brickGridThrowsOnZeroColumns()
    {
        assertThrows(Exception.class, () -> new BrickGrid(0, 4, 100, 30));
    }

    @Test
    public void brickGridThrowsOnZeroRows()
    {
        assertThrows(Exception.class, () -> new BrickGrid(4, 0, 100, 30));
    }

    @Test
    public void brickGridThrowsOnZeroBrickWidth()
    {
        assertThrows(IllegalArgumentException.class, () -> new BrickGrid(4, 4, 0, 30));
    }

    @Test
    public void brickGridThrowsOnZeroBrickHeight()
    {
        assertThrows(IllegalArgumentException.class, () -> new BrickGrid(4, 4, 100, 0));
    }

    @Test
    public void brickGridGetWidthIsColumnCountTimesBrickWidth()
    {
        var grid = new BrickGrid(5, 3, 80, 25);
        assertEquals(5 * 80, grid.getWidth());
    }

    @Test
    public void brickGridGetHeightIsRowCountTimesBrickHeight()
    {
        var grid = new BrickGrid(5, 3, 80, 25);
        assertEquals(3 * 25, grid.getHeight());
    }

    @Test
    public void brickGridInitiallyHasNoBricks()
    {
        assertTrue(emptyGrid().isEmpty());
    }

    @Test
    public void brickGridIsNotEmptyAfterAddingBrick()
    {
        var grid = emptyGrid();
        grid.addStandardBrick(new Point(0, 0));
        assertFalse(grid.isEmpty());
    }

    @Test
    public void brickGridGetBrickAtReturnsNullForEmptyCell()
    {
        assertNull(emptyGrid().getBrickAt(new Point(0, 0)));
    }

    @Test
    public void brickGridContainsBrickAtReturnsFalseOutsideBounds()
    {
        assertFalse(emptyGrid().containsBrickAt(new Point(99, 99)));
    }

    @Test
    public void brickGridRemoveBrickMakesCellEmpty()
    {
        var grid = emptyGrid();
        var pos = new Point(0, 0);
        var brick = grid.addStandardBrick(pos);
        grid.removeBrick(brick);
        assertFalse(grid.containsBrickAt(pos));
    }

    @Test
    public void brickGridGetBricksReflectsAllAddedBricks()
    {
        var grid = emptyGrid();
        grid.addStandardBrick(new Point(0, 0));
        grid.addStandardBrick(new Point(1, 1));
        assertEquals(2, grid.getBricks().size());
    }

    @Test
    public void brickGridGetBrickRectangleHasCorrectPosition()
    {
        var grid = new BrickGrid(5, 5, 80, 25);
        var rect = grid.getBrickRectangle(new Point(2, 3));
        assertEquals(2 * 80, rect.getLeft());
        assertEquals(3 * 25, rect.getTop());
    }

    @Test
    public void brickGridGetBrickRectangleHasCorrectDimensions()
    {
        var grid = new BrickGrid(5, 5, 80, 25);
        var rect = grid.getBrickRectangle(new Point(0, 0));
        assertEquals(80, rect.getWidth());
        assertEquals(25, rect.getHeight());
    }

    // --- Ball ---

    @Test
    public void ballConstructorThrowsOnNullAllowedArea()
    {
        assertThrows(IllegalArgumentException.class,
                () -> new Ball(null, new Circle(new Point(0, 0), 5), new Vector(1, 0), new StandardBehavior()));
    }

    @Test
    public void ballConstructorThrowsOnNullGeometry()
    {
        assertThrows(IllegalArgumentException.class,
                () -> new Ball(new Rectangle(0, 0, 500, 500), null, new Vector(1, 0), new StandardBehavior()));
    }

    @Test
    public void ballConstructorThrowsOnNullVelocity()
    {
        assertThrows(IllegalArgumentException.class,
                () -> new Ball(new Rectangle(0, 0, 500, 500),
                        new Circle(new Point(100, 100), 5), null, new StandardBehavior()));
    }

    @Test
    public void ballConstructorThrowsOnNullBehavior()
    {
        assertThrows(IllegalArgumentException.class,
                () -> new Ball(new Rectangle(0, 0, 500, 500),
                        new Circle(new Point(100, 100), 5), new Vector(1, 0), null));
    }

    @Test
    public void ballGetCenterMatchesGeometryCenter()
    {
        var state = freshState();
        var ball = addCentreBall(state);
        assertEquals(ball.getGeometry().getCenter(), ball.getCenter());
    }

    @Test
    public void ballComputeDestinationDoesNotMutateBall()
    {
        var ball = addCentreBall(freshState());
        var before = ball.getGeometry();
        ball.computeDestination(100);
        assertEquals(before, ball.getGeometry());
    }

    @Test
    public void ballSetBehaviorReplacesOldBehavior()
    {
        var ball = addCentreBall(freshState());
        var b = new StrongBallBehavior();
        ball.setBehavior(b);
        assertSame(b, ball.getBehavior());
    }

    @Test
    public void ballSpeedUpDoesNotExceedMax()
    {
        var state = freshState();
        var centre = state.getBoundingRectangle().getCenter();
        // squaredLength of (100,0) = 10000 = MAXIMUM_SPEEDUP_SQUARED_SPEED
        var ball = state.addBall(new Circle(centre, 5), new Vector(100, 0), new StandardBehavior());
        var before = ball.getVelocity();
        ball.speedUp();
        assertEquals(before, ball.getVelocity());
    }

    @Test
    public void ballSlowDownDoesNotGoBelowMin()
    {
        var state = freshState();
        var centre = state.getBoundingRectangle().getCenter();
        // squaredLength of (5,0) = 25 = MINIMUM_SLOWDOWN_SQUARED_SPEED
        var ball = state.addBall(new Circle(centre, 5), new Vector(5, 0), new StandardBehavior());
        var before = ball.getVelocity();
        ball.slowDown();
        assertEquals(before, ball.getVelocity());
    }

    // --- BreakoutState ---

    @Test
    public void breakoutStateThrowsOnNullGrid()
    {
        assertThrows(IllegalArgumentException.class,
                () -> new BreakoutState(null, 100, 10, 70));
    }

    @Test
    public void breakoutStateThrowsOnNegativeHp()
    {
        assertThrows(IllegalArgumentException.class,
                () -> new BreakoutState(emptyGrid(), 100, 10, -1));
    }

    @Test
    public void breakoutStateInitialBallsEmpty()
    {
        assertTrue(freshState().getBalls().isEmpty());
    }

    @Test
    public void breakoutStateInitialHpsMatchesConstructorArgument()
    {
        assertEquals(42, new BreakoutState(emptyGrid(), 100, 10, 42).getHps());
    }

    @Test
    public void breakoutStateLose1LifeDecrementsHpsByOne()
    {
        var state = new BreakoutState(emptyGrid(), 100, 10, 5);
        state.lose1Life();
        assertEquals(4, state.getHps());
    }

    @Test
    public void breakoutStateLose1LifeDoesNotGoBelowZero()
    {
        var state = new BreakoutState(emptyGrid(), 100, 10, 0);
        state.lose1Life();
        assertEquals(0, state.getHps());
    }

    @Test
    public void breakoutStateIsGameLostWhenNoBalls()
    {
        assertTrue(freshState().isGameLost());
    }

    @Test
    public void breakoutStateIsGameLostWhenHpsZero()
    {
        var state = new BreakoutState(emptyGrid(), 100, 10, 0);
        addCentreBall(state);
        assertTrue(state.isGameLost());
    }

    @Test
    public void breakoutStateIsNotGameLostWithBallAndPositiveHps()
    {
        var state = new BreakoutState(emptyGrid(), 100, 10, 5);
        addCentreBall(state);
        assertFalse(state.isGameLost());
    }

    @Test
    public void breakoutStateIsGameWonWhenOnlyIndestructibleBricksRemain()
    {
        var state = freshState();
        addCentreBall(state);
        state.getBrickGrid().addSpikeyBrick(new Point(0, 0));
        assertTrue(state.isGameWon());
    }

    @Test
    public void breakoutStateIsNotGameWonWithDestructibleBrick()
    {
        var state = freshState();
        addCentreBall(state);
        state.getBrickGrid().addStandardBrick(new Point(0, 0));
        assertFalse(state.isGameWon());
    }

    @Test
    public void breakoutStateIsGameWonWhenNoBricksAtAll()
    {
        var state = freshState();
        addCentreBall(state);
        assertTrue(state.isGameWon());
    }

    @Test
    public void breakoutStateIsGameOverWhenLost()
    {
        assertTrue(freshState().isGameOver());
    }

    @Test
    public void breakoutStateRemoveBallRemovesBallFromList()
    {
        var state = freshState();
        var ball = addCentreBall(state);
        state.removeBall(ball);
        assertFalse(state.getBalls().contains(ball));
    }

    @Test
    public void breakoutStateAddBallIncreasesCount()
    {
        var state = freshState();
        assertEquals(0, state.getBalls().size());
        addCentreBall(state);
        assertEquals(1, state.getBalls().size());
    }

    @Test
    public void breakoutStateGetBallsReturnsCopy()
    {
        var state = freshState();
        addCentreBall(state);
        assertNotSame(state.getBalls(), state.getBalls());
    }

    @Test
    public void breakoutStateGetWallsReturnsCopy()
    {
        assertNotSame(freshState().getWalls(), freshState().getWalls());
    }

    @Test
    public void breakoutStateHasThreeWalls()
    {
        assertEquals(3, freshState().getWalls().size());
    }

    // --- StandardBrick ---

    @Test
    public void standardBrickHitRemovesBrickFromGrid()
    {
        var state = freshState();
        var ball = addCentreBall(state);
        var pos = new Point(0, 0);
        var brick = state.getBrickGrid().addStandardBrick(pos);
        brick.hit(state, ball);
        assertFalse(state.getBrickGrid().containsBrickAt(pos));
    }

    @Test
    public void standardBrickIsNotIndestructible()
    {
        assertFalse(emptyGrid().addStandardBrick(new Point(0, 0)).isIndestructible());
    }

    // --- SpikeyBrick ---

    @Test
    public void spikeyBrickIsIndestructible()
    {
        assertTrue(emptyGrid().addSpikeyBrick(new Point(0, 0)).isIndestructible());
    }

    @Test
    public void spikeyBrickHitDecreasesHpsByOne()
    {
        var state = freshState();
        var ball = addCentreBall(state);
        var brick = state.getBrickGrid().addSpikeyBrick(new Point(0, 0));
        int before = state.getHps();
        brick.hit(state, ball);
        assertEquals(before - 1, state.getHps());
    }

    @Test
    public void spikeyBrickHitDoesNotRemoveBrickFromGrid()
    {
        var state = freshState();
        var ball = addCentreBall(state);
        var pos = new Point(0, 0);
        state.getBrickGrid().addSpikeyBrick(pos);
        state.getBrickGrid().getBrickAt(pos).hit(state, ball);
        assertTrue(state.getBrickGrid().containsBrickAt(pos));
    }

    @Test
    public void spikeyBrickStrongHitReturnsTrueAndDecreasesHp()
    {
        var state = freshState();
        var ball = addCentreBall(state);
        var brick = state.getBrickGrid().addSpikeyBrick(new Point(0, 0));
        int before = state.getHps();
        boolean survived = brick.strongHit(state, ball);
        assertTrue(survived);
        assertEquals(before - 1, state.getHps());
    }

    // --- ShrinkPaddleBrick ---

    @Test
    public void shrinkPaddleBrickHitShrinksPaddle()
    {
        var state = freshState();
        var ball = addCentreBall(state);
        long widthBefore = state.getPaddle().getWidth();
        state.getBrickGrid().addShrinkPaddleBrick(new Point(0, 0)).hit(state, ball);
        assertTrue(state.getPaddle().getWidth() < widthBefore);
    }

    @Test
    public void shrinkPaddleBrickHitRemovesBrickFromGrid()
    {
        var state = freshState();
        var ball = addCentreBall(state);
        var pos = new Point(0, 0);
        state.getBrickGrid().addShrinkPaddleBrick(pos).hit(state, ball);
        assertFalse(state.getBrickGrid().containsBrickAt(pos));
    }

    // --- InvertPaddleBrick ---

    @Test
    public void invertPaddleBrickHitInvertsPaddle()
    {
        var state = freshState();
        var ball = addCentreBall(state);
        assertFalse(state.getPaddle().isInverted());
        state.getBrickGrid().addInvertPaddleBrick(new Point(0, 0)).hit(state, ball);
        assertTrue(state.getPaddle().isInverted());
    }

    @Test
    public void invertPaddleBrickHitRemovesBrickFromGrid()
    {
        var state = freshState();
        var ball = addCentreBall(state);
        var pos = new Point(0, 0);
        state.getBrickGrid().addInvertPaddleBrick(pos).hit(state, ball);
        assertFalse(state.getBrickGrid().containsBrickAt(pos));
    }

    // --- BlinkingBallBrick ---

    @Test
    public void blinkingBallBrickHitGivesBlinkingBehavior()
    {
        var state = freshState();
        var ball = addCentreBall(state);
        state.getBrickGrid().addBlinkingBallBrick(new Point(0, 0)).hit(state, ball);
        assertTrue(ball.getBehavior() instanceof BlinkingBallBehavior);
    }

    @Test
    public void blinkingBallBrickHitRemovesBrickFromGrid()
    {
        var state = freshState();
        var ball = addCentreBall(state);
        var pos = new Point(0, 0);
        state.getBrickGrid().addBlinkingBallBrick(pos).hit(state, ball);
        assertFalse(state.getBrickGrid().containsBrickAt(pos));
    }

    // --- StrengtheningBrick ---

    @Test
    public void strengtheningBrickHitGivesStrongBehavior()
    {
        var state = freshState();
        var ball = addCentreBall(state);
        state.getBrickGrid().addStrengtheningBrick(new Point(0, 0)).hit(state, ball);
        assertTrue(ball.getBehavior() instanceof StrongBallBehavior);
    }

    @Test
    public void strengtheningBrickHitRemovesBrickFromGrid()
    {
        var state = freshState();
        var ball = addCentreBall(state);
        var pos = new Point(0, 0);
        state.getBrickGrid().addStrengtheningBrick(pos).hit(state, ball);
        assertFalse(state.getBrickGrid().containsBrickAt(pos));
    }

    // --- SpeedUpBrick ---

    @Test
    public void speedUpBrickHitRemovesBrickFromGrid()
    {
        var state = freshState();
        var ball = addCentreBall(state);
        var pos = new Point(0, 0);
        state.getBrickGrid().addSpeedUpBrick(pos).hit(state, ball);
        assertFalse(state.getBrickGrid().containsBrickAt(pos));
    }

    @Test
    public void speedUpBrickHitDoesNotReduceSpeed()
    {
        var state = freshState();
        var centre = state.getBoundingRectangle().getCenter();
        var ball = state.addBall(new Circle(centre, 5), new Vector(10, 0), new StandardBehavior());
        long sqBefore = ball.getVelocity().getSquaredLength();
        state.getBrickGrid().addSpeedUpBrick(new Point(0, 0)).hit(state, ball);
        assertTrue(ball.getVelocity().getSquaredLength() >= sqBefore);
    }

    // --- SpawnBallBrick ---

    @Test
    public void spawnBallBrickHitRemovesBrickFromGrid()
    {
        var state = freshState();
        var ball = addCentreBall(state);
        var pos = new Point(0, 0);
        state.getBrickGrid().addSpawnBallBrick(pos).hit(state, ball);
        assertFalse(state.getBrickGrid().containsBrickAt(pos));
    }

    @Test
    public void spawnBallBrickHitAddsOneNewBall()
    {
        var state = freshState();
        var ball = addCentreBall(state);
        int before = state.getBalls().size();
        state.getBrickGrid().addSpawnBallBrick(new Point(0, 0)).hit(state, ball);
        assertEquals(before + 1, state.getBalls().size());
    }

    // --- TemporaryBehavior ---

    @Test
    public void temporaryBehaviorTimeLeftDecreases()
    {
        var state = freshState();
        var centre = state.getBoundingRectangle().getCenter();
        var behavior = new StrongBallBehavior();
        state.addBall(new Circle(centre, 5), new Vector(1, -1), behavior);
        int before = behavior.getTimeLeft();
        behavior.update(state, state.getBalls().get(0), 10);
        assertTrue(behavior.getTimeLeft() <= before);
    }

    @Test
    public void temporaryBehaviorRevertsToStandardAfterExpiry()
    {
        var state = freshState();
        var centre = state.getBoundingRectangle().getCenter();
        var behavior = new StrongBallBehavior();
        var ball = state.addBall(new Circle(centre, 5), new Vector(1, -1), behavior);
        behavior.update(state, ball, StrongBallBehavior.DURATION + 1);
        assertFalse(ball.getBehavior() instanceof StrongBallBehavior);
        assertTrue(ball.getBehavior() instanceof StandardBehavior);
    }

    @Test
    public void temporaryBehaviorTimeLeftNeverNegative()
    {
        var state = freshState();
        var centre = state.getBoundingRectangle().getCenter();
        var behavior = new StrongBallBehavior();
        var ball = state.addBall(new Circle(centre, 5), new Vector(1, -1), behavior);
        behavior.update(state, ball, StrongBallBehavior.DURATION + 9999);
        assertEquals(0, behavior.getTimeLeft());
    }

    // --- BlinkingBallBehavior ---

    @Test
    public void blinkingBallBehaviorStartsNormalInvisible()
    {
        // At spawn: activeSince = DURATION - DURATION = 0; aux = (0/900)%2 = 0 → NORMAL_INV
        assertFalse(new BlinkingBallBehavior().isWeakVisible());
    }

    @Test
    public void blinkingBallBehaviorBecomesWeakVisibleAfterFirstBlink()
    {
        // After 900ms: activeSince = 900; aux = (900/900)%2 = 1 → WEAK_VIS
        var state = freshState();
        var centre = state.getBoundingRectangle().getCenter();
        var behavior = new BlinkingBallBehavior();
        var ball = state.addBall(new Circle(centre, 5), new Vector(1, -1), behavior);
        behavior.update(state, ball, 900);
        assertTrue(behavior.isWeakVisible());
    }

    // --- Paddle ---

    @Test
    public void paddleConstructorThrowsOnNullAllowedInterval()
    {
        assertThrows(IllegalArgumentException.class,
                () -> new Paddle(null, new Point(500, 0), 100, 10));
    }

    @Test
    public void paddleConstructorThrowsOnNullTopCenter()
    {
        assertThrows(IllegalArgumentException.class,
                () -> new Paddle(new Interval(0, 1000), null, 100, 10));
    }

    @Test
    public void paddleConstructorThrowsOnZeroHalfWidth()
    {
        assertThrows(IllegalArgumentException.class,
                () -> new Paddle(new Interval(0, 1000), new Point(500, 0), 0, 10));
    }

    @Test
    public void paddleInitiallyStationary()
    {
        var paddle = new Paddle(new Interval(0, 10000), new Point(500, 0), 200, 1);
        assertEquals(PaddleMotionDirection.STATIONARY, paddle.getMotionDirection());
    }

    @Test
    public void paddleInitiallyNotInverted()
    {
        assertFalse(new Paddle(new Interval(0, 10000), new Point(500, 0), 200, 1).isInverted());
    }

    @Test
    public void paddleApplyInvertedMakesIsInvertedTrue()
    {
        var paddle = new Paddle(new Interval(0, 10000), new Point(500, 0), 200, 1);
        paddle.applyInverted();
        assertTrue(paddle.isInverted());
    }

    @Test
    public void paddleShrinkReducesWidth()
    {
        var paddle = new Paddle(new Interval(0, 10000), new Point(500, 0), 200, 1);
        long before = paddle.getWidth();
        paddle.shrink();
        assertTrue(paddle.getWidth() < before);
    }

    @Test
    public void paddleGrowIncreasesWidth()
    {
        var paddle = new Paddle(new Interval(0, 100000), new Point(500, 0), 200, 1);
        long before = paddle.getWidth();
        paddle.grow();
        assertTrue(paddle.getWidth() > before);
    }

    @Test
    public void paddleMoveStaysWithinAllowedInterval()
    {
        var paddle = new Paddle(new Interval(0, 1000), new Point(500, 0), 100, 1);
        paddle.move(10000); // large distance → clamped
        assertTrue(paddle.getTopCenter().x() + paddle.getHalfWidth() <= 1000);
    }

    @Test
    public void paddleSetMotionDirectionUpdatesDirection()
    {
        var paddle = new Paddle(new Interval(0, 1000), new Point(500, 0), 100, 1);
        paddle.setMotionDirection(PaddleMotionDirection.LEFT);
        assertEquals(PaddleMotionDirection.LEFT, paddle.getMotionDirection());
    }

    @Test
    public void paddleGeometryHasCorrectLeftCoordinate()
    {
        var paddle = new Paddle(new Interval(0, 10000), new Point(500, 0), 100, 1);
        assertEquals(500 - 100, paddle.getGeometry().getLeft());
    }

    @Test
    public void paddleGeometryHasCorrectWidth()
    {
        var paddle = new Paddle(new Interval(0, 10000), new Point(500, 0), 100, 1);
        assertEquals(200, paddle.getGeometry().getWidth());
    }

    @Test
    public void paddleGeometryHasCorrectHeight()
    {
        var paddle = new Paddle(new Interval(0, 10000), new Point(500, 0), 100, 1);
        assertEquals(Paddle.HEIGHT, paddle.getGeometry().getHeight());
    }
}
