package edu.gmu.mason.vanilla.environment;

import java.util.ArrayList;
import java.util.List;

import edu.gmu.mason.vanilla.WorldModel;
import edu.gmu.mason.vanilla.log.Characteristics;
import edu.gmu.mason.vanilla.log.Referenceable;
import edu.gmu.mason.vanilla.log.State;
import sim.util.geo.MasonGeometry;

/**
 * General description_________________________________________________________
 * A class to represent building objects (polygons). Each building is one of
 * {@code BuildingType} enumeration and may contain multiple building units.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
@Referenceable(keyMethod = "getId", keyType = Long.class)
public class Building implements java.io.Serializable {

	private static final long serialVersionUID = -3149297554683421984L;

	private long id;
	@Characteristics
	private MasonGeometry location;
	@Characteristics
	private BuildingType buildingType;
	@Characteristics
 	private double attractivenessPercentile;
	@Characteristics
	private double attractiveness;
	@Characteristics
	private int totalPersonCapacity;
	@Characteristics
	private int totalRoomCapacity;
	private List<BuildingUnit> units;
	private WorldModel world;
	@Characteristics
	private int neighborhoodId;
	@Characteristics
	private int blockId;
	@Characteristics
	private int blockGroupId;
	@Characteristics
	private int censusTractId;
	@State
	private boolean usable = true;

	public Building(WorldModel world, long id) {
		this.world = world;
		this.id = id;
		totalPersonCapacity = 0;
		totalRoomCapacity = 0;
		units = new ArrayList<BuildingUnit>();
	}

	public WorldModel getWorld() {
		return world;
	}

	public MasonGeometry getLocation() {
		return location;
	}

	public void setLocation(MasonGeometry location) {
		this.location = location;
	}

	public BuildingType getBuildingType() {
		return buildingType;
	}

	public void setBuildingType(BuildingType buildingType) {
		this.buildingType = buildingType;
	}

	public double getAttractiveness() {
		return attractiveness;
	}

	public void setAttractiveness(double attractiveness) {
		this.attractiveness = attractiveness;
	}
	
	public double getAttractivenessPercentile() {
 		return attractivenessPercentile;
 	}

  	public void setAttractivenessPercentile(double attractivenessPercentile) {
 		this.attractivenessPercentile = attractivenessPercentile;
 	}

	public List<BuildingUnit> getUnits() {
		return units;
	}

	public boolean addUnit(BuildingUnit unit) {
		totalPersonCapacity += unit.getPersonCapacity();
		totalRoomCapacity += unit.getNumberOfRooms();
		return units.add(unit);
	}

	public int getTotalPersonCapacity() {
		return totalPersonCapacity;
	}

	public int getTotalRoomCapacity() {
		return totalRoomCapacity;
	}

	public int getAvailablePersonCapacity() {

		// available person capacity is only used in apartments

		if (this.buildingType == BuildingType.Residental) { // Residential is
															// the only building
															// type that
															// includes
															// apartments
			int occupancy = 0;
			for (BuildingUnit unit : units) {
				Apartment apartment = (Apartment) unit;
				occupancy += apartment.getHousehold().getMembers().size();
			}
			return totalPersonCapacity - occupancy;
		} else {
			return Integer.MAX_VALUE;
		}
	}

	public long getId() {
		return id;
	}

	public int getNeighborhoodId() {
		return neighborhoodId;
	}

	public void setNeighborhoodId(int neighborhoodId) {
		this.neighborhoodId = neighborhoodId;
	}

	public int getBlockId() {
		return blockId;
	}

	public void setBlockId(int blockId) {
		this.blockId = blockId;
	}

	public int getBlockGroupId() {
		return blockGroupId;
	}

	public void setBlockGroupId(int blockGroupId) {
		this.blockGroupId = blockGroupId;
	}

	public int getCensusTractId() {
		return censusTractId;
	}

	public void setCensusTractId(int censusTractId) {
		this.censusTractId = censusTractId;
	}

	public boolean isUsable() {
		return usable;
	}

}
