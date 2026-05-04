package ogp.multiclass;

import java.awt.Color;

import ogp.BreakoutState;
import ogp.Collision;
import ogp.balls.Ball;
import ogp.balls.BallBehavior;



public class EvilBallBehavior extends BallBehavior {
	
	/**
	 * @peerObject
	 * 
	 * @invar | owner == null || owner.evilBalls.contains(this)
	 */
	EvilBrick owner;
	
	/**
	 * see computeSpeedModifierPkg()
	 * 
	 * @invar | speedModifier == computeSpeedModifierPkg()
	 */
	int speedModifier;
	
	/**
	 * Returns the evil brick that owns this ball, or null if unlinked.
	 * 
	 * @post | result == null || result.getEvilBalls().contains(this)
	 */
	public EvilBrick getOwner() {
		return owner;
	}
	
	/**
	 * LEGIT
	 */
	public int getSpeedModifier() {
		return speedModifier;
	}
	
	/**
	 * Constructs an EvilBallBehavior linked to the given owner evil brick.
	 * Registers this ball in the owner's evil balls list.
	 * 
	 * @pre | owner != null
	 * @post | getOwner() == owner
	 * @post | owner.getEvilBalls().contains(this)
	 */
	public EvilBallBehavior(EvilBrick owner) {
		this.owner = owner;
		owner.evilBalls.add(this);
		// update hps of all guard bricks linked to owner
		for (GuardBrick g : owner.guardBricks) {
			g.hps = g.computeHpsPkg();
		}
		// update speed modifier
		this.speedModifier = computeSpeedModifierPkg();
		// update speed modifiers of all sibling evil balls (µ may have changed)
		for (EvilBallBehavior b : owner.evilBalls) {
			if (b != this) {
				b.speedModifier = b.computeSpeedModifierPkg();
			}
		}
	}
	
	/**
	 * LEGIT
	 * unlink from owner
	 */
	public void unlink() {
		unlinkPkg();
		
	}
	
	/**
	 * NOSPEC
	 * post: computes the speedmodifier as in the assignment
	 */
	//note to developer: calling a public method runs the package scoped invariant.
	public int computeSpeedModifier() {
		return computeSpeedModifierPkg();
	}
	
	int computeSpeedModifierPkg() {
		if (owner == null) { return 0;}
		else {
			int g = owner.guardBricks.size();
			int preres = (maxFiberSizePriv() < g? 1 : -1) * g; 
			return clamp(-3,3,preres);			
		}

	}
	
	void unlinkPkg() {
		if (owner == null) return;
		EvilBrick prevOwner = owner;
		owner = null;
		speedModifier = 0;
		prevOwner.evilBalls.remove(this);
		// update hps of all guard bricks linked to prevOwner
		for (GuardBrick g : prevOwner.guardBricks) {
			g.hps = g.computeHpsPkg();
		}
		// update speed modifiers of remaining evil balls
		for (EvilBallBehavior b : prevOwner.evilBalls) {
			b.speedModifier = b.computeSpeedModifierPkg();
		}
	}
	
	
	
	/**
	 * the µ function from the assignment: max over guard bricks of |g.evilBricks|
	 * @post | result >= 1
	 */
	private int maxFiberSizePriv() {
		if (owner == null || owner.guardBricks.isEmpty()) return 1;
		int max = 1;
		for (GuardBrick g : owner.guardBricks) {
			int sz = g.evilBricks.size();
			if (sz > max) max = sz;
		}
		return max;
	}

	/**
	 * @post | lbound <= result
	 * @post | result <= ubound
	 */
	public static int clamp(int lbound, int ubound, int arg) {
		int res;
		if (arg <= lbound) {
			res = lbound;
		}
		else if (arg >= ubound) {
			res = ubound;
		}
		else {
			res = arg;
		}
		return res;
	}
	
	/**
	 * LEGIT
	 * static wrapper for constructor, with shorter name
	 */
	public static EvilBallBehavior mkEball(EvilBrick ebrick) {
		return new EvilBallBehavior(ebrick);
	}
	
	//----------------Integration into breakout---------------------

	
	public Color getColor() {
		return Color.red;
	}

	//calling super in overriden methods may reduce code duplication and is common
	
	/**
	 * When an evil ball bounces off the paddle, the player loses 1 hp.
	 * 
	 * @pre | state != null
	 * @pre | ball != null
	 * @pre | collision != null
	 * @pre | state.getBalls().contains(ball)
	 * @mutates | state
	 * @mutates | ball
	 */
	@Override
	public void bounceOffPaddle(BreakoutState state, Ball ball, Collision collision) {
		super.bounceOffPaddle(state, ball, collision);
		state.lose1Life();
	}
	
	/**
	 * When an evil ball leaves the game field it is removed from the association.
	 * 
	 * @pre | state != null
	 * @pre | ball != null
	 * @pre | state.getBalls().contains(ball)
	 * @mutates | state
	 * @mutates | ball
	 */
    @Override
    public void ballLost(BreakoutState state, Ball ball) {
    	unlinkPkg();
    	super.ballLost(state, ball);
    }
    
    /**
     * When an evil ball bounces off a wall, it is sped up speedModifier times
     * (slowed down if speedModifier is negative).
     * 
     * @pre | state != null
     * @pre | ball != null
     * @pre | collision != null
     * @pre | state.getBalls().contains(ball)
     * @mutates | state
     * @mutates | ball
     */
    @Override
    public void bounceOffWall(BreakoutState state, Ball ball, Collision collision) {
    	super.bounceOffWall(state, ball, collision);
    	int mod = speedModifier;
    	if (mod > 0) {
    		for (int i = 0; i < mod; i++) ball.speedUp();
    	} else {
    		for (int i = 0; i < -mod; i++) ball.slowDown();
    	}
    }
	
	
}
