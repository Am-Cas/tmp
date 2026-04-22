package ogp.balls;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ogp.math.Circle;
import ogp.math.Point;
import ogp.math.Rectangle;
import ogp.math.Vector;

public class BallTest {

    // A rectangle large enough to contain all test ball centers.
    private Rectangle allowedArea;
    private Circle geometry;
    private Vector velocity;
    private BallBehavior behavior;

    @BeforeEach
    void setUp() {
        allowedArea = new Rectangle(0, 0, 10000, 10000);
        geometry    = new Circle(new Point(5000, 5000), 500);
        velocity    = new Vector(10, -10);
        behavior    = new StandardBehavior();
    }

    // ── constructor: null allowedArea ────────────────────────────────────────

    @Test
    void constructor_nullAllowedArea_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Ball(null, geometry, velocity, behavior));
    }

    // ── constructor: null geometry ───────────────────────────────────────────

    @Test
    void constructor_nullGeometry_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Ball(allowedArea, null, velocity, behavior));
    }

    // ── constructor: null velocity ───────────────────────────────────────────

    @Test
    void constructor_nullVelocity_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Ball(allowedArea, geometry, null, behavior));
    }

    // ── constructor: null behavior ───────────────────────────────────────────

    @Test
    void constructor_nullBehavior_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Ball(allowedArea, geometry, velocity, null));
    }

    // ── constructor postconditions ───────────────────────────────────────────

    @Test
    void constructor_geometryStoredCorrectly() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        assertEquals(geometry, ball.getGeometry());
    }

    @Test
    void constructor_velocityStoredCorrectly() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        assertEquals(velocity, ball.getVelocity());
    }

    @Test
    void constructor_behaviorStoredCorrectly() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        assertSame(behavior, ball.getBehavior());
    }

    @Test
    void constructor_allowedAreaStoredCorrectly() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        assertSame(allowedArea, ball.getAllowedArea());
    }

    // ── getCenter ────────────────────────────────────────────────────────────

    @Test
    void getCenter_returnsGeometryCenter() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        assertEquals(geometry.getCenter(), ball.getCenter());
    }

    // ── setGeometry ──────────────────────────────────────────────────────────

    @Test
    void setGeometry_updatesGeometry() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        Circle newGeometry = new Circle(new Point(6000, 6000), 300);
        ball.setGeometry(newGeometry);
        assertEquals(newGeometry, ball.getGeometry());
    }

    // ── setVelocity ──────────────────────────────────────────────────────────

    @Test
    void setVelocity_updatesVelocity() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        Vector newVelocity = new Vector(20, 30);
        ball.setVelocity(newVelocity);
        assertEquals(newVelocity, ball.getVelocity());
    }

    // ── setBehavior ──────────────────────────────────────────────────────────

    @Test
    void setBehavior_updatesBehavior() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        BallBehavior newBehavior = new BlinkingBallBehavior();
        ball.setBehavior(newBehavior);
        assertSame(newBehavior, ball.getBehavior());
    }

    // ── move ─────────────────────────────────────────────────────────────────

    @Test
    void move_zeroTime_geometryUnchanged() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        ball.move(0);
        assertEquals(geometry, ball.getGeometry());
    }

    @Test
    void move_positiveTime_geometryUpdatedByVelocityTimesElapsed() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        long elapsed = 10;
        Circle expected = geometry.move(velocity.multiply(elapsed));
        ball.move(elapsed);
        assertEquals(expected, ball.getGeometry());
    }

    // ── computeDestination ───────────────────────────────────────────────────

    @Test
    void computeDestination_returnsCorrectFuturePosition() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        long elapsed = 5;
        Circle expected = geometry.move(velocity.multiply(elapsed));
        assertEquals(expected, ball.computeDestination(elapsed));
    }

    @Test
    void computeDestination_doesNotMutateBall() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        ball.computeDestination(100);
        assertEquals(geometry, ball.getGeometry());
    }

    // ── isValidScaledVelocity ────────────────────────────────────────────────

    @Test
    void isValidScaledVelocity_speedWithinRange_returnsTrue() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        // velocity (10,-10) → squaredLength = 200, within [25, 10000]
        assertTrue(ball.isValidScaledVelocity(velocity));
    }

    @Test
    void isValidScaledVelocity_speedTooLow_returnsFalse() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        Vector slow = new Vector(1, 1); // squaredLength = 2 < 25
        assertFalse(ball.isValidScaledVelocity(slow));
    }

    @Test
    void isValidScaledVelocity_speedTooHigh_returnsFalse() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        Vector fast = new Vector(200, 200); // squaredLength = 80000 > 10000
        assertFalse(ball.isValidScaledVelocity(fast));
    }

    // ── speedUp ──────────────────────────────────────────────────────────────

    @Test
    void speedUp_belowMaximum_increasesSpeed() {
        Ball ball = new Ball(allowedArea, geometry, new Vector(10, -10), behavior);
        Vector before = ball.getVelocity();
        ball.speedUp();
        Vector expected = before.multiply(Ball.SPEED_UP_FACTOR).divide(1000);
        assertEquals(expected, ball.getVelocity());
    }

    @Test
    void speedUp_atMaximum_velocityUnchanged() {
        // velocity with squared length just above the maximum
        Ball ball = new Ball(allowedArea, geometry, new Vector(100, 0), behavior);
        Vector before = ball.getVelocity();
        ball.speedUp();
        assertEquals(before, ball.getVelocity());
    }

    // ── slowDown ─────────────────────────────────────────────────────────────

    @Test
    void slowDown_aboveMinimum_decreasesSpeed() {
        Ball ball = new Ball(allowedArea, geometry, new Vector(10, -10), behavior);
        Vector before = ball.getVelocity();
        ball.slowDown();
        Vector expected = before.multiply(Ball.SLOW_DOWN_FACTOR).divide(1000);
        assertEquals(expected, ball.getVelocity());
    }

    @Test
    void slowDown_atMinimum_velocityUnchanged() {
        // velocity with squared length just below minimum after scaling
        Ball ball = new Ball(allowedArea, geometry, new Vector(5, 0), behavior);
        // 5*0.95 = 4.75 → (4,0) squared = 16 < 25 → no change
        Vector before = ball.getVelocity();
        ball.slowDown();
        assertEquals(before, ball.getVelocity());
    }

    // ── scaleUp ──────────────────────────────────────────────────────────────

    @Test
    void scaleUp_withinMaxScaleIndex_radiusGrows() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        long radiusBefore = ball.getGeometry().getRadius();
        ball.scaleUp();
        assertTrue(ball.getGeometry().getRadius() > radiusBefore);
    }

    @Test
    void scaleUp_atMaxScaleIndex_radiusUnchanged() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        // Call scaleUp 10 times to reach SCALE_INDEX_MAX
        for (int i = 0; i < 10; i++) ball.scaleUp();
        long radiusAtMax = ball.getGeometry().getRadius();
        ball.scaleUp(); // should have no effect
        assertEquals(radiusAtMax, ball.getGeometry().getRadius());
    }

    // ── scaleDown ────────────────────────────────────────────────────────────

    @Test
    void scaleDown_withinMinScaleIndex_radiusShrinks() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        long radiusBefore = ball.getGeometry().getRadius();
        ball.scaleDown();
        assertTrue(ball.getGeometry().getRadius() < radiusBefore);
    }

    @Test
    void scaleDown_atMinScaleIndex_radiusUnchanged() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        // Call scaleDown 2 times to reach SCALE_INDEX_MIN
        for (int i = 0; i < 2; i++) ball.scaleDown();
        long radiusAtMin = ball.getGeometry().getRadius();
        ball.scaleDown(); // should have no effect
        assertEquals(radiusAtMin, ball.getGeometry().getRadius());
    }

    // ── regression: constructor used to set all fields to null ───────────────

    @Test
    void regression_constructor_geometryNotNull() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        assertNotNull(ball.getGeometry());
    }

    @Test
    void regression_constructor_velocityNotNull() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        assertNotNull(ball.getVelocity());
    }

    @Test
    void regression_constructor_behaviorNotNull() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        assertNotNull(ball.getBehavior());
    }

    @Test
    void regression_constructor_allowedAreaNotNull() {
        Ball ball = new Ball(allowedArea, geometry, velocity, behavior);
        assertNotNull(ball.getAllowedArea());
    }
}
