package ogp.math;

import java.util.Objects;

import ogp.util.MPOOPLegitGenerated;

/**
 * This class represents a point on a 2-dimensional integer grid.
 *
 * @immutable
 */
public class Point
{
	
	public static Point O() {
		return new Point(0,0);
	}
	
    private final long x;
    private final long y;

    /**
     * Return a new Point with given x and y coordinates.
     *
     * @post | x() == x
     * @post | y() == y
     */
    public Point(long x, long y)
    {
        this.x = x;
        this.y = y;
    }

    /** Return this point's x coordinate. */
    public long x()
    {
        return x;
    }

    /** Return this point's y coordinate. */
    public long y()
    {
        return y;
    }

    /**
     * Return the point obtained by adding vector `v` to this point.
     * 
     * @pre | v != null
     * @creates | result
     * @post | result != null
     * @post | result.x() == x() + v.x() / 1000
     * @post | result.y() == y() + v.y() / 1000
     */
    public Point add(Vector v)
    {
        return new Point(x + v.x() / 1000, y + v.y() / 1000);
    }

    /**
     * Return the point obtained by adding vector `- v` to this point.
     * 
     * @pre | v != null
     * @creates | result
     * @post | result != null
     * @post | result.x() == x() - v.x() / 1000
     * @post | result.y() == y() - v.y() / 1000
     */
    public Point subtract(Vector v)
    {
        return new Point(x - v.x() / 1000, y - v.y() / 1000);
    }

    /**
     * Return a new point moved down by dy.
     * 
     * @creates | result
     * @post | result != null
     * @post | result.x() == x()
     * @post | result.y() == y() + dy
     */
    public Point moveDown(int dy)
    {
        return new Point(x, y + dy);
    }

    /**
     * Return a new point moved up by dy.
     * 
     * @creates | result
     * @post | result != null
     * @post | result.x() == x()
     * @post | result.y() == y() - dy
     */
    public Point moveUp(int dy)
    {
        return new Point(x, y - dy);
    }

    /**
     * Return a new point moved left by dx.
     * 
     * @creates | result
     * @post | result != null
     * @post | result.x() == x() - dx
     * @post | result.y() == y()
     */
    public Point moveLeft(int dx)
    {
        return new Point(x - dx, y);
    }

    /**
     * Return a new point moved right by dx.
     * 
     * @creates | result
     * @post | result != null
     * @post | result.x() == x() + dx
     * @post | result.y() == y()
     */
    public Point moveRight(int dx)
    {
        return new Point(x + dx, y);
    }

    /**
     * Return a copy of this point.
     * 
     * @creates | result
     * @post | result != null
     * @post | result.equals(this)
     */
    public Point copy()
    {
        return new Point(x, y);
    }

    /**
     * LEGIT
     */
    @Override
    @MPOOPLegitGenerated
    public int hashCode()
    {
        return Objects.hash(x, y);
    }

    /**
     * LEGIT
     */
    @Override
    @MPOOPLegitGenerated
    public boolean equals(Object obj)
    {
        if ( obj instanceof Point that )
        {
            return this.x == that.x && this.y == that.y;
        }
        else
        {
            return false;
        }
    }

    /**
     * Return a string representation of this point.
     *
     * LEGIT
     */
    @Override
    @MPOOPLegitGenerated
    public String toString()
    {
        return "(" + x + "," + y + ")";
    }
}
