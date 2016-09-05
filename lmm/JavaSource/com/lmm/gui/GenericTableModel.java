package com.lmm.gui;
import java.awt.Color;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;


public class GenericTableModel extends AbstractTableModel implements DisplayableModel {

	//a vector of vectors
    private Vector<Vector> rows = null;
    
    //contains Strings
	private String[] columns = null;
	private Class[] colClasses = null;

	//unique ID column number for each row is in this column
	public static final int COL_UNIQUE_ID = 1;
	public static final int COL_INVIS_DATA_ID = 4;
	
    /**
     * GenericTableModel constructor comment.
     */
    public GenericTableModel( String[] columns, Class[] colClasses ) {
        super();
		this.columns = columns;
		this.colClasses = colClasses;
    }

    public void updateRow( int rowNum, Vector row) {
		getRows().set( rowNum, row );
		fireTableRowsUpdated( 0, getRowCount()-1 );			
    }

	public Class getColumnClass(int aColumn) {
		return colClasses[aColumn];
	}

    /**
     * Removes all data from the table
     */
    public void clear() {
        getRows().clear();
        fireTableDataChanged();
    }

	public Color getSelectedFGColor(int row, int col) {
		return getCellFGColor(row, col).darker();
	}

	public Color getCellBGColor(int row, int col) {
		return Color.WHITE;
	}

    public Color getCellFGColor(int row, int col) {
        return Color.BLACK;
    }

    public int getColumnCount() {
        return columns.length;
    }

    public String getColumnName(int index) {
        return columns[index];
    }

    /**
     * All rows
     */
    private Vector<Vector> getRows() {
        return rows;
    }

	public void setRows( Vector<Vector> v ) {
		rows = v;
		fireTableDataChanged();
	}


    /**
     * Returns the value of a row in the form of a ClientStateMsg object.
     */
    public synchronized Vector getRowAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= getRowCount())
            return null;
        else
            return (Vector)getRows().get( rowIndex );
    }

    /**
     * Returns the number of rows
     */
    public int getRowCount() {
        return getRows().size();
    }

    /**
     * getValueAt method comment.
     */
    public synchronized Object getValueAt(int row, int col) {
        if (row < getRowCount() && row >= 0) {
            Vector rowVect = getRowAt(row);            
            return rowVect.get(col);
        }
        else
            return null;
    }
    
    public String getToolTip( int row, int col ){
		if (row < getRowCount() && row >= 0)    	
			return getValueAt(row, col).toString();
    	else
    		return "";
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void removeRow(int indx) {

        if (indx >= 0) {
            getRows().remove( indx );
            fireTableRowsDeleted( indx, indx );
        }

    }

}
