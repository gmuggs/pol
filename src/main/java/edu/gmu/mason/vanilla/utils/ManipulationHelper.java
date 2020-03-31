package edu.gmu.mason.vanilla.utils;

import java.util.ArrayList;
import java.util.List;

import edu.gmu.mason.vanilla.utils.Accessor.ManipulationType;
import edu.gmu.mason.vanilla.utils.Manipulation.Operator;

/**
 * General description_________________________________________________________
 * A helper class for manipulation
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class ManipulationHelper {

	/**
	 * agent.age += x
	 * @return
	 */
	public static String exampleAgentAgeAdd(double x) {
		Manipulation m = new Manipulation();
		m.setActor(Manipulation.PERSON);
		m.setId("1");
		m.setSteps(100);
		m.setOperator(Operator.ADD);
		m.setFieldName("age");
		m.setValue(x);
		String json = ManipulationLoader.saveToJson(m);
		return json;
	}
	
	/**
	 * agent.financialSafetyNeed.availableBalance += x
	 * @return
	 */
	public static String exampleAgentMoneyAdd(double x) {
		List<Accessor> accessors = new ArrayList<Accessor>();
		Accessor financialSafetyNeed = new Accessor();
		financialSafetyNeed.setManipulationType(ManipulationType.FIELD);
		financialSafetyNeed.setName("financialSafetyNeed");
		accessors.add(financialSafetyNeed);
		
		Manipulation m = new Manipulation();
		m.setActor(Manipulation.PERSON);
		m.setId("1");
		m.setSteps(100);
		m.setOperator(Operator.ADD);
		m.setFieldName("availableBalance");
		m.setValue(x);
		m.setAccessors(accessors);
		String json = ManipulationLoader.saveToJson(m);
		return json;
	}
	
	/**
	 * agent.financialSafetyNeed.job.hourlyRate += x
	 * @return
	 */
	public static String exampleAgentIncomeMultiply(double x) {
		List<Accessor> accessors = new ArrayList<Accessor>();
		Accessor financialSafetyNeed = new Accessor();
		financialSafetyNeed.setManipulationType(ManipulationType.FIELD);
		financialSafetyNeed.setName("financialSafetyNeed");
		accessors.add(financialSafetyNeed);
		
		Accessor job = new Accessor();
		job.setManipulationType(ManipulationType.FIELD);
		job.setName("job");
		accessors.add(job);
		
		Manipulation m = new Manipulation();
		m.setActor(Manipulation.PERSON);
		m.setId("1");
		m.setSteps(100);
		m.setOperator(Operator.MULTIPLY);
		m.setFieldName("hourlyRate");
		m.setValue(x);
		m.setAccessors(accessors);
		String json = ManipulationLoader.saveToJson(m);
		return json;
	}
	
	/**
	 * road.attributes.get("damaged") = x
	 * @return
	 */
	public static String exampleRoadDestroy(double x) {
		List<Accessor> accessors = new ArrayList<Accessor>();
		Accessor attributes = new Accessor();
		attributes.setManipulationType(ManipulationType.FIELD);
		attributes.setName("attributes");
		accessors.add(attributes);
		
		Accessor damaged = new Accessor();
		damaged.setManipulationType(ManipulationType.METHOD);
		damaged.setName("get");
		damaged.setParameters(new Object[] {"damaged"});
		accessors.add(damaged);
		
		Manipulation m = new Manipulation();
		m.setActor(Manipulation.ROAD);
		m.setId("1");
		m.setSteps(100);
		m.setOperator(Operator.SET);
		m.setFieldName("value");
		m.setValue(x);
		m.setAccessors(accessors);
		String json = ManipulationLoader.saveToJson(m);
		return json;
	}
	
	public static void saveJsonFile(String json, String fileName) {
		
	}
}
