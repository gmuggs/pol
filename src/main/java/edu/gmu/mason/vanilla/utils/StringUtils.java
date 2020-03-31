package edu.gmu.mason.vanilla.utils;

import java.util.Iterator;

/**
 * General description_________________________________________________________
 * String utility class.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class StringUtils {
	
	public static double trimDecimals(double number, int numberOfDigits) {
		String formatZeros = "";
		for (int i=0; i<numberOfDigits; i++) {
			formatZeros += "0";
		}
		return Double.parseDouble(new java.text.DecimalFormat("0."+formatZeros).format( number ));
	}
	
	public static <T> String join(String delimeter, Iterable<T> list) {
		String finalString = "";
		boolean firstValue = false;
		Iterator<T> iter = list.iterator();
		while (list != null && iter.hasNext() == true) {
			if (firstValue == false) {
				finalString = iter.next()+"";
				firstValue = true;
			} else {
				finalString += delimeter + iter.next();
			}
		}
		
		return finalString;
	}
	
}