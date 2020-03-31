package edu.gmu.mason.vanilla;

/**
 * General description_________________________________________________________
 * Enumeration to represent four different characteristics of agents.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public enum EducationLevel {
	Unknown (0),
	Low (1),
	HighSchoolOrCollege (2),
	Bachelors (3),
	Graduate (4);
	
	private int index;
	
	private EducationLevel(int index) {
		this.index = index;
	}

	public static EducationLevel valueOf(int index) {
		for(EducationLevel b : EducationLevel.values()) {
			if(b.index == index)
				return b;
		}
		return null;
	}
	
	public int getValue() {
        return this.index;
    }
}
