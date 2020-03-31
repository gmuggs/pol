package edu.gmu.mason.vanilla.utils;

import java.util.ArrayList;
import java.util.List;

import sim.field.network.Edge;
import sim.field.network.Network;
import sim.util.Bag;


/**
 * General description_________________________________________________________
 * A class for network utility methods.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class NetworkUtil {
	
	public static List<Long> getCommmonFriendNodes(Network network, long from, long to) {
		List<Long> nodesToReturn = new ArrayList<Long>();
		List<Long> fromNodes = new ArrayList<>();
		List<Long> toNodes = new ArrayList<>();
		
		// find all connected nodes of the 'from' node
		Bag outEdges = network.getEdgesOut(from);
		
		for(Object edgeObj: outEdges) {
			Edge edge = (Edge) edgeObj;
			fromNodes.add((Long) (edge.getTo()));
		}
		
		
		// find all connected nodes of the 'to' node
		outEdges = network.getEdgesOut(to);
		
		for(Object edgeObj: outEdges) {
			Edge edge = (Edge) edgeObj;
			toNodes.add((Long)edge.getTo());
		}
				
		// if one of the connected node list has no element, so it means there is no way to have common nodes thus we return the empty list
		if (fromNodes.size()*toNodes.size() == 0) {
			return nodesToReturn;
		}
		
		// check which one has less number of elements
		
		if (fromNodes.size() < toNodes.size()) {
			
			for (Long aNode: fromNodes) {
				if (aNode != to && (network.getEdge(aNode, to) != null || network.getEdge(to, aNode) != null)) {
					nodesToReturn.add(aNode);
				}
			}
			
		} else {
			for (Long aNode: toNodes) {
				if (aNode != from && (network.getEdge(aNode, from) != null || network.getEdge(from, aNode) != null) ) {
					nodesToReturn.add(aNode);
				}
			}
		}
		
		return nodesToReturn;
	}
	
	public static boolean areFriends(Network network, long from, long to) {
		return network.getEdge(from, to) != null;
	}
	
	public static double friendshipStrength(Network network, long from, long to) {
		return (double) network.getEdge(from, to).getInfo();
	}
}
