package edu.gmu.mason.vanilla.environment;

import org.apache.commons.collections4.keyvalue.MultiKey;

import edu.gmu.mason.vanilla.VisitReason;
import sim.util.geo.MasonGeometry;

/**
 * General description_________________________________________________________
 * A data structure class to keep origin-destination pairs
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu), Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
@SuppressWarnings("rawtypes")
public class Travel {

	private MasonGeometry origin;
	private MasonGeometry destination;
	private double plannedVisitLength;
	private VisitReason visitReason;

	public Travel(MasonGeometry origin, MasonGeometry destination,
			double plannedVisitLength, VisitReason reason) {
		this.origin = origin;
		this.destination = destination;
		this.plannedVisitLength = plannedVisitLength;
		this.visitReason = reason;
	}

	/**
	 * Reverses the origin-destination pair and returns a new object.
	 * 
	 * @return a new {@code Travel} object
	 */
	public Travel reverse() {
		return new Travel(new MasonGeometry(this.destination.geometry),
				new MasonGeometry(this.origin.geometry), plannedVisitLength,
				visitReason);
	}

	@Override
	public boolean equals(Object obj) {
		Travel travel = (Travel) obj;
		return travel.getOrigin().getGeometry()
				.equals(this.getOrigin().geometry)
				&& travel.getDestination().getGeometry()
						.equals(this.getDestination().geometry);
	}

	public MasonGeometry getOrigin() {
		return origin;
	}

	public void setOrigin(MasonGeometry origin) {
		this.origin = origin;
	}

	public MasonGeometry getDestination() {
		return destination;
	}

	public void setDestination(MasonGeometry destination) {
		this.destination = destination;
	}

	@Deprecated
	public MultiKey getMultiKeyNormal() {
		return new MultiKey<>(origin.geometry.getCoordinate().x,
				origin.geometry.getCoordinate().y,
				destination.geometry.getCoordinate().x,
				destination.geometry.getCoordinate().y);
	}

	@Deprecated
	public MultiKey getMultiKeyReverse() {
		return this.reverse().getMultiKeyNormal();
	}

	public double getPlannedVisitLength() {
		return plannedVisitLength;
	}

	public void setPlannedVisitLength(double plannedVisitLength) {
		this.plannedVisitLength = plannedVisitLength;
	}

	public VisitReason getVisitReason() {
		return visitReason;
	}
}
