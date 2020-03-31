package edu.gmu.mason.vanilla.utils;

import edu.gmu.mason.vanilla.Person;

/**
 * General description_________________________________________________________
 * This class is used as the base of instructions to be executed by agents
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public abstract class AgentInstruction implements java.io.Serializable {

	private static final long serialVersionUID = 41974078336726505L;
	private static final int DEFAULT_PRIORITY = 500;
	
	protected AgentInstruction(int priority, boolean onlyAllowOneInstanceAtATime) {
		this.priority = priority;
		this.onlyAllowOneInstanceAtATime = onlyAllowOneInstanceAtATime;
	}
	
	protected AgentInstruction() {
		this(DEFAULT_PRIORITY, false);
	}
	
	/**
	 * This value decides the order of execution. 
	 * Higher number means higher priority. 
	 */
	private int priority;
	
	/**
	 * This variable represents whether only one instance of this instruction is
	 * available at a time.
	 */
	private boolean onlyAllowOneInstanceAtATime;

	public abstract boolean preCondition(Person agent);
	
	public abstract void planOfActions(Person agent);

	/**
	 * This value decides the order of execution. 
	 * Higher number means higher priority. 
	 */
	public int getPriority() {
		return priority;
	}
	
	/**
	 * This value represents whether only one instance of this instruction is
	 * available at a time.
	 */
	public boolean isOnlyAllowOneInstanceAtATime() {
		return onlyAllowOneInstanceAtATime;
	}
	
	public String toString(){
		return "Class:" + this.getClass() + " - Priority: "+ priority;
	}
}
