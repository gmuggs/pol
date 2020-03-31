package edu.gmu.mason.vanilla.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * General description_________________________________________________________
 * An accessor class
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class Accessor implements java.io.Serializable {
	private static final long serialVersionUID = 1723144070350269238L;

	private String name;
	private ManipulationType manipulationType;
	private Object[] parameters;

	public Object access(Object target) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		if (manipulationType == ManipulationType.FIELD) {
			Field field = target.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return field.get(target);
		}
		Class[] types = null;
		if (parameters != null) {
			types = new Class[parameters.length];
			for (int i = 0; i < types.length; i++) {
				types[i] = parameters[i].getClass();
			}
		}
		Method method = null;
		try {
			method = target.getClass().getDeclaredMethod(name, types);
		} catch (NoSuchMethodException e) {
			Method[] methods = target.getClass().getMethods();
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getName().equals(name)) {
					Class[] parameterTypes = methods[i].getParameterTypes();
					if (parameterTypes.length == types.length) {
						boolean assignable = true;
						Object[] numParameters = parameters.clone();
						for (int j = 0; j < parameterTypes.length; j++) {
							if (types[j].equals(Double.class)) {
								if (parameterTypes[j].equals(Integer.class) || parameterTypes[j].equals(int.class)) {
									numParameters[j] = ((Double) parameters[j])
											.intValue();
								} else if (parameterTypes[j].equals(Float.class) || parameterTypes[j].equals(float.class)) {
									numParameters[j] = ((Double) parameters[j])
											.floatValue();
								} else if (parameterTypes[j].equals(Long.class) || parameterTypes[j].equals(long.class)) {
									numParameters[j] = ((Double) parameters[j])
											.longValue();
								} else if (parameterTypes[j].equals(Short.class) || parameterTypes[j].equals(short.class)) {
									numParameters[j] = ((Double) parameters[j])
											.shortValue();
								} else if (parameterTypes[j].equals(Byte.class) || parameterTypes[j].equals(byte.class)) {
									numParameters[j] = ((Double) parameters[j])
											.byteValue();
								} else if (parameterTypes[j].equals(Double.class) || parameterTypes[j].equals(double.class)) {
									numParameters[j] = ((Double) parameters[j])
											.doubleValue();
								} else {
									assignable = false;
									break;
								}
							} else if (types[j].equals(Boolean.class) ||types[j].equals(boolean.class)) {
									numParameters[j] = ((Boolean) parameters[j]).booleanValue();
							} else if (!parameterTypes[j]
									.isAssignableFrom(types[j])) {
								assignable = false;
								break;
							}
						}
						if (assignable) {
							method = methods[i];
							parameters = numParameters;
							break;
						}
					}
				}
			}
			if (method == null)
				throw e;
		}
		return method.invoke(target, parameters);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public ManipulationType getManipulationType() {
		return manipulationType;
	}

	public void setManipulationType(ManipulationType manipulationType) {
		this.manipulationType = manipulationType;
	}

	public enum ManipulationType {
		FIELD, METHOD
	}
}
