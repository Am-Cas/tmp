package ogp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import ogp.math.Vector;

public class CollisionTest {

    // ── constructor: null kiloNormal ─────────────────────────────────────────

    @Test
    void constructor_nullKiloNormal_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Collision(0, null));
    }

    // ── constructor: negative time ────────────────────────────────────────────

    @Test
    void constructor_negativeTime_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Collision(-1, Vector.KILO_DOWN));
    }

    // ── constructor postconditions ────────────────────────────────────────────

    @Test
    void constructor_timeStoredCorrectly() {
        Collision c = new Collision(10, Vector.KILO_DOWN);
        assertEquals(10, c.getMillisecondsUntilCollision());
    }

    @Test
    void constructor_kiloNormalStoredCorrectly() {
        Collision c = new Collision(0, Vector.KILO_UP);
        assertEquals(Vector.KILO_UP, c.getKiloNormal());
    }

    @Test
    void constructor_zeroTime_allowed() {
        Collision c = new Collision(0, Vector.KILO_DOWN);
        assertEquals(0, c.getMillisecondsUntilCollision());
    }

    // ── getMillisecondsUntilCollision ─────────────────────────────────────────

    @Test
    void getMillisecondsUntilCollision_returnsNonNegative() {
        Collision c = new Collision(5, Vector.KILO_LEFT);
        assertTrue(c.getMillisecondsUntilCollision() >= 0);
    }

    // ── getKiloNormal ─────────────────────────────────────────────────────────

    @Test
    void getKiloNormal_returnsNotNull() {
        Collision c = new Collision(0, Vector.KILO_RIGHT);
        assertNotNull(c.getKiloNormal());
    }

    @Test
    void getKiloNormal_isKiloUnitVector() {
        Collision c = new Collision(0, Vector.KILO_RIGHT);
        assertTrue(c.getKiloNormal().isKiloUnitVector());
    }

    // ── getEarliestCollision ──────────────────────────────────────────────────

    @Test
    void getEarliestCollision_firstNull_returnsSecond() {
        Collision c2 = new Collision(5, Vector.KILO_DOWN);
        assertSame(c2, Collision.getEarliestCollision(null, c2));
    }

    @Test
    void getEarliestCollision_secondNull_returnsFirst() {
        Collision c1 = new Collision(5, Vector.KILO_DOWN);
        assertSame(c1, Collision.getEarliestCollision(c1, null));
    }

    @Test
    void getEarliestCollision_bothNull_returnsNull() {
        assertNull(Collision.getEarliestCollision(null, null));
    }

    @Test
    void getEarliestCollision_firstEarlier_returnsFirst() {
        Collision c1 = new Collision(3, Vector.KILO_DOWN);
        Collision c2 = new Collision(7, Vector.KILO_UP);
        assertSame(c1, Collision.getEarliestCollision(c1, c2));
    }

    @Test
    void getEarliestCollision_secondEarlier_returnsSecond() {
        Collision c1 = new Collision(10, Vector.KILO_DOWN);
        Collision c2 = new Collision(2, Vector.KILO_UP);
        assertSame(c2, Collision.getEarliestCollision(c1, c2));
    }

    @Test
    void getEarliestCollision_equalTime_returnsFirst() {
        Collision c1 = new Collision(5, Vector.KILO_DOWN);
        Collision c2 = new Collision(5, Vector.KILO_UP);
        assertSame(c1, Collision.getEarliestCollision(c1, c2));
    }
}
