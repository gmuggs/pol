package edu.gmu.mason.vanilla;

/**
 * General description_________________________________________________________
 * An enumeration to represent the love need status of the agent. This is set based on number of friends.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public enum LoveNeedStatus {
	NA(0), Awful (1), Unsatisfactory(2), OK(3), Good(4), Perfect(5);
	
	private int index;
	
	private LoveNeedStatus(int index) {
		this.index = index;
	}
	
	public static LoveNeedStatus valueOf(int index) {
		for(LoveNeedStatus b : LoveNeedStatus.values()) {
			if(b.index == index)
				return b;
		}
		return null;
	}
	
	public int getIndex() {
		return this.index;
	}
}
