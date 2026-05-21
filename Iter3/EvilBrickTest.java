package ogp.multiclass;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import ogp.BreakoutState;
import ogp.BrickGrid;
import ogp.balls.StandardBehavior;
import ogp.balls.StrongBallBehavior;
import ogp.math.Circle;
import ogp.math.Point;
import ogp.math.Vector;

public class EvilBrickTest {

    private GuardBrick g1, g2;
    private EvilBrick eb1, eb2;

    @BeforeEach
    void setup() {
        g1 = new GuardBrick();
        g2 = new GuardBrick();
        eb1 = new EvilBrick();
        eb2 = new EvilBrick();
    }

    // ---- constructor ----

    @Test
    void constructorGuardBricksIsEmpty() {
        assertTrue(eb1.getGuardBricks().isEmpty());
    }

    @Test
    void constructorEvilBallsIsEmpty() {
        assertTrue(eb1.getEvilBalls().isEmpty());
    }

    // ---- getGuardBricks defensive copy ----

    @Test
    void getGuardBricksReturnsDefensiveCopy() {
        g1.link(eb1);
        var copy = eb1.getGuardBricks();
        copy.clear();
        assertEquals(1, eb1.getGuardBricks().size());
    }

    // ---- getEvilBalls defensive copy ----

    @Test
    void getEvilBallsReturnsDefensiveCopy() {
        new EvilBallBehavior(eb1);
        var copy = eb1.getEvilBalls();
        copy.clear();
        assertEquals(1, eb1.getEvilBalls().size());
    }

    // ---- bidirectional association with GuardBrick ----

    @Test
    void linkingGuardAddsToEvilBrickGuardList() {
        g1.link(eb1);
        assertTrue(eb1.getGuardBricks().contains(g1));
    }

    @Test
    void linkingEvilBallAddsToEvilBallsList() {
        var ball = new EvilBallBehavior(eb1);
        assertTrue(eb1.getEvilBalls().contains(ball));
    }

    // ---- hit: no guards, no evil balls → brick destroyed ----

    @Test
    void hitWithNoProtectorsDestroysBrick() {
        var grid = new BrickGrid(7, 7, 10000, 3000);
        var pos = new Point(0, 0);
        var eb = grid.addEvilBrick(pos, new ArrayList<>());

        var state = new BreakoutState(grid, 1000, 100, 70);
        var ball = state.addBall(
            new Circle(new Point(35000, 15000), 500),
            new Vector(0, -33),
            new StandardBehavior()
        );

        eb.hit(state, ball);

        assertNull(grid.getBrickAt(pos));
    }

    // ---- hit: has guard brick → brick survives ----

    @Test
    void hitWithGuardBrickLinkedBrickSurvives() {
        var grid = new BrickGrid(7, 7, 10000, 3000);
        var ePos = new Point(0, 0);
        var gPos = new Point(1, 0);

        var g = grid.addGuardBrick(gPos);
        var eb = grid.addEvilBrick(ePos, new ArrayList<>());
        g.link(eb);

        var state = new BreakoutState(grid, 1000, 100, 70);
        var ball = state.addBall(
            new Circle(new Point(35000, 15000), 500),
            new Vector(0, -33),
            new StandardBehavior()
        );

        eb.hit(state, ball);

        assertNotNull(grid.getBrickAt(ePos));
    }

    // ---- hit: has evil ball → brick survives ----

    @Test
    void hitWithEvilBallLinkedBrickSurvives() {
        var grid = new BrickGrid(7, 7, 10000, 3000);
        var pos = new Point(0, 0);
        var eb = grid.addEvilBrick(pos, new ArrayList<>());
        new EvilBallBehavior(eb); // evil ball protects eb

        var state = new BreakoutState(grid, 1000, 100, 70);
        var ball = state.addBall(
            new Circle(new Point(35000, 15000), 500),
            new Vector(0, -33),
            new StandardBehavior()
        );

        eb.hit(state, ball);

        assertNotNull(grid.getBrickAt(pos));
    }

    // ---- strongHit: unprotected → destroyed, returns false ----
    // This is also a regression test for the bug that was fixed.

    @Test
    void strongHitOnUnprotectedBrickReturnsFalse() {
        var grid = new BrickGrid(7, 7, 10000, 3000);
        var pos = new Point(0, 0);
        var eb = grid.addEvilBrick(pos, new ArrayList<>());

        var state = new BreakoutState(grid, 1000, 100, 70);
        var ball = state.addBall(
            new Circle(new Point(35000, 15000), 500),
            new Vector(0, -33),
            new StrongBallBehavior()
        );

        boolean survived = eb.strongHit(state, ball);

        assertFalse(survived);
    }

    @Test
    void strongHitOnUnprotectedBrickDestroysBrick() {
        var grid = new BrickGrid(7, 7, 10000, 3000);
        var pos = new Point(0, 0);
        var eb = grid.addEvilBrick(pos, new ArrayList<>());

        var state = new BreakoutState(grid, 1000, 100, 70);
        var ball = state.addBall(
            new Circle(new Point(35000, 15000), 500),
            new Vector(0, -33),
            new StrongBallBehavior()
        );

        eb.strongHit(state, ball);

        assertNull(grid.getBrickAt(pos));
    }

    // ---- strongHit: protected → survives, returns true ----

    @Test
    void strongHitOnProtectedBrickReturnsTrue() {
        var grid = new BrickGrid(7, 7, 10000, 3000);
        var ePos = new Point(0, 0);
        var gPos = new Point(1, 0);

        var g = grid.addGuardBrick(gPos);
        var eb = grid.addEvilBrick(ePos, new ArrayList<>());
        g.link(eb);

        var state = new BreakoutState(grid, 1000, 100, 70);
        var ball = state.addBall(
            new Circle(new Point(35000, 15000), 500),
            new Vector(0, -33),
            new StrongBallBehavior()
        );

        boolean survived = eb.strongHit(state, ball);

        assertTrue(survived);
    }

    @Test
    void strongHitOnProtectedBrickBrickSurvivesInGrid() {
        var grid = new BrickGrid(7, 7, 10000, 3000);
        var ePos = new Point(0, 0);
        var gPos = new Point(1, 0);

        var g = grid.addGuardBrick(gPos);
        var eb = grid.addEvilBrick(ePos, new ArrayList<>());
        g.link(eb);

        var state = new BreakoutState(grid, 1000, 100, 70);
        var ball = state.addBall(
            new Circle(new Point(35000, 15000), 500),
            new Vector(0, -33),
            new StrongBallBehavior()
        );

        eb.strongHit(state, ball);

        assertNotNull(grid.getBrickAt(ePos));
    }
}
