package edu.gmu.mason.vanilla.utils;

import sim.engine.Steppable;

/**
 * General description_________________________________________________________
 * Schedulable interface
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public interface Schedulable extends Steppable {
	long getSteps();
	int getPriority();
}
