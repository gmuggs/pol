package edu.gmu.mason.vanilla.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import at.granul.mason.collector.DataCollector;
import sim.display.Controller;
import sim.display.GUIState;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * General description_________________________________________________________
 * Data collector chart class adapted from Mason-Tools (Roman Seidl, 2015)
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class CustomDataCollectorChart {

	JFrame collectorFrame = null;
	DataCollector dataCollector;
	GUIState state;
	JTable table = new JTable(new BarStatsTableModel());
	BarUpdateStep barUpdate;

	public CustomDataCollectorChart(DataCollector dataCollector, GUIState state) {
		this.dataCollector = dataCollector;
		this.state = state;
		createChartFrame(state.controller);
	}

	private void createChartFrame(Controller controller) {

		table.setDefaultRenderer(Color.class, new BarStatsTableColorRenderer(
				true));
		table.getColumnModel().getColumn(0).setPreferredWidth(40);
		table.getColumnModel().getColumn(4).setPreferredWidth(50);
		table.getColumnModel().getColumn(5).setPreferredWidth(50);
		table.getColumnModel().getColumn(6).setPreferredWidth(80);
		table.setFillsViewportHeight(true);

		JScrollPane tablePane = new JScrollPane(table);
		tablePane.setAutoscrolls(true);
		tablePane.setWheelScrollingEnabled(true);

		// Create a Tabbed Pane with a table
		JTabbedPane graphTabs = new JTabbedPane();
		graphTabs.addTab("Table", tablePane);

		collectorFrame = new JFrame("Place profile visualization");
		collectorFrame.add(graphTabs);
		collectorFrame.setVisible(true);
		collectorFrame.pack();

		controller.registerFrame(collectorFrame);
	}

	public void schedule() {
		barUpdate = new BarUpdateStep(dataCollector);

		state.scheduleRepeatingImmediatelyAfter(barUpdate);
	}

	public class BarUpdateStep implements Steppable {

		private static final long serialVersionUID = -450116884034925480L;

		DataCollector dataCollector;

		public BarUpdateStep(DataCollector dataCollector) {
			this.dataCollector = dataCollector;
		}

		@Override
		public void step(SimState simState) {
			final double x = state.state.schedule.getTime();
			// now add the data
			if (x >= Schedule.EPOCH && x < Schedule.AFTER_SIMULATION) {

				((BarStatsTableModel) table.getModel())
						.setAllValues((Object[][]) dataCollector.objectArrayData
								.get(0).provide());
				table.repaint();
			}
		}
	}

	public void dispose() {

		if (collectorFrame != null)
			collectorFrame.dispose();
		collectorFrame = null;
	}

	public void setSize(int w, int h) {
		collectorFrame.setSize(w, h);
	}

	public Dimension getSize() {
		return collectorFrame.getSize();
	}

	public void setLocation(int x, int y) {
		collectorFrame.setLocation(x, y);
	}

	public JFrame getFrame() {
		return collectorFrame;
	}

}
