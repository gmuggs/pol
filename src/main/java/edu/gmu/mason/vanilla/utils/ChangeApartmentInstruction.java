package edu.gmu.mason.vanilla.utils;

import edu.gmu.mason.vanilla.Person;
import edu.gmu.mason.vanilla.PersonMode;
import edu.gmu.mason.vanilla.environment.Apartment;

/**
 * General description_________________________________________________________
 * This class is used to instruct agent to vacate home at a suitable time and 
 * move to a new home.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class ChangeApartmentInstruction extends AgentInstruction {

	private static final long serialVersionUID = 7720549436950755340L;
	private boolean forceToCheaperApartment;
	private Apartment newApartmentToMove;
	
	public ChangeApartmentInstruction(boolean forceToCheaperApartment) {
		this(forceToCheaperApartment, null);
	}
	
	public ChangeApartmentInstruction(boolean forceToCheaperApartment, Apartment newApartment) {
		super(500, true);
		this.forceToCheaperApartment = forceToCheaperApartment;
		this.newApartmentToMove = newApartment;
	}
	

	@Override
	public boolean preCondition(Person agent) {
		return agent.getShelterNeed().getCurrentShelter() != null &&
				agent.getCurrentMode() != PersonMode.Transport && 
				agent.getCurrentMode() != PersonMode.AtHome;
	}

	@Override
	public void planOfActions(Person agent) {
		
		if (this.forceToCheaperApartment == true) {// should move to a cheaper apartment
			agent.getShelterNeed().forceToMoveToCheaperApartment();
			return;
		}
		
		// should move to an apartment
		agent.getShelterNeed().vacate(); // first, vacate current one
		
		if (newApartmentToMove != null) { // we know which apartment to move
			agent.getShelterNeed().move(newApartmentToMove);
		} else {
			agent.getShelterNeed().satisfy();
		}
		
	}

}
