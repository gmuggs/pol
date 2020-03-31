package edu.gmu.mason.vanilla.utils;

import java.util.List;

import com.vividsolutions.jts.geom.Envelope;

import edu.gmu.mason.vanilla.environment.BuildingUnit;
import sim.field.geo.GeomVectorField;
import sim.util.Bag;
import sim.util.geo.MasonGeometry;

/**
 * General description_________________________________________________________
 * A class for collecting geography-related utility methods.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu), Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class GeoUtils {
	public static final int MAXIMUM_THRESHOLD = 50;
	public static final double DEFAULT_DISTANCE = 1;

	/**
	 * This method aligns the minimum bounding rectangles of geographic layers.
	 * 
	 * @param layers
	 */
	public static void alignMBRs(List<GeomVectorField> layers) {

		Envelope globalMBR = layers.get(0).getMBR();

		for (int i = 1; i < layers.size(); i++) {
			globalMBR.expandToInclude(layers.get(i).getMBR());
		}

		for (GeomVectorField layer : layers) {
			layer.setMBR(globalMBR);
		}
	}

	static int count = 0;

	/**
	 * This method is to find the nearest geometry from a given geometry in a given
	 * GeomVectorField.
	 * 
	 * @param from
	 * @param field
	 * @return
	 */
	public static MasonGeometry findNearestGeometry(MasonGeometry from, GeomVectorField field) {
		return findNearestGeometry(from, field, DEFAULT_DISTANCE);
	}
	
	/**
	 * This method is to find the nearest geometry from a given geometry in a given GeomVectorField. 
	 * 
	 * @param from
	 * @param field
	 * @param startDistance
	 * @return
	 */
	public static MasonGeometry findNearestGeometry(MasonGeometry from, GeomVectorField field, double startDistance) {
		// we assume the index of field is up-to-dated.

		// TODO: we need to optimize NNQ later
		// For now, we gradually increase a distance for the nearest neighbor query, and
		// then perform range query.
		Bag candidates = field.getGeometries();
		double dist = startDistance;
		// if size of data is small enough, we will not perform any range query any
		// more.
		if (candidates.size() > MAXIMUM_THRESHOLD) {
			// filter step
			while (true) {
				// simply increase or decrease distance to find an appropriate number of
				// candidates
				candidates = field.getObjectsWithinDistance(from, dist);
				if (candidates.size() == 0)
					dist *= 10;
				else if (candidates.size() > MAXIMUM_THRESHOLD)
					dist *= 0.5;
				else
					break;
			}
		}

		double minDist = Double.MAX_VALUE;
		MasonGeometry nearest = null;
		// refinement step: now it's time to find the nearest among candidates
		for (Object ele : candidates) {
			MasonGeometry geo = (MasonGeometry) ele;
			double tmp = geo.geometry.distance(from.geometry);
			if (minDist > tmp) {
				nearest = geo;
				minDist = tmp;
			}
		}

		return nearest;
	}
	
	/**
	 * This method is to find the nearest geometry from a given geometry within distance 
	 * in a given GeomVectorField. 
	 * If there is no geometry within the distance, it will return null.
	 * 
	 * @param from
	 * @param field
	 * @param withinDistance
	 * @return
	 */
	public static MasonGeometry findNearestGeometryWithin(MasonGeometry from, GeomVectorField field, double withinDistance) {
		// we assume the index of field is up-to-dated.

		// TODO: we need to optimize NNQ later
		// For now, we gradually increase a distance for the nearest neighbor query, and
		// then perform range query.
		double dist = withinDistance;
		Bag candidates = field.getObjectsWithinDistance(from, dist);
		// if size of data is small enough, we will not perform any range query any
		// more.
		if (candidates.size() > MAXIMUM_THRESHOLD) {
			// filter step
			while (true) {
				// simply increase or decrease distance to find an appropriate number of
				// candidates
				if (candidates.size() == 0)
					dist *= 10;
				else if (candidates.size() > MAXIMUM_THRESHOLD)
					dist *= 0.5;
				else
					break;
				candidates = field.getObjectsWithinDistance(from, dist);
			}
		}

		double minDist = Double.MAX_VALUE;
		MasonGeometry nearest = null;
		// refinement step: now it's time to find the nearest among candidates
		for (Object ele : candidates) {
			MasonGeometry geo = (MasonGeometry) ele;
			double tmp = geo.geometry.distance(from.geometry);
			if (minDist > tmp) {
				nearest = geo;
				minDist = tmp;
			}
		}

		return nearest;
	}
	
	public static BuildingUnit findNearestUnit(MasonGeometry from, List<BuildingUnit> candidates) {
		double minDist = Double.MAX_VALUE;
		BuildingUnit nearest = null;
		// refinement step: now it's time to find the nearest among candidates
		for (BuildingUnit ele : candidates) {
			MasonGeometry geo = ele.getLocation();
			double tmp = geo.geometry.distance(from.geometry);
			if (minDist > tmp) {
				nearest = ele;
				minDist = tmp;
			}
		}

		return nearest;
	}
}
