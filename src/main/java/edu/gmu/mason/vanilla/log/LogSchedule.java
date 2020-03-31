package edu.gmu.mason.vanilla.log;

import java.util.function.Supplier;

import org.apache.logging.log4j.Level;

import edu.gmu.mason.vanilla.utils.Schedulable;
import sim.engine.SimState;

/**
 * General description_________________________________________________________
 * Log schedule class
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class LogSchedule implements Schedulable {
	private static final long serialVersionUID = 729975088705913795L;
	private final static ExtLogger logger = ExtLogger.create();
	protected boolean repeat;
	private boolean updateOnly = false;
	protected String previous = null;
	protected long period;
	protected long steps;
	protected int priority;
	protected Supplier supplier;
	protected String level;
	protected OutputFormatter format;

	public LogSchedule(long startStep, String level, Supplier supplier,
			OutputFormatter format, int priority) {
		this.steps = startStep;
		this.level = level;
		this.supplier = supplier;
		this.format = format;
		this.priority = priority;
		this.repeat = false;
	}

	public LogSchedule(long startStep, long period, String level,
			Supplier supplier, OutputFormatter format, int priority) {
		this(startStep, level, supplier, format, priority);
		this.period = period;
		this.repeat = true;
	}

	public LogSchedule(long startStep, long period, String level,
			Supplier supplier, OutputFormatter format, int priority,
			boolean updateOnly) {
		this(startStep, period, level, supplier, format, priority);
		this.updateOnly = updateOnly;
	}

	@Override
	public void step(SimState state) {
		Object value = supplier.get();
		String output;
		if (value != null && (output = format.print(value)) != null) {
			if (updateOnly) {
				if (!output.equals(previous)) {
					logger.log(Level.getLevel(level), output);
				}
				previous = output;
			} else
				logger.log(Level.getLevel(level), output);
		}

		if (repeat) {
			steps += period;
		}
	}

	public boolean isRepeat() {
		return repeat;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public long getSteps() {
		return steps;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public boolean isUpdateOnly() {
		return updateOnly;
	}

	public void setUpdateOnly(boolean updateOnly) {
		this.updateOnly = updateOnly;
	}

	public String getLevel() {
		return level;
	}
}
