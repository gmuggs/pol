package edu.gmu.mason.vanilla.environment;

import edu.gmu.mason.vanilla.log.Referenceable;

/**
 * General description_________________________________________________________
 * A class to represent classrooms that agents' kids go.
 *
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
@Referenceable(keyMethod = "getId", keyType = Long.class)
public class Classroom extends BuildingUnit {

	private static final long serialVersionUID = -8311839371061911438L;
	
	private double monthlyCost;
	
	public Classroom(long id, Building building) {
		super(id, building, "Classroom");
		monthlyCost = 100;
	}

	public double getMonthlyCost() {
		return monthlyCost;
	}

	public void setMonthlyCost(double monthlyCost) {
		this.monthlyCost = monthlyCost;
	}
	
}
