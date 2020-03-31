package edu.gmu.mason.vanilla;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.LocalDateTime;

import edu.gmu.mason.vanilla.log.Characteristics;
import edu.gmu.mason.vanilla.log.Skip;

/**
 * General description_________________________________________________________
 * A data structure class to represent the meeting of multiple agents at a
 * location.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu), Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
@SuppressWarnings({"unused"})
public class Meeting implements java.io.Serializable {
	private static final long serialVersionUID = -4990129507126182596L;
	@Skip
	private boolean planned;
	private String meetingId;
	private LocalDateTime startTime;
	private List<Long> participants;

	public Meeting(boolean planned, LocalDateTime startTime, String meetingId) {
		this.planned = planned;
		this.startTime = startTime;
		participants = new ArrayList<>();
		this.meetingId = meetingId;
	}

	/**
	 * 
	 * @return {@code true} if planned, {@code false} if spontaneous.
	 */
	public boolean isPlanned() {
		return planned;
	}

	public void addParticipant(long agentId) {
		if (agentExists(agentId) == false) {
			this.participants.add(agentId);
		}
	}

	public void removeParticipant(Long agentId) {

		Iterator<Long> participantsIter = participants.iterator();
		while (participantsIter.hasNext()) {
			Long personId = participantsIter.next();
			if (personId.longValue() == agentId.longValue()) {
				participantsIter.remove();
				break;
			}
		}

	}

	private boolean agentExists(long personId) {
		for (Long prsId : participants) {
			if (prsId == personId) {
				return true;
			}
		}
		return false;
	}

	public int size() {
		return participants.size();
	}

	public List<Long> getParticipants() {
		return participants;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

}
