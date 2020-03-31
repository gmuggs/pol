package edu.gmu.mason.vanilla;

import edu.gmu.mason.vanilla.environment.Classroom;
import edu.gmu.mason.vanilla.log.Characteristics;
import edu.gmu.mason.vanilla.log.State;

/**
 * General description_________________________________________________________
 * A class to represent family information.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class Family implements java.io.Serializable {
	private static final long serialVersionUID = -2527715491868256676L;
	@Characteristics
	private int numberOfPeople;
	@Characteristics
	private boolean haveKids;
	@State
	private Classroom classroom;

	public Family(int numberOfPeople, boolean haveKids) {
		this.numberOfPeople = numberOfPeople;
		this.haveKids = haveKids;
	}

	public int getNumberOfPeople() {
		return numberOfPeople;
	}

	public boolean haveKids() {
		return haveKids;
	}

	public Classroom getClassroom() {
		return classroom;
	}

	public void setClassroom(Classroom classroom) {
		this.classroom = classroom;
	}
}
