package ogp;

import java.util.ArrayList;
import java.util.stream.Stream;

import ogp.balls.Ball;
import ogp.bricks.Brick;
import ogp.bricks.BrickKind;
import ogp.math.Point;
import ogp.math.Rectangle;
import ogp.math.Vector;
import ogp.util.Grid;
import ogp.util.MPOOPLegitGenerated;
import ogp.util.SpecUtil;

/**
 * Manages a grid of bricks in the Breakout game.
 * 
 * @invar | getBrickWidth() > 0
 * @invar | getBrickHeight() > 0
 * @invar | getColumnCount() > 0
 * @invar | getRowCount() > 0
 * @invar | getWidth() == getColumnCount() * getBrickWidth()
 * @invar | getHeight() == getRowCount() * getBrickHeight()
 */
public class BrickGrid
{

    private final Grid<Brick> grid;

    private final int brickWidth;

    private final int brickHeight;

    /**
     * Constructor for BrickGrid.
     * 
     * @pre | columnCount > 0
     * @pre | rowCount > 0
     * @pre | brickWidth > 0
     * @pre | brickHeight > 0
     * @post | getColumnCount() == columnCount
     * @post | getRowCount() == rowCount
     * @post | getBrickWidth() == brickWidth
     * @post | getBrickHeight() == brickHeight
     * @throws IllegalArgumentException | columnCount <= 0
     * @throws IllegalArgumentException | rowCount <= 0
     * @throws IllegalArgumentException | brickWidth <= 0
     * @throws IllegalArgumentException | brickHeight <= 0
     */
    public BrickGrid(int columnCount, int rowCount, int brickWidth, int brickHeight)
    {
        if (columnCount <= 0 || rowCount <= 0 || brickWidth <= 0 || brickHeight <= 0) {
            throw new IllegalArgumentException("All dimensions must be positive");
        }
        this.brickWidth = brickWidth;
        this.brickHeight = brickHeight;
        this.grid = new Grid<Brick>(columnCount, rowCount);
    }

    /**
     * Returns the width of each brick.
     * 
     * @post | result > 0
     */
    public int getBrickWidth()
    {
        return brickWidth;
    }

    /**
     * Returns the height of each brick.
     * 
     * @post | result > 0
     */
    public int getBrickHeight()
    {
        return brickHeight;
    }

    /**
     * Returns the number of columns in the grid.
     * 
     * @post | result > 0
     */
    public int getColumnCount()
    {
        return grid.getColumnCount();
    }

    /**
     * Returns the number of rows in the grid (number of bricks vertically).
     * 
     * @post | result > 0
     */
    public int getRowCount()
    {
        return grid.getRowCount();
    }

    /**
     * Returns the width of the grid in game units.
     * 
     * @post | result == getColumnCount() * getBrickWidth()
     */
    public int getWidth()
    {
       return getColumnCount() * brickWidth;
    }

    /**
     * Returns the height of the grid in game units.
     * 
     * @post | result == getRowCount() * getBrickHeight()
     */
    public int getHeight()
    {
        return getRowCount() * brickHeight;
    }

    /**
     * Returns the brick at the given grid position.
     * 
     * @pre | gridPosition != null
     * @pre | isValidGridPosition(gridPosition)
     * @creates | result
     * @post | result == null || result != null
     */
    public Brick getBrickAt(Point gridPosition)
    {
        return grid.at(gridPosition);
    }

    /**
     * Checks if the given grid position is valid.
     * 
     * @inspects | gridPosition
     * @pre | gridPosition != null
     * @post | result == (0 <= gridPosition.x() && gridPosition.x() < getColumnCount() && 0 <= gridPosition.y() && gridPosition.y() < getRowCount())
     */
    public boolean isValidGridPosition(Point gridPosition)
    {
        return this.grid.isValidPosition(gridPosition);
    }

    /**
     * Returns the brick at the given grid position if it exists, or null otherwise.
     *
     * @inspects | gridPosition
     * @pre | gridPosition != null
     * @creates | result
     * @post | !isValidGridPosition(gridPosition) ==> result == null
     */
    public Brick getBrickAtGridPositionOrNull(Point gridPosition)
    {
        if (!isValidGridPosition(gridPosition)) {
            return null;
        }
        return grid.at(gridPosition);
    }

    /**
     * LEGIT
     */
    @MPOOPLegitGenerated
    public Collision findEarliestCollision(Ball ball)
    {
        var earliestHorizontalCollision = findEarliestHorizontalCollision(ball);
        var earliestVerticalCollision = findEarliestVerticalCollision(ball);

        return Collision.getEarliestCollision(earliestHorizontalCollision, earliestVerticalCollision);
    }

    /**
     * LEGIT
     */
    @MPOOPLegitGenerated
    private Collision findEarliestVerticalCollision(Ball ball)
    {
        if ( ball.getVelocity().y() < 0 )
        {
            return findEarliestUpwardsCollision(ball);
        }
        else if ( ball.getVelocity().y() > 0 )
        {
            return findEarliestDownwardsCollision(ball);
        }
        else
        {
            return null;
        }
    }

    /**
     * LEGIT
     */
    @MPOOPLegitGenerated
    private Collision findEarliestHorizontalCollision(Ball ball)
    {
        if ( ball.getVelocity().x() < 0 )
        {
            return findEarliestLeftwardsCollision(ball);
        }
        else if ( ball.getVelocity().x() > 0 )
        {
            return findEarliestRightwardsCollision(ball);
        }
        else
        {
            return null;
        }
    }

    /**
     * LEGIT
     */
    @MPOOPLegitGenerated
    private Collision findEarliestUpwardsCollision(Ball ball)
    {
        var v = ball.getVelocity();
        var p = ball.getGeometry().getPointInDirection(v);
        var y = p.y() / this.brickHeight * this.brickHeight;

        while ( y > 0 )
        {
            var preciseT = (y - p.y()) * 1000 / v.y();
            var x = p.x() + v.x() * preciseT / 1000;
            var brickGridPosition = new Point(Math.floorDiv(x, brickWidth), Math.floorDiv(y, brickHeight) - 1);
            var brick = getBrickAtGridPositionOrNull(brickGridPosition);

            if ( brick != null )
            {
                return new Collision(preciseT / 1000, Vector.KILO_DOWN, brick);
            }

            y -= this.brickHeight;
        }

        return null;
    }

    /**
     * LEGIT
     */
    @MPOOPLegitGenerated
    private Collision findEarliestDownwardsCollision(Ball ball)
    {
        var v = ball.getVelocity();
        var p = ball.getGeometry().getPointInDirection(v);
        var y = (p.y() + this.brickHeight - 1) / this.brickHeight * this.brickHeight;
        var yMax = this.getHeight();

        while ( y < yMax )
        {
            var preciseT = (y - p.y()) * 1000 / v.y();
            var x = p.x() + v.x() * preciseT / 1000;
            var brickGridPosition = new Point(Math.floorDiv(x, brickWidth), Math.floorDiv(y, brickHeight));
            var brick = getBrickAtGridPositionOrNull(brickGridPosition);

            if ( brick != null )
            {
                return new Collision(preciseT / 1000, Vector.KILO_UP, brick);
            }

            y += this.brickHeight;
        }

        return null;
    }

    /**
     * LEGIT
     */
    @MPOOPLegitGenerated
    private Collision findEarliestLeftwardsCollision(Ball ball)
    {
        var v = ball.getVelocity();
        var p = ball.getGeometry().getPointInDirection(v);
        var x = p.x() / this.brickWidth * this.brickWidth;

        while ( x > 0 )
        {
            var preciseT = (x - p.x()) * 1000 / v.x();
            var y = p.y() + v.y() * preciseT / 1000;
            var brickGridPosition = new Point(Math.floorDiv(x, brickWidth) - 1, Math.floorDiv(y, brickHeight));
            var brick = getBrickAtGridPositionOrNull(brickGridPosition);

            if ( brick != null )
            {
                return new Collision(preciseT / 1000, Vector.KILO_RIGHT, brick);
            }

            x -= this.brickWidth;
        }

        return null;
    }

    /**
     * LEGIT
     */
    @MPOOPLegitGenerated
    private Collision findEarliestRightwardsCollision(Ball ball)
    {
        var v = ball.getVelocity();
        var p = ball.getGeometry().getPointInDirection(v);
        var x = (p.x() + this.brickWidth - 1) / this.brickWidth * this.brickWidth;
        var xMax = this.getWidth();

        while ( x < xMax )
        {
            var preciseT = (x - p.x()) * 1000 / v.x();
            var y = p.y() + v.y() * preciseT / 1000;
            var brickGridPosition = new Point(Math.floorDiv(x, brickWidth), Math.floorDiv(y, brickHeight));
            var brick = getBrickAtGridPositionOrNull(brickGridPosition);

            if ( brick != null )
            {
                return new Collision(preciseT / 1000, Vector.KILO_LEFT, brick);
            }

            x += this.brickWidth;
        }

        return null;
    }

    /**
     * Checks whether there is a brick at the given position.
     * This method returns false for positions outside the grid.
     *
     * @inspects | gridPosition
     * @pre | gridPosition != null
     * @post | result == (isValidGridPosition(gridPosition) && getBrickAt(gridPosition) != null)
     */
    public boolean containsBrickAt(Point gridPosition)
    {
        return isValidGridPosition(gridPosition) && grid.at(gridPosition) != null;
    }

    /**
     * Adds a standard brick at the given grid position.
     * 
     * @pre | isValidGridPosition(gridPosition)
     * @creates | result
     * @post | result != null
     * @post | result.getBrickKind() == BrickKind.STANDARD
     * @post | result.getGridPosition().equals(gridPosition)
     * @mutates | this
     */
    public Brick addStandardBrick(Point gridPosition)
    {
    	var brick = new Brick(getBrickRectangle(gridPosition), gridPosition, BrickKind.STANDARD);
    	grid.setAt(gridPosition, brick);
    	return brick;
    }

    /**
     * Adds a spikey brick at the given grid position.
     * 
     * @pre | isValidGridPosition(gridPosition)
     * @creates | result
     * @post | result != null
     * @post | result.getBrickKind() == BrickKind.SPIKEY
     * @post | result.getGridPosition().equals(gridPosition)
     * @mutates | this
     */
    public Brick addSpikeyBrick(Point gridPosition)
    {
    	var brick = new Brick(getBrickRectangle(gridPosition), gridPosition, BrickKind.SPIKEY);
    	grid.setAt(gridPosition, brick);
    	return brick;
    }

    /**
     * Adds an invert paddle brick at the given grid position.
     * 
     * @pre | isValidGridPosition(gridPosition)
     * @creates | result
     * @post | result != null
     * @post | result.getBrickKind() == BrickKind.INVERTPADDLE
     * @post | result.getGridPosition().equals(gridPosition)
     * @mutates | this
     */
    public Brick addInvertPaddleBrick(Point gridPosition)
    {
    	var brick = new Brick(getBrickRectangle(gridPosition), gridPosition, BrickKind.INVERTPADDLE);
    	grid.setAt(gridPosition, brick);
    	return brick;
    }

    /**
     * Adds a shrink paddle brick at the given grid position.
     * 
     * @pre | isValidGridPosition(gridPosition)
     * @creates | result
     * @post | result != null
     * @post | result.getBrickKind() == BrickKind.SHRINKPADDLE
     * @post | result.getGridPosition().equals(gridPosition)
     * @mutates | this
     */
    public Brick addShrinkPaddleBrick(Point gridPosition)
    {
    	var brick = new Brick(getBrickRectangle(gridPosition), gridPosition, BrickKind.SHRINKPADDLE);
    	grid.setAt(gridPosition, brick);
    	return brick;
    }

    /**
     * Adds a spawn ball brick at the given grid position.
     * 
     * @pre | isValidGridPosition(gridPosition)
     * @creates | result
     * @post | result != null
     * @post | result.getBrickKind() == BrickKind.SPAWNBALL
     * @post | result.getGridPosition().equals(gridPosition)
     * @mutates | this
     */
    public Brick addSpawnBallBrick(Point gridPosition)
    {
    	var brick = new Brick(getBrickRectangle(gridPosition), gridPosition, BrickKind.SPAWNBALL);
    	grid.setAt(gridPosition, brick);
    	return brick;
    }

    /**
     * Returns the bounding rectangle for a brick at the given grid coordinates.
     * 
     * @creates | result
     * @inspects | gridCoordinates
     * @pre | gridCoordinates != null
     * @post | result != null
     * @post | result.getLeft() == gridCoordinates.x() * getBrickWidth()
     * @post | result.getTop() == gridCoordinates.y() * getBrickHeight()
     * @post | result.getWidth() == getBrickWidth()
     * @post | result.getHeight() == getBrickHeight()
     */
    public Rectangle getBrickRectangle(Point gridCoordinates)
    {
        return new Rectangle(
            gridCoordinates.x() * brickWidth,
            gridCoordinates.y() * brickHeight,
            brickWidth,
            brickHeight
        );
    }

    /**
     * LEGIT
     * Checks whether there are any bricks left.
     *
     */
    public boolean isEmpty()
    {
        return !grid.getPositionStream().anyMatch(this::containsBrickAt);
    }

    /**
     * Returns all bricks from the grid in a list.
     *
     * @creates | result
     * @post | result != null
     * @post | SpecUtil.sameElementsInPossiblyDifferentOrder(enumerateGridPositions().map(this::getBrickAt).filter(x -> x != null).toList(), result)
     */
    public ArrayList<Brick> getBricks()
    {
        return new ArrayList<Brick>(this.grid.getPositionStream().map(this.grid::at).filter(b -> b != null).toList());
    }

    /**
     * Returns the smallest rectangle that encompasses the entire grid.
     * 
     * @creates | result
     * @post | result != null
     * @post | result.getLeft() == 0
     * @post | result.getTop() == 0
     * @post | result.getWidth() == getWidth()
     * @post | result.getHeight() == getHeight()
     */
    public Rectangle getBoundingRectangle()
    {
        return new Rectangle(0, 0, getWidth(), getHeight());
    }

    /**
     * Removes the brick at the given grid position.
     * 
     * @pre | isValidGridPosition(gridPosition)
     * @mutates | this
     */
    public void removeBrickAt(Point gridPosition)
    {
        grid.setAt(gridPosition, null);
    }

    /**
     * Removes the brick from the grid.
     * 
     * @pre | brick != null
     * @pre | getBricks().contains(brick)
     * @mutates | this
     */
    public void removeBrick(Brick brick)
    {
        removeBrickAt(brick.getGridPosition());
    }

    /**
     * LEGIT
     */
    @MPOOPLegitGenerated
    public Stream<Point> enumerateGridPositions()
    {
        return this.grid.getPositionStream();
    }
}
