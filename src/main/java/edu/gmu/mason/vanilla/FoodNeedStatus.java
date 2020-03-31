package edu.gmu.mason.vanilla;

/**
 * General description_________________________________________________________
 * An enumeration to represent five different stages of food need or hungriness.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public enum FoodNeedStatus {
	JustAte, // this status will increase the fullness for 30-60 minutes
	BecameFull, // this status follows justAte status for about 2-3 hours
	BecomingHungry, // this status is kicked in after being becameFull state and
					// decreases the fullness
	Hungry, // this status is kicked in after fullness reaches a certain
			// threshold
	Starving // if hungry state continues for a long time, the agent starts this
				// status
}
