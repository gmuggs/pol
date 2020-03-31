package edu.gmu.mason.vanilla.environment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;

import edu.gmu.mason.vanilla.Person;
import edu.gmu.mason.vanilla.log.Referenceable;
import edu.gmu.mason.vanilla.log.Skip;

/**
 * General description_________________________________________________________
 * Workplace class for providing agents jobs.
 *
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
@Referenceable(keyMethod = "getId", keyType = Long.class)
public class Workplace extends BuildingUnit{
	
	private static final long serialVersionUID = -5043378084132232673L;
	@Skip
	private List<Job> jobs;
	
	public Workplace(long id, Building building) {
		super(id, building, "Workplace");
		this.jobs = new ArrayList<Job>();
	}

	public void addJob(Job j){
		this.jobs.add(j);
	}
	
	public int getNumberOfJobs() {
		return jobs.size();
	}
	
	public List<Job> getAvailableJobs() {
		return this.jobs.stream().filter(p -> p.getWorker() == null).collect(Collectors.toList());
	}

	@Override
	public void agentArrives(Person agent, double visitLength) {
		super.agentArrives(agent, visitLength);
		// mark agent plan for agent's arrival to work
		agent.getTodaysPlan().setBeenAtWork(true);
	}
	
	@Override 
	public void agentLeaves(Person agent) {
		
		LocalDateTime entryTime = getAgentArrival(agent.getAgentId());
		int minuteDiff = Minutes.minutesBetween(entryTime, agent.getSimulationTime()).getMinutes();
		double differenceInHour = (double)minuteDiff / 60.0;
		
		double cost = agent.getFinancialSafetyNeed().getJob().getHourlyRate() *  differenceInHour;
		
		// agent gets paid and removed from arrival list.
		agent.getFinancialSafetyNeed().depositMoney(cost);
		
		// remove from the building as well.
		super.agentLeaves(agent);
	}
	
	public List<Job> getJobs(){
		return jobs;
	}
	
}
