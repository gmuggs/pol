package edu.gmu.mason.vanilla.utils;

import java.lang.reflect.Field;
import java.util.List;

import edu.gmu.mason.vanilla.WorldModel;
import edu.gmu.mason.vanilla.log.ExtLogger;
import sim.engine.SimState;
import sim.util.geo.AttributeValue;
import sim.util.geo.GeomPlanarGraph;
import sim.util.geo.GeomPlanarGraphEdge;

/**
 * General description_________________________________________________________
 * A general manipulation class used for providing interventions in the model
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class Manipulation implements Schedulable {
	public static final String PERSON = "Person";
	public static final String ROAD = "Road";
	public static final String MODEL = "Model";

	private static final long serialVersionUID = -4643782986071493272L;
	private static final ExtLogger logger = ExtLogger.create();
	private String actor;
	private String id;
	private List<Accessor> accessors;
	private Operator operator;
	private String fieldName;
	private Object value;

	private long steps;
	private int priority = Integer.MAX_VALUE;

	public Manipulation() {
	}

	@Override
	public void step(SimState state) {
		WorldModel model = (WorldModel) state;
		Object target = null;
		try {
			// select * from actor where id
			if (actor.equalsIgnoreCase(PERSON)) {
				target = model.getAgent(Long.parseLong(id));
			} else if (actor.equalsIgnoreCase(ROAD)) {
				GeomPlanarGraph graph = model.getSpatialNetwork()
						.getWalkwayNetwork();
				for (Object obj : graph.getEdges()) {
					GeomPlanarGraphEdge edge = (GeomPlanarGraphEdge) obj;
					AttributeValue attribute = (AttributeValue) edge
							.getAttribute("id");
					if (attribute.getValue().equals(Integer.parseInt(id))) {
						target = edge;
						break;
					}
				}
			} else if (actor.equalsIgnoreCase(MODEL)) {
				// this is very risky though.
				target = model;
			}

			if (accessors != null) {
				for (Accessor accessor : accessors) {
					target = accessor.access(target);
				}
			}

			if (fieldName == null)
				return;

			Field field = target.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			Object op1 = field.get(target);
			Object result = null;
			switch (operator) {
			case ADD:
				if (op1 instanceof Long) {
					if (value instanceof Double)
						result = (Long) op1 + ((Double) value).longValue();
					else if (value instanceof Integer)
						result = (Long) op1 + ((Integer) value).longValue();
					else
						result = (Long) op1 + (Long) value;
				} else if (op1 instanceof Integer) {
					if (value instanceof Double)
						result = (Integer) op1 + ((Double) value).intValue();
					else if (value instanceof Long)
						result = (Integer) op1 + ((Long) value).intValue();
					else
						result = (Integer) op1 + (Integer) value;
				} else {
					if (value instanceof Long)
						result = (Double) op1 + ((Long) value).doubleValue();
					else if (value instanceof Integer)
						result = (Double) op1 + ((Integer) value).doubleValue();
					else
						result = (Double) op1 + (Double) value;
				}
				break;
			case DIVIDE:
				if (op1 instanceof Long) {
					if (value instanceof Double)
						result = (Long) op1 / ((Double) value).longValue();
					else if (value instanceof Integer)
						result = (Long) op1 / ((Integer) value).longValue();
					else
						result = (Long) op1 / (Long) value;
				} else if (op1 instanceof Integer) {
					if (value instanceof Double)
						result = (Integer) op1 + ((Double) value).intValue();
					else if (value instanceof Long)
						result = (Integer) op1 / ((Long) value).intValue();
					else
						result = (Integer) op1 / (Integer) value;
				} else {
					if (value instanceof Long)
						result = (Double) op1 / ((Long) value).doubleValue();
					else if (value instanceof Integer)
						result = (Double) op1 / ((Integer) value).doubleValue();
					else
						result = (Double) op1 / (Double) value;
				}
				break;
			case MULTIPLY:
				if (op1 instanceof Long) {
					if (value instanceof Double)
						result = (Long) op1 * ((Double) value).longValue();
					else if (value instanceof Integer)
						result = (Long) op1 * ((Integer) value).longValue();
					else
						result = (Long) op1 * (Long) value;
				} else if (op1 instanceof Integer) {
					if (value instanceof Double)
						result = (Integer) op1 * ((Double) value).intValue();
					else if (value instanceof Long)
						result = (Integer) op1 * ((Long) value).intValue();
					else
						result = (Integer) op1 * (Integer) value;
				} else {
					if (value instanceof Long)
						result = (Double) op1 * ((Long) value).doubleValue();
					else if (value instanceof Integer)
						result = (Double) op1 * ((Integer) value).doubleValue();
					else
						result = (Double) op1 * (Double) value;
				}
				break;
			case SUBSTRACT:
				if (op1 instanceof Long) {
					if (value instanceof Double)
						result = (Long) op1 - ((Double) value).longValue();
					else if (value instanceof Integer)
						result = (Long) op1 - ((Integer) value).longValue();
					else
						result = (Long) op1 - (Long) value;
				} else if (op1 instanceof Integer) {
					if (value instanceof Double)
						result = (Integer) op1 - ((Double) value).intValue();
					else if (value instanceof Long)
						result = (Integer) op1 - ((Long) value).intValue();
					else
						result = (Integer) op1 - (Integer) value;
				} else {
					if (value instanceof Long)
						result = (Double) op1 - ((Long) value).doubleValue();
					else if (value instanceof Integer)
						result = (Double) op1 - ((Integer) value).doubleValue();
					else
						result = (Double) op1 - (Double) value;
				}
				break;
			case SET:
				result = value;
				break;
			default:
				result = value;
				break;
			}

			field.set(target, result);
		} catch (Exception e) {
			logger.error("Error occured during manipulation", e);
		}
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Accessor> getAccessors() {
		return accessors;
	}

	public void setAccessors(List<Accessor> accessors) {
		this.accessors = accessors;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public enum Operator {
		ADD, SUBSTRACT, MULTIPLY, DIVIDE, SET
	}

	@Override
	public long getSteps() {
		return steps;
	}

	public void setSteps(long steps) {
		this.steps = steps;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
}
