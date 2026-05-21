package ogp.multiclass;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ogp.BreakoutState;
import ogp.BrickGrid;
import ogp.Collision;
import ogp.balls.StandardBehavior;
import ogp.math.Circle;
import ogp.math.Point;
import ogp.math.Vector;

public class EvilBallBehaviorTest {

    private GuardBrick g1, g2, g3, g4;
    private EvilBrick eb1, eb2, eb3, eb4;

    @BeforeEach
    void setup() {
        g1 = new GuardBrick();
        g2 = new GuardBrick();
        g3 = new GuardBrick();
        g4 = new GuardBrick();
        eb1 = new EvilBrick();
        eb2 = new EvilBrick();
        eb3 = new EvilBrick();
        eb4 = new EvilBrick();
    }

    private BreakoutState makeState() {
        var grid = new BrickGrid(7, 7, 10000, 3000);
        return new BreakoutState(grid, 1000, 100, 70);
    }

    private ogp.balls.Ball addBall(BreakoutState state) {
        return state.addBall(
            new Circle(new Point(35000, 15000), 500),
            new Vector(10, -33),
            new StandardBehavior()
        );
    }

    // ---- constructor: links to owner ----

    @Test
    void constructorSetsOwner() {
        var b = new EvilBallBehavior(eb1);
        assertEquals(eb1, b.getOwner());
    }

    @Test
    void constructorAddsToEvilBrickEvilBallsList() {
        var b = new EvilBallBehavior(eb1);
        assertTrue(eb1.getEvilBalls().contains(b));
    }

    @Test
    void constructorUpdatesHpsOfLinkedGuardBrick() {
        g1.link(eb1);
        assertEquals(1, g1.getHps());
        new EvilBallBehavior(eb1);
        assertEquals(2, g1.getHps());
    }

    @Test
    void constructorWithNullOwnerThrows() {
        assertThrows(IllegalArgumentException.class, () -> new EvilBallBehavior(null));
    }

    // ---- unlink ----

    @Test
    void unlinkSetsOwnerToNull() {
        var b = new EvilBallBehavior(eb1);
        b.unlink();
        assertNull(b.getOwner());
    }

    @Test
    void unlinkRemovesFromEvilBrickEvilBallsList() {
        var b = new EvilBallBehavior(eb1);
        b.unlink();
        assertFalse(eb1.getEvilBalls().contains(b));
    }

    @Test
    void unlinkUpdatesGuardBrickHps() {
        g1.link(eb1);
        var b = new EvilBallBehavior(eb1);
        assertEquals(2, g1.getHps());
        b.unlink();
        assertEquals(1, g1.getHps());
    }

    @Test
    void unlinkTwiceDoesNotThrow() {
        var b = new EvilBallBehavior(eb1);
        b.unlink();
        assertDoesNotThrow(b::unlink);
    }

    // ---- speedModifier: no guards → 0 ----

    @Test
    void speedModifierIsZeroWhenNoGuards() {
        var b = new EvilBallBehavior(eb1);
        // eb1 has no guard bricks, numGuards = 0
        assertEquals(0, b.getSpeedModifier());
    }

    // ---- speedModifier: 1 guard, 1 evil brick ----
    // µ = |g1.evilBricks| = 1, numGuards = 1
    // µ < numGuards → 1 < 1 = false → sign = -1, raw = -1, clamp = -1

    @Test
    void speedModifierIsMinusOneWithOneGuardOneEvilBrick() {
        g1.link(eb1);
        var b = new EvilBallBehavior(eb1);
        assertEquals(-1, b.getSpeedModifier());
    }

    // ---- speedModifier: 2 guards, 1 evil brick ----
    // µ = max(1, 1) = 1, numGuards = 2
    // 1 < 2 = true → sign = 1, raw = 2, clamp = 2

    @Test
    void speedModifierIsTwoWithTwoGuardsOneEvilBrick() {
        g1.link(eb1);
        g2.link(eb1);
        var b = new EvilBallBehavior(eb1);
        assertEquals(2, b.getSpeedModifier());
    }

    // ---- speedModifier: 1 guard, 2 evil bricks ----
    // µ = |g1.evilBricks| = 2, numGuards = 1
    // 2 < 1 = false → sign = -1, raw = -1, clamp = -1

    @Test
    void speedModifierIsMinusOneWithOneGuardTwoEvilBricks() {
        g1.link(eb1);
        g1.link(eb2);
        var b = new EvilBallBehavior(eb1);
        assertEquals(-1, b.getSpeedModifier());
    }

    // ---- speedModifier clamped to 3 ----
    // 4 guards each with 1 evil brick: µ=1, numGuards=4, 1<4 → sign=1, raw=4, clamp=3

    @Test
    void speedModifierClampsToThree() {
        g1.link(eb1);
        g2.link(eb1);
        g3.link(eb1);
        g4.link(eb1);
        var b = new EvilBallBehavior(eb1);
        assertEquals(3, b.getSpeedModifier());
    }

    // ---- speedModifier updates after unlink ----

    @Test
    void speedModifierUpdatesAfterGuardUnlink() {
        g1.link(eb1);
        g2.link(eb1);
        var b = new EvilBallBehavior(eb1);
        assertEquals(2, b.getSpeedModifier()); // 2 guards, sign=1
        g2.unlink(eb1);
        // now 1 guard, 1 evil brick: µ=1, numGuards=1, sign=-1, raw=-1
        assertEquals(-1, b.getSpeedModifier());
    }

    // ---- speedModifier is 0 after owner unlinked ----

    @Test
    void speedModifierIsZeroAfterUnlink() {
        g1.link(eb1);
        var b = new EvilBallBehavior(eb1);
        b.unlink();
        assertEquals(0, b.getSpeedModifier());
    }

    // ---- bounceOffPaddle: loses 1 life ----

    @Test
    void bounceOffPaddleLosesOneLife() {
        var state = makeState();
        var gameBall = addBall(state);
        var b = new EvilBallBehavior(eb1);
        var collision = new Collision(0, Vector.KILO_UP);
        int hpsBefore = state.getHps();

        b.bounceOffPaddle(state, gameBall, collision);

        assertEquals(hpsBefore - 1, state.getHps());
    }

    // ---- bounceOffWall: speeds up by speedModifier ----

    @Test
    void bounceOffWallSpeedsUpBallWhenPositiveSpeedModifier() {
        // 2 guards → speedModifier = 2
        g1.link(eb1);
        g2.link(eb1);
        var b = new EvilBallBehavior(eb1);
        assertEquals(2, b.getSpeedModifier());

        var state = makeState();
        var gameBall = addBall(state);
        long speedBefore = gameBall.getVelocity().getSquaredLength();

        var collision = new Collision(0, Vector.KILO_RIGHT);
        b.bounceOffWall(state, gameBall, collision);

        long speedAfter = gameBall.getVelocity().getSquaredLength();
        assertTrue(speedAfter >= speedBefore);
    }

    @Test
    void bounceOffWallSlowsDownBallWhenNegativeSpeedModifier() {
        // 1 guard, 1 evil brick → speedModifier = -1
        g1.link(eb1);
        var b = new EvilBallBehavior(eb1);
        assertEquals(-1, b.getSpeedModifier());

        var state = makeState();
        // Give ball a fast velocity so slowing it down doesn't violate minimum
        var gameBall = state.addBall(
            new Circle(new Point(35000, 15000), 500),
            new Vector(50, -50),
            new StandardBehavior()
        );
        long speedBefore = gameBall.getVelocity().getSquaredLength();

        var collision = new Collision(0, Vector.KILO_RIGHT);
        b.bounceOffWall(state, gameBall, collision);

        long speedAfter = gameBall.getVelocity().getSquaredLength();
        assertTrue(speedAfter <= speedBefore);
    }

    // ---- ballLost: unlinks and removes ball from state ----

    @Test
    void ballLostRemovesBallFromState() {
        var state = makeState();
        var gameBall = addBall(state);
        var b = new EvilBallBehavior(eb1);
        gameBall.setBehavior(b);

        b.ballLost(state, gameBall);

        assertFalse(state.getBalls().contains(gameBall));
    }

    @Test
    void ballLostSetsOwnerToNull() {
        var state = makeState();
        var gameBall = addBall(state);
        var b = new EvilBallBehavior(eb1);
        gameBall.setBehavior(b);

        b.ballLost(state, gameBall);

        assertNull(b.getOwner());
    }

    @Test
    void ballLostRemovesBallFromEvilBrickEvilBallsList() {
        var state = makeState();
        var gameBall = addBall(state);
        var b = new EvilBallBehavior(eb1);
        gameBall.setBehavior(b);

        b.ballLost(state, gameBall);

        assertFalse(eb1.getEvilBalls().contains(b));
    }

    // ---- bidirectional consistency after multiple operations ----

    @Test
    void twoEvilBallsLinkedToSameEvilBrick() {
        var b1 = new EvilBallBehavior(eb1);
        var b2 = new EvilBallBehavior(eb1);
        assertEquals(2, eb1.getEvilBalls().size());
        assertTrue(eb1.getEvilBalls().contains(b1));
        assertTrue(eb1.getEvilBalls().contains(b2));
    }

    @Test
    void unlinkOneOfTwoEvilBallsLeavesOtherIntact() {
        g1.link(eb1);
        var b1 = new EvilBallBehavior(eb1);
        var b2 = new EvilBallBehavior(eb1);
        b1.unlink();
        assertEquals(1, eb1.getEvilBalls().size());
        assertTrue(eb1.getEvilBalls().contains(b2));
    }

    // ---- getColor ----

    @Test
    void getColorReturnsExtra1Constant() {
        var b = new EvilBallBehavior(eb1);
        assertEquals(EvilBallBehavior.COLOR, b.getColor());
    }

    @Test
    void colorConstantIsBlueViolet() {
        assertEquals(new java.awt.Color(138, 43, 226), EvilBallBehavior.COLOR);
    }

    // ---- mkEball static factory ----

    @Test
    void mkEballCreatesLinkedBall() {
        var b = EvilBallBehavior.mkEball(eb1);
        assertNotNull(b);
        assertEquals(eb1, b.getOwner());
    }

    @Test
    void mkEballAddsToEvilBrickList() {
        EvilBallBehavior.mkEball(eb1);
        assertEquals(1, eb1.getEvilBalls().size());
    }

    @Test
    void mkEballWithNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> EvilBallBehavior.mkEball(null));
    }

    // ---- speedModifier = 0 edge cases ----

    @Test
    void speedModifierZeroForNoGuards() {
        var b = new EvilBallBehavior(eb1);
        assertEquals(0, b.getSpeedModifier());
    }

    @Test
    void speedModifierZeroAfterUnlink() {
        g1.link(eb1);
        var b = new EvilBallBehavior(eb1);
        b.unlink();
        assertEquals(0, b.getSpeedModifier());
    }

    // ---- speedModifier clamped to -3 ----

    /**
     * 3 guards each linked to eb1, eb2, eb3.
     * For ball on eb1: numGuards=3, µ=max(3,3,3)=3, 3<3 false → sign=-1, raw=-3, clamp=-3.
     */
    @Test
    void speedModifierClampedToMinusThreeWhenMuEqualsNumGuards() {
        g1.link(eb1); g1.link(eb2); g1.link(eb3);
        g2.link(eb1); g2.link(eb2); g2.link(eb3);
        g3.link(eb1); g3.link(eb2); g3.link(eb3);

        var b = new EvilBallBehavior(eb1);
        assertEquals(-3, b.getSpeedModifier());
    }

    /**
     * 4 guards each linked to eb1,eb2,eb3,eb4.
     * numGuards=4, µ=4, 4<4 false → sign=-1, raw=-4, clamp=-3.
     */
    @Test
    void speedModifierClampedToMinusThreeWhenMuFour() {
        EvilBrick eb3local = new EvilBrick();
        EvilBrick eb4local = new EvilBrick();
        g1.link(eb1); g1.link(eb2); g1.link(eb3local); g1.link(eb4local);
        g2.link(eb1); g2.link(eb2); g2.link(eb3local); g2.link(eb4local);
        g3.link(eb1); g3.link(eb2); g3.link(eb3local); g3.link(eb4local);
        g4.link(eb1); g4.link(eb2); g4.link(eb3local); g4.link(eb4local);

        var b = new EvilBallBehavior(eb1);
        // numGuards=4, µ=4, 4<4 false → sign=-1, raw=-4, clamp=-3
        assertEquals(-3, b.getSpeedModifier());
    }

    // ---- speedModifier clamped to +3 ----

    /**
     * 4 guards each only linked to eb1.
     * numGuards=4, µ=max(1,1,1,1)=1, 1<4 true → sign=1, raw=4, clamp=3.
     */
    @Test
    void speedModifierClampedToThreeWithFourGuardsOneEvilBrick() {
        g1.link(eb1);
        g2.link(eb1);
        g3.link(eb1);
        g4.link(eb1);
        var b = new EvilBallBehavior(eb1);
        assertEquals(3, b.getSpeedModifier());
    }

    // ---- bounceOffWall with speedModifier = 0 ----

    @Test
    void bounceOffWallWithModifierZeroDoesNotChangeSpeed() {
        // No guards → speedModifier = 0
        var b = new EvilBallBehavior(eb1);
        assertEquals(0, b.getSpeedModifier());

        var state = makeState();
        var gameBall = addBall(state);
        long speedBefore = gameBall.getVelocity().getSquaredLength();

        b.bounceOffWall(state, gameBall, new Collision(0, Vector.KILO_RIGHT));

        assertEquals(speedBefore, gameBall.getVelocity().getSquaredLength());
    }

    // ---- bounceOffWall with speedModifier = +3 ----

    @Test
    void bounceOffWallWithModifierThreeIncreasesSpeed() {
        g1.link(eb1); g2.link(eb1); g3.link(eb1); g4.link(eb1);
        var b = new EvilBallBehavior(eb1);
        assertEquals(3, b.getSpeedModifier());

        var state = makeState();
        var gameBall = state.addBall(
            new Circle(new Point(35000, 15000), 500),
            new Vector(10, -10),
            new StandardBehavior()
        );
        long speedBefore = gameBall.getVelocity().getSquaredLength();

        b.bounceOffWall(state, gameBall, new Collision(0, Vector.KILO_RIGHT));

        assertTrue(gameBall.getVelocity().getSquaredLength() >= speedBefore);
    }

    // ---- ballLost decreases guard hps ----

    @Test
    void ballLostDecreasesGuardBrickHps() {
        g1.link(eb1);
        var b = new EvilBallBehavior(eb1);
        int hpsBefore = g1.getHps(); // 2

        var state = makeState();
        var gameBall = addBall(state);
        gameBall.setBehavior(b);
        b.ballLost(state, gameBall);

        assertEquals(hpsBefore - 1, g1.getHps());
    }

    @Test
    void ballLostRemovesBallFromEvilBrickList() {
        var b = new EvilBallBehavior(eb1);

        var state = makeState();
        var gameBall = addBall(state);
        gameBall.setBehavior(b);
        b.ballLost(state, gameBall);

        assertFalse(eb1.getEvilBalls().contains(b));
    }

    // ---- speedModifier updates dynamically ----

    @Test
    void speedModifierUpdatesWhenSecondGuardLinked() {
        g1.link(eb1);
        var b = new EvilBallBehavior(eb1);
        // 1 guard, µ=1, 1<1 false → -1
        assertEquals(-1, b.getSpeedModifier());

        g2.link(eb1);
        // 2 guards, µ=1, 1<2 true → +2
        assertEquals(2, b.getSpeedModifier());
    }

    @Test
    void speedModifierUpdatesWhenGuardUnlinked() {
        g1.link(eb1); g2.link(eb1);
        var b = new EvilBallBehavior(eb1);
        assertEquals(2, b.getSpeedModifier());

        g2.unlink(eb1);
        // back to 1 guard, µ=1, 1<1 false → -1
        assertEquals(-1, b.getSpeedModifier());
    }
}
