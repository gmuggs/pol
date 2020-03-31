package edu.gmu.mason.vanilla;

import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * General description_________________________________________________________
 * This class schedules evening and midnight routines that have to be executed
 * in order for model to run appropriately.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu), Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class DailyRoutines implements Steppable {

	private static final long serialVersionUID = -646600383548431660L;
	private RoutineType routineType;

	public DailyRoutines() {
		this(RoutineType.Midnight);
	}

	public DailyRoutines(RoutineType type) {
		this.routineType = type;
	}

	@Override
	public void step(SimState state) {
		WorldModel model = (WorldModel) state;

		// execute daily routines

		if (routineType == RoutineType.Midnight) {
			// execute monthly routines
			if (model.getSimulationTime().getDayOfMonth() == 1) { // beginning
																	// of the
																	// month
				model.monthlyRoutine();
			}
			// nightly, agent's next day plans are switched to today's plan.
			model.nightlyRoutine();

		} else if (routineType == RoutineType.Evening) {

			model.eveningRoutine();
		}
	}

	public enum RoutineType {
		Midnight, // for routines at 12:00AM
		Evening // for routines at 7PM
	}
}
