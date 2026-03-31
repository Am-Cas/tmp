package ogp;

import static ogp.util.SpecUtil.*;

import ogp.math.Vector;
import ogp.util.MPOOPLegitGenerated;

/**
 * Objects of this class contain collision-related information.
 * Collisions can happen between balls and walls & paddles & bricks.
 * Note that there is a more specialized BrickCollision class for ball/brick collisions.
 *
 * @immutable
 *
 * @invar | getMillisecondsUntilCollision() >= 0
 * @invar | getKiloNormal() != null
 * @invar | getKiloNormal().isKiloUnitVector()
 */
public class Collision
{
    /**
     * Time until collision.
     *
     * @invar | millisecondsUntilCollision >= 0
     */
    private final long millisecondsUntilCollision;

    /**
     * Normal vector on the surface that was hit.
     * The vector must have size approximately 1000, see {@link Vector#isKiloUnitVector()}.
     *
     * @invar | kiloNormal != null
     * @invar | kiloNormal.isKiloUnitVector()
     */
    private final Vector kiloNormal;

    /**
     * @throws IllegalArgumentException | kiloNormal == null
     * @throws IllegalArgumentException | millisecondsUntilCollision < 0
     * @post | getMillisecondsUntilCollision() == millisecondsUntilCollision
     * @post | getKiloNormal() == kiloNormal
     */
    public Collision(long millisecondsUntilCollision, Vector kiloNormal)
    {
        if (kiloNormal == null) throw new IllegalArgumentException();
        if (millisecondsUntilCollision < 0) throw new IllegalArgumentException();

        this.millisecondsUntilCollision = millisecondsUntilCollision;
        this.kiloNormal = kiloNormal;
    }

    /**
     * @post | result >= 0
     */
    public long getMillisecondsUntilCollision()
    {
        return millisecondsUntilCollision;
    }

    /**
     * @post | result != null
     * @post | result.isKiloUnitVector()
     */
    public Vector getKiloNormal()
    {
        return this.kiloNormal;
    }

    /**
     * Returns the "earliest" collision, i.e., the one with the lowest milliseconds until collision.
     * Note that the parameters can be null.
     *
     * @post | implies(c1 == null, result == c2)
     * @post | implies(c2 == null, result == c1)
     * @post | implies(c1 != null && c2 != null && c1.getMillisecondsUntilCollision() <= c2.getMillisecondsUntilCollision(), result == c1)
     * @post | implies(c1 != null && c2 != null && c1.getMillisecondsUntilCollision() > c2.getMillisecondsUntilCollision(), result == c2)
     */
    public static <T extends Collision> T getEarliestCollision(T c1, T c2)
    {
        if (c1 == null)
        {
            return c2;
        }
        else if (c2 == null)
        {
            return c1;
        }
        else if (c1.getMillisecondsUntilCollision() <= c2.getMillisecondsUntilCollision())
        {
            return c1;
        }
        else
        {
            return c2;
        }
    }

    /**
     * LEGIT
     */
    @MPOOPLegitGenerated
    @Override
    public String toString()
    {
        return String.format("Collision(t=%d, n=%s)", millisecondsUntilCollision, kiloNormal);
    }
}
