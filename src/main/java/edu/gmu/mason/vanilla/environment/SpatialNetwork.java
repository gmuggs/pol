package edu.gmu.mason.vanilla.environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.planargraph.Node;

import edu.gmu.mason.vanilla.VisitReason;
import edu.gmu.mason.vanilla.WorldModel;
import edu.gmu.mason.vanilla.utils.AStar;
import edu.gmu.mason.vanilla.utils.GeoUtils;
import sim.field.geo.GeomVectorField;
import sim.io.geo.ShapeFileImporter;
import sim.util.geo.GeomPlanarGraph;
import sim.util.geo.GeomPlanarGraphDirectedEdge;
import sim.util.geo.MasonGeometry;

/**
 * General description_________________________________________________________
 * A class to handle spatial network operations including place finding and
 * trajectory calculations etc.
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu), Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SpatialNetwork implements java.io.Serializable {
	private static final long serialVersionUID = 3481862190046428709L;
	private static final double OFFSET_DIST = 60.0;
	private MultiKeyMap preComputedPaths;
	private GeomPlanarGraph walkwayNetwork;
	private GeomVectorField walkwayLayer;
	private GeomVectorField buildingLayer;
	private GeomVectorField buildingUnitLayer;
	private GeomVectorField bombLayer;
	private Map<Integer, List<MasonGeometry>> buildingUnitTable;
	private Map<MultiKey, MasonGeometry> nearestJunctionTable;

	public SpatialNetwork(int width, int height) {
		preComputedPaths = new MultiKeyMap();
		walkwayLayer = new GeomVectorField(width, height);
		buildingLayer = new GeomVectorField(width, height);
		buildingUnitLayer = new GeomVectorField(width, height);
		buildingUnitTable = new HashMap<Integer, List<MasonGeometry>>();
		nearestJunctionTable = new HashMap<MultiKey, MasonGeometry>();
		walkwayNetwork = new GeomPlanarGraph();
	}

	/*
	 * If running from jar extract the resources from JAR
	 */
	private void extractResourcesFromJar(URL codeBase, String path, String maps)
			throws Exception {
		createDirectories(path, maps);
		java.util.jar.JarInputStream jin = new java.util.jar.JarInputStream(
				codeBase.openStream());
		ZipEntry entry;
		while ((entry = jin.getNextEntry()) != null) {
			if ((entry.getName().startsWith(maps) || entry.getName()
					.startsWith("stylesheet")) && !entry.isDirectory()) {
				System.out.println("Entry: " + entry.getName());
				ExportResource(path, "/" + entry.getName());
			}
		}
	}

	/*
	 * Create resource directories if doesn't exists
	 */
	private void createDirectories(String path, String maps) {
		String[] folders = { maps, "stylesheet", "target" };
		for (int i = 0; i < folders.length; i++) {
			File file = new File(path + "/" + folders[i]);
			if (!file.exists()) {
				if (file.mkdir()) {
					System.out.println(folders[i] + " directory is created!");
				} else {
					System.out.println(folders[i] + " already exists!");
				}
			}
		}
	}

	/*
	 * copy the resource from JAR to specified folder
	 */
	private void ExportResource(String path, String resourceName)
			throws Exception {
		System.out.println("Copying Resource Name..." + resourceName);
		InputStream stream = null;
		OutputStream resStreamOut = null;
		try {
			stream = WorldModel.class.getResourceAsStream(resourceName);
			if (stream == null) {
				throw new Exception("Cannot get resource \"" + resourceName
						+ "\" from Jar file.");
			}
			int readBytes;
			byte[] buffer = new byte[4096];
			resStreamOut = new FileOutputStream(path + resourceName);
			while ((readBytes = stream.read(buffer)) > 0) {
				resStreamOut.write(buffer, 0, readBytes);
			}
			resStreamOut.close();
			stream.close();
		} catch (Exception ex) {
			throw ex;
		}
	}

	// Movement/Geography-related methods
	public void loadMapLayers(String directory, String walkwayShapefile,
			String buildingShapefile, String buildingUnitShapefile)
					throws IOException, Exception {
		URL codeBase = WorldModel.class.getProtectionDomain().getCodeSource()
				.getLocation();
		// Must be a jar.
		if (codeBase.getPath().endsWith(".jar")) {

			Path currentRelativePath = Paths.get("");
			String base = currentRelativePath.toAbsolutePath().toString();

			extractResourcesFromJar(codeBase, base, directory);
			// walkway map
			String walkwayPath = base + "/" + directory + "/" + walkwayShapefile;
			URL geometry = Paths.get(walkwayPath).toUri().toURL();
			System.out.println(geometry);
			ShapeFileImporter.read(geometry, walkwayLayer);

			String buildingPath = base + "/" + directory + "/" + buildingShapefile;
			geometry = Paths.get(buildingPath).toUri().toURL();
			System.out.println(geometry);
			ShapeFileImporter.read(geometry, buildingLayer);

			String buildingUnitPath = base + "/" + directory + "/" + buildingUnitShapefile;
			geometry = Paths.get(buildingUnitPath).toUri().toURL();
			System.out.println(geometry);
			ShapeFileImporter.read(geometry, buildingUnitLayer);

		} else {
			// walkway map
			URL geometry = WorldModel.class.getResource("/" + directory + "/" + walkwayShapefile);
			ShapeFileImporter.read(geometry, walkwayLayer);

			geometry = WorldModel.class.getResource("/" + directory + "/" + buildingShapefile);
			ShapeFileImporter.read(geometry, buildingLayer);

			geometry = WorldModel.class.getResource("/" + directory + "/" + buildingUnitShapefile);
			ShapeFileImporter.read(geometry, buildingUnitLayer);

		}

		createBuildingUnitTable();

		walkwayNetwork.createFromGeomField(walkwayLayer);
	}

	private void createBuildingUnitTable() {
		for (Object obj : buildingUnitLayer.getGeometries()) {
			MasonGeometry geo = (MasonGeometry) obj;
			int id = geo.getIntegerAttribute("building");
			if (!buildingUnitTable.containsKey(id)) {
				buildingUnitTable.put(id, new ArrayList<MasonGeometry>());
			}
			List<MasonGeometry> units = buildingUnitTable.get(id);
			units.add(geo);
		}
	}

	/**
	 * Returns the path of travel from cache or new
	 * 
	 * @param travel
	 * @return
	 */
	public PrecomputedPath getPath(Travel travel, boolean savePath) {
		PrecomputedPath path = new PrecomputedPath();
		MultiKey origin = getNearestKey(travel.getOrigin());
		MultiKey destination = getNearestKey(travel.getDestination());

		MultiKey fromTo = getKey(origin, destination);
		MultiKey toFrom = getKey(destination, origin);

		if (preComputedPaths.containsKey(fromTo) == true) { // if this route has
															// already been
															// generated
			path = (PrecomputedPath) preComputedPaths.get(fromTo);
			path.setForward(true);
		} else if (preComputedPaths.containsKey(toFrom) == true) { // if this
																	// route has
																	// already
																	// been
																	// generated
																	// but in
																	// the
																	// reverse
																	// order
			path = (PrecomputedPath) preComputedPaths.get(toFrom);
			path.setForward(false);
		} else { // this origin-destination pair was not seen before. let's
					// generate the shortest path and store it.

			// System.out.println("Path to be generated.");
			// double t = System.currentTimeMillis();

			MasonGeometry startPoint = nearestJunctionTable.get(origin);
			// For simplicity, we assume that the geometry returned by
			// findNearestGeometry
			// method is a line string
			// Also, we assume that the first coordinate will be one of node in
			// the spatial
			// network.
			Node startNode = walkwayNetwork.findNode(startPoint.getGeometry()
					.getCoordinates()[0]);
			MasonGeometry endPoint = nearestJunctionTable.get(destination);
			Node endNode = walkwayNetwork.findNode(endPoint.getGeometry()
					.getCoordinates()[0]);
			// System.out.print("Nearest time: " + (System.currentTimeMillis() -
			// t));

			AStar algorithm = new AStar();
			// find a path
			// NOTE: if the network is not a connected graph, the path can be
			// empty.
			ArrayList<GeomPlanarGraphDirectedEdge> pathToReturn = algorithm
					.astarPath(startNode, endNode);
			// System.out.println(", AStar computing time: " +
			// (System.currentTimeMillis() - t));

			double lengthOfRoad = 0.0;

			// calculate path length
			for (int i = 0; i < pathToReturn.size(); i++) {
				// We assume this edge will be GeomPlanarGraphEdge
				GeomPlanarGraphDirectedEdge dE = pathToReturn.get(i);
				// GeomPlanarGraphEdge edge = (GeomPlanarGraphEdge)
				// dE.getEdge();
				lengthOfRoad += AStar.weight(dE);
			}

			// System.out.println("Path to be calculated. "+agent.getAgentId());
			path.setLength(lengthOfRoad);
			path.setPath(pathToReturn);
			path.setForward(true);
			
			if (savePath == true) {
				// store the path
				this.preComputedPaths.put(fromTo, path);
			}
		}

		if (path.getPath() == null) {
			System.out.println("Path is null");
		}

		return path;
	}

	private MultiKey getNearestKey(MasonGeometry geo) {
		MultiKey key = getKey(geo);
		MasonGeometry nearest = null;

		if (nearestJunctionTable.containsKey(key)) {
			nearest = nearestJunctionTable.get(key);
			return getKey(nearest);
		}
		// To optimize NN, we use initial distance
		nearest = GeoUtils.findNearestGeometry(geo, walkwayLayer, OFFSET_DIST);
		key = getKey(nearest);

		nearestJunctionTable.put(key, nearest);
		return key;
	}

	public  MultiKey getKey(MasonGeometry geo) {
		Coordinate coord = geo.getGeometry().getCoordinate();
		return new MultiKey(coord.x, coord.y);
	}

	public MultiKey getKey(MultiKey from, MultiKey to) {
		return new MultiKey(from.getKey(0), from.getKey(1), to.getKey(0),
				to.getKey(1));
	}
	
	public double getDistance(MasonGeometry origin, MasonGeometry destination, boolean savePath) {
		Travel travel = new Travel(origin, destination, 0, VisitReason.None);
		return getPath(travel, savePath).getLength();
	}

	public double getDistance(MasonGeometry origin, MasonGeometry destination) {
		return getDistance(origin, destination, false);
	}

	/**
	 * 
	 * @param origin
	 * @param destination
	 * @param minutePerStep
	 * @param speed
	 *            unit is meter/second
	 * @return
	 */
	public int getDistanceAsTicks(MasonGeometry origin,
			MasonGeometry destination, int minutePerStep, double speed) {
		double distance = getDistance(origin, destination);
		double distanceToBeTakenPerTick = minutePerStep * 60.0 * speed;
		int distanceAsTicks = Math.max(1,
				(int) Math.ceil(distance / distanceToBeTakenPerTick)); // there
																		// will
																		// be at
																		// least
																		// 1
																		// tick.

		return distanceAsTicks;
	}

	public int getDistanceAsMinute(MasonGeometry origin,
			MasonGeometry destination, int minutePerStep, double speed) {
		int distanceAsTicks = getDistanceAsTicks(origin, destination,
				minutePerStep, speed);

		return minutePerStep * distanceAsTicks;
	}

	/**
	 * Estimate travel time from origin to destination with given speed.
	 * 
	 * @param origin
	 * @param destination
	 * @param speed
	 *            unit is meter/second
	 * @return
	 */
	public double getEstimatedTravelTime(MasonGeometry origin,
			MasonGeometry destination, double speed) {
		double distance = getDistance(origin, destination);
		double time = distance / speed;
		return time;
	}

	public List<GeomVectorField> getAllLayers() {
		List<GeomVectorField> layers = new ArrayList<GeomVectorField>();

		layers.add(walkwayLayer);
		// layers.add(agentLayer);
		layers.add(buildingLayer);

		return layers;
	}
	
	/**
	 * Clears all the pre-computed paths
	 */
	public void clearPrecomputedPaths() {
		MapIterator iter =  preComputedPaths.mapIterator();
		
		while(iter.hasNext()) {
			iter.next();
			iter.remove();
		}
		/*
		preComputedPaths.clear();
		preComputedPaths = null;*/
		preComputedPaths = new MultiKeyMap();
	}

	/**
	 * Remove all paths containing a given edge.
	 * 
	 * @param edge
	 */
	public Set update(GeomPlanarGraphDirectedEdge edge) {
		Set removeList = new HashSet();
		for (Object obj : preComputedPaths.keySet()) {
			PrecomputedPath path = (PrecomputedPath) preComputedPaths.get(obj);
			if (path.contains(edge))
				removeList.add(obj);
		}
		removeList.forEach(o -> preComputedPaths.remove(o));
		return removeList;
	}

	public GeomPlanarGraph getWalkwayNetwork() {
		return walkwayNetwork;
	}

	public GeomVectorField getWalkwayLayer() {
		return walkwayLayer;
	}

	public GeomVectorField getBuildingLayer() {
		return buildingLayer;
	}

	public GeomVectorField getBuildingUnitLayer() {
		return buildingUnitLayer;
	}

	public Map<Integer, List<MasonGeometry>> getBuildingUnitTable() {
		return buildingUnitTable;
	}

	public MultiKeyMap getPreComputedPaths() {
		return preComputedPaths;
	}

	public void setNearestJunctionTable(
			Map<MultiKey, MasonGeometry> nearestJunctionTable) {
		this.nearestJunctionTable = nearestJunctionTable;
	}

	public GeomVectorField getBombTrajectoryLayer(String shapeFile) {
		try {
			URL geometry = WorldModel.class.getResource(shapeFile);
			if (bombLayer == null)
				bombLayer = new GeomVectorField(walkwayLayer.fieldWidth, walkwayLayer.fieldHeight);
			ShapeFileImporter.read(geometry, bombLayer);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bombLayer;
	}
}
