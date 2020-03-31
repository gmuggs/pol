package edu.gmu.mason.vanilla.utils;


import java.util.function.Function;

import edu.gmu.mason.vanilla.log.ExtLogger;
import sim.engine.SimState;
/**
 * General description_________________________________________________________
 * Used to schedule events
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class EventSchedule implements Schedulable {
	private static final long serialVersionUID = 4490715801035729484L;
	private final static ExtLogger logger = ExtLogger.create();
	protected boolean repeat;
	protected long period;
	protected long steps;
	protected int priority;
	protected Function func;
	protected Object input;
	private Object output;
	
	public EventSchedule(Function func, long startStep, long period, int priority, boolean repeat) {
		this.func = func;
		this.steps = startStep;
		this.period = period;
		this.priority = priority;
		this.repeat = repeat;
	}

	@Override
	public void step(SimState sim) {
		output = func.apply(input);
		if(repeat) {
			steps += period;
		}
	}

	@Override
	public long getSteps() {
		return steps;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public Object getInput() {
		return input;
	}

	public void setInput(Object input) {
		this.input = input;
	}

	public Object getOutput() {
		return output;
	}

	public void setOutput(Object output) {
		this.output = output;
	}
}
