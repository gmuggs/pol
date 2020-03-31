package edu.gmu.mason.vanilla.environment;

import java.util.ArrayList;

import sim.util.geo.GeomPlanarGraphDirectedEdge;

/**
 * General description_________________________________________________________
 * This class is used to store pre-computed paths for fast access later.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class PrecomputedPath implements java.io.Serializable {
	private static final long serialVersionUID = 5586993133673885356L;
	private ArrayList<GeomPlanarGraphDirectedEdge> path;
	private double length;
	private boolean isForward;	
	
	public PrecomputedPath() {
		
	}

	public ArrayList<GeomPlanarGraphDirectedEdge> getPath() {
		return path;
	}

	public void setPath(ArrayList<GeomPlanarGraphDirectedEdge> path) {
		this.path = path;
	}
	
	public boolean contains(GeomPlanarGraphDirectedEdge edge) {
		return path.contains(edge);
	}

	public boolean isForward() {
		return isForward;
	}

	public void setForward(boolean isForward) {
		this.isForward = isForward;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

}
