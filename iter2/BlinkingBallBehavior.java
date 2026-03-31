package ogp.balls;

import java.awt.Color;

import ogp.BreakoutState;
import ogp.BrickCollision;
import ogp.ui.Canvas;
import ogp.util.MPOOPLegitGenerated;

/**
 * A blinking ball alternates between a "weak visible state" and
 * a "normal but invisible state" every 900ms.
 * At spawn the ball is in the weak-visible state.
 * The behavior lasts 5 seconds before reverting to standard.
 *
 * @invar | getTimeLeft() >= 0
 */
public class BlinkingBallBehavior extends TemporaryBehavior
{
    public static final Color COLOR = Color.GRAY;

    public static final int DURATION = 5000;
    public static final int BLINK_TIME = 900;

    /**
     * @post | getTimeLeft() == BlinkingBallBehavior.DURATION
     */
    public BlinkingBallBehavior()
    {
        super(DURATION);
    }

    /**
     * In weak-visible state: moves ball to impact and notifies the brick it was hit.
     * In normal-invisible state: does nothing (ball passes through bricks).
     *
     * @pre | state != null
     * @pre | ball != null
     * @pre | collision != null
     * @mutates | ball
     * @mutates | state
     */
    @Override
    public void bounceOffBrick(BreakoutState state, Ball ball, BrickCollision collision)
    {
        BlinkState bstate = computeBlinkStatePriv();
        switch (bstate)
        {
        case NORMAL_INV:
            // pass through: do nothing, don't even move to impact
            break;
        case WEAK_VIS:
            ball.move(collision.getMillisecondsUntilCollision());
            collision.getBrick().hit(state, ball);
            // no velocity reflection for weak-visible
            break;
        }
    }

    /**
     * Paints the ball only if it is in the weak-visible state (gray).
     * In normal-invisible state the ball is not painted.
     *
     * NOSPEC
     */
    @Override
    public void paint(Canvas can, Ball ball)
    {
        if (isWeakVisible())
        {
            can.drawFilledCircle(getColor(), ball.getGeometry());
        }
        // normal-invisible: don't paint
    }

    /**
     * Weak balls are colored gray.
     *
     * @post | result != null
     * @post | result.equals(BlinkingBallBehavior.COLOR)
     */
    @Override
    public Color getColor()
    {
        return BlinkingBallBehavior.COLOR;
    }

    /**
     * LEGIT
     */
    @MPOOPLegitGenerated
    public boolean isWeakVisible()
    {
        return computeBlinkStatePriv() == BlinkState.WEAK_VIS;
    }

    private BlinkState computeBlinkStatePriv()
    {
        int activeSince = DURATION - this.getTimeLeft();
        int aux = (activeSince / BLINK_TIME) % 2;
        if (aux == 1)
        {
            return BlinkState.WEAK_VIS;
        }
        else
        {
            return BlinkState.NORMAL_INV;
        }
    }

    private static enum BlinkState
    {
        NORMAL_INV, WEAK_VIS;
    }
}
