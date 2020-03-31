package edu.gmu.mason.vanilla.utils;

import edu.gmu.mason.vanilla.Person;
import edu.gmu.mason.vanilla.PersonMode;
import edu.gmu.mason.vanilla.environment.Job;

/**
 * General description_________________________________________________________
 * This class is used to instruct agent to change job at a suitable time. This 
 * one is different from {@code QuitJobInstruction} class in a way that quitting
 * the job is followed by accepting the new one.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class ChangeJobInstruction extends AgentInstruction {

	private static final long serialVersionUID = -5179667434416643323L;
	private Job newJob; 

	public ChangeJobInstruction(Job newJob) {
		super(350, true);
		this.newJob = newJob;
	}
	
	@Override
	public boolean preCondition(Person agent) {
		boolean workdayCondition = agent.getTodaysPlan().isWorkDay() && agent.getTodaysPlan().cameBackFromWork();
		
		return agent.getFinancialSafetyNeed().isEmployed() == true && 
				agent.getCurrentMode() != PersonMode.Transport &&
				( workdayCondition || agent.getTodaysPlan().isWorkDay() == false);
	}

	@Override
	public void planOfActions(Person agent){
		agent.getFinancialSafetyNeed().quitCurrentJob();
		agent.getFinancialSafetyNeed().acceptTheJob(newJob);
	}

	public void setNewJob(Job newJob) {
		this.newJob = newJob;
	}

}
