package ogp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ogp.bricks.StandardBrick;
import ogp.math.Point;

public class BrickGridTest {

    private BrickGrid grid;

    @BeforeEach
    void setUp() {
        grid = new BrickGrid(7, 8, 100, 30);
    }

    // ── constructor validation ────────────────────────────────────────────────

    @Test
    void constructor_zeroColumnCount_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new BrickGrid(0, 8, 100, 30));
    }

    @Test
    void constructor_negativeColumnCount_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new BrickGrid(-1, 8, 100, 30));
    }

    @Test
    void constructor_zeroRowCount_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new BrickGrid(7, 0, 100, 30));
    }

    @Test
    void constructor_negativeRowCount_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new BrickGrid(7, -1, 100, 30));
    }

    @Test
    void constructor_zeroBrickWidth_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new BrickGrid(7, 8, 0, 30));
    }

    @Test
    void constructor_zeroBrickHeight_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new BrickGrid(7, 8, 100, 0));
    }

    // ── constructor postconditions ────────────────────────────────────────────

    @Test
    void constructor_columnCountStoredCorrectly() {
        assertEquals(7, grid.getColumnCount());
    }

    @Test
    void constructor_rowCountStoredCorrectly() {
        assertEquals(8, grid.getRowCount());
    }

    @Test
    void constructor_brickWidthStoredCorrectly() {
        assertEquals(100, grid.getBrickWidth());
    }

    @Test
    void constructor_brickHeightStoredCorrectly() {
        assertEquals(30, grid.getBrickHeight());
    }

    @Test
    void constructor_initiallyEmpty() {
        assertTrue(grid.getBricks().isEmpty());
    }

    // ── getWidth / getHeight ──────────────────────────────────────────────────

    @Test
    void getWidth_equalsColumnCountTimesBrickWidth() {
        assertEquals(grid.getColumnCount() * grid.getBrickWidth(), grid.getWidth());
    }

    @Test
    void getHeight_equalsRowCountTimesBrickHeight() {
        assertEquals(grid.getRowCount() * grid.getBrickHeight(), grid.getHeight());
    }

    // ── addStandardBrick ─────────────────────────────────────────────────────

    @Test
    void addStandardBrick_brickAppearsAtPosition() {
        Point pos = new Point(0, 0);
        grid.addStandardBrick(pos);
        assertNotNull(grid.getBrickAt(pos));
    }

    @Test
    void addStandardBrick_brickHasCorrectGeometry() {
        Point pos = new Point(1, 2);
        grid.addStandardBrick(pos);
        assertEquals(grid.getBrickRectangle(pos), grid.getBrickAt(pos).getGeometry());
    }

    @Test
    void addStandardBrick_brickHasCorrectGridPosition() {
        Point pos = new Point(2, 3);
        grid.addStandardBrick(pos);
        assertEquals(pos, grid.getBrickAt(pos).getGridPosition());
    }

    // ── containsBrickAt ───────────────────────────────────────────────────────

    @Test
    void containsBrickAt_noBrick_returnsFalse() {
        assertFalse(grid.containsBrickAt(new Point(0, 0)));
    }

    @Test
    void containsBrickAt_afterAdding_returnsTrue() {
        Point pos = new Point(0, 0);
        grid.addStandardBrick(pos);
        assertTrue(grid.containsBrickAt(pos));
    }

    @Test
    void containsBrickAt_outsideGrid_returnsFalse() {
        assertFalse(grid.containsBrickAt(new Point(100, 100)));
    }

    // ── removeBrick ───────────────────────────────────────────────────────────

    @Test
    void removeBrick_brickRemovedFromGrid() {
        Point pos = new Point(0, 0);
        StandardBrick b = grid.addStandardBrick(pos);
        grid.removeBrick(b);
        assertNull(grid.getBrickAt(pos));
    }

    // ── getBrickRectangle ─────────────────────────────────────────────────────

    @Test
    void getBrickRectangle_leftIsXTimesBrickWidth() {
        Point pos = new Point(3, 2);
        assertEquals(pos.x() * grid.getBrickWidth(), grid.getBrickRectangle(pos).getLeft());
    }

    @Test
    void getBrickRectangle_topIsYTimesBrickHeight() {
        Point pos = new Point(3, 2);
        assertEquals(pos.y() * grid.getBrickHeight(), grid.getBrickRectangle(pos).getTop());
    }

    @Test
    void getBrickRectangle_widthEqualsBrickWidth() {
        Point pos = new Point(0, 0);
        assertEquals(grid.getBrickWidth(), grid.getBrickRectangle(pos).getWidth());
    }

    @Test
    void getBrickRectangle_heightEqualsBrickHeight() {
        Point pos = new Point(0, 0);
        assertEquals(grid.getBrickHeight(), grid.getBrickRectangle(pos).getHeight());
    }

    // ── isEmpty ───────────────────────────────────────────────────────────────

    @Test
    void isEmpty_noStandardBricks_returnsTrue() {
        assertTrue(grid.isEmpty());
    }

    @Test
    void isEmpty_hasStandardBrick_returnsFalse() {
        grid.addStandardBrick(new Point(0, 0));
        assertFalse(grid.isEmpty());
    }

    // ── isValidGridPosition ───────────────────────────────────────────────────

    @Test
    void isValidGridPosition_withinBounds_returnsTrue() {
        assertTrue(grid.isValidGridPosition(new Point(0, 0)));
    }

    @Test
    void isValidGridPosition_negativeX_returnsFalse() {
        assertFalse(grid.isValidGridPosition(new Point(-1, 0)));
    }

    @Test
    void isValidGridPosition_negativeY_returnsFalse() {
        assertFalse(grid.isValidGridPosition(new Point(0, -1)));
    }

    @Test
    void isValidGridPosition_xEqualColumnCount_returnsFalse() {
        assertFalse(grid.isValidGridPosition(new Point(grid.getColumnCount(), 0)));
    }

    @Test
    void isValidGridPosition_yEqualRowCount_returnsFalse() {
        assertFalse(grid.isValidGridPosition(new Point(0, grid.getRowCount())));
    }

    // ── getBricks defensive copy ──────────────────────────────────────────────

    @Test
    void getBricks_returnsDefensiveCopy() {
        grid.addStandardBrick(new Point(0, 0));
        grid.getBricks().clear();
        assertEquals(1, grid.getBricks().size());
    }
}
