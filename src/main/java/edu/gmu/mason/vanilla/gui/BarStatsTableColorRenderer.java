package edu.gmu.mason.vanilla.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 * General description_________________________________________________________
 * A class to color bar profile table.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class BarStatsTableColorRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 3193812570039232457L;
	Border unselectedBorder = null;
	Border selectedBorder = null;
	boolean isBordered = true;

	public BarStatsTableColorRenderer(boolean isBordered) {
		this.isBordered = isBordered;
		setOpaque(true); // MUST do this for background to show up.
	}

	
	public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if(color instanceof Color) {
			Color newColor = (Color) color;
			setBackground(newColor);
			setBorder(BorderFactory.createLineBorder(newColor));
		}
	
		return this;
	}
}