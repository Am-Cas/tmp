package ogp.bricks;

import java.awt.Color;

import ogp.BreakoutState;
import ogp.balls.Ball;
import ogp.balls.StandardBehavior;
import ogp.math.Circle;
import ogp.math.Point;
import ogp.math.Rectangle;
import ogp.math.Vector;

/**
 * Represents a brick that, when hit, is destroyed and spawns a new standard ball
 * at the bottom-center of the brick grid.
 *
 * @immutable
 */
public class SpawnBallBrick extends LabeledBrick
{
    public static final Color COLOR = ColorSet.SPAWN_BALL_BRICK;

    public static final String LABEL = "o";

    /**
     * @throws IllegalArgumentException | geometry == null
     * @throws IllegalArgumentException | gridPosition == null
     * @post | getGeometry().equals(geometry)
     * @post | getGridPosition().equals(gridPosition)
     */
    public SpawnBallBrick(Rectangle geometry, Point gridPosition)
    {
        super(geometry, gridPosition);
    }

    /**
     * @post | result != null
     * @post | result.equals(SpawnBallBrick.COLOR)
     */
    @Override
    public Color getColor()
    {
        return SpawnBallBrick.COLOR;
    }

    /**
     * @post | result != null
     * @post | result.equals(SpawnBallBrick.LABEL)
     */
    @Override
    public String getLabel()
    {
        return LABEL;
    }

    /**
     * Removes this brick from the state and spawns a new standard ball
     * at the bottom-center of the brick grid.
     *
     * @pre | state != null
     * @pre | ball != null
     * @mutates | state
     * @post | !state.getBricks().contains(this)
     * @post | state.getBalls().size() == old(state.getBalls().size()) + 1
     */
    @Override
    public void hit(BreakoutState state, Ball ball)
    {
        state.getBrickGrid().removeBrick(this);

        Point c = state.getBrickGrid().getBoundingRectangle().getBottomCenter();
        Vector up = new Vector(0, -35);
        var sbehavior = new StandardBehavior();
        state.addBall(new Circle(c, 500), up, sbehavior);
    }
}
