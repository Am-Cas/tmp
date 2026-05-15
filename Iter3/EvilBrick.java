package ogp.multiclass;

import java.awt.Color;
import java.util.ArrayList;

import ogp.BreakoutState;
import ogp.balls.Ball;
import ogp.bricks.ColorSet;
import ogp.bricks.LabeledBrick;
import ogp.math.Point;
import ogp.math.Rectangle;
import ogp.util.SpecUtil;

/**
 * Package representation invariant:
 * @invar | guardBricks != null
 * @invar | guardBricks.stream().allMatch(g -> g != null && g.evilBricks.contains(this))
 * @invar | !SpecUtil.containsDuplicateObjects(guardBricks)
 * @invar | evilBalls != null
 * @invar | evilBalls.stream().allMatch(b -> b != null && b.owner == this)
 * @invar | !SpecUtil.containsDuplicateObjects(evilBalls)
 *
 * Public (abstract) invariant:
 * @invar | getGuardBricks() != null
 * @invar | getGuardBricks().stream().allMatch(g -> g != null && g.getEvilBricks().contains(this))
 * @invar | getEvilBalls() != null
 * @invar | getEvilBalls().stream().allMatch(b -> b != null && b.getOwner() == this)
 */
public class EvilBrick extends LabeledBrick {

	/**
	 * @representationObject
	 * @peerObjects
	 */
	public ArrayList<GuardBrick> guardBricks;

	/**
	 * @representationObject
	 * @peerObjects
	 */
	public ArrayList<EvilBallBehavior> evilBalls;

	/**
	 * Returns the guard bricks protecting this evil brick.
	 *
	 * @creates | result
	 * @post | result != null
	 * @post | result.stream().allMatch(g -> g != null)
	 * @post | result.stream().allMatch(g -> g.getEvilBricks().contains(this))
	 */
	public ArrayList<GuardBrick> getGuardBricks() {
	    return new ArrayList<>(guardBricks);
	}

	/**
	 * Returns the evil balls protecting this evil brick.
	 *
	 * @creates | result
	 * @post | result != null
	 * @post | result.stream().allMatch(b -> b != null)
	 * @post | result.stream().allMatch(b -> b.getOwner() == this)
	 */
	public ArrayList<EvilBallBehavior> getEvilBalls() {
		return new ArrayList<>(evilBalls);
	}

	/**
	 * Removes this brick's reference from all peers.
	 * The invariant is preserved after execution.
	 */
	void nukePkg() {
		// unlink from all guard bricks
		var tempGuards = new ArrayList<GuardBrick>(guardBricks);
		for (GuardBrick g : tempGuards) {
			g.unlinkPkg(this);
		}
		// unlink all evil balls
		var tempBalls = new ArrayList<EvilBallBehavior>(evilBalls);
		for (EvilBallBehavior b : tempBalls) {
			b.unlinkPkg();
		}
	}


	//------------Integration in Breakout----------------------

	/**
	 * @post | result != null
	 * @post | result.equals("E")
	 */
	@Override
	public String getLabel() {
		return "E";
	}

	/**
	 * @post | result != null
	 */
	@Override
	public Color getColor() {
		return ColorSet.EXTRA1;
	}

	/**
	 * Constructs an EvilBrick at the given geometry and grid position with empty peer sets.
	 *
	 * @throws IllegalArgumentException | geometry == null
	 * @throws IllegalArgumentException | gridPosition == null
	 * @post | getGuardBricks().isEmpty()
	 * @post | getEvilBalls().isEmpty()
	 */
	EvilBrick(Rectangle geometry, Point gridPosition) {
		super(geometry, gridPosition);
		guardBricks = new ArrayList<>();
		evilBalls = new ArrayList<>();
	}

	/**
	 * LEGIT
	 * pre: guardBricks have no links
	 */
	public EvilBrick(Rectangle geometry, Point gridPosition, ArrayList<GuardBrick> guardBricks) {
		this(geometry, gridPosition);
		this.guardBricks = new ArrayList<GuardBrick>();
		for (var g : guardBricks) { this.guardBricks.add(g) ; g.evilBricks.add(this); }
	}

	/**
	 * LEGIT
	 *
	 * Builds an evil brick with empty sets of peers,
	 * and arbitrary position in the game.
	 *
	 * Only useful for testing purposes.
	 */
	public EvilBrick() {
		this(new Rectangle(new Point(0,0), new Point(50,50)), new Point(2,2));
	}

	/**
	 * When this evil brick is hit: if it has no directly linked guard bricks or evil balls,
	 * it is destroyed. Otherwise nothing happens.
	 *
	 * @pre | state != null
	 * @pre | ball != null
	 * @pre | state.getBalls().contains(ball)
	 * @post | SpecUtil.implies(old(getGuardBricks()).isEmpty() && old(getEvilBalls()).isEmpty(),
	 *       |     state.getBrickGrid().getBrickAt(getGridPosition()) == null)
	 * @post | SpecUtil.implies(!old(getGuardBricks()).isEmpty() || !old(getEvilBalls()).isEmpty(),
	 *       |     state.getBrickGrid().getBrickAt(getGridPosition()) == this)
	 * @mutates | state
	 * @mutates | ball
	 */
	@Override
	public void hit(BreakoutState state, Ball ball) {
		if (guardBricks.isEmpty() && evilBalls.isEmpty()) {
			nukePkg();
			state.getBrickGrid().removeBrick(this);
		}
	}

	/**
	 * Treats a strong ball as a standard ball (calls hit).
	 * Returns false if this brick was destroyed, true if it survived.
	 *
	 * @pre | state != null
	 * @pre | ball != null
	 * @pre | state.getBalls().contains(ball)
	 * @post | result == (!old(getGuardBricks()).isEmpty() || !old(getEvilBalls()).isEmpty())
	 * @mutates | state
	 * @mutates | ball
	 */
	@Override
	public boolean strongHit(BreakoutState state, Ball ball) {
		hit(state , ball);
		return true;
	}

}
