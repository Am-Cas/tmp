package ogp.balls;

import ogp.BreakoutState;
import ogp.util.MPOOPLegitGenerated;
import ogp.util.SpecUtil;

/**
 * Convenience supertype for temporary behaviors.
 * After a certain amount of time has elapsed, a temporary behavior
 * causes the ball to revert back to the standard behavior.
 *
 * @invar | getTimeLeft() >= 0
 */
public class TemporaryBehavior extends BallBehavior
{
    /**
     * Number of milliseconds this behavior is still active.
     *
     * @invar | timeLeft >= 0
     */
    private int timeLeft;

    /**
     * @throws IllegalArgumentException | duration < 0
     * @post | getTimeLeft() == duration
     */
    public TemporaryBehavior(int duration)
    {
        if (duration < 0) throw new IllegalArgumentException();
        this.timeLeft = duration;
    }

    /**
     * @post | result >= 0
     */
    public int getTimeLeft()
    {
        return this.timeLeft;
    }

    /**
     * LEGIT
     *
     * @pre | state != null
     * @pre | ball != null
     * @pre | state.getBalls().contains(ball)
     * @pre | elapsedMilliseconds >= 0
     * @mutates | this
     * @mutates | state
     * @mutates | ball
     * @post | getTimeLeft() == Math.max(0, old(getTimeLeft()) - elapsedMilliseconds)
     * @post | SpecUtil.implies(getTimeLeft() == 0, ball.getBehavior() != this)
     */
    @MPOOPLegitGenerated
    @Override
    public void update(BreakoutState state, Ball ball, long elapsedMilliseconds)
    {
        if (timeLeft <= elapsedMilliseconds)
        {
            super.update(state, ball, timeLeft);
            var standardBehavior = new StandardBehavior();
            ball.setBehavior(standardBehavior);
            ball.tick(state, elapsedMilliseconds - timeLeft);
            timeLeft = 0;
        }
        else
        {
            super.update(state, ball, elapsedMilliseconds);
            timeLeft -= elapsedMilliseconds;
        }
    }
}
