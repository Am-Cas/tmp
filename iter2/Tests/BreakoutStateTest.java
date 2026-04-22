package ogp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ogp.balls.Ball;
import ogp.balls.StandardBehavior;
import ogp.math.Circle;
import ogp.math.Point;
import ogp.math.Vector;

public class BreakoutStateTest {

    private BrickGrid emptyGrid;
    private BrickGrid gridWithStandard;
    private BrickGrid gridWithSpikey;

    @BeforeEach
    void setUp() {
        emptyGrid        = new BrickGrid(7, 8, 100, 30);
        gridWithStandard = new BrickGrid(7, 8, 100, 30);
        gridWithSpikey   = new BrickGrid(7, 8, 100, 30);

        gridWithStandard.addStandardBrick(new Point(0, 0));
        gridWithSpikey.addSpikeyBrick(new Point(0, 0));
    }

    // ── constructor: null brickGrid ───────────────────────────────────────────

    @Test
    void constructor_nullBrickGrid_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new BreakoutState(null, 100, 10, 3));
    }

    // ── constructor: negative initHP ─────────────────────────────────────────

    @Test
    void constructor_negativeInitHP_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new BreakoutState(emptyGrid, 100, 10, -1));
    }

    // ── constructor postconditions ────────────────────────────────────────────

    @Test
    void constructor_brickGridStoredCorrectly() {
        BreakoutState state = new BreakoutState(emptyGrid, 100, 10, 3);
        assertSame(emptyGrid, state.getBrickGrid());
    }

    @Test
    void constructor_ballsInitiallyEmpty() {
        BreakoutState state = new BreakoutState(emptyGrid, 100, 10, 3);
        assertTrue(state.getBalls().isEmpty());
    }

    @Test
    void constructor_threeWallsCreated() {
        BreakoutState state = new BreakoutState(emptyGrid, 100, 10, 3);
        assertEquals(3, state.getWalls().size());
    }

    @Test
    void constructor_paddleNotNull() {
        BreakoutState state = new BreakoutState(emptyGrid, 100, 10, 3);
        assertNotNull(state.getPaddle());
    }

    @Test
    void constructor_hpsInitialisedCorrectly() {
        BreakoutState state = new BreakoutState(emptyGrid, 100, 10, 5);
        assertEquals(5, state.getHps());
    }

    @Test
    void constructor_zeroHps_allowed() {
        BreakoutState state = new BreakoutState(emptyGrid, 100, 10, 0);
        assertEquals(0, state.getHps());
    }

    // ── getHps / lose1Life ────────────────────────────────────────────────────

    @Test
    void lose1Life_decreasesHpsByOne() {
        BreakoutState state = new BreakoutState(emptyGrid, 100, 10, 5);
        state.lose1Life();
        assertEquals(4, state.getHps());
    }

    @Test
    void lose1Life_atZero_staysAtZero() {
        BreakoutState state = new BreakoutState(emptyGrid, 100, 10, 0);
        state.lose1Life();
        assertEquals(0, state.getHps());
    }

    // ── addBall / removeBall ──────────────────────────────────────────────────

    @Test
    void addBall_ballAppearsInBallsList() {
        BreakoutState state = new BreakoutState(emptyGrid, 100, 10, 3);
        Point center = state.getBrickGrid().getBoundingRectangle().getBottomCenter()
                           .add(new Vector(0, -1000));
        Ball ball = state.addBall(new Circle(center, 300), new Vector(10, -10), new StandardBehavior());
        assertTrue(state.getBalls().contains(ball));
    }

    @Test
    void removeBall_ballRemovedFromList() {
        BreakoutState state = new BreakoutState(emptyGrid, 100, 10, 3);
        Point center = state.getBrickGrid().getBoundingRectangle().getBottomCenter()
                           .add(new Vector(0, -1000));
        Ball ball = state.addBall(new Circle(center, 300), new Vector(10, -10), new StandardBehavior());
        state.removeBall(ball);
        assertFalse(state.getBalls().contains(ball));
    }

    // ── isGameLost ────────────────────────────────────────────────────────────

    @Test
    void isGameLost_noBalls_returnsTrue() {
        BreakoutState state = new BreakoutState(emptyGrid, 100, 10, 3);
        assertTrue(state.isGameLost());
    }

    @Test
    void isGameLost_zeroHps_returnsTrue() {
        BreakoutState state = new BreakoutState(emptyGrid, 100, 10, 0);
        assertTrue(state.isGameLost());
    }

    @Test
    void isGameLost_hasBallsAndHps_returnsFalse() {
        BreakoutState state = new BreakoutState(emptyGrid, 100, 10, 3);
        Point center = state.getBrickGrid().getBoundingRectangle().getBottomCenter()
                           .add(new Vector(0, -1000));
        state.addBall(new Circle(center, 300), new Vector(10, -10), new StandardBehavior());
        assertFalse(state.isGameLost());
    }

    // ── isGameWon ─────────────────────────────────────────────────────────────

    @Test
    void isGameWon_emptyGrid_returnsTrue() {
        BreakoutState state = new BreakoutState(emptyGrid, 100, 10, 3);
        assertTrue(state.isGameWon());
    }

    @Test
    void isGameWon_standardBrickRemaining_returnsFalse() {
        BreakoutState state = new BreakoutState(gridWithStandard, 100, 10, 3);
        assertFalse(state.isGameWon());
    }

    @Test
    void isGameWon_onlySpikeyBricksRemaining_returnsTrue() {
        BreakoutState state = new BreakoutState(gridWithSpikey, 100, 10, 3);
        assertTrue(state.isGameWon());
    }

    // ── isGameOver ────────────────────────────────────────────────────────────

    @Test
    void isGameOver_gameLost_returnsTrue() {
        BreakoutState state = new BreakoutState(emptyGrid, 100, 10, 0);
        assertTrue(state.isGameOver());
    }

    @Test
    void isGameOver_gameWon_returnsTrue() {
        BreakoutState state = new BreakoutState(emptyGrid, 100, 10, 3);
        assertTrue(state.isGameOver()); // empty grid = won
    }

    // ── getBalls returns defensive copy ──────────────────────────────────────

    @Test
    void getBalls_returnsDefensiveCopy_mutatingDoesNotAffectState() {
        BreakoutState state = new BreakoutState(emptyGrid, 100, 10, 3);
        Point center = state.getBrickGrid().getBoundingRectangle().getBottomCenter()
                           .add(new Vector(0, -1000));
        Ball ball = state.addBall(new Circle(center, 300), new Vector(10, -10), new StandardBehavior());
        state.getBalls().clear(); // mutate the returned copy
        assertTrue(state.getBalls().contains(ball)); // original unaffected
    }
}
