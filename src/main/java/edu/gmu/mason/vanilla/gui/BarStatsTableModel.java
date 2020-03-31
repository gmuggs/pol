package edu.gmu.mason.vanilla.gui;

import java.awt.Color;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;


/**
 * General description_________________________________________________________
 * Table model class for the bar profile visualization.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class BarStatsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 8388950018802673842L;


	private String[] columnNames = {"Bar#",
             "1st interest",
             "2nd interest",
             "3rd interest",
             "Age",
             "Income",
             "# of visitors",
             "open"};

	
	/*
    private Object[][] data = {
    	    {1, Color.BLACK,  Color.YELLOW, Color.PINK, 36.6, 18.9, 10123},
    	    {2, Color.BLUE,  Color.BLACK, Color.YELLOW, 31.2, 18.2, 546754},
    	    {3, Color.CYAN,  Color.BLUE, Color.BLACK, 39.7, 19.9, 3345},
    	    {4, Color.ORANGE,  Color.CYAN, Color.BLUE, 33.8, 22.9, 11},
    	    {5, Color.DARK_GRAY,  Color.ORANGE, Color.CYAN, 34.6, 11.9, 67896789},
    	    {6, Color.MAGENTA,  Color.DARK_GRAY, Color.ORANGE, 35.7, 16.9, 32623},
    	    {7, Color.GREEN,  Color.MAGENTA, Color.DARK_GRAY, 38.6, 12.9, 11234}
    	   };*/
	
	 private Object[][] data = {
	    	    {1, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {2, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {3, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {4, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {5, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {6, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {7, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {8, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {9, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {10, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {11, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {12, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {13, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {14, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {15, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {16, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {17, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {18, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {19, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {20, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {21, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {22, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {23, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {24, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true},
	    	    {25, Color.BLACK,  Color.BLACK, Color.BLACK, 0.00, 0.0, 0, true}
	    	   };
	 
	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		
	}
	
	public void setAllValues(Object[][] data) {
		boolean same = this.data.equals(data);
		if(same != true) {
			this.data = data;
			fireTableDataChanged();
		}
	}
 
}
