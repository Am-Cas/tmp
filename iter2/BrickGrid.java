package ogp;

import java.util.ArrayList;
import java.util.stream.Stream;

import ogp.balls.Ball;
import ogp.bricks.Brick;
import ogp.bricks.InvertPaddleBrick;
import ogp.bricks.ShrinkPaddleBrick;
import ogp.bricks.SpawnBallBrick;
import ogp.bricks.SpeedUpBrick;
import ogp.bricks.StandardBrick;
import ogp.bricks.StrengtheningBrick;
import ogp.bricks.SpikeyBrick;
import ogp.bricks.BlinkingBallBrick;
import ogp.math.Point;
import ogp.math.Rectangle;
import ogp.math.Vector;
import ogp.util.Grid;
import ogp.util.MPOOPLegitGenerated;
import ogp.util.SpecUtil;

/**
 * Represents the grid of bricks in the breakout game.
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
    /**
     * @invar | grid != null
     */
    private final Grid<Brick> grid;

    /**
     * @invar | brickWidth > 0
     */
    private final int brickWidth;

    /**
     * @invar | brickHeight > 0
     */
    private final int brickHeight;

    /**
     * Creates a new BrickGrid with the specified dimensions.
     * All cells are initially empty (no bricks).
     *
     * @throws IllegalArgumentException | columnCount <= 0
     * @throws IllegalArgumentException | rowCount <= 0
     * @throws IllegalArgumentException | brickWidth <= 0
     * @throws IllegalArgumentException | brickHeight <= 0
     * @post | getColumnCount() == columnCount
     * @post | getRowCount() == rowCount
     * @post | getBrickWidth() == brickWidth
     * @post | getBrickHeight() == brickHeight
     * @post | getBricks().isEmpty()
     */
    public BrickGrid(int columnCount, int rowCount, int brickWidth, int brickHeight)
    {
        if (columnCount <= 0) throw new IllegalArgumentException();
        if (rowCount <= 0) throw new IllegalArgumentException();
        if (brickWidth <= 0) throw new IllegalArgumentException();
        if (brickHeight <= 0) throw new IllegalArgumentException();

        this.brickWidth = brickWidth;
        this.brickHeight = brickHeight;
        this.grid = new Grid<>(columnCount, rowCount);
    }

    /**
     * Returns the width of each brick in game-world units.
     *
     * @post | result > 0
     */
    public int getBrickWidth()
    {
        return this.brickWidth;
    }

    /**
     * Returns the height of each brick in game-world units.
     *
     * @post | result > 0
     */
    public int getBrickHeight()
    {
        return this.brickHeight;
    }

    /**
     * Returns the number of columns in this grid.
     *
     * @post | result > 0
     */
    public int getColumnCount()
    {
        return this.grid.getWidth();
    }

    /**
     * Returns the number of rows in this grid.
     *
     * @post | result > 0
     */
    public int getRowCount()
    {
        return this.grid.getHeight();
    }

    /**
     * Returns the total width of the grid in game-world units.
     *
     * @post | result == getColumnCount() * getBrickWidth()
     */
    public int getWidth()
    {
        return this.getColumnCount() * this.brickWidth;
    }

    /**
     * Returns the total height of the grid in game-world units.
     *
     * @post | result == getRowCount() * getBrickHeight()
     */
    public int getHeight()
    {
        return this.getRowCount() * this.brickHeight;
    }

    /**
     * Returns the brick at the given grid position, or null if the cell is empty.
     *
     * @pre | gridPosition != null
     * @pre | isValidGridPosition(gridPosition)
     * @post | result == null || result.getGridPosition().equals(gridPosition)
     */
    public Brick getBrickAt(Point gridPosition)
    {
        return this.grid.at(gridPosition);
    }

    /**
     * Returns whether the given grid position is within the bounds of the grid.
     *
     * @pre | gridPosition != null
     * @post | result == (0 <= gridPosition.x() && gridPosition.x() < getColumnCount() && 0 <= gridPosition.y() && gridPosition.y() < getRowCount())
     */
    public boolean isValidGridPosition(Point gridPosition)
    {
        return this.grid.isValidPosition(gridPosition);
    }

    /**
     * Returns the brick at the given {@code gridPosition} if this {@code gridPosition}
     * falls within the borders of the playing field. If not, {@code null} is returned.
     *
     * @pre | gridPosition != null
     * @post | SpecUtil.implies(isValidGridPosition(gridPosition), () -> result == getBrickAt(gridPosition))
     * @post | SpecUtil.implies(!isValidGridPosition(gridPosition), result == null)
     */
    public Brick getBrickAtGridPositionOrNull(Point gridPosition)
    {
        if (this.grid.isValidPosition(gridPosition))
        {
            return this.getBrickAt(gridPosition);
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
    public BrickCollision findEarliestCollision(Ball ball)
    {
        var earliestHorizontalCollision = findEarliestHorizontalCollision(ball);
        var earliestVerticalCollision = findEarliestVerticalCollision(ball);

        return Collision.getEarliestCollision(earliestHorizontalCollision, earliestVerticalCollision);
    }

    /**
     * LEGIT
     */
    @MPOOPLegitGenerated
    private BrickCollision findEarliestVerticalCollision(Ball ball)
    {
        if (ball.getVelocity().y() < 0)
        {
            return findEarliestUpwardsCollision(ball);
        }
        else if (ball.getVelocity().y() > 0)
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
    private BrickCollision findEarliestHorizontalCollision(Ball ball)
    {
        if (ball.getVelocity().x() < 0)
        {
            return findEarliestLeftwardsCollision(ball);
        }
        else if (ball.getVelocity().x() > 0)
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
    private BrickCollision findEarliestUpwardsCollision(Ball ball)
    {
        var v = ball.getVelocity();
        var p = ball.getGeometry().getPointInDirection(v);
        var y = p.y() / this.brickHeight * this.brickHeight;

        while (y > 0)
        {
            var preciseT = (y - p.y()) * 1000 / v.y();
            var x = p.x() + v.x() * preciseT / 1000;
            var brickGridPosition = new Point(Math.floorDiv(x, brickWidth), Math.floorDiv(y, brickHeight) - 1);
            var brick = getBrickAtGridPositionOrNull(brickGridPosition);

            if (brick != null)
            {
                return new BrickCollision(preciseT / 1000, Vector.KILO_DOWN, brick);
            }

            y -= this.brickHeight;
        }

        return null;
    }

    /**
     * LEGIT
     */
    @MPOOPLegitGenerated
    private BrickCollision findEarliestDownwardsCollision(Ball ball)
    {
        var v = ball.getVelocity();
        var p = ball.getGeometry().getPointInDirection(v);
        var y = (p.y() + this.brickHeight - 1) / this.brickHeight * this.brickHeight;
        var yMax = this.getHeight();

        while (y < yMax)
        {
            var preciseT = (y - p.y()) * 1000 / v.y();
            var x = p.x() + v.x() * preciseT / 1000;
            var brickGridPosition = new Point(Math.floorDiv(x, brickWidth), Math.floorDiv(y, brickHeight));
            var brick = getBrickAtGridPositionOrNull(brickGridPosition);

            if (brick != null)
            {
                return new BrickCollision(preciseT / 1000, Vector.KILO_UP, brick);
            }

            y += this.brickHeight;
        }

        return null;
    }

    /**
     * LEGIT
     */
    @MPOOPLegitGenerated
    private BrickCollision findEarliestLeftwardsCollision(Ball ball)
    {
        var v = ball.getVelocity();
        var p = ball.getGeometry().getPointInDirection(v);
        var x = p.x() / this.brickWidth * this.brickWidth;

        while (x > 0)
        {
            var preciseT = (x - p.x()) * 1000 / v.x();
            var y = p.y() + v.y() * preciseT / 1000;
            var brickGridPosition = new Point(Math.floorDiv(x, brickWidth) - 1, Math.floorDiv(y, brickHeight));
            var brick = getBrickAtGridPositionOrNull(brickGridPosition);

            if (brick != null)
            {
                return new BrickCollision(preciseT / 1000, Vector.KILO_RIGHT, brick);
            }

            x -= this.brickWidth;
        }

        return null;
    }

    /**
     * LEGIT
     */
    @MPOOPLegitGenerated
    private BrickCollision findEarliestRightwardsCollision(Ball ball)
    {
        var v = ball.getVelocity();
        var p = ball.getGeometry().getPointInDirection(v);
        var x = (p.x() + this.brickWidth - 1) / this.brickWidth * this.brickWidth;
        var xMax = this.getWidth();

        while (x < xMax)
        {
            var preciseT = (x - p.x()) * 1000 / v.x();
            var y = p.y() + v.y() * preciseT / 1000;
            var brickGridPosition = new Point(Math.floorDiv(x, brickWidth), Math.floorDiv(y, brickHeight));
            var brick = getBrickAtGridPositionOrNull(brickGridPosition);

            if (brick != null)
            {
                return new BrickCollision(preciseT / 1000, Vector.KILO_LEFT, brick);
            }

            x += this.brickWidth;
        }

        return null;
    }

    /**
     * Checks whether there is a brick at the given position.
     * This method returns {@code false} for positions outside the grid.
     *
     * @pre | gridPosition != null
     * @post | SpecUtil.implies(isValidGridPosition(gridPosition), () -> result == (getBrickAt(gridPosition) != null))
     * @post | SpecUtil.implies(!isValidGridPosition(gridPosition), result == false)
     */
    public boolean containsBrickAt(Point gridPosition)
    {
        return this.getBrickAtGridPositionOrNull(gridPosition) != null;
    }

    /**
     * Adds a new StandardBrick at the given grid position.
     *
     * @creates | result
     * @inspects | gridPosition
     * @pre | gridPosition != null
     * @pre | isValidGridPosition(gridPosition)
     * @pre | !containsBrickAt(gridPosition)
     * @post | result != null
     * @post | getBrickAt(gridPosition) == result
     * @post | result.getGeometry().equals(getBrickRectangle(gridPosition))
     * @post | result.getGridPosition().equals(gridPosition)
     */
    public StandardBrick addStandardBrick(Point gridPosition)
    {
        var rectangle = getBrickRectangle(gridPosition);
        var brick = new StandardBrick(rectangle, gridPosition);
        this.grid.setAt(gridPosition, brick);
        return brick;
    }

    /**
     * Adds a new SpikeyBrick at the given grid position.
     *
     * @creates | result
     * @inspects | gridPosition
     * @pre | gridPosition != null
     * @pre | isValidGridPosition(gridPosition)
     * @pre | !containsBrickAt(gridPosition)
     * @post | result != null
     * @post | getBrickAt(gridPosition) == result
     * @post | result.getGeometry().equals(getBrickRectangle(gridPosition))
     * @post | result.getGridPosition().equals(gridPosition)
     */
    public SpikeyBrick addSpikeyBrick(Point gridPosition)
    {
        var rectangle = getBrickRectangle(gridPosition);
        var brick = new SpikeyBrick(rectangle, gridPosition);
        this.grid.setAt(gridPosition, brick);
        return brick;
    }

    /**
     * Adds a new InvertPaddleBrick at the given grid position.
     *
     * @creates | result
     * @inspects | gridPosition
     * @pre | gridPosition != null
     * @pre | isValidGridPosition(gridPosition)
     * @pre | !containsBrickAt(gridPosition)
     * @post | result != null
     * @post | getBrickAt(gridPosition) == result
     * @post | result.getGeometry().equals(getBrickRectangle(gridPosition))
     * @post | result.getGridPosition().equals(gridPosition)
     */
    public InvertPaddleBrick addInvertPaddleBrick(Point gridPosition)
    {
        var rectangle = getBrickRectangle(gridPosition);
        var brick = new InvertPaddleBrick(rectangle, gridPosition);
        this.grid.setAt(gridPosition, brick);
        return brick;
    }

    /**
     * Adds a new ShrinkPaddleBrick at the given grid position.
     *
     * @creates | result
     * @inspects | gridPosition
     * @pre | gridPosition != null
     * @pre | isValidGridPosition(gridPosition)
     * @pre | !containsBrickAt(gridPosition)
     * @post | result != null
     * @post | getBrickAt(gridPosition) == result
     * @post | result.getGeometry().equals(getBrickRectangle(gridPosition))
     * @post | result.getGridPosition().equals(gridPosition)
     */
    public ShrinkPaddleBrick addShrinkPaddleBrick(Point gridPosition)
    {
        var rectangle = getBrickRectangle(gridPosition);
        var brick = new ShrinkPaddleBrick(rectangle, gridPosition);
        this.grid.setAt(gridPosition, brick);
        return brick;
    }

    /**
     * Adds a new BlinkingBallBrick at the given grid position.
     *
     * @creates | result
     * @inspects | gridPosition
     * @pre | gridPosition != null
     * @pre | isValidGridPosition(gridPosition)
     * @pre | !containsBrickAt(gridPosition)
     * @post | result != null
     * @post | getBrickAt(gridPosition) == result
     * @post | result.getGeometry().equals(getBrickRectangle(gridPosition))
     * @post | result.getGridPosition().equals(gridPosition)
     */
    public BlinkingBallBrick addBlinkingBallBrick(Point gridPosition)
    {
        var rectangle = getBrickRectangle(gridPosition);
        var brick = new BlinkingBallBrick(rectangle, gridPosition);
        this.grid.setAt(gridPosition, brick);
        return brick;
    }

    /**
     * Adds a new StrengtheningBrick at the given grid position.
     *
     * @creates | result
     * @inspects | gridPosition
     * @pre | gridPosition != null
     * @pre | isValidGridPosition(gridPosition)
     * @pre | !containsBrickAt(gridPosition)
     * @post | result != null
     * @post | getBrickAt(gridPosition) == result
     * @post | result.getGeometry().equals(getBrickRectangle(gridPosition))
     * @post | result.getGridPosition().equals(gridPosition)
     */
    public StrengtheningBrick addStrengtheningBrick(Point gridPosition)
    {
        var rectangle = getBrickRectangle(gridPosition);
        var brick = new StrengtheningBrick(rectangle, gridPosition);
        this.grid.setAt(gridPosition, brick);
        return brick;
    }

    /**
     * Adds a new SpeedUpBrick at the given grid position.
     *
     * @creates | result
     * @inspects | gridPosition
     * @pre | gridPosition != null
     * @pre | isValidGridPosition(gridPosition)
     * @pre | !containsBrickAt(gridPosition)
     * @post | result != null
     * @post | getBrickAt(gridPosition) == result
     * @post | result.getGeometry().equals(getBrickRectangle(gridPosition))
     * @post | result.getGridPosition().equals(gridPosition)
     */
    public SpeedUpBrick addSpeedUpBrick(Point gridPosition)
    {
        var rectangle = getBrickRectangle(gridPosition);
        var brick = new SpeedUpBrick(rectangle, gridPosition);
        this.grid.setAt(gridPosition, brick);
        return brick;
    }

    /**
     * Adds a new SpawnBallBrick at the given grid position.
     *
     * @creates | result
     * @inspects | gridPosition
     * @pre | gridPosition != null
     * @pre | isValidGridPosition(gridPosition)
     * @pre | !containsBrickAt(gridPosition)
     * @post | result != null
     * @post | getBrickAt(gridPosition) == result
     * @post | result.getGeometry().equals(getBrickRectangle(gridPosition))
     * @post | result.getGridPosition().equals(gridPosition)
     */
    public SpawnBallBrick addSpawnBallBrick(Point gridPosition)
    {
        var rectangle = getBrickRectangle(gridPosition);
        var brick = new SpawnBallBrick(rectangle, gridPosition);
        this.grid.setAt(gridPosition, brick);
        return brick;
    }

    /**
     * Returns the bounding rectangle for the brick at the given grid coordinates.
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
        var left = gridCoordinates.x() * brickWidth;
        var top = gridCoordinates.y() * brickHeight;

        return new Rectangle(left, top, brickWidth, brickHeight);
    }

    /**
     * Checks whether there are any destructible bricks left.
     *
     * @post | result == getBricks().isEmpty()
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
     * Removes the brick at the given {@code gridPosition}.
     *
     * @pre | gridPosition != null
     * @pre | isValidGridPosition(gridPosition)
     * @mutates | this
     * @post | getBrickAt(gridPosition) == null
     */
    public void removeBrickAt(Point gridPosition)
    {
        this.grid.setAt(gridPosition, null);
    }

    /**
     * Removes the given brick from the grid.
     *
     * @pre | brick != null
     * @pre | getBrickAt(brick.getGridPosition()) == brick
     * @mutates | this
     * @post | getBrickAt(brick.getGridPosition()) == null
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
