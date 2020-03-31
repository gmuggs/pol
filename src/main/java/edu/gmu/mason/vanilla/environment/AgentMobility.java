package edu.gmu.mason.vanilla.environment;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;

import sim.util.geo.GeomPlanarGraphDirectedEdge;
import sim.util.geo.GeomPlanarGraphEdge;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.linearref.LengthIndexedLine;

import edu.gmu.mason.vanilla.JournalRecord;
import edu.gmu.mason.vanilla.Person;
import edu.gmu.mason.vanilla.PersonMode;
import edu.gmu.mason.vanilla.VisitReason;

/**
 * General description_________________________________________________________
 * A class used for handling mobility-related operations such as moving between
 * places and so on.
 * 
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu), Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class AgentMobility implements java.io.Serializable {
	private static final long serialVersionUID = 121737305214919911L;
	Person agent;

	private PersonMode destinationMode; // when we transport from one to
										// another, we need a destination
										// information
	private PersonMode origin; // when we transport from one to another, we need
								// an origin information
	private BuildingUnit destinationUnit; // building in the destination.
	private BuildingUnit originUnit; // building in the origin.

	// speed unit = meter/second (according to Preferred walking speed of Wiki)
	private double walkingSpeed;

	private double basemoveRate = 1;
	// How much to move the agent by in each step(); may become negative if
	// agent is moving from the end to the start of current line.
	private double moveRate = basemoveRate;
	// Used by agent to walk along line segment;
	private transient LengthIndexedLine segment = null;
	double startIndex = 0.0; // start position of current line
	double endIndex = 0.0; // end position of current line
	double currentIndex = 0.0; // current location along line
	double plannedVisitLength = 0.0;

	// private boolean isPathChanged = true; // when the path is changed, this
	// flag should be set as true
	private boolean isForward = true; // we don't need to change the path if the
										// direction of the path is reverse

	ArrayList<GeomPlanarGraphDirectedEdge> path = new ArrayList<GeomPlanarGraphDirectedEdge>(); // path

	// to calculate travel distance
	private LocalDateTime previousTime;
	//private LocalDateTime travelBeginTime;
	
	private VisitReason currentTravelReason;
	// index for 'path' starting from 0
	private int currentPathIndex = 0;

	public AgentMobility(Person person) {
		this.agent = person;
	}

	/**
	 * Initiate movement of this agent
	 * 
	 * @param from
	 * @param to
	 * @param destination
	 */
	public void beginToTransport(Travel travel, PersonMode destinationMode,
			BuildingUnit destinationUnit, boolean savePath) {

		agent.unjitter();
		currentTravelReason = travel.getVisitReason();

		this.origin = agent.getCurrentMode();
		this.originUnit = agent.getCurrentUnit();
		this.destinationMode = destinationMode;
		this.destinationUnit = destinationUnit;

		if (this.originUnit != null) {
			originUnit.agentLeaves(agent); // remove agent from the current unit
		}

		PrecomputedPath precomputedPath = agent.getModel().getSpatialNetwork()
				.getPath(travel, savePath);
		path = precomputedPath.getPath();
		currentPathIndex = 0;
		startIndex = 0.0;
		isForward = precomputedPath.isForward();
		// let's move in the next step. just set up time
		previousTime = agent.getSimulationTime();
		//travelBeginTime = agent.getSimulationTime();
		plannedVisitLength = travel.getPlannedVisitLength();
		
		// finish an existing journal record
		// check journal record. if there is a record, that means the agent is leaving a place now.
		if (agent.getCurrentJournal() != null) {
			agent.endCurrentJournalRecord();
		}

		// the following statement checks if we capture this visit in the agent journal
		if (agent.getModel().getJournalSettings().checkMode(destinationMode) == true) {
			agent.recordANewJournal(destinationMode, destinationUnit, travel.getVisitReason());
		}

		agent.setCurrentUnit(null);
		agent.setCurrentMode(PersonMode.Transport); // set agent mode to transport
	}

	/**
	 * Agent transports from one point to another
	 */
	public boolean transport() {
		LocalDateTime currentTime = agent.getSimulationTime();
		boolean isArrived = true;
		Coordinate position = null;
		List<Integer> pass = new ArrayList<>();

		// If the start and end node are identical, we just arrive and don't
		// need to
		// calculate
		if (path.size() > 0) {
			// time interval in second
			double intervalSecond = (double) Seconds.secondsBetween(
					previousTime, currentTime).getSeconds();
			// We assume moveRate is speed of agent.
			// We don't consider about traffic in this logic at this moment.
			double accumulatedIndex = startIndex;
			double lengthOfRoad = 0.0;
			double weight = 1.0;
			double difference = 0.0;
			currentIndex += intervalSecond * moveRate * walkingSpeed;
			int i = currentPathIndex;
			if (isForward) {
				if(startIndex == 0.0) 
					i = 0;
				for (; i < path.size(); i++) {
					currentPathIndex = i;
					startIndex = accumulatedIndex;
					// We assume this edge will be GeomPlanarGraphEdge
					GeomPlanarGraphDirectedEdge dE = path.get(i);
					GeomPlanarGraphEdge edge = (GeomPlanarGraphEdge) dE
							.getEdge();
					// if weight of edge exists, we will consider it.
					if (edge.hasAttribute("weight")) {
						weight = edge.getDoubleAttribute("weight");
						// Simply multiply the length with weight;
						lengthOfRoad = edge.getLine().getLength() * weight;
					} else {
						lengthOfRoad = edge.getLine().getLength();
					}
					pass.add(edge.getIntegerAttribute("id"));
					if (currentIndex < accumulatedIndex + lengthOfRoad) {
						// we found the current position in the path
						segment = new LengthIndexedLine(edge.getLine());
						if (dE.getEdgeDirection()) {
							difference = currentIndex - accumulatedIndex;
							if (edge.hasAttribute("weight")) {
								difference /= weight;
							}
							position = segment.extractPoint(difference);
						} else {
							// backward
							difference = segment.getEndIndex()
									- (currentIndex - accumulatedIndex);
							if (edge.hasAttribute("weight")) {
								difference /= weight;
							}
							position = segment.extractPoint(difference);
						}
						isArrived = false;
						agent.writeTrajectoryToFile(pass);
						break;
					}
					accumulatedIndex += lengthOfRoad;
					position = path.get(i).getToNode().getCoordinate();
				}
			} else {
				// reverse
				if(startIndex == 0.0) 
					i = path.size() - 1;
				for (; i > 0; i--) {
					currentPathIndex = i;
					startIndex = accumulatedIndex;
					// We assume all edges are bidirectional and each direction
					// of an edge has the
					// same weight.
					GeomPlanarGraphDirectedEdge dE = path.get(i);
					GeomPlanarGraphEdge edge = (GeomPlanarGraphEdge) dE
							.getEdge();
					if (edge.hasAttribute("weight")) {
						weight = edge.getDoubleAttribute("weight");
						// Simply multiply the length with weight;
						lengthOfRoad = edge.getLine().getLength() * weight;
					} else {
						lengthOfRoad = edge.getLine().getLength();
					}
					pass.add(edge.getIntegerAttribute("id"));
					if (currentIndex < accumulatedIndex + lengthOfRoad) {
						// we found the current position in the path
						segment = new LengthIndexedLine(edge.getLine());
						if (!dE.getEdgeDirection()) {
							difference = currentIndex - accumulatedIndex;
							if (edge.hasAttribute("weight")) {
								difference /= weight;
							}
							position = segment.extractPoint(difference);
						} else {
							// backward
							difference = segment.getEndIndex()
									- (currentIndex - accumulatedIndex);
							if (edge.hasAttribute("weight")) {
								difference /= weight;
							}
							position = segment.extractPoint(difference);
						}
						isArrived = false;
						agent.writeTrajectoryToFile(pass);
						break;
					}
					accumulatedIndex += lengthOfRoad;
					position = path.get(i).getFromNode().getCoordinate();
				}
			}
		}
		previousTime = currentTime;
		if (isArrived) {
			// reset currentIndex
			currentIndex = 0.0;
			currentPathIndex = 0;
			startIndex = 0.0;
			
			// now entering the building
			if (agent.getCurrentJournal() != null) {
				JournalRecord record = agent.getCurrentJournal();
				record.setCheckInTime(agent.getSimulationTime());
				record.setMoneyBalanceBefore(agent.getFinancialSafetyNeed().getAvailableBalance());
				record.setTravelEndTime(agent.getSimulationTime());
				record.setTravelEndLocationId(destinationUnit.getId());
				record.setTravelEndPlaceType(destinationMode);
			}
			
			// The agent arrived at the destination and current mode will change
			agent.setCurrentMode(destinationMode);
			agent.setCurrentUnit(destinationUnit);
			destinationUnit.agentArrives(agent, plannedVisitLength);

			agent.moveTo(destinationUnit.getLocation().geometry.getCoordinate());
			agent.jitter();
			agent.setVisitReason(currentTravelReason);
		} else
			agent.moveTo(position);

		return isArrived;
	}

	public PersonMode getOrigin() {
		return origin;
	}

	public BuildingUnit getOriginUnit() {
		return originUnit;
	}

	public BuildingUnit getDestinationUnit() {
		return destinationUnit;
	}

	public double getPlannedVisitLength() {
		return plannedVisitLength;
	}

	/**
	 * Returns speed (meter/second).
	 * 
	 * @return
	 */
	public double getSpeed() {
		return moveRate * walkingSpeed;
	}

	public double getMoveRate() {
		return moveRate;
	}

	public void setMoveRate(double moveRate) {
		this.moveRate = moveRate;
	}

	public double getWalkingSpeed() {
		return walkingSpeed;
	}

	public void setWalkingSpeed(double walkingSpeed) {
		this.walkingSpeed = walkingSpeed;
	}

}
