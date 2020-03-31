package edu.gmu.mason.vanilla;

import com.vividsolutions.jts.geom.Geometry;

import sim.engine.Steppable;
import sim.util.geo.MasonGeometry;

/**
 * General description_________________________________________________________
 * A wrapper class for agent geometry representation.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class AgentGeometry extends MasonGeometry {

	private static final long serialVersionUID = -8818707833273751300L;
	private Steppable agent;

	public AgentGeometry() {
		super();
	}

	public AgentGeometry(Geometry g) {
		super(g);
	}

	public AgentGeometry(Geometry g, Object o) {
		super(g, o);
	}

	public Steppable getAgent() {
		return agent;
	}

	public void setAgent(Steppable agent) {
		this.agent = agent;
	}
}
