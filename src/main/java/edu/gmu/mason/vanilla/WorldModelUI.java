package edu.gmu.mason.vanilla;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.Viewer.CloseFramePolicy;
import org.joda.time.DateTime;

import at.granul.mason.ChartedGUIState;
import at.granul.mason.collector.DataCollector;
import at.granul.mason.inspector.TitledSimpleInspector;
import edu.gmu.mason.vanilla.gui.CustomDataCollectorChart;
import edu.gmu.mason.vanilla.utils.ColorUtils;
import edu.gmu.mason.vanilla.utils.SimulationEvent;
import edu.gmu.mason.vanilla.utils.SupplierHUD;
import edu.gmu.mason.vanilla.utils.DateTimeUtil;
import sim.display.Controller;
import sim.display.Display2D;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.Inspector;
import sim.portrayal.geo.GeomPortrayal;
import sim.portrayal.geo.GeomVectorFieldPortrayal;
import sim.portrayal.inspector.TabbedInspector;
import sim.util.geo.MasonGeometry;
import sim.util.media.chart.TimeSeriesChartGenerator;

/**
 * General description_________________________________________________________
 * This is the class to create graphical user interface and trace the model
 * execution via provided panels.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu), Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
@SuppressWarnings("serial")
public class WorldModelUI extends ChartedGUIState {

	// Assume that 1 unit in coordinates is 1 meter.
	public final static double RADIUS = 30; // the radius of ellipsoid displayed
											// on screen
	public final static double RADIUS_RATIO = 3.0 / 2;
	public final static double DEFAULT_SCALE = RADIUS / RADIUS_RATIO;

	// display elements and settings
	Display2D display;
	JFrame displayFrame;
	TimeSeriesChartGenerator tsc = new sim.util.media.chart.TimeSeriesChartGenerator();
	CustomDataCollectorChart customChart = null;
	JFrame socialNetworkFrame;

	// geography components
	GeomVectorFieldPortrayal walkwayPortrayal = new GeomVectorFieldPortrayal();
	GeomVectorFieldPortrayal buildingPortrayal = new GeomVectorFieldPortrayal();
	GeomVectorFieldPortrayal agentPortrayal = new GeomVectorFieldPortrayal();
	SupplierHUD textPortrayal = new SupplierHUD();

	private DateTimeUtil timeUtil = new DateTimeUtil();

	public WorldModelUI(SimState state) {
		super(state);
	}

	public WorldModelUI(WorldParameters params) throws IOException, Exception {
		super(new WorldModel(params.seed, params));
	}

	public static String getName() {
		return "Group World";
	}

	public void load(SimState state) {
		super.load(state);
		setupPortrayals();

		controller.unregisterFrame(customChart.getFrame());
		controller.unregisterFrame(socialNetworkFrame);
		customChart.dispose();
		if (socialNetworkFrame != null)
			socialNetworkFrame.dispose();

		int snVisWidth = (int) layout.getChartSize().getWidth();
		int snVisHeigth = (int) GraphicsEnvironment
				.getLocalGraphicsEnvironment().getMaximumWindowBounds()
				.getHeight()
				- (int) layout.getChartSize().getHeight();
		Point socialNetworkVisGraphLocation = new Point((int) layout
				.getChartLocation().getX(), (int) layout.getChartLocation()
				.getY() + (int) layout.getChartSize().getHeight());

		Viewer viewer = ((WorldModel) state).networkViewer();
		if (viewer != null) {
			socialNetworkFrame = (JFrame) viewer.getDefaultView().getParent()
					.getParent().getParent().getParent();
			socialNetworkFrame.setTitle("Social network visualization");
			viewer.setCloseFramePolicy(CloseFramePolicy.HIDE_ONLY);
			socialNetworkFrame.setBounds(socialNetworkVisGraphLocation.x,
					socialNetworkVisGraphLocation.y, snVisWidth, snVisHeigth);
			controller.registerFrame(socialNetworkFrame);
		}

		customChart = new CustomDataCollectorChart(getDataCollector(), this);
		customChart.schedule();
		customChart
				.setLocation(
						(int) (socialNetworkVisGraphLocation.x + snVisWidth - customChart
								.getSize().getWidth()),
						(int) (socialNetworkVisGraphLocation.y + snVisHeigth - customChart
								.getSize().getHeight()));

	}

	@Override
	public Inspector getInspector() {
		final TabbedInspector insp = new TabbedInspector();

		insp.addInspector(new TitledSimpleInspector(
				((WorldModel) state).params, this, null), "Parameters");
		insp.addInspector(new TitledSimpleInspector(
				((WorldModel) state).getQuantitiesOfInterest(), this, null), "QOI");
		return insp;
	}

	@Override
	public DataCollector getDataCollector() {
		return ((WorldModel) state).dataCollector;
	}

	@Override
	public Object getSimulationInspectedObject() {
		return state;
	}

	@Override
	public void init(Controller controller) {
		timeUtil.addEventTime(SimulationEvent.EnvironmentInitStart,
				new DateTime());
		super.init(controller);

		display = new Display2D(WorldModel.WIDTH, WorldModel.HEIGHT, this);
		display.setBackdrop(Color.LIGHT_GRAY);

		display.attach(walkwayPortrayal, "walkway", true);
		display.attach(buildingPortrayal, "buildings", true);
		display.attach(agentPortrayal, "agents", true);
		display.attach(textPortrayal, "label");

		displayFrame = display.createFrame();
		controller.registerFrame(displayFrame);
		displayFrame.setVisible(true);

		layout(displayFrame);

		customChart = new CustomDataCollectorChart(getDataCollector(), this);

		int snVisWidth = (int) layout.getChartSize().getWidth();
		int snVisHeigth = (int) GraphicsEnvironment
				.getLocalGraphicsEnvironment().getMaximumWindowBounds()
				.getHeight()
				- (int) layout.getChartSize().getHeight();
		Point socialNetworkVisGraphLocation = new Point((int) layout
				.getChartLocation().getX(), (int) layout.getChartLocation()
				.getY() + (int) layout.getChartSize().getHeight());

		Viewer viewer = ((WorldModel) state).networkViewer();
		if (viewer != null) {

			socialNetworkFrame = (JFrame) viewer.getDefaultView().getParent()
					.getParent().getParent().getParent();
			socialNetworkFrame.setTitle("Social network visualization");
			viewer.setCloseFramePolicy(CloseFramePolicy.HIDE_ONLY);
			socialNetworkFrame.setBounds(socialNetworkVisGraphLocation.x,
					socialNetworkVisGraphLocation.y, snVisWidth, snVisHeigth);
			controller.registerFrame(socialNetworkFrame);
		}

		customChart
				.setLocation(
						(int) (socialNetworkVisGraphLocation.x + snVisWidth - customChart
								.getSize().getWidth()),
						(int) (socialNetworkVisGraphLocation.y + snVisHeigth - customChart
								.getSize().getHeight()));

		timeUtil.addEventTime(SimulationEvent.EnvironmentInitEnd,
				new DateTime());
		timeUtil.logTimeSpent(SimulationEvent.EnvironmentInitStart,
				SimulationEvent.EnvironmentInitEnd, "GUI initialization time");
	}

	@Override
	public void start() {
		timeUtil.addEventTime(SimulationEvent.CustomEvent1Start, new DateTime());

		super.start();
		setupPortrayals();
		customChart.schedule();
		timeUtil.addEventTime(SimulationEvent.CustomEvent1End, new DateTime());
		timeUtil.logTimeSpent(SimulationEvent.CustomEvent1Start,
				SimulationEvent.CustomEvent1End, "Portrayal setup time");
	}

	private void setupPortrayals() {
		WorldModel model = (WorldModel) state;

		walkwayPortrayal.setField(model.getSpatialNetwork().getWalkwayLayer());
		walkwayPortrayal.setPortrayalForAll(new GeomPortrayal(Color.BLACK,
				DEFAULT_SCALE, true));

		buildingPortrayal
				.setField(model.getSpatialNetwork().getBuildingLayer());
		GeomPortrayal buildingGP = getPolygonalBuildingPortrayal();
		buildingPortrayal.setPortrayalForAll(buildingGP);

		agentPortrayal.setField(model.getAgentLayer());
		GeomPortrayal agentGP = getAgentPortrayal();
		agentPortrayal.setPortrayalForAll(agentGP);

		textPortrayal.addProperty("Day", () -> model.getDay());
		textPortrayal.addProperty("Date", () -> model.getSimulationTime()
				.dayOfWeek().getAsShortText());
		textPortrayal.addProperty("Time", () -> model.getSimulationTime()
				.toString("HH:mm"));

		// update the size of the display appropriately.
		double w = WorldModel.WIDTH;
		double h = WorldModel.HEIGHT;
		if (w == h) {
			display.insideDisplay.width = display.insideDisplay.height = layout
					.getDisplayWidth();
		} else if (w > h) {
			display.insideDisplay.width = layout.getDisplayWidth();
			display.insideDisplay.height = layout.getDisplayWidth() * (h / w);
		} else if (w < h) {
			display.insideDisplay.height = layout.getDisplayWidth();
			display.insideDisplay.width = layout.getDisplayWidth() * (w / h);
		}

		display.reset();
		display.repaint();
	}

	private GeomPortrayal getPolygonalBuildingPortrayal() {
		return new GeomPortrayal() {
			public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
				MasonGeometry geom = (MasonGeometry) object;
				int function = geom.getIntegerAttribute("function");
				
				if (function == 0) {
					paint = new Color(43, 233, 59); // bright green
				} else if (function == 1) {
					paint = new Color(188, 217, 92); // green
				} else if (function == 2) {
					paint = new Color(216, 139, 254); // purple
				} else if (function == 3) {
					paint = new Color(216, 153, 114); // dark orange
				} else if (function == 4) {
					paint = new Color(211, 109, 157); // pink
				} else if (function == 5) {
					paint = new Color(110, 130, 218); // blue
				} else if (function == 5) {
					paint = new Color(76, 229, 224); // cyan
				}
				
				super.draw(object, graphics, info);
			}
		};
	}

	private GeomPortrayal getAgentPortrayal() {
		return new GeomPortrayal() {
			public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
				AgentGeometry geom = (AgentGeometry) object;
				Person person = (Person) geom.getAgent();

				if (person.getLifeStatus() != LifeStatus.Alive) {
					return;
				}

				if (person.getModel().params.showAgentInterestColor == true) {
					paint = ColorUtils.getInterestColorMap().get(
							person.getInterest());
				} else {
					int mapid = (int) Math.floor(person.getFoodNeed()
							.getFullness() / 10.0);

					mapid = mapid == 10 ? 9 : mapid;
					paint = ColorUtils.getAgentHungerColorMap().get(mapid);
				}

				this.scale = DEFAULT_SCALE;

				super.draw(object, graphics, info);
			}
		};
	}
	
	public void quit() {
		super.quit();
		if (displayFrame != null) {
			displayFrame.dispose();
		}
		displayFrame = null;
		display = null;
		customChart.dispose();
	}

	public static void main(String[] args) {
		setNativeLookAndFeel();

		try {
			WorldParameters params = null;
			try {
				String configurationPath = WorldModel.argumentForKey("-configuration", args);
				if(configurationPath==null) {
					configurationPath = WorldParameters.DEFAULT_PROPERTY_FILE_NAME;
				}
				params = new WorldParameters(configurationPath);
			} catch (ConfigurationException e) {
				params = new WorldParameters();
				params.store(WorldParameters.DEFAULT_PROPERTY_FILE_NAME);
				System.err.println("WARNING: Counld not find a configuration file:"
						+ WorldParameters.DEFAULT_PROPERTY_FILE_NAME + ". A new configuration file is generated.");
			}
			new WorldModelUI(params).createController();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
