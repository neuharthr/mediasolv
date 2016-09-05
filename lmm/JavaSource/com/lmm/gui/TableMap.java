package com.lmm.gui;

/** 
 * In a chain of data manipulators some behaviour is common. TableMap
 * provides most of this behavour and can be subclassed by filters
 * that only need to override a handful of specific methods. TableMap 
 * implements TableModel by routing all requests to its model, and
 * TableModelListener by routing all events to its listeners. Inserting 
 * a TableMap which has not been subclassed into a chain of table filters 
 * should have no effect.
 *
 * @version 1.6 08/26/98
 * @author Philip Milne */

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class TableMap extends AbstractTableModel implements TableModelListener {
    protected TableModel model;

    public Class getColumnClass(int aColumn) {
        try {
            Class clas = model.getColumnClass(aColumn);
            return clas;
        }
        catch (NullPointerException e) {
            return String.class;
        }

    }
    public int getColumnCount() {
        return (model == null) ? 0 : model.getColumnCount();
    }
    public String getColumnName(int aColumn) {
        return model.getColumnName(aColumn);
    }
    public TableModel getModel() {
        return model;
    }
    public int getRowCount() {
        return (model == null) ? 0 : model.getRowCount();
    }
    // By default, Implement TableModel by forwarding all messages 
    // to the model. 

    public Object getValueAt(int aRow, int aColumn) {
        return model.getValueAt(aRow, aColumn);
    }
    public boolean isCellEditable(int row, int column) {
        return model.isCellEditable(row, column);
    }
    public void setModel(TableModel model) {
        this.model = model;
        model.addTableModelListener(this);
    }
    public void setValueAt(Object aValue, int aRow, int aColumn) {
        model.setValueAt(aValue, aRow, aColumn);
    }
    //
    //	   Implementation of the TableModelListener interface, 
    //

    // By default forward all events to all the listeners. 
    public void tableChanged(TableModelEvent e) {
        fireTableChanged(e);
    }

}