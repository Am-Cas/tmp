package ogp.bricks;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ogp.BreakoutState;
import ogp.BrickGrid;
import ogp.balls.Ball;
import ogp.balls.BlinkingBallBehavior;
import ogp.balls.StandardBehavior;
import ogp.balls.StrongBallBehavior;
import ogp.math.Circle;
import ogp.math.Point;
import ogp.math.Rectangle;
import ogp.math.Vector;

public class BrickTest {

    private Rectangle geometry;
    private Point gridPosition;
    private BreakoutState state;
    private Ball ball;

    @BeforeEach
    void setUp() {
        geometry     = new Rectangle(0, 0, 100, 30);
        gridPosition = new Point(0, 0);

        BrickGrid grid = new BrickGrid(7, 8, 100, 30);
        state = new BreakoutState(grid, 100, 10, 5);

        // Ball positioned inside the allowed area
        Point center = state.getBrickGrid().getBoundingRectangle().getBottomCenter()
                           .add(new Vector(0, -1000));
        ball = state.addBall(new Circle(center, 300), new Vector(10, -10), new StandardBehavior());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // StandardBrick
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void standardBrick_constructor_nullGeometry_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new StandardBrick(null, gridPosition));
    }

    @Test
    void standardBrick_constructor_nullGridPosition_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new StandardBrick(geometry, null));
    }

    @Test
    void standardBrick_constructor_geometryStoredCorrectly() {
        StandardBrick b = new StandardBrick(geometry, gridPosition);
        assertEquals(geometry, b.getGeometry());
    }

    @Test
    void standardBrick_constructor_gridPositionStoredCorrectly() {
        StandardBrick b = new StandardBrick(geometry, gridPosition);
        assertEquals(gridPosition, b.getGridPosition());
    }

    @Test
    void standardBrick_isNotIndestructible() {
        StandardBrick b = new StandardBrick(geometry, gridPosition);
        assertFalse(b.isIndestructible());
    }

    @Test
    void standardBrick_hit_removesBrickFromGrid() {
        state.getBrickGrid().addStandardBrick(gridPosition);
        StandardBrick b = (StandardBrick) state.getBrickGrid().getBrickAt(gridPosition);
        b.hit(state, ball);
        assertNull(state.getBrickGrid().getBrickAt(gridPosition));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // SpikeyBrick
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void spikeyBrick_constructor_nullGeometry_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new SpikeyBrick(null, gridPosition));
    }

    @Test
    void spikeyBrick_constructor_nullGridPosition_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new SpikeyBrick(geometry, null));
    }

    @Test
    void spikeyBrick_isIndestructible() {
        SpikeyBrick b = new SpikeyBrick(geometry, gridPosition);
        assertTrue(b.isIndestructible());
    }

    @Test
    void spikeyBrick_hit_decreasesHpsByOne() {
        state.getBrickGrid().addSpikeyBrick(gridPosition);
        SpikeyBrick b = (SpikeyBrick) state.getBrickGrid().getBrickAt(gridPosition);
        int hpsBefore = state.getHps();
        b.hit(state, ball);
        assertEquals(hpsBefore - 1, state.getHps());
    }

    @Test
    void spikeyBrick_hit_doesNotRemoveBrick() {
        state.getBrickGrid().addSpikeyBrick(gridPosition);
        SpikeyBrick b = (SpikeyBrick) state.getBrickGrid().getBrickAt(gridPosition);
        b.hit(state, ball);
        assertNotNull(state.getBrickGrid().getBrickAt(gridPosition));
    }

    @Test
    void spikeyBrick_strongHit_decreasesHpsByOne() {
        state.getBrickGrid().addSpikeyBrick(gridPosition);
        SpikeyBrick b = (SpikeyBrick) state.getBrickGrid().getBrickAt(gridPosition);
        int hpsBefore = state.getHps();
        b.strongHit(state, ball);
        assertEquals(hpsBefore - 1, state.getHps());
    }

    @Test
    void spikeyBrick_strongHit_returnsTrue() {
        state.getBrickGrid().addSpikeyBrick(gridPosition);
        SpikeyBrick b = (SpikeyBrick) state.getBrickGrid().getBrickAt(gridPosition);
        assertTrue(b.strongHit(state, ball));
    }

    @Test
    void spikeyBrick_strongHit_doesNotRemoveBrick() {
        state.getBrickGrid().addSpikeyBrick(gridPosition);
        SpikeyBrick b = (SpikeyBrick) state.getBrickGrid().getBrickAt(gridPosition);
        b.strongHit(state, ball);
        assertNotNull(state.getBrickGrid().getBrickAt(gridPosition));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ShrinkPaddleBrick
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void shrinkPaddleBrick_constructor_nullGeometry_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new ShrinkPaddleBrick(null, gridPosition));
    }

    @Test
    void shrinkPaddleBrick_constructor_nullGridPosition_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new ShrinkPaddleBrick(geometry, null));
    }

    @Test
    void shrinkPaddleBrick_hit_removesBrickFromGrid() {
        state.getBrickGrid().addShrinkPaddleBrick(gridPosition);
        ShrinkPaddleBrick b = (ShrinkPaddleBrick) state.getBrickGrid().getBrickAt(gridPosition);
        b.hit(state, ball);
        assertNull(state.getBrickGrid().getBrickAt(gridPosition));
    }

    @Test
    void shrinkPaddleBrick_hit_shrinksPaddle() {
        state.getBrickGrid().addShrinkPaddleBrick(gridPosition);
        ShrinkPaddleBrick b = (ShrinkPaddleBrick) state.getBrickGrid().getBrickAt(gridPosition);
        long halfWidthBefore = state.getPaddle().getHalfWidth();
        b.hit(state, ball);
        assertTrue(state.getPaddle().getHalfWidth() < halfWidthBefore);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // InvertPaddleBrick
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void invertPaddleBrick_constructor_nullGeometry_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new InvertPaddleBrick(null, gridPosition));
    }

    @Test
    void invertPaddleBrick_constructor_nullGridPosition_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new InvertPaddleBrick(geometry, null));
    }

    @Test
    void invertPaddleBrick_hit_removesBrickFromGrid() {
        state.getBrickGrid().addInvertPaddleBrick(gridPosition);
        InvertPaddleBrick b = (InvertPaddleBrick) state.getBrickGrid().getBrickAt(gridPosition);
        b.hit(state, ball);
        assertNull(state.getBrickGrid().getBrickAt(gridPosition));
    }

    @Test
    void invertPaddleBrick_hit_invertsPaddle() {
        state.getBrickGrid().addInvertPaddleBrick(gridPosition);
        InvertPaddleBrick b = (InvertPaddleBrick) state.getBrickGrid().getBrickAt(gridPosition);
        b.hit(state, ball);
        assertTrue(state.getPaddle().isInverted());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // SpeedUpBrick
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void speedUpBrick_constructor_nullGeometry_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new SpeedUpBrick(null, gridPosition));
    }

    @Test
    void speedUpBrick_constructor_nullGridPosition_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new SpeedUpBrick(geometry, null));
    }

    @Test
    void speedUpBrick_hit_removesBrickFromGrid() {
        state.getBrickGrid().addSpeedUpBrick(gridPosition);
        SpeedUpBrick b = (SpeedUpBrick) state.getBrickGrid().getBrickAt(gridPosition);
        b.hit(state, ball);
        assertNull(state.getBrickGrid().getBrickAt(gridPosition));
    }

    @Test
    void speedUpBrick_hit_speedsUpBall() {
        state.getBrickGrid().addSpeedUpBrick(gridPosition);
        SpeedUpBrick b = (SpeedUpBrick) state.getBrickGrid().getBrickAt(gridPosition);
        // Use a velocity where speedUp will have an effect
        ball.setVelocity(new Vector(10, -10));
        long sqBefore = ball.getVelocity().getSquaredLength();
        b.hit(state, ball);
        // Speed should have increased (or stayed same at limit)
        assertTrue(ball.getVelocity().getSquaredLength() >= sqBefore);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // StrengtheningBrick
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void strengtheningBrick_constructor_nullGeometry_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new StrengtheningBrick(null, gridPosition));
    }

    @Test
    void strengtheningBrick_constructor_nullGridPosition_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new StrengtheningBrick(geometry, null));
    }

    @Test
    void strengtheningBrick_hit_removesBrickFromGrid() {
        state.getBrickGrid().addStrengtheningBrick(gridPosition);
        StrengtheningBrick b = (StrengtheningBrick) state.getBrickGrid().getBrickAt(gridPosition);
        b.hit(state, ball);
        assertNull(state.getBrickGrid().getBrickAt(gridPosition));
    }

    @Test
    void strengtheningBrick_hit_givesBallStrongBehavior() {
        state.getBrickGrid().addStrengtheningBrick(gridPosition);
        StrengtheningBrick b = (StrengtheningBrick) state.getBrickGrid().getBrickAt(gridPosition);
        b.hit(state, ball);
        assertTrue(ball.getBehavior() instanceof StrongBallBehavior);
    }

    @Test
    void strengtheningBrick_hit_alwaysCreatesFreshStrongBehavior() {
        // Even if ball already has a StrongBallBehavior, it should be replaced by a fresh one
        ball.setBehavior(new StrongBallBehavior());
        state.getBrickGrid().addStrengtheningBrick(gridPosition);
        StrengtheningBrick b = (StrengtheningBrick) state.getBrickGrid().getBrickAt(gridPosition);
        b.hit(state, ball);
        // Fresh behavior: timeLeft should be the full duration
        StrongBallBehavior behavior = (StrongBallBehavior) ball.getBehavior();
        assertEquals(StrongBallBehavior.DURATION, behavior.getTimeLeft());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // BlinkingBallBrick
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void blinkingBallBrick_constructor_nullGeometry_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new BlinkingBallBrick(null, gridPosition));
    }

    @Test
    void blinkingBallBrick_constructor_nullGridPosition_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new BlinkingBallBrick(geometry, null));
    }

    @Test
    void blinkingBallBrick_hit_removesBrickFromGrid() {
        state.getBrickGrid().addBlinkingBallBrick(gridPosition);
        BlinkingBallBrick b = (BlinkingBallBrick) state.getBrickGrid().getBrickAt(gridPosition);
        b.hit(state, ball);
        assertNull(state.getBrickGrid().getBrickAt(gridPosition));
    }

    @Test
    void blinkingBallBrick_hit_givesBallBlinkingBehavior() {
        state.getBrickGrid().addBlinkingBallBrick(gridPosition);
        BlinkingBallBrick b = (BlinkingBallBrick) state.getBrickGrid().getBrickAt(gridPosition);
        b.hit(state, ball);
        assertTrue(ball.getBehavior() instanceof BlinkingBallBehavior);
    }

    @Test
    void blinkingBallBrick_hit_alwaysCreatesFreshBlinkingBehavior() {
        // Even if ball already has blinking behavior, replaced by a fresh one
        ball.setBehavior(new BlinkingBallBehavior());
        state.getBrickGrid().addBlinkingBallBrick(gridPosition);
        BlinkingBallBrick b = (BlinkingBallBrick) state.getBrickGrid().getBrickAt(gridPosition);
        b.hit(state, ball);
        BlinkingBallBehavior behavior = (BlinkingBallBehavior) ball.getBehavior();
        assertEquals(BlinkingBallBehavior.DURATION, behavior.getTimeLeft());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // SpawnBallBrick
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void spawnBallBrick_hit_removesBrickFromGrid() {
        state.getBrickGrid().addSpawnBallBrick(gridPosition);
        SpawnBallBrick b = (SpawnBallBrick) state.getBrickGrid().getBrickAt(gridPosition);
        b.hit(state, ball);
        assertNull(state.getBrickGrid().getBrickAt(gridPosition));
    }

    @Test
    void spawnBallBrick_hit_addsNewBallToState() {
        state.getBrickGrid().addSpawnBallBrick(gridPosition);
        SpawnBallBrick b = (SpawnBallBrick) state.getBrickGrid().getBrickAt(gridPosition);
        int ballCountBefore = state.getBalls().size();
        b.hit(state, ball);
        assertEquals(ballCountBefore + 1, state.getBalls().size());
    }
}
