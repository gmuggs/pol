package edu.gmu.mason.vanilla.environment;

/**
 * General description_________________________________________________________
 * A class to apply jitter on agent visualizations.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class Jitter implements java.io.Serializable {
	private static final long serialVersionUID = -5125900076378106349L;
	private double x;
	private double y;
	private boolean applied;
	
	public Jitter(double x, double y) {
		this.x = x;
		this.y = y;
		applied = false;
	}
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public boolean isApplied() {
		return applied;
	}
	public void setApplied(boolean applied) {
		this.applied = applied;
	}
	
	
}
