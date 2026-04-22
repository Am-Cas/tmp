package ogp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ogp.bricks.StandardBrick;
import ogp.math.Point;
import ogp.math.Rectangle;
import ogp.math.Vector;

public class BrickCollisionTest {

    private Vector kiloNormal;
    private StandardBrick brick;

    @BeforeEach
    void setUp() {
        kiloNormal = Vector.KILO_DOWN;
        brick = new StandardBrick(new Rectangle(0, 0, 100, 30), new Point(0, 0));
    }

    // ── constructor: null brick ───────────────────────────────────────────────

    @Test
    void constructor_nullBrick_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new BrickCollision(0, kiloNormal, null));
    }

    // ── constructor: null kiloNormal ─────────────────────────────────────────

    @Test
    void constructor_nullKiloNormal_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new BrickCollision(0, null, brick));
    }

    // ── constructor: negative time ────────────────────────────────────────────

    @Test
    void constructor_negativeTime_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new BrickCollision(-1, kiloNormal, brick));
    }

    // ── constructor postconditions ────────────────────────────────────────────

    @Test
    void constructor_brickStoredCorrectly() {
        BrickCollision bc = new BrickCollision(5, kiloNormal, brick);
        assertSame(brick, bc.getBrick());
    }

    @Test
    void constructor_timeStoredCorrectly() {
        BrickCollision bc = new BrickCollision(7, kiloNormal, brick);
        assertEquals(7, bc.getMillisecondsUntilCollision());
    }

    @Test
    void constructor_kiloNormalStoredCorrectly() {
        BrickCollision bc = new BrickCollision(0, kiloNormal, brick);
        assertEquals(kiloNormal, bc.getKiloNormal());
    }

    @Test
    void constructor_zeroTime_allowed() {
        BrickCollision bc = new BrickCollision(0, kiloNormal, brick);
        assertEquals(0, bc.getMillisecondsUntilCollision());
    }

    // ── regression: constructor used to set this.brick = null ────────────────

    @Test
    void regression_getBrick_notNull() {
        BrickCollision bc = new BrickCollision(10, kiloNormal, brick);
        assertNotNull(bc.getBrick());
    }

    @Test
    void regression_getBrick_returnsBrickPassedToConstructor() {
        BrickCollision bc = new BrickCollision(10, kiloNormal, brick);
        assertSame(brick, bc.getBrick());
    }
}
