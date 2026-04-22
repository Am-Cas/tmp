package ogp.paddles;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ogp.math.Interval;
import ogp.math.Point;

public class PaddleTest {

    private Interval allowedInterval;
    private Point topCenter;
    private long halfWidth;
    private long speed;

    @BeforeEach
    void setUp() {
        allowedInterval = new Interval(0, 10000);
        topCenter       = new Point(5000, 8000);
        halfWidth       = 1000;
        speed           = 10;
    }

    // ── constructor: null allowedInterval ────────────────────────────────────

    @Test
    void constructor_nullAllowedInterval_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Paddle(null, topCenter, halfWidth, speed));
    }

    // ── constructor: null topCenter ───────────────────────────────────────────

    @Test
    void constructor_nullTopCenter_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Paddle(allowedInterval, null, halfWidth, speed));
    }

    // ── constructor: zero halfWidth ───────────────────────────────────────────

    @Test
    void constructor_zeroHalfWidth_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Paddle(allowedInterval, topCenter, 0, speed));
    }

    // ── constructor: negative halfWidth ───────────────────────────────────────

    @Test
    void constructor_negativeHalfWidth_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Paddle(allowedInterval, topCenter, -1, speed));
    }

    // ── constructor: zero speed ───────────────────────────────────────────────

    @Test
    void constructor_zeroSpeed_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Paddle(allowedInterval, topCenter, halfWidth, 0));
    }

    // ── constructor: negative speed ───────────────────────────────────────────

    @Test
    void constructor_negativeSpeed_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Paddle(allowedInterval, topCenter, halfWidth, -5));
    }

    // ── constructor postconditions ────────────────────────────────────────────

    @Test
    void constructor_halfWidthStoredCorrectly() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        assertEquals(halfWidth, p.getHalfWidth());
    }

    @Test
    void constructor_speedStoredCorrectly() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        assertEquals(speed, p.getSpeed());
    }

    @Test
    void constructor_allowedIntervalStoredCorrectly() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        assertSame(allowedInterval, p.getAllowedInterval());
    }

    @Test
    void constructor_motionDirectionIsStationary() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        assertEquals(PaddleMotionDirection.STATIONARY, p.getMotionDirection());
    }

    @Test
    void constructor_notInverted() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        assertFalse(p.isInverted());
    }

    @Test
    void constructor_topCenterIsClamped() {
        // topCenter well within interval → stored as-is
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        assertEquals(topCenter, p.getTopCenter());
    }

    @Test
    void constructor_topCenterClampedWhenTooFarLeft() {
        Point farLeft = new Point(0, 8000); // 0 - halfWidth < lowerBound
        Paddle p = new Paddle(allowedInterval, farLeft, halfWidth, speed);
        // Clamped: x = lowerBound + halfWidth = 0 + 1000 = 1000
        assertEquals(1000, p.getTopCenter().x());
    }

    @Test
    void constructor_topCenterClampedWhenTooFarRight() {
        Point farRight = new Point(10000, 8000); // 10000 + halfWidth > upperBound
        Paddle p = new Paddle(allowedInterval, farRight, halfWidth, speed);
        // Clamped: x = upperBound - halfWidth = 10000 - 1000 = 9000
        assertEquals(9000, p.getTopCenter().x());
    }

    // ── getGeometry ───────────────────────────────────────────────────────────

    @Test
    void getGeometry_leftIsTopCenterXMinusHalfWidth() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        assertEquals(topCenter.x() - halfWidth, p.getGeometry().getLeft());
    }

    @Test
    void getGeometry_topIsTopCenterY() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        assertEquals(topCenter.y(), p.getGeometry().getTop());
    }

    @Test
    void getGeometry_widthIs2TimesHalfWidth() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        assertEquals(halfWidth * 2, p.getGeometry().getWidth());
    }

    @Test
    void getGeometry_heightIsPaddleHeight() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        assertEquals(Paddle.HEIGHT, p.getGeometry().getHeight());
    }

    // ── setMotionDirection ────────────────────────────────────────────────────

    @Test
    void setMotionDirection_left_updatesDirection() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        p.setMotionDirection(PaddleMotionDirection.LEFT);
        assertEquals(PaddleMotionDirection.LEFT, p.getMotionDirection());
    }

    @Test
    void setMotionDirection_right_updatesDirection() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        p.setMotionDirection(PaddleMotionDirection.RIGHT);
        assertEquals(PaddleMotionDirection.RIGHT, p.getMotionDirection());
    }

    // ── move / setTopCenterX ──────────────────────────────────────────────────

    @Test
    void move_positiveDistance_paddleMoves() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        long xBefore = p.getTopCenter().x();
        p.move(100);
        assertEquals(xBefore + 100, p.getTopCenter().x());
    }

    @Test
    void move_wouldExceedRight_clampedToUpperBound() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        p.move(100000); // far beyond right
        assertEquals(allowedInterval.getUpperBound() - halfWidth, p.getTopCenter().x());
    }

    @Test
    void move_wouldExceedLeft_clampedToLowerBound() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        p.move(-100000); // far beyond left
        assertEquals(allowedInterval.getLowerBound() + halfWidth, p.getTopCenter().x());
    }

    // ── getWidth ──────────────────────────────────────────────────────────────

    @Test
    void getWidth_isTwiceHalfWidth() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        assertEquals(halfWidth * 2, p.getWidth());
    }

    // ── getHeight ─────────────────────────────────────────────────────────────

    @Test
    void getHeight_equalsPaddleConstant() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        assertEquals(Paddle.HEIGHT, p.getHeight());
    }

    // ── scale / shrink / grow ─────────────────────────────────────────────────

    @Test
    void shrink_reducesHalfWidth() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        long before = p.getHalfWidth();
        p.shrink();
        assertTrue(p.getHalfWidth() < before);
    }

    @Test
    void grow_increasesHalfWidth() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        long before = p.getHalfWidth();
        p.grow();
        assertTrue(p.getHalfWidth() > before);
    }

    @Test
    void scale_paddleCannotExceedAllowedIntervalWidth() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        p.scale(1000000); // enormous factor
        assertTrue(p.getWidth() <= allowedInterval.getWidth());
    }

    // ── applyInverted ─────────────────────────────────────────────────────────

    @Test
    void applyInverted_paddleBecomesInverted() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        p.applyInverted();
        assertTrue(p.isInverted());
    }

    // ── computeMovementDistance ───────────────────────────────────────────────

    @Test
    void computeMovementDistance_stationary_returnsZero() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        p.setMotionDirection(PaddleMotionDirection.STATIONARY);
        assertEquals(0, p.computeMovementDistance(10));
    }

    @Test
    void computeMovementDistance_right_returnsPositive() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        p.setMotionDirection(PaddleMotionDirection.RIGHT);
        assertTrue(p.computeMovementDistance(10) > 0);
    }

    @Test
    void computeMovementDistance_left_returnsNegative() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        p.setMotionDirection(PaddleMotionDirection.LEFT);
        assertTrue(p.computeMovementDistance(10) < 0);
    }

    @Test
    void computeMovementDistance_inverted_right_returnsNegative() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        p.applyInverted();
        p.setMotionDirection(PaddleMotionDirection.RIGHT);
        assertTrue(p.computeMovementDistance(10) < 0);
    }

    @Test
    void computeMovementDistance_inverted_left_returnsPositive() {
        Paddle p = new Paddle(allowedInterval, topCenter, halfWidth, speed);
        p.applyInverted();
        p.setMotionDirection(PaddleMotionDirection.LEFT);
        assertTrue(p.computeMovementDistance(10) > 0);
    }

    // ── regression: constructor used to skip clamping ─────────────────────────

    @Test
    void regression_constructor_paddleStaysInsideAllowedInterval() {
        Point center = new Point(5000, 8000);
        Paddle p = new Paddle(allowedInterval, center, halfWidth, speed);
        assertTrue(allowedInterval.isInside(p.getTopCenter().x() - p.getHalfWidth()));
        assertTrue(allowedInterval.isInside(p.getTopCenter().x() + p.getHalfWidth()));
    }
}
