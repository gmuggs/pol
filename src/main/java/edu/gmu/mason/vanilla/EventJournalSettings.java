package edu.gmu.mason.vanilla;

/**
 * General description_________________________________________________________
 * Event journal settings helper.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */

import java.util.ArrayList;
import java.util.List;

public class EventJournalSettings implements java.io.Serializable {
	private static final long serialVersionUID = 3368376228933618196L;
	private List<PersonMode> modesToCapture;
	
	public EventJournalSettings() {
		modesToCapture = new ArrayList<>();
	}
	
	public EventJournalSettings addMode(PersonMode mode) {
		modesToCapture.add(mode);
		return this;
	}
	
	public boolean checkMode(PersonMode mode) {
		return modesToCapture.contains(mode);
	}
}