package edu.gmu.mason.vanilla;

/**
 * General description_________________________________________________________
 * Supports journal events accessors.
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */

import java.util.HashMap;
import java.util.Map;

public class JournalEvent implements java.io.Serializable {
	private int maxNumberOfFriends = 0;
	private int minNumberOfFriends = Integer.MAX_VALUE;
	private int maxNumberOfColleauges = 0;
	private int minNumberOfColleauges = Integer.MAX_VALUE;

	private Map<Long,Boolean> participants;
	
	public JournalEvent() {
		participants = new HashMap<>(); 
	}
	
	public int getMaxNumberOfFriends() {
		return maxNumberOfFriends;
	}
	public void setMaxNumberOfFriends(int maxNumberOfFriends) {
		this.maxNumberOfFriends = maxNumberOfFriends;
	}
	public int getMinNumberOfFriends() {
		return minNumberOfFriends;
	}
	public void setMinNumberOfFriends(int minNumberOfFriends) {
		this.minNumberOfFriends = minNumberOfFriends;
	}
	public int getMaxNumberOfColleauges() {
		return maxNumberOfColleauges;
	}
	public void setMaxNumberOfColleauges(int maxNumberOfColleauges) {
		this.maxNumberOfColleauges = maxNumberOfColleauges;
	}
	public int getMinNumberOfColleauges() {
		return minNumberOfColleauges;
	}
	public void setMinNumberOfColleauges(int minNumberOfColleauges) {
		this.minNumberOfColleauges = minNumberOfColleauges;
	}
	public Map<Long, Boolean> getParticipants() {
		return participants;
	}
	public void setParticipants(Map<Long, Boolean> participants) {
		this.participants = participants;
	}
	
	
}