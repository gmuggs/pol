package edu.gmu.mason.vanilla.utils;

import java.time.Duration;

import org.apache.commons.configuration2.convert.DefaultConversionHandler;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.joda.time.LocalDateTime;
/**
 * General description_________________________________________________________
 * Used for conversions of type
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class CustomConversionHandler extends DefaultConversionHandler {
	protected <T> T convertValue(Object src, Class<T> targetCls, ConfigurationInterpolator ci) {
		if (targetCls == LocalDateTime.class)
			return (T) LocalDateTime.parse(src.toString());
		else if (targetCls == Duration.class)
			return (T) Duration.parse(src.toString());
		return super.convertValue(src, targetCls, ci);
	}
}
