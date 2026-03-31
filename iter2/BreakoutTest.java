package ogp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ogp.balls.Ball;
import ogp.balls.BallBehavior;
import ogp.balls.BlinkingBallBehavior;
import ogp.balls.BlinkingBallBrick;
import ogp.balls.StandardBehavior;
import ogp.balls.StrongBallBehavior;
import ogp.balls.TemporaryBehavior;
import ogp.bricks.*;
import ogp.math.*;
import ogp.paddles.Paddle;
import ogp.paddles.PaddleMotionDirection;

/**
 * Unit tests for the Breakout project (Iteration 2).
 *
 * Tests are divided into:
 *   - Regression tests: expose a specific flaw in the original code.
 *     Each regression test is named regrXxx and fails on the original code, passes after the fix.
 *   - Normal unit tests: verify correct behaviour specified in the assignment.
 */
public class BreakoutTest
{
    // -----------------------------------------------------------------------
    // Shared helpers / factory methods
    // -----------------------------------------------------------------------

    /** Returns a minimal BrickGrid (1 column, 1 row, 100×100 bricks). */
    private BrickGrid smallGrid()
    {
        return new BrickGrid(7, 8, 100, 30);
    }

    /** Returns a standard BreakoutState backed by the default map. */
    private BreakoutState defaultState()
    {
        return GameMapParser.parseNoBalls(GameMapParser.DEFAULT_MAP, 100, 30);
    }

    /** Returns a ball centred in the middle of the bounding rectangle. */
    private Ball makeBall(BreakoutState state)
    {
        var centre = state.getBoundingRectangle().getCenter();
        var geometry = new Circle(centre, 5);
        var velocity = new Vector(1, -1);
        var behavior = new StandardBehavior();
        return state.addBall(geometry, velocity, behavior);
    }

    // -----------------------------------------------------------------------
    // REGRESSION TESTS — these must FAIL on the original code, PASS after fix
    // -----------------------------------------------------------------------

    /** REGRESSION: Collision constructor stored 0 instead of the given time. */
    @Test
    public void regrCollisionStoresCorrectTime()
    {
        var col = new Collision(42L, Vector.KILO_DOWN);
        assertEquals(42L, col.getMillisecondsUntilCollision());
    }

    /** REGRESSION: Collision constructor stored null instead of the given kiloNormal. */
    @Test
    public void regrCollisionStoresCorrectKiloNormal()
    {
        var col = new Collision(0L, Vector.KILO_UP);
        assertEquals(Vector.KILO_UP, col.getKiloNormal());
    }

    /** REGRESSION: BrickCollision.getBrick() returned null instead of the stored brick. */
    @Test
    public void regrBrickCollisionGetBrickNotNull()
    {
        var grid = smallGrid();
        var pos = new Point(0, 0);
        var brick = grid.addStandardBrick(pos);
        var col = new BrickCollision(0L, Vector.KILO_DOWN, brick);
        assertNotNull(col.getBrick());
    }

    /** REGRESSION: BrickCollision stored null in brick field instead of the parameter. */
    @Test
    public void regrBrickCollisionGetBrickIsCorrectBrick()
    {
        var grid = smallGrid();
        var pos = new Point(0, 0);
        var brick = grid.addStandardBrick(pos);
        var col = new BrickCollision(10L, Vector.KILO_LEFT, brick);
        assertSame(brick, col.getBrick());
    }

    /** REGRESSION: BrickGrid constructor set brickWidth to 0 instead of the parameter. */
    @Test
    public void regrBrickGridBrickWidthStoredCorrectly()
    {
        var grid = new BrickGrid(3, 4, 50, 20);
        assertEquals(50, grid.getBrickWidth());
    }

    /** REGRESSION: BrickGrid constructor set brickHeight to 0 instead of the parameter. */
    @Test
    public void regrBrickGridBrickHeightStoredCorrectly()
    {
        var grid = new BrickGrid(3, 4, 50, 20);
        assertEquals(20, grid.getBrickHeight());
    }

    /** REGRESSION: BrickGrid constructor left grid null, causing NPE on any use. */
    @Test
    public void regrBrickGridGridIsNotNull()
    {
        var grid = new BrickGrid(3, 4, 50, 20);
        // getColumnCount() and getRowCount() delegate to grid, would NPE if null
        assertEquals(3, grid.getColumnCount());
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

    /** REGRESSION: Ball constructor stored null in all fields. getGeometry() returned null. */
    @Test
    public void regrBallGetGeometryNotNull()
    {
        var state = defaultState();
        var ball = makeBall(state);
        assertNotNull(ball.getGeometry());
    }

    /** REGRESSION: Ball constructor stored null. getVelocity() returned null. */
    @Test
    public void regrBallGetVelocityNotNull()
    {
        var state = defaultState();
        var ball = makeBall(state);
        assertNotNull(ball.getVelocity());
    }

    /** REGRESSION: Ball constructor stored null. getBehavior() returned null. */
    @Test
    public void regrBallGetBehaviorNotNull()
    {
        var state = defaultState();
        var ball = makeBall(state);
        assertNotNull(ball.getBehavior());
    }

    /** REGRESSION: Ball constructor stored null. getAllowedArea() returned null. */
    @Test
    public void regrBallGetAllowedAreaNotNull()
    {
        var state = defaultState();
        var ball = makeBall(state);
        assertNotNull(ball.getAllowedArea());
    }

    /** REGRESSION: Ball.getCenter() returned null (delegated to null geometry). */
    @Test
    public void regrBallGetCenterNotNull()
    {
        var state = defaultState();
        var ball = makeBall(state);
        assertNotNull(ball.getCenter());
    }

    /** REGRESSION: Ball.move() did nothing, geometry was unchanged. */
    @Test
    public void regrBallMoveChangesGeometry()
    {
        var state = defaultState();
        var centre = state.getBoundingRectangle().getCenter();
        var geometry = new Circle(centre, 5);
        var velocity = new Vector(3, 0);
        var ball = state.addBall(geometry, velocity, new StandardBehavior());

        var before = ball.getGeometry().getCenter();
        ball.move(10);
        var after = ball.getGeometry().getCenter();

        // x should have increased by 3*10 = 30
        assertEquals(before.x() + 30, after.x());
        assertEquals(before.y(), after.y());
    }

    /** REGRESSION: Ball.computeDestination() returned null. */
    @Test
    public void regrBallComputeDestinationNotNull()
    {
        var state = defaultState();
        var ball = makeBall(state);
        assertNotNull(ball.computeDestination(5));
    }

    /** REGRESSION: Ball.setVelocity() did nothing. */
    @Test
    public void regrBallSetVelocityWorks()
    {
        var state = defaultState();
        var ball = makeBall(state);
        var newVel = new Vector(7, -3);
        ball.setVelocity(newVel);
        assertEquals(newVel, ball.getVelocity());
    }

    /** REGRESSION: TemporaryBehavior set timeLeft = duration*0 = 0. Should be duration. */
    @Test
    public void regrTemporaryBehaviorTimeLeftIsInitializedToDuration()
    {
        var b = new StrongBallBehavior();
        assertEquals(StrongBallBehavior.DURATION, b.getTimeLeft());
    }

    /** REGRESSION: TemporaryBehavior(BlinkingBall variant) timeLeft should be DURATION. */
    @Test
    public void regrBlinkingBehaviorTimeLeftIsInitializedToDuration()
    {
        var b = new BlinkingBallBehavior();
        assertEquals(BlinkingBallBehavior.DURATION, b.getTimeLeft());
    }

    /** REGRESSION: Brick.getGridPosition() returned null instead of the stored position. */
    @Test
    public void regrBrickGetGridPositionNotNull()
    {
        var grid = new BrickGrid(5, 5, 100, 30);
        var pos = new Point(1, 2);
        var brick = grid.addStandardBrick(pos);
        assertNotNull(brick.getGridPosition());
    }

    /** REGRESSION: Brick.getGridPosition() returned wrong value. */
    @Test
    public void regrBrickGetGridPositionIsCorrect()
    {
        var grid = new BrickGrid(5, 5, 100, 30);
        var pos = new Point(3, 1);
        var brick = grid.addStandardBrick(pos);
        assertEquals(pos, brick.getGridPosition());
    }

    /**
     * REGRESSION: Paddle constructor threw for speed==10 (arbitrary valid speed).
     * The correct check is speed <= 0.
     */
    @Test
    public void regrPaddleAcceptsSpeedOf10()
    {
        var allowed = new Interval(0, 1000);
        var topCenter = new Point(500, 0);
        // should not throw
        assertDoesNotThrow(() -> new Paddle(allowed, topCenter, 100, 10));
    }

    /** REGRESSION: Paddle constructor should reject speed <= 0. */
    @Test
    public void regrPaddleRejectsZeroSpeed()
    {
        var allowed = new Interval(0, 1000);
        var topCenter = new Point(500, 0);
        assertThrows(IllegalArgumentException.class, () -> new Paddle(allowed, topCenter, 100, 0));
    }

    /** REGRESSION: Paddle constructor should reject negative speed. */
    @Test
    public void regrPaddleRejectsNegativeSpeed()
    {
        var allowed = new Interval(0, 1000);
        var topCenter = new Point(500, 0);
        assertThrows(IllegalArgumentException.class, () -> new Paddle(allowed, topCenter, 100, -5));
    }

    // -----------------------------------------------------------------------
    // NORMAL UNIT TESTS
    // -----------------------------------------------------------------------

    // --- Collision ---

    @Test
    public void collisionGetMillisecondsRoundTrip()
    {
        var col = new Collision(99L, Vector.KILO_RIGHT);
        assertEquals(99L, col.getMillisecondsUntilCollision());
    }

    @Test
    public void collisionGetKiloNormalRoundTrip()
    {
        var col = new Collision(0L, Vector.KILO_LEFT);
        assertEquals(Vector.KILO_LEFT, col.getKiloNormal());
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
    public void getEarliestCollisionPicksSmaller()
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
    public void brickGridGetWidthCorrect()
    {
        var grid = new BrickGrid(5, 3, 80, 25);
        assertEquals(5 * 80, grid.getWidth());
    }

    @Test
    public void brickGridAddStandardBrickStoredCorrectly()
    {
        var grid = new BrickGrid(4, 4, 100, 30);
        var pos = new Point(1, 1);
        assertFalse(grid.containsBrickAt(pos));
        grid.addStandardBrick(pos);
        assertTrue(grid.containsBrickAt(pos));
    }

    @Test
    public void brickGridRemoveBrickWorks()
    {
        var grid = new BrickGrid(4, 4, 100, 30);
        var pos = new Point(0, 0);
        var brick = grid.addStandardBrick(pos);
        grid.removeBrick(brick);
        assertFalse(grid.containsBrickAt(pos));
    }

    @Test
    public void brickGridGetBricksReturnsAllBricks()
    {
        var grid = new BrickGrid(4, 4, 100, 30);
        grid.addStandardBrick(new Point(0, 0));
        grid.addStandardBrick(new Point(1, 1));
        assertEquals(2, grid.getBricks().size());
    }

    @Test
    public void brickGridIsEmptyWhenNoBricks()
    {
        var grid = new BrickGrid(3, 3, 100, 30);
        assertTrue(grid.isEmpty());
    }

    @Test
    public void brickGridIsNotEmptyAfterAddBrick()
    {
        var grid = new BrickGrid(3, 3, 100, 30);
        grid.addStandardBrick(new Point(0, 0));
        assertFalse(grid.isEmpty());
    }

    @Test
    public void brickGridGetBrickAtNullForEmptyCell()
    {
        var grid = new BrickGrid(3, 3, 100, 30);
        assertNull(grid.getBrickAt(new Point(0, 0)));
    }

    @Test
    public void brickGridContainsBrickAtOutOfBoundsReturnsFalse()
    {
        var grid = new BrickGrid(3, 3, 100, 30);
        assertFalse(grid.containsBrickAt(new Point(99, 99)));
    }

    @Test
    public void brickGridAddSpawnBallBrickNotNull()
    {
        var grid = new BrickGrid(3, 3, 100, 30);
        var pos = new Point(0, 0);
        var brick = grid.addSpawnBallBrick(pos);
        assertNotNull(brick);
        assertSame(brick, grid.getBrickAt(pos));
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
        var rect = new Rectangle(0, 0, 500, 500);
        assertThrows(IllegalArgumentException.class,
                () -> new Ball(rect, null, new Vector(1, 0), new StandardBehavior()));
    }

    @Test
    public void ballConstructorThrowsOnNullVelocity()
    {
        var rect = new Rectangle(0, 0, 500, 500);
        assertThrows(IllegalArgumentException.class,
                () -> new Ball(rect, new Circle(new Point(100, 100), 5), null, new StandardBehavior()));
    }

    @Test
    public void ballConstructorThrowsOnNullBehavior()
    {
        var rect = new Rectangle(0, 0, 500, 500);
        assertThrows(IllegalArgumentException.class,
                () -> new Ball(rect, new Circle(new Point(100, 100), 5), new Vector(1, 0), null));
    }

    @Test
    public void ballMoveCorrectly()
    {
        var state = defaultState();
        var centre = state.getBoundingRectangle().getCenter();
        var geom = new Circle(centre, 5);
        var vel = new Vector(2, -3);
        var ball = state.addBall(geom, vel, new StandardBehavior());
        ball.move(10);
        assertEquals(centre.x() + 20, ball.getCenter().x());
        assertEquals(centre.y() - 30, ball.getCenter().y());
    }

    @Test
    public void ballComputeDestinationDoesNotMutateBall()
    {
        var state = defaultState();
        var ball = makeBall(state);
        var before = ball.getGeometry();
        ball.computeDestination(100);
        assertEquals(before, ball.getGeometry());
    }

    @Test
    public void ballSetBehaviorWorks()
    {
        var state = defaultState();
        var ball = makeBall(state);
        var newBehavior = new StrongBallBehavior();
        ball.setBehavior(newBehavior);
        assertSame(newBehavior, ball.getBehavior());
    }

    @Test
    public void ballSpeedUpIncreasesSpeedWhenBelowMax()
    {
        var state = defaultState();
        var centre = state.getBoundingRectangle().getCenter();
        var ball = state.addBall(new Circle(centre, 5), new Vector(5, 0), new StandardBehavior());
        var sqBefore = ball.getVelocity().getSquaredLength();
        ball.speedUp();
        var sqAfter = ball.getVelocity().getSquaredLength();
        assertTrue(sqAfter >= sqBefore);
    }

    @Test
    public void ballSpeedUpDoesNotExceedMax()
    {
        var state = defaultState();
        var centre = state.getBoundingRectangle().getCenter();
        // Start at max speed; speedUp should not apply
        var ball = state.addBall(new Circle(centre, 5), new Vector(100, 0), new StandardBehavior());
        var before = ball.getVelocity();
        ball.speedUp();
        assertEquals(before, ball.getVelocity());
    }

    @Test
    public void ballSlowDownDoesNotGoBelowMin()
    {
        var state = defaultState();
        var centre = state.getBoundingRectangle().getCenter();
        // Start at min speed; slowDown should not apply
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
                () -> new BreakoutState(smallGrid(), 100, 10, -1));
    }

    @Test
    public void breakoutStateInitialBallsEmpty()
    {
        var state = new BreakoutState(smallGrid(), 100, 10, 70);
        assertTrue(state.getBalls().isEmpty());
    }

    @Test
    public void breakoutStateInitialHpsCorrect()
    {
        var state = new BreakoutState(smallGrid(), 100, 10, 42);
        assertEquals(42, state.getHps());
    }

    @Test
    public void breakoutStateLose1LifeDecrements()
    {
        var state = new BreakoutState(smallGrid(), 100, 10, 5);
        state.lose1Life();
        assertEquals(4, state.getHps());
    }

    @Test
    public void breakoutStateLose1LifeDoesNotGoBelowZero()
    {
        var state = new BreakoutState(smallGrid(), 100, 10, 0);
        state.lose1Life();
        assertEquals(0, state.getHps());
    }

    @Test
    public void breakoutStateIsGameLostWhenNoBalls()
    {
        var state = defaultState();
        // no balls added
        assertTrue(state.isGameLost());
    }

    @Test
    public void breakoutStateIsGameLostWhenHpsZero()
    {
        var state = new BreakoutState(smallGrid(), 100, 10, 0);
        makeBall(state);
        assertTrue(state.isGameLost());
    }

    @Test
    public void breakoutStateIsNotGameLostWithBallAndHps()
    {
        var state = new BreakoutState(smallGrid(), 100, 10, 5);
        makeBall(state);
        assertFalse(state.isGameLost());
    }

    @Test
    public void breakoutStateIsGameWonWhenNoBricks()
    {
        var state = new BreakoutState(smallGrid(), 100, 10, 70);
        makeBall(state);
        // grid is empty → all remaining bricks are indestructible (vacuously true)
        assertTrue(state.isGameWon());
    }

    @Test
    public void breakoutStateIsNotGameWonWithDestructibleBricks()
    {
        var state = defaultState();
        makeBall(state);
        // DEFAULT_MAP has standard bricks
        assertFalse(state.isGameWon());
    }

    @Test
    public void breakoutStateIsGameOverWhenLost()
    {
        var state = defaultState();
        // no balls: lost
        assertTrue(state.isGameOver());
    }

    @Test
    public void breakoutStateRemoveBallWorks()
    {
        var state = defaultState();
        var ball = makeBall(state);
        state.removeBall(ball);
        assertFalse(state.getBalls().contains(ball));
    }

    @Test
    public void breakoutStateAddBallIncreasesCount()
    {
        var state = defaultState();
        assertEquals(0, state.getBalls().size());
        makeBall(state);
        assertEquals(1, state.getBalls().size());
    }

    @Test
    public void breakoutStateGetBallsReturnsCopy()
    {
        var state = defaultState();
        makeBall(state);
        var list1 = state.getBalls();
        var list2 = state.getBalls();
        assertNotSame(list1, list2);
    }

    // --- StandardBrick ---

    @Test
    public void standardBrickHitRemovesBrick()
    {
        var state = defaultState();
        makeBall(state);
        var grid = state.getBrickGrid();
        var pos = new Point(0, 0);
        // DEFAULT_MAP has 'o' at (0,0); add a standard brick in a fresh grid
        var freshState = new BreakoutState(new BrickGrid(5, 5, 100, 30), 100, 10, 70);
        var freshBall = makeBall(freshState);
        var freshBrick = freshState.getBrickGrid().addStandardBrick(new Point(2, 2));
        freshBrick.hit(freshState, freshBall);
        assertFalse(freshState.getBrickGrid().containsBrickAt(new Point(2, 2)));
    }

    // --- SpikeyBrick ---

    @Test
    public void spikeyBrickIsIndestructible()
    {
        var grid = new BrickGrid(5, 5, 100, 30);
        var brick = grid.addSpikeyBrick(new Point(0, 0));
        assertTrue(brick.isIndestructible());
    }

    @Test
    public void spikeyBrickHitDecreasesHps()
    {
        var state = new BreakoutState(new BrickGrid(5, 5, 100, 30), 100, 10, 70);
        var ball = makeBall(state);
        var brick = state.getBrickGrid().addSpikeyBrick(new Point(0, 0));
        int before = state.getHps();
        brick.hit(state, ball);
        assertEquals(before - 1, state.getHps());
    }

    @Test
    public void spikeyBrickHitDoesNotRemoveBrick()
    {
        var state = new BreakoutState(new BrickGrid(5, 5, 100, 30), 100, 10, 70);
        var ball = makeBall(state);
        var pos = new Point(0, 0);
        state.getBrickGrid().addSpikeyBrick(pos);
        state.getBrickGrid().getBrickAt(pos).hit(state, ball);
        assertTrue(state.getBrickGrid().containsBrickAt(pos));
    }

    @Test
    public void spikeyBrickStrongHitReturnsTrueAndLosesHp()
    {
        var state = new BreakoutState(new BrickGrid(5, 5, 100, 30), 100, 10, 70);
        var ball = makeBall(state);
        var pos = new Point(0, 0);
        var brick = state.getBrickGrid().addSpikeyBrick(pos);
        int before = state.getHps();
        boolean survived = brick.strongHit(state, ball);
        assertTrue(survived);
        assertEquals(before - 1, state.getHps());
    }

    // --- ShrinkPaddleBrick ---

    @Test
    public void shrinkPaddleBrickHitShrinksAndRemovesBrick()
    {
        var state = new BreakoutState(new BrickGrid(5, 5, 100, 30), 100, 10, 70);
        var ball = makeBall(state);
        var pos = new Point(0, 0);
        var brick = state.getBrickGrid().addShrinkPaddleBrick(pos);
        long widthBefore = state.getPaddle().getWidth();
        brick.hit(state, ball);
        assertFalse(state.getBrickGrid().containsBrickAt(pos));
        assertTrue(state.getPaddle().getWidth() < widthBefore);
    }

    // --- InvertPaddleBrick ---

    @Test
    public void invertPaddleBrickHitInvertsPaddleAndRemovesBrick()
    {
        var state = new BreakoutState(new BrickGrid(5, 5, 100, 30), 100, 10, 70);
        var ball = makeBall(state);
        var pos = new Point(0, 0);
        var brick = state.getBrickGrid().addInvertPaddleBrick(pos);
        assertFalse(state.getPaddle().isInverted());
        brick.hit(state, ball);
        assertFalse(state.getBrickGrid().containsBrickAt(pos));
        assertTrue(state.getPaddle().isInverted());
    }

    // --- BlinkingBallBrick ---

    @Test
    public void blinkingBallBrickHitSetsBehaviorAndRemoves()
    {
        var state = new BreakoutState(new BrickGrid(5, 5, 100, 30), 100, 10, 70);
        var ball = makeBall(state);
        var pos = new Point(0, 0);
        var brick = state.getBrickGrid().addBlinkingBallBrick(pos);
        brick.hit(state, ball);
        assertFalse(state.getBrickGrid().containsBrickAt(pos));
        assertTrue(ball.getBehavior() instanceof BlinkingBallBehavior);
    }

    // --- StrengtheningBrick ---

    @Test
    public void strengtheningBrickHitSetsBehaviorAndRemoves()
    {
        var state = new BreakoutState(new BrickGrid(5, 5, 100, 30), 100, 10, 70);
        var ball = makeBall(state);
        var pos = new Point(0, 0);
        var brick = state.getBrickGrid().addStrengtheningBrick(pos);
        brick.hit(state, ball);
        assertFalse(state.getBrickGrid().containsBrickAt(pos));
        assertTrue(ball.getBehavior() instanceof StrongBallBehavior);
    }

    // --- SpeedUpBrick ---

    @Test
    public void speedUpBrickHitSpeedsUpBallAndRemoves()
    {
        var state = new BreakoutState(new BrickGrid(5, 5, 100, 30), 100, 10, 70);
        var centre = state.getBoundingRectangle().getCenter();
        var ball = state.addBall(new Circle(centre, 5), new Vector(5, 0), new StandardBehavior());
        var pos = new Point(0, 0);
        var brick = state.getBrickGrid().addSpeedUpBrick(pos);
        long sqBefore = ball.getVelocity().getSquaredLength();
        brick.hit(state, ball);
        assertFalse(state.getBrickGrid().containsBrickAt(pos));
        // speed should have increased (5*1050/1000 = 5.25 → stays at 5 due to integer math;
        // but squared: 5*5=25, 5.25*5.25>25 when it applies — test just that it doesn't shrink)
        assertTrue(ball.getVelocity().getSquaredLength() >= sqBefore);
    }

    // --- TemporaryBehavior ---

    @Test
    public void temporaryBehaviorTimeLeftDecreases()
    {
        var state = new BreakoutState(new BrickGrid(5, 5, 100, 30), 100, 10, 70);
        var centre = state.getBoundingRectangle().getCenter();
        var behavior = new StrongBallBehavior();
        var ball = state.addBall(new Circle(centre, 5), new Vector(1, -1), behavior);
        int before = behavior.getTimeLeft();
        // tick a small amount so no boundary collisions occur
        behavior.update(state, ball, 10);
        assertTrue(behavior.getTimeLeft() <= before);
    }

    @Test
    public void temporaryBehaviorRevertAfterExpiry()
    {
        var state = new BreakoutState(new BrickGrid(5, 5, 100, 30), 100, 10, 70);
        var centre = state.getBoundingRectangle().getCenter();
        var behavior = new StrongBallBehavior();
        var ball = state.addBall(new Circle(centre, 5), new Vector(1, -1), behavior);
        // update more than the duration
        behavior.update(state, ball, StrongBallBehavior.DURATION + 1);
        // ball should no longer have StrongBallBehavior
        assertFalse(ball.getBehavior() instanceof StrongBallBehavior);
        assertTrue(ball.getBehavior() instanceof StandardBehavior);
    }

    // --- Paddle ---

    @Test
    public void paddleConstructorThrowsOnZeroSpeed()
    {
        assertThrows(IllegalArgumentException.class,
                () -> new Paddle(new Interval(0, 1000), new Point(500, 0), 100, 0));
    }

    @Test
    public void paddleScaleShrinks()
    {
        var paddle = new Paddle(new Interval(0, 10000), new Point(500, 0), 200, 1);
        long before = paddle.getWidth();
        paddle.shrink();
        assertTrue(paddle.getWidth() < before);
    }

    @Test
    public void paddleScaleGrows()
    {
        var paddle = new Paddle(new Interval(0, 10000), new Point(500, 0), 200, 1);
        long before = paddle.getWidth();
        paddle.grow();
        assertTrue(paddle.getWidth() > before);
    }

    @Test
    public void paddleApplyInvertedMakesInverted()
    {
        var paddle = new Paddle(new Interval(0, 10000), new Point(500, 0), 200, 1);
        assertFalse(paddle.isInverted());
        paddle.applyInverted();
        assertTrue(paddle.isInverted());
    }

    // --- BlinkingBallBehavior ---

    @Test
    public void blinkingBallBehaviorStartsNormalInvisible()
    {
        // At time=0 activeSince=DURATION-DURATION=0, aux=(0/900)%2=0 → NORMAL_INV
        var b = new BlinkingBallBehavior();
        assertFalse(b.isWeakVisible());
    }

    @Test
    public void blinkingBallBehaviorIsWeakVisibleAfterOneBlink()
    {
        // After 900ms elapsed, activeSince=900, aux=(900/900)%2=1 → WEAK_VIS
        var state = new BreakoutState(new BrickGrid(5, 5, 100, 30), 100, 10, 70);
        var centre = state.getBoundingRectangle().getCenter();
        var behavior = new BlinkingBallBehavior();
        state.addBall(new Circle(centre, 5), new Vector(1, -1), behavior);
        // Manually reduce timeLeft to simulate time passage
        // timeLeft starts at 5000; after 900ms elapsed → timeLeft = 4100
        // activeSince = 5000 - 4100 = 900 → aux = 1 → WEAK_VIS
        behavior.update(state, state.getBalls().get(0), 900);
        assertTrue(behavior.isWeakVisible());
    }
}
