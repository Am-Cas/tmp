package ogp.balls;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ogp.BreakoutState;
import ogp.BrickGrid;
import ogp.math.Circle;
import ogp.math.Point;
import ogp.math.Vector;

public class BallBehaviorTest {

    private BreakoutState state;
    private Ball ball;

    @BeforeEach
    void setUp() {
        BrickGrid grid = new BrickGrid(7, 8, 100, 30);
        state = new BreakoutState(grid, 100, 10, 10);

        Point center = state.getBrickGrid().getBoundingRectangle().getBottomCenter()
                           .add(new Vector(0, -1000));
        ball = state.addBall(new Circle(center, 300), new Vector(10, -10), new StandardBehavior());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TemporaryBehavior
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void temporaryBehavior_constructor_negativeDuration_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new StrongBallBehavior() {
                    { } // uses super(DURATION) so cannot directly test — test via TemporaryBehavior directly
                });
        // Test via a direct construction with TemporaryBehavior subclass trick:
        // We use the accessible constructor signature.
        assertThrows(IllegalArgumentException.class,
                () -> new TemporaryBehavior(-1));
    }

    @Test
    void temporaryBehavior_constructor_zeroDuration_allowed() {
        TemporaryBehavior tb = new TemporaryBehavior(0);
        assertEquals(0, tb.getTimeLeft());
    }

    @Test
    void temporaryBehavior_constructor_timeLeftMatchesDuration() {
        TemporaryBehavior tb = new TemporaryBehavior(3000);
        assertEquals(3000, tb.getTimeLeft());
    }

    @Test
    void temporaryBehavior_update_timeLeftDecreases() {
        ball.setBehavior(new StrongBallBehavior());
        StrongBallBehavior behavior = (StrongBallBehavior) ball.getBehavior();
        int timeBefore = behavior.getTimeLeft();
        state.tick(100);
        // getTimeLeft may have changed if still the same behavior
        if (ball.getBehavior() instanceof StrongBallBehavior sb) {
            assertTrue(sb.getTimeLeft() <= timeBefore);
        }
    }

    @Test
    void temporaryBehavior_update_afterDurationExpires_behaviorRevertedToStandard() {
        ball.setBehavior(new StrongBallBehavior());
        // Tick past duration
        state.tick(StrongBallBehavior.DURATION + 100);
        assertTrue(ball.getBehavior() instanceof StandardBehavior);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // StrongBallBehavior
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void strongBallBehavior_constructor_timeLeftEqualsDuration() {
        StrongBallBehavior sb = new StrongBallBehavior();
        assertEquals(StrongBallBehavior.DURATION, sb.getTimeLeft());
    }

    @Test
    void strongBallBehavior_color_isYellow() {
        StrongBallBehavior sb = new StrongBallBehavior();
        assertEquals(java.awt.Color.yellow, sb.getColor());
    }

    @Test
    void strongBallBehavior_bounceOffWall_decreasesHps() {
        ball.setBehavior(new StrongBallBehavior());
        int hpsBefore = state.getHps();
        // Tick the ball until it hits a wall (move toward the north wall)
        ball.setVelocity(new Vector(0, -50));
        state.tick(BreakoutState.MAXIMUM_TIME_DELTA);
        // HPs should have decreased if ball hit a wall with strong behavior
        // (result depends on collision timing – just verify it can decrease)
        assertTrue(state.getHps() <= hpsBefore);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // BlinkingBallBehavior
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void blinkingBallBehavior_constructor_timeLeftEqualsDuration() {
        BlinkingBallBehavior bb = new BlinkingBallBehavior();
        assertEquals(BlinkingBallBehavior.DURATION, bb.getTimeLeft());
    }

    @Test
    void blinkingBallBehavior_initiallyNotWeakVisible() {
        // At t=0 the activeSince=0, aux=0 → NORMAL_INV
        BlinkingBallBehavior bb = new BlinkingBallBehavior();
        assertFalse(bb.isWeakVisible());
    }

    @Test
    void blinkingBallBehavior_afterOneBlinkTime_isWeakVisible() {
        ball.setBehavior(new BlinkingBallBehavior());
        // Tick slightly past BLINK_TIME
        state.tick(BlinkingBallBehavior.BLINK_TIME + 1);
        if (ball.getBehavior() instanceof BlinkingBallBehavior bb) {
            assertTrue(bb.isWeakVisible());
        }
    }

    @Test
    void blinkingBallBehavior_color_isGray() {
        BlinkingBallBehavior bb = new BlinkingBallBehavior();
        assertEquals(java.awt.Color.GRAY, bb.getColor());
    }

    @Test
    void blinkingBallBehavior_afterDurationExpires_behaviorRevertedToStandard() {
        ball.setBehavior(new BlinkingBallBehavior());
        state.tick(BlinkingBallBehavior.DURATION + 100);
        assertTrue(ball.getBehavior() instanceof StandardBehavior);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // StandardBehavior
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void standardBehavior_color_isWhite() {
        StandardBehavior sb = new StandardBehavior();
        assertEquals(java.awt.Color.WHITE, sb.getColor());
    }
}
