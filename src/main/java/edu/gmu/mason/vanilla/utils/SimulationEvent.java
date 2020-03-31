package edu.gmu.mason.vanilla.utils;

/**
 * General description_________________________________________________________
 * An enumeration to represent different events in the model. This is used for
 * calculating execution time and has no impact on model logic.
 * 
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public enum SimulationEvent {
	SimulationStart,
	SimulationEnd,
	PreComputingStart,
	PreComputingEnd,
	EnvironmentInitStart,
	EnvironmentInitEnd,
	AgentInitStart,
	AgentInitEnd,
	GUILoadStart,
	GUILoadEnd,
	DayStart,
	DayEnd,
	WeekStart,
	WeekEnd,
	MonthStart,
	MonthEnd,
	CustomEvent1Start,
	CustomEvent1End,
	CustomEvent2Start,
	CustomEvent2End,
	CustomEvent3Start,
	CustomEvent3End
}
