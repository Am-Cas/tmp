package ogp;

import ogp.bricks.Brick;
import ogp.math.Vector;
import ogp.util.MPOOPLegitGenerated;

/**
 * Objects of this class contain data about the collision
 * between a ball and a brick.
 *
 * @immutable
 *
 * @invar | getBrick() != null
 */
public class BrickCollision extends Collision
{
    /**
     * @invar | brick != null
     */
    private final Brick brick;

    /**
     * @throws IllegalArgumentException | brick == null
     * @throws IllegalArgumentException | kiloNormal == null
     * @throws IllegalArgumentException | time < 0
     * @post | getBrick() == brick
     * @post | getMillisecondsUntilCollision() == time
     * @post | getKiloNormal() == kiloNormal
     */
    public BrickCollision(long time, Vector kiloNormal, Brick brick)
    {
        super(time, kiloNormal);
        if (brick == null) throw new IllegalArgumentException();
        this.brick = brick;
    }

    /**
     * @post | result != null
     */
    public Brick getBrick()
    {
        return this.brick;
    }

    /**
     * LEGIT
     */
    @MPOOPLegitGenerated
    @Override
    public String toString()
    {
        return String.format("Collision(t=%d, n=%s, p=%s)", getMillisecondsUntilCollision(), getKiloNormal(), getBrick().getGridPosition());
    }
}
