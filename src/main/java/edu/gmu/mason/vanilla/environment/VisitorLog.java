package edu.gmu.mason.vanilla.environment;

import edu.gmu.mason.vanilla.AgentInterest;

/**
 * General description_________________________________________________________
 * A simple data structure class to hold a visitor log.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class VisitorLog implements java.io.Serializable {
	private static final long serialVersionUID = 3977904249264413069L;
	private double age;
	private double income;
	private AgentInterest interest;
	
	public double getAge() {
		return age;
	}
	public void setAge(double age) {
		this.age = age;
	}
	public double getIncome() {
		return income;
	}
	public void setIncome(double income) {
		this.income = income;
	}
	public AgentInterest getInterest() {
		return interest;
	}
	public void setInterest(AgentInterest interest) {
		this.interest = interest;
	}

}
