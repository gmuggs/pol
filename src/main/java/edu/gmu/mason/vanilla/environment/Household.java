package edu.gmu.mason.vanilla.environment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.gmu.mason.vanilla.Person;
import edu.gmu.mason.vanilla.log.ExtLogger;
/**
 * General description
 * A class to represent households living in apartments.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class Household implements java.io.Serializable {
	
	private static final long serialVersionUID = -607453658461257824L;
	private final static ExtLogger logger = ExtLogger.create(Household.class);
	
	//// GT_N#10
	private List<Person> members;
	
	public Household() {
		this.members = new ArrayList<Person>();
	}
	
	public void addMember(Person agent) {
		
		// check if this agent already in the household
		Iterator<Person> memberIterator = members.iterator();
		while(memberIterator.hasNext()) {
			if(memberIterator.next().getAgentId() == agent.getAgentId()) {
				logger.error("Household already exists.");
				return;
			}
		}
		this.members.add(agent);
	}
	
	public void removeMember(Person agent) {
		
		Iterator<Person> memberIterator = members.iterator();
		
		while(memberIterator.hasNext()) {
			if(memberIterator.next().getAgentId() == agent.getAgentId()) {
				memberIterator.remove();
				break;
			}
		}
		
		memberIterator = members.iterator();
		
		// notify other household agents that there will be a change in rental priceså
		while(memberIterator.hasNext()) {
			memberIterator.next().getShelterNeed().setRentChanged(true);
		}
	}
	
	public List<Person> getMembers(){
		return this.members;
	}

}
