package ogp;

import static ogp.util.SpecUtil.*;

import ogp.bricks.Brick;
import ogp.math.Vector;
import ogp.util.MPOOPLegitGenerated;

/**
 * Objects of this class contain collision-related information.
 * Collisions can happen between balls and walls & paddles & bricks.
 *
 * @invar | getMillisecondsUntilCollision() >= 0
 * @invar | getKiloNormal() != null
 * @invar | getKiloNormal().isKiloUnitVector()
 */
public class Collision
{
	/**
	 * not null iff brick collision
	 * 
	 * @invar | brick == null || brick != null
	 */
	private final Brick brick;
	
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
     * Constructor for a non-brick collision object.
     * 
     * @pre | millisecondsUntilCollision >= 0
     * @pre | kiloNormal != null
     * @pre | kiloNormal.isKiloUnitVector()
     * @post | getMillisecondsUntilCollision() == millisecondsUntilCollision
     * @post | getKiloNormal().equals(kiloNormal)
     * @post | getBrick() == null
     * @throws IllegalArgumentException | millisecondsUntilCollision < 0
     * @throws IllegalArgumentException | kiloNormal == null
     * @throws IllegalArgumentException | !kiloNormal.isKiloUnitVector()
     */
    public Collision(long millisecondsUntilCollision, Vector kiloNormal)
    {
        if ( millisecondsUntilCollision < 0 )
        {
            throw new IllegalArgumentException("millisecondsUntilCollision cannot be negative");
        }

        if ( kiloNormal == null || !kiloNormal.isKiloUnitVector() )
        {
            throw new IllegalArgumentException("kiloNormal must be a kilo unit vector");
        }

        this.millisecondsUntilCollision = millisecondsUntilCollision;
        this.kiloNormal = kiloNormal;
        this.brick = null;
    }

    /**
     * Constructor for any collision (wall, paddle, or brick).
     * 
     * @pre | millisecondsUntilCollision >= 0
     * @pre | kiloNormal != null
     * @pre | kiloNormal.isKiloUnitVector()
     * @post | getMillisecondsUntilCollision() == millisecondsUntilCollision
     * @post | getKiloNormal().equals(kiloNormal)
     * @post | getBrick() == brick
     * @throws IllegalArgumentException | millisecondsUntilCollision < 0
     * @throws IllegalArgumentException | kiloNormal == null
     * @throws IllegalArgumentException | !kiloNormal.isKiloUnitVector()
     */
    public Collision(long millisecondsUntilCollision, Vector kiloNormal, Brick brick)
    {
        if ( millisecondsUntilCollision < 0 )
        {
            throw new IllegalArgumentException("millisecondsUntilCollision cannot be negative");
        }

        if ( kiloNormal == null || !kiloNormal.isKiloUnitVector() )
        {
            throw new IllegalArgumentException("kiloNormal must be a kilo unit vector");
        }

        this.millisecondsUntilCollision = millisecondsUntilCollision;
        this.kiloNormal = kiloNormal;
        this.brick = brick;
    }

    /**
     * Returns the time until collision in milliseconds.
     * 
     * @post | result >= 0
     */
    public long getMillisecondsUntilCollision()
    {
        return millisecondsUntilCollision;
    }

    /**
     * Returns the collision normal vector (scaled to approximately 1000).
     * 
     * @creates | result
     * @post | result != null
     * @post | result.equals(kiloNormal)
     */
    public Vector getKiloNormal()
    {
        return kiloNormal;
    }
    
    /**
     * Returns the brick involved in this collision, or null if no brick.
     * 
     * @post | result == null || result != null
     */
    public Brick getBrick()
    {
        return brick;
    }

    /**
     * LEGIT
     * 
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
        if ( c1 == null )
        {
            return c2;
        }
        else if ( c2 == null )
        {
            return c1;
        }
        else if ( c1.getMillisecondsUntilCollision() <= c2.getMillisecondsUntilCollision() )
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
    @Override
    @MPOOPLegitGenerated
    public String toString()
    {
    	if (this.brick == null) {
    		return String.format("Collision(t=%d, n=%s)", millisecondsUntilCollision, kiloNormal);    		
    	}
    	else {
    		return String.format("Collision(t=%d, n=%s, p=%s)", getMillisecondsUntilCollision(), getKiloNormal(), getBrick().getGridPosition());    		
    	}
    }
}
