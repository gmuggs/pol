package edu.gmu.mason.vanilla;

/**
 * General description_________________________________________________________
 *  Minimum set of methods that each need has to satisfy.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public interface Need {
	

	/**
	 * Should include a logic to update the status of the need.
	 */
	public void update();
	
	/**
	 * Should include the logic of what happens when the need is satisfied.
	 */
	public void satisfy();
	
	/**
	 * 
	 * @return returns {@code true} when satisfied, {@code false} 
	 */
	public boolean isSatisfied();
}
