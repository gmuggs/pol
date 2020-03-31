package edu.gmu.mason.vanilla.environment;

/**
 * General description_________________________________________________________
 * Enumeration to represent building types
 * 
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public enum BuildingType {
	Unknown(0), Residental(1), Commercial(2), School(3);
	
	private int index;
	private BuildingType(int index) {
		this.index = index;
	}
	
	public static BuildingType valueOf(int index) {
		for(BuildingType b : BuildingType.values()) {
			if(b.index == index)
				return b;
		}
		return null;
	}
}
