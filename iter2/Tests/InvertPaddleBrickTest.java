package ogp.bricks;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import ogp.math.Point;
import ogp.math.Rectangle;

/**
 * Regression tests for InvertPaddleBrick.
 * The original getLabel() had a syntax error: return InvertPaddleBrick.LABEL);
 * which would cause a compile error.
 */
public class InvertPaddleBrickTest {

    private final Rectangle geometry     = new Rectangle(0, 0, 100, 30);
    private final Point     gridPosition = new Point(0, 0);

    // ── constructor: null geometry ────────────────────────────────────────────

    @Test
    void constructor_nullGeometry_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new InvertPaddleBrick(null, gridPosition));
    }

    // ── constructor: null gridPosition ────────────────────────────────────────

    @Test
    void constructor_nullGridPosition_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new InvertPaddleBrick(geometry, null));
    }

    // ── constructor postconditions ────────────────────────────────────────────

    @Test
    void constructor_geometryStoredCorrectly() {
        InvertPaddleBrick b = new InvertPaddleBrick(geometry, gridPosition);
        assertEquals(geometry, b.getGeometry());
    }

    @Test
    void constructor_gridPositionStoredCorrectly() {
        InvertPaddleBrick b = new InvertPaddleBrick(geometry, gridPosition);
        assertEquals(gridPosition, b.getGridPosition());
    }

    // ── getLabel ──────────────────────────────────────────────────────────────

    /**
     * Regression: getLabel() had a syntax error (extra ')'), causing compile failure.
     */
    @Test
    void regression_getLabel_returnsExpectedLabel() {
        InvertPaddleBrick b = new InvertPaddleBrick(geometry, gridPosition);
        assertEquals(InvertPaddleBrick.LABEL, b.getLabel());
    }

    @Test
    void getLabel_notNull() {
        InvertPaddleBrick b = new InvertPaddleBrick(geometry, gridPosition);
        assertNotNull(b.getLabel());
    }

    // ── getColor ──────────────────────────────────────────────────────────────

    @Test
    void getColor_returnsInvertPaddleBrickColor() {
        InvertPaddleBrick b = new InvertPaddleBrick(geometry, gridPosition);
        assertEquals(InvertPaddleBrick.COLOR, b.getColor());
    }
}
