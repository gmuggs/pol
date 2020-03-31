package edu.gmu.mason.vanilla;
import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.LocalDateTime;

/**
 * Extended version of Journal Record.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 *
 */
public class JournalRecord implements java.io.Serializable {
	
	private LocalDateTime travelStartTime;
	private PersonMode travelStartPlaceType;
	private long travelStartLocationId;
	private LocalDateTime travelEndTime;
	private long travelEndLocationId;
	private long intendedTravelEndLocationId;
	private PersonMode travelEndPlaceType;
	private PersonMode intendedTravelEndPlaceType;
	private String purpose;
	private LocalDateTime checkInTime;
	private LocalDateTime checkOutTime;
	private double moneyBalanceBefore;
	private double moneyBalanceAfter;
	private double moneyOffset = 0;
	private int maxNumberOfPeopleAtThePlace = 0;
	private int minNumberOfPeopleAtThePlace = Integer.MAX_VALUE;
	private Map<Long, JournalEvent> events;
	
	public JournalRecord () {
		events = new LinkedHashMap<>();
	}
	
	public int getMaxNumberOfPeopleAtThePlace() {
		return maxNumberOfPeopleAtThePlace;
	}
	public void setMaxNumberOfPeopleAtThePlace(int maxNumberOfPeopleAtThePlace) {
		this.maxNumberOfPeopleAtThePlace = maxNumberOfPeopleAtThePlace;
	}
	public int getMinNumberOfPeopleAtThePlace() {
		return minNumberOfPeopleAtThePlace;
	}
	public void setMinNumberOfPeopleAtThePlace(int minNumberOfPeopleAtThePlace) {
		this.minNumberOfPeopleAtThePlace = minNumberOfPeopleAtThePlace;
	}
	
	public LocalDateTime getTravelStartTime() {
		return travelStartTime;
	}
	public PersonMode getTravelStartPlaceType() {
		return travelStartPlaceType;
	}
	public void setTravelStartPlaceType(PersonMode travelStartPlaceType) {
		this.travelStartPlaceType = travelStartPlaceType;
	}
	public void setTravelStartTime(LocalDateTime travelStartTime) {
		this.travelStartTime = travelStartTime;
	}
	public long getTravelStartLocationId() {
		return travelStartLocationId;
	}
	public void setTravelStartLocationId(long travelStartLocationId) {
		this.travelStartLocationId = travelStartLocationId;
	}
	public LocalDateTime getTravelEndTime() {
		return travelEndTime;
	}
	public void setTravelEndTime(LocalDateTime travelEndTime) {
		this.travelEndTime = travelEndTime;
	}
	public long getTravelEndLocationId() {
		return travelEndLocationId;
	}
	public void setTravelEndLocationId(long travelEndLocationId) {
		this.travelEndLocationId = travelEndLocationId;
	}
	public long getIntendedTravelEndLocationId() {
		return intendedTravelEndLocationId;
	}
	public void setIntendedTravelEndLocationId(long intendedTravelEndLocationId) {
		this.intendedTravelEndLocationId = intendedTravelEndLocationId;
	}
	public PersonMode getTravelEndPlaceType() {
		return travelEndPlaceType;
	}
	public void setTravelEndPlaceType(PersonMode travelEndPlaceType) {
		this.travelEndPlaceType = travelEndPlaceType;
	}
	public PersonMode getIntendedTravelEndPlaceType() {
		return intendedTravelEndPlaceType;
	}
	public void setIntendedTravelEndPlaceType(PersonMode intendedTravelEndPlaceType) {
		this.intendedTravelEndPlaceType = intendedTravelEndPlaceType;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public LocalDateTime getCheckInTime() {
		return checkInTime;
	}
	public void setCheckInTime(LocalDateTime checkInTime) {
		this.checkInTime = checkInTime;
	}
	public LocalDateTime getCheckOutTime() {
		return checkOutTime;
	}
	public void setCheckOutTime(LocalDateTime checkOutTime) {
		this.checkOutTime = checkOutTime;
	}
	public double getMoneyBalanceBefore() {
		return moneyBalanceBefore;
	}
	public void setMoneyBalanceBefore(double moneyBalanceBefore) {
		this.moneyBalanceBefore = moneyBalanceBefore;
	}
	public double getMoneyBalanceAfter() {
		return moneyBalanceAfter;
	}
	public void setMoneyBalanceAfter(double moneyBalanceAfter) {
		this.moneyBalanceAfter = moneyBalanceAfter;
	}
	public double getMoneyOffset() {
		return moneyOffset;
	}
	public void setMoneyOffset(double moneyOffset) {
		this.moneyOffset = moneyOffset;
	}
	public Map<Long, JournalEvent> getEvents() {
		return events;
	}
	public void setEvents(Map<Long, JournalEvent> events) {
		this.events = events;
	}
}