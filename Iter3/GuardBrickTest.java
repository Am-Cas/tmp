package ogp.multiclass;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import ogp.BreakoutState;
import ogp.BrickGrid;
import ogp.balls.StandardBehavior;
import ogp.math.Circle;
import ogp.math.Point;
import ogp.math.Vector;

public class GuardBrickTest {

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
    void constructorHpsIsOne() {
        assertEquals(1, g1.getHps());
    }

    @Test
    void constructorEvilBricksIsEmpty() {
        assertTrue(g1.getEvilBricks().isEmpty());
    }

    // ---- link ----

    @Test
    void linkAddsEvilBrickToGuardList() {
        g1.link(eb1);
        assertTrue(g1.getEvilBricks().contains(eb1));
    }

    @Test
    void linkAddsGuardBrickToEvilBrickList() {
        g1.link(eb1);
        assertTrue(eb1.getGuardBricks().contains(g1));
    }

    @Test
    void linkTwiceSamePairNoDuplicateInGuard() {
        g1.link(eb1);
        g1.link(eb1);
        assertEquals(1, g1.getEvilBricks().size());
    }

    @Test
    void linkTwiceSamePairNoDuplicateInEvil() {
        g1.link(eb1);
        g1.link(eb1);
        assertEquals(1, eb1.getGuardBricks().size());
    }

    @Test
    void linkTwoDistinctEvilBricks() {
        g1.link(eb1);
        g1.link(eb2);
        assertEquals(2, g1.getEvilBricks().size());
    }

    @Test
    void linkDoesNotChangeHpsWhenNoEvilBalls() {
        g1.link(eb1);
        assertEquals(1, g1.getHps());
    }

    // ---- unlink ----

    @Test
    void unlinkNonExistentLinkSilentlySucceeds() {
        assertDoesNotThrow(() -> g1.unlink(eb1));
    }

    @Test
    void unlinkRemovesFromGuardList() {
        g1.link(eb1);
        g1.unlink(eb1);
        assertFalse(g1.getEvilBricks().contains(eb1));
    }

    @Test
    void unlinkRemovesGuardFromEvilBrick() {
        g1.link(eb1);
        g1.unlink(eb1);
        assertFalse(eb1.getGuardBricks().contains(g1));
    }

    @Test
    void unlinkUpdatesHps() {
        g1.link(eb1);
        var ball = new EvilBallBehavior(eb1);
        assertEquals(2, g1.getHps());
        g1.unlink(eb1);
        // After unlink, eb1 is no longer in g1's evilBricks so its balls don't count
        assertEquals(1, g1.getHps());
    }

    // ---- hps invariant ----

    @Test
    void hpsIsOneWithNoEvilBalls() {
        g1.link(eb1);
        assertEquals(1, g1.getHps());
    }

    @Test
    void hpsIncreasesWhenEvilBallLinkedToLinkedEvilBrick() {
        g1.link(eb1);
        new EvilBallBehavior(eb1);
        assertEquals(2, g1.getHps());
    }

    @Test
    void hpsCountsTwoEvilBallsOnSameEvilBrick() {
        g1.link(eb1);
        new EvilBallBehavior(eb1);
        new EvilBallBehavior(eb1);
        assertEquals(3, g1.getHps());
    }

    @Test
    void hpsSumsAcrossMultipleLinkedEvilBricks() {
        g1.link(eb1);
        g1.link(eb2);
        new EvilBallBehavior(eb1);
        new EvilBallBehavior(eb2);
        new EvilBallBehavior(eb2);
        // hps = 1 + |eb1.evilBalls| + |eb2.evilBalls| = 1 + 1 + 2 = 4
        assertEquals(4, g1.getHps());
    }

    @Test
    void hpsDecreasesWhenEvilBallUnlinks() {
        g1.link(eb1);
        var ball = new EvilBallBehavior(eb1);
        assertEquals(2, g1.getHps());
        ball.unlink();
        assertEquals(1, g1.getHps());
    }

    // ---- getEvilBricks returns defensive copy ----

    @Test
    void getEvilBricksReturnsDefensiveCopy() {
        g1.link(eb1);
        var copy = g1.getEvilBricks();
        copy.clear();
        assertEquals(1, g1.getEvilBricks().size());
    }

    // ---- hit: hps > 1, nothing happens ----

    @Test
    void hitWithHpsAbove1DoesNotDestroyBrick() {
        var grid = new BrickGrid(7, 7, 10000, 3000);
        var gPos = new Point(0, 0);
        var g = grid.addGuardBrick(gPos);
        var eb = new EvilBrick();
        g.link(eb);
        new EvilBallBehavior(eb); // hps becomes 2

        var state = new BreakoutState(grid, 1000, 100, 70);
        var ball = state.addBall(
            new Circle(new Point(35000, 15000), 500),
            new Vector(0, -33),
            new StandardBehavior()
        );

        assertEquals(2, g.getHps());
        g.hit(state, ball);

        assertNotNull(grid.getBrickAt(gPos)); // brick still present
    }

    // ---- hit: hps == 1, brick destroyed ----

    @Test
    void hitWithHps1DestroysGuardBrick() {
        var grid = new BrickGrid(7, 7, 10000, 3000);
        var gPos = new Point(0, 0);
        var g = grid.addGuardBrick(gPos);

        var state = new BreakoutState(grid, 1000, 100, 70);
        var ball = state.addBall(
            new Circle(new Point(35000, 15000), 500),
            new Vector(0, -33),
            new StandardBehavior()
        );

        assertEquals(1, g.getHps());
        g.hit(state, ball);

        assertNull(grid.getBrickAt(gPos));
    }

    // ---- hit: hps == 1, evil balls spawned per linked evil brick ----

    @Test
    void hitWithHps1SpawnsOneEvilBallPerLinkedEvilBrick() {
        var grid = new BrickGrid(7, 7, 10000, 3000);
        var gPos = new Point(0, 0);
        var ePos = new Point(1, 0);

        var g = grid.addGuardBrick(gPos);
        var eb = grid.addEvilBrick(ePos, new ArrayList<>());
        g.link(eb);

        var state = new BreakoutState(grid, 1000, 100, 70);
        var ball = state.addBall(
            new Circle(new Point(35000, 15000), 500),
            new Vector(0, -33),
            new StandardBehavior()
        );

        int ballsBefore = state.getBalls().size(); // 1
        g.hit(state, ball); // spawns 1 evil ball for eb
        assertEquals(ballsBefore + 1, state.getBalls().size());
    }

    @Test
    void hitWithHps1SpawnsTwoEvilBallsForTwoLinkedEvilBricks() {
        var grid = new BrickGrid(7, 7, 10000, 3000);
        var gPos = new Point(0, 0);
        var ePos1 = new Point(1, 0);
        var ePos2 = new Point(2, 0);

        var g = grid.addGuardBrick(gPos);
        var eb1 = grid.addEvilBrick(ePos1, new ArrayList<>());
        var eb2 = grid.addEvilBrick(ePos2, new ArrayList<>());
        g.link(eb1);
        g.link(eb2);

        var state = new BreakoutState(grid, 1000, 100, 70);
        var ball = state.addBall(
            new Circle(new Point(35000, 15000), 500),
            new Vector(0, -33),
            new StandardBehavior()
        );

        int ballsBefore = state.getBalls().size();
        g.hit(state, ball);
        assertEquals(ballsBefore + 2, state.getBalls().size());
    }

    // ---- hit: hps == 1, guard brick removed from association ----

    @Test
    void hitWithHps1RemovesGuardFromEvilBrickAssociation() {
        var grid = new BrickGrid(7, 7, 10000, 3000);
        var gPos = new Point(0, 0);
        var ePos = new Point(1, 0);

        var g = grid.addGuardBrick(gPos);
        var eb = grid.addEvilBrick(ePos, new ArrayList<>());
        g.link(eb);

        var state = new BreakoutState(grid, 1000, 100, 70);
        var ball = state.addBall(
            new Circle(new Point(35000, 15000), 500),
            new Vector(0, -33),
            new StandardBehavior()
        );

        g.hit(state, ball);

        assertFalse(eb.getGuardBricks().contains(g));
    }
}
