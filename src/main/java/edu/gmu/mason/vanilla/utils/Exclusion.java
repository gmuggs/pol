package edu.gmu.mason.vanilla.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * General description_________________________________________________________
 * A class used for excluding class variables from data logging.
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class Exclusion implements ExclusionStrategy {
	private final Class<?>[] typeToSkip;
	private final List<Class> fieldsToSkip;

	public Exclusion(Class<?>... typeToSkip) {
		this.typeToSkip = typeToSkip;
		fieldsToSkip = new ArrayList<Class>();
	}

	public void addSkipField(Class<?> clazz) {
		fieldsToSkip.add(clazz);
	}

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		for (int i = 0; i < typeToSkip.length; i++) {
			if (clazz == typeToSkip[i])
				return true;
		}
		return false;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		for (Class clazz : fieldsToSkip) {
			if (f.getAnnotation(clazz) != null)
				return true;
		}
		return false;
	}

}
