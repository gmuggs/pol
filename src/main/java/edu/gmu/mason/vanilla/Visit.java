package edu.gmu.mason.vanilla;

import org.joda.time.LocalDateTime;

/**
 * General description_________________________________________________________
 * A simple data structure class to represent agent visit items.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class Visit implements java.io.Serializable {
	private static final long serialVersionUID = 6293445081452121880L;
	private LocalDateTime arrivalTime;
	private double visitLength;

	public Visit(LocalDateTime arrivalTime, double visitLength) {
		this.arrivalTime = arrivalTime;
		this.visitLength = visitLength;
	}

	public LocalDateTime getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(LocalDateTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public double getVisitLength() {
		return visitLength;
	}

	public void setVisitLength(double visitLength) {
		this.visitLength = visitLength;
	}

}
