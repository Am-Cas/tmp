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
 * @invar | guardBricks != null
 * @invar | guardBricks.stream().allMatch(g -> g != null && g.evilBricks.contains(this))
 * @invar | !SpecUtil.containsDuplicateObjects(guardBricks)
 * @invar | evilBalls != null
 * @invar | evilBalls.stream().allMatch(b -> b != null && b.owner == this)
 * @invar | !SpecUtil.containsDuplicateObjects(evilBalls)
 */
public class EvilBrick extends LabeledBrick {
	
	/**
	 * @representationObject
	 * @peerObjects
	 */
	ArrayList<GuardBrick> guardBricks;
	
	/**
	 * @representationObject
	 * @peerObjects
	 */
	ArrayList<EvilBallBehavior> evilBalls;
	
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

	@Override
	public String getLabel() {
		return "E";
	}
	
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
	public EvilBrick(Rectangle geometry, Point gridPosition) {
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
		// recompute hps for each guard brick
		for (var g : guardBricks) { g.hps = g.computeHpsPkg(); }
	}

	/**
	 * LEGIT
	 * 
	 * Builds an evil brick with empty sets of peers,
	 * and arbitrary position in the game
	 * 
	 * Only useful for testing puroposes.
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
	 * LEGIT
	 */
	@Override
	public boolean strongHit(BreakoutState state, Ball ball) {
		hit(state , ball);
		return true;
	}

}
