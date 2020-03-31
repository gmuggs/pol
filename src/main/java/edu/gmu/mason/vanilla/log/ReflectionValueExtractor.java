package edu.gmu.mason.vanilla.log;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;

/**
 * General description_________________________________________________________
 * Reflection value extracting class
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class ReflectionValueExtractor implements Function<Object, Object>, java.io.Serializable {
	private static final long serialVersionUID = 8103759706550818037L;
	private List<Field> accessor;

	public ReflectionValueExtractor(List<Field> accessor) {
		this.accessor = accessor;
	}

	@Override
	public Object apply(Object value) {
		Object currentValue = value;
		try {
			for (Field field : accessor) {
				field.setAccessible(true);
				currentValue = field.get(currentValue);
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		return currentValue;
	}

}
