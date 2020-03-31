package edu.gmu.mason.vanilla.utils;

import java.util.ArrayList;
import java.util.List;

import edu.gmu.mason.vanilla.utils.Accessor.ManipulationType;
import edu.gmu.mason.vanilla.utils.Manipulation.Operator;

/**
 * General description_________________________________________________________
 * A general manipulation builder class
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class ManipulationBuilder {
	private List<Manipulation> elements;
	
	public ManipulationBuilder() {
		elements = new ArrayList<Manipulation>();
	}
	
	public List<Manipulation> toList() {
		return elements;
	}
	
	public void append(Manipulation mani) {
		elements.add(mani);
	}
	
	public void appendModel(long step, String accessorExp, String fieldName, Operator op, Object value) throws Exception {
		String[] tokens = accessorExp.split("->");
		List<Accessor> accessors = new ArrayList<Accessor>();
		
		for (int i = 0; i < tokens.length; i++) {
			Accessor accessor = new Accessor();
			int idx = tokens[i].indexOf("(");
			if(idx == -1) {
				idx = tokens[i].length();
				accessor.setManipulationType(ManipulationType.FIELD);
			}
			else if(tokens[i].endsWith(")")) {
				accessor.setManipulationType(ManipulationType.METHOD);
				
				String paramStr = tokens[i].substring(idx+1, tokens[i].length()-1);
				if (!paramStr.equals("")) {
					String[] paramTokens = paramStr.split(",");
					Object[] parameters = new Object[paramTokens.length];

					for (int j = 0; j < paramTokens.length; j++) {
						String[] typeValuePair = paramTokens[j].split(" ");
						if (typeValuePair != null && typeValuePair.length == 2) {
							Class type = Class.forName(typeValuePair[0]);
							if (type.equals(Double.class)) {
								parameters[j] = Double.valueOf(typeValuePair[1]);
							} else if (type.equals(Integer.class)) {
								parameters[j] = Integer.valueOf(typeValuePair[1]);
							} else if (type.equals(Long.class)) {
								parameters[j] = Long.valueOf(typeValuePair[1]);
							} else if (type.equals(Float.class)) {
								parameters[j] = Float.valueOf(typeValuePair[1]);
							} else if (type.equals(Boolean.class)) {
								parameters[j] = Boolean.valueOf(typeValuePair[1]);
							} else if (type.equals(String.class)) {
								parameters[j] = typeValuePair[1];
							} else
								throw new Exception("Unsupported type: " + type);
						}
						else if(typeValuePair!=null && typeValuePair.length == 1) {
							parameters = null;
						}
						else throw new Exception("Parsing error: check accessorExp " + accessorExp);
					}

					accessor.setParameters(parameters);
				}
			}
			else
				throw new Exception("Parsing error: check accessorExp " + accessorExp);
			
			accessor.setName(tokens[i].substring(0, idx));
			accessors.add(accessor);
		}
		
		Manipulation m = new Manipulation();
		m.setActor(Manipulation.MODEL);
		m.setId(null);
		m.setSteps(step);
		m.setOperator(op);
		m.setFieldName(fieldName);
		m.setValue(value);
		m.setAccessors(accessors);
		
		elements.add(m);
	}
	
	public void appendRoad(long step, String id, String accessorExp, String fieldName, Operator op, Object value) throws Exception {
		String[] tokens = accessorExp.split("->");
		List<Accessor> accessors = new ArrayList<Accessor>();
		
		for (int i = 0; i < tokens.length; i++) {
			Accessor accessor = new Accessor();
			int idx = tokens[i].indexOf("(");
			if(idx == -1) {
				idx = tokens[i].length();
				accessor.setManipulationType(ManipulationType.FIELD);
			}
			else if(tokens[i].endsWith(")")) {
				accessor.setManipulationType(ManipulationType.METHOD);
				
				String paramStr = tokens[i].substring(idx+1, tokens[i].length()-1);
				String[] paramTokens = paramStr.split(",");
				Object[] parameters = new Object[paramTokens.length];
				
				for (int j = 0; j < paramTokens.length; j++) {
					String[] typeValuePair = paramTokens[j].split(" ");
					if(typeValuePair!=null && typeValuePair.length == 2) {
						Class type = Class.forName(typeValuePair[0]);
						if(type.equals(Double.class)) {
							parameters[j] = Double.valueOf(typeValuePair[1]);
						} else if(type.equals(Integer.class)) {
							parameters[j] = Integer.valueOf(typeValuePair[1]);
						} else if(type.equals(Long.class)) {
							parameters[j] = Long.valueOf(typeValuePair[1]);
						} else if(type.equals(Float.class)) {
							parameters[j] = Float.valueOf(typeValuePair[1]);
						} else if(type.equals(Boolean.class)) {
							parameters[j] = Boolean.valueOf(typeValuePair[1]);
						} else if(type.equals(String.class)) {
							parameters[j] = typeValuePair[1];
						} else
							throw new Exception("Unsupported type: " + type);
					}
					else if(typeValuePair!=null && typeValuePair.length == 1) {
						parameters = null;
					}
					else throw new Exception("Parsing error: check accessorExp " + accessorExp);
				}
				
				accessor.setParameters(parameters);
			}
			else
				throw new Exception("Parsing error: check accessorExp " + accessorExp);
			
			accessor.setName(tokens[i].substring(0, idx));
			accessors.add(accessor);
		}
		
		Manipulation m = new Manipulation();
		m.setActor(Manipulation.ROAD);
		m.setId(id);
		m.setSteps(step);
		m.setOperator(op);
		m.setFieldName(fieldName);
		m.setValue(value);
		m.setAccessors(accessors);
		
		elements.add(m);
	}
	
	public void appendPerson(long step, String id, String accessorExp, String fieldName, Operator op, Object value) throws Exception {
		String[] tokens = accessorExp.split("->");
		List<Accessor> accessors = new ArrayList<Accessor>();
		
		for (int i = 0; i < tokens.length; i++) {
			Accessor accessor = new Accessor();
			int idx = tokens[i].indexOf("(");
			if(idx == -1) {
				idx = tokens[i].length();
				accessor.setManipulationType(ManipulationType.FIELD);
			}
			else if(tokens[i].endsWith(")")) {
				accessor.setManipulationType(ManipulationType.METHOD);
				
				String paramStr = tokens[i].substring(idx+1, tokens[i].length()-1);
				String[] paramTokens = paramStr.split(",");
				Object[] parameters = new Object[paramTokens.length];
				
				for (int j = 0; j < paramTokens.length; j++) {
					String[] typeValuePair = paramTokens[j].split(" ");
					if(typeValuePair!=null && typeValuePair.length == 2) {
						Class type = Class.forName(typeValuePair[0]);
						if(type.equals(Double.class)) {
							parameters[j] = Double.valueOf(typeValuePair[1]);
						} else if(type.equals(Integer.class)) {
							parameters[j] = Integer.valueOf(typeValuePair[1]);
						} else if(type.equals(Long.class)) {
							parameters[j] = Long.valueOf(typeValuePair[1]);
						} else if(type.equals(Float.class)) {
							parameters[j] = Float.valueOf(typeValuePair[1]);
						} else if(type.equals(Boolean.class)) {
							parameters[j] = Boolean.valueOf(typeValuePair[1]);
						} else if(type.equals(String.class)) {
							parameters[j] = typeValuePair[1];
						} else
							throw new Exception("Unsupported type: " + type);
					}
					else if(typeValuePair!=null && typeValuePair.length == 1) {
						parameters = null;
					}
					else throw new Exception("Parsing error: check accessorExp " + accessorExp);
				}
				
				accessor.setParameters(parameters);
			}
			else
				throw new Exception("Parsing error: check accessorExp " + accessorExp);
			
			accessor.setName(tokens[i].substring(0, idx));
			accessors.add(accessor);
		}
		
		Manipulation m = new Manipulation();
		m.setActor(Manipulation.PERSON);
		m.setId(id);
		m.setSteps(step);
		m.setOperator(op);
		m.setFieldName(fieldName);
		m.setValue(value);
		m.setAccessors(accessors);
		
		elements.add(m);
	}
}
