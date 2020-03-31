package edu.gmu.mason.vanilla.utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import edu.gmu.mason.vanilla.AgentInterest;

/**
 * General description_________________________________________________________
 * A class to capture colors for GUI.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu), Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class ColorUtils {
	private static Map<AgentInterest,Color> interestColorMap; 
	private static Map<Integer, Color> agentHungerColorMap;
	
	public static Map<AgentInterest,Color> getInterestColorMap(){
		
		if (interestColorMap == null) {
			interestColorMap = new HashMap<AgentInterest,Color>();
			
			interestColorMap.put(AgentInterest.NA, Color.BLACK); 
			interestColorMap.put(AgentInterest.A, Color.decode("#FE802A")); 
			interestColorMap.put(AgentInterest.B, Color.decode("#30A039")); 
			interestColorMap.put(AgentInterest.C, Color.decode("#D52A2D")); 
			interestColorMap.put(AgentInterest.D, Color.decode("#9367BA")); 
			interestColorMap.put(AgentInterest.E, Color.decode("#8C564C")); 
			interestColorMap.put(AgentInterest.F, Color.decode("#E278C0")); 
			interestColorMap.put(AgentInterest.G, Color.decode("#7F7F7F")); 
			interestColorMap.put(AgentInterest.H, Color.decode("#BCBD3B")); 
			interestColorMap.put(AgentInterest.I, Color.decode("#1FBECD")); 
			interestColorMap.put(AgentInterest.J, Color.decode("#A2A7AD")); 
		}
		return interestColorMap;
	}
	
	public static Map<Integer, Color> getAgentHungerColorMap(){
		
		if (agentHungerColorMap == null) {
			agentHungerColorMap = new HashMap<Integer, Color>();
			
			agentHungerColorMap.put(0, new Color(255, 0, 0));
			agentHungerColorMap.put(1, new Color(255, 0, 0));
			agentHungerColorMap.put(2, new Color(255, 51, 51));
			agentHungerColorMap.put(3, new Color(255, 51, 51));
			agentHungerColorMap.put(4, new Color(255, 102, 102));
			agentHungerColorMap.put(5, new Color(102, 102, 255));
			agentHungerColorMap.put(6, new Color(51, 51, 255));
			agentHungerColorMap.put(7, new Color(51, 51, 255));
			agentHungerColorMap.put(8, new Color(0, 0, 255));
			agentHungerColorMap.put(9, new Color(0, 0, 255));
		}
		return agentHungerColorMap;
	}
}
