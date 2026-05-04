package ogp.multiclass;

import java.awt.Color;
import java.util.ArrayList;

import ogp.BreakoutState;
import ogp.balls.Ball;
import ogp.bricks.ColorSet;
import ogp.bricks.LabeledBrick;
import ogp.math.Point;
import ogp.math.Rectangle;
import ogp.math.Vector;
import ogp.util.SpecUtil;

/**
 * A brick "guarding" some EvilBrick's
 * 
 * @invar | evilBricks != null
 * @invar | evilBricks.stream().allMatch(e -> e != null && e.guardBricks.contains(this))
 * @invar | !SpecUtil.containsDuplicateObjects(evilBricks)
 * @invar | hps == computeHpsPkg()
 * 
 */
public class GuardBrick extends LabeledBrick {
	
	/**
	 * @representationObject
	 * @peerObjects
	 */
	ArrayList<EvilBrick> evilBricks;
	
	/**
	 * see computeHpsPkg()
	 */
	int hps;
	
	/**
	 * Returns the evil bricks that this guard brick protects.
	 * 
	 * @creates | result
	 * @post | result != null
	 * @post | result.stream().allMatch(e -> e != null)
	 * @post | result.stream().allMatch(e -> e.getGuardBricks().contains(this))
	 */
	public ArrayList<EvilBrick> getEvilBricks() {
		return new ArrayList<>(evilBricks);
	}
	
	/**
	 * LEGIT
	 */
	public int getHps() {
		return hps;
	}
	
	/**
	 * Registers a link between this guard brick and ebrick.
	 * Linking twice has no additional effect.
	 * 
	 * @pre | ebrick != null
	 * @post | getEvilBricks().contains(ebrick)
	 * @post | ebrick.getGuardBricks().contains(this)
	 * @mutates | this
	 * @mutates | ebrick
	 */
	public void link(EvilBrick ebrick) {
		if (evilBricks.contains(ebrick)) return;
		evilBricks.add(ebrick);
		ebrick.guardBricks.add(this);
		// recompute hps (no evil balls changed, so only need to add 1 + sum of evilBalls for ebrick)
		hps = computeHpsPkg();
		// update speed modifiers of all evil balls in ebrick (µ may have changed for all guard bricks of ebrick)
		for (EvilBallBehavior b : ebrick.evilBalls) {
			b.speedModifier = b.computeSpeedModifierPkg();
		}
		// also update speed modifiers of evil balls of other evil bricks in this guard brick
		// because µ for those balls depends on this guard brick's evilBricks count
		for (EvilBrick e : evilBricks) {
			if (e != ebrick) {
				for (EvilBallBehavior b : e.evilBalls) {
					b.speedModifier = b.computeSpeedModifierPkg();
				}
			}
		}
	}
	
	/**
	 * LEGIT
	 * fails silently if ebrick is not found
	 */
	public void unlink(EvilBrick ebrick) {
		unlinkPkg(ebrick);
	}
	
	/**
	 * NOSPEC
	 * post: the result is 1 + Sum[ebr : getEvilBricks()] |ebr.getEvilBalls()|
	 */
	//note to developer: calling a public method runs the package scoped invariant.
	public int computeHps() {
		return computeHpsPkg();
	}

	void unlinkPkg(EvilBrick ebrick) {
		if (!evilBricks.contains(ebrick)) return;
		evilBricks.remove(ebrick);
		ebrick.guardBricks.remove(this);
		hps = computeHpsPkg();
		// update speed modifiers of evil balls in ebrick (µ may have changed)
		for (EvilBallBehavior b : ebrick.evilBalls) {
			b.speedModifier = b.computeSpeedModifierPkg();
		}
		// update speed modifiers of evil balls of remaining evil bricks in this guard
		for (EvilBrick e : evilBricks) {
			for (EvilBallBehavior b : e.evilBalls) {
				b.speedModifier = b.computeSpeedModifierPkg();
			}
		}
	}

	/**
	 * @post | 1 <= result
	 */
	int computeHpsPkg() {
		int sum = 1;
		for (EvilBrick ebr : evilBricks) {
			sum += ebr.evilBalls.size();
		}
		return sum;
	}
	
	
	
	
	
	
	
	//------------integration in Breakout-----------------

	/**
	 * Removes this brick reference from any peer.
	 * The invariant is preserved after execution. 
	 * post: getEvilBricks() is empty
	 * 
	 */
	void nukePkg() {
		var temp = new ArrayList<EvilBrick>(evilBricks);
		for (var ebrick : temp) { 
			unlinkPkg(ebrick);
		}
	}

	@Override
	public Color getColor() {
		return ColorSet.EXTRA2;
	}

	@Override
	public String getLabel() {
		return "G" + hps;
	}

	/**
	 * Constructs a GuardBrick with an empty set of linked evil bricks.
	 * 
	 * @throws IllegalArgumentException | geometry == null
	 * @throws IllegalArgumentException | gridPosition == null
	 * @post | getEvilBricks().isEmpty()
	 * @post | getHps() == 1
	 */
	public GuardBrick(Rectangle geometry, Point gridPosition) {
		super(geometry, gridPosition);
		evilBricks = new ArrayList<>();
		hps = 1;
	}

	/**
	 * LEGIT
	 * Builds a guard brick with empty set of peers,
	 * and arbitrary position in the game.
	 * 
	 * Only useful for testing puroposes.
	 */
	public GuardBrick() {
		this(new Rectangle(new Point(0,0), new Point(50,50)), new Point(2,2));
	}

	/**
	 * When hit:
	 * - if hps > 1, nothing special happens (brick survives via strongHit returning true).
	 * - if hps == 1, for each linked evil brick, spawns 1 evil ball; then destroys this guard brick.
	 * 
	 * @pre | state != null
	 * @pre | ball != null
	 * @pre | state.getBalls().contains(ball)
	 */
	@Override
	public void hit(BreakoutState state, Ball ball) {
		if (hps > 1) {
			// decrement hps by removing one evil ball link conceptually; 
			// but per assignment: if >1 hp nothing special happens (brick survives)
			// hps is maintained by the invariant; we need to reduce it.
			// The assignment says "nothing special happens" meaning brick stays alive.
			// But we need to reduce hps somehow? 
			// Re-reading: hps = 1 + sum|ebr.evilBalls|. It's computed, not decremented.
			// So "if hps > 1, nothing special happens" means brick just stays.
			return;
		} else {
			// hps == 1: spawn evil balls and destroy this guard brick
			ArrayList<EvilBrick> temp = new ArrayList<>(evilBricks);
			for (int i = 0; i < temp.size(); i++) {
				EvilBrick ebrick = temp.get(i);
				var ebehavior = new EvilBallBehavior(ebrick);
				state.addBall(ball.getGeometry().scaleUp(), new Vector(-(4*i)+1, 30), ebehavior);
			}
			// destroy this guard brick
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
