package com.lmm.sched.gui;

import java.awt.Color;
import java.util.Collections;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import com.lmm.gui.*;
import com.lmm.msg.ClientStateMsg;
import com.lmm.msg.MsgUtils;
import com.lmm.tools.FormatUtils;

public class ClientTableModel extends AbstractTableModel implements DisplayableModel {

	private static final long serialVersionUID = 1L;

	// (ClientStateMsg) instances
    private Vector<ClientStateMsg> clients = null;

    //The columns and their column index	
    public static final int STATE = 0;
    public static final int NAME = 1;
    public static final int TIME = 2;
    public static final int CURRENT_PLAY = 3;
    
    //The column names based on their column index
    public static final String[] COLUMN_NAMES =
        { "", "Name", "Time", "Playing (Order)" };

    //The color schemes
    public static final Color[] CELL_COLORS = {
        //Running
		Color.BLACK,
		//Idle
		Color.GRAY,
        //Non-updated
        Color.BLACK,

        //down
        Color.GRAY
	};

    /**
     * ClientTableModel constructor comment.
     */
    public ClientTableModel() {
        super();

		//set all our clients to an IDLE state since we are just starting up
		for( int i = 0; i < getRowCount(); i++ )
			getRowAt(i).setStatus( MsgUtils.Statuses.Idle );
    }

    public void updateClient(ClientStateMsg client) {    	

    	boolean isNameChange = false;
    	int pos = -1;
    	
    	//linear search for UUID, but this should not be a huge list. If it is, change
    	// this to a UUID to ClientMsg mapping
    	synchronized( getClients() ) {
			for( int i = 0; i < getClients().size(); i++ ) {
				if( ClientStateMsg.MsgUUIDComparator.compare(client, getClients().get(i)) == 0 ) {
					pos = i;
					isNameChange = !client.getName().equals(getClients().get(i).getName());
					break;
				}
			}
    	}

		if( pos >= 0 ) {
			getClients().set( pos, client );

			//re-sort our list if the name changed
			if( isNameChange )
				Collections.sort( getClients(), ClientStateMsg.MsgNameComparator );

			//by using fireTableRowsUpdated(int,int) we do not clear the table selection
			fireTableRowsUpdated( 0, getRowCount()-1 );
		}
		else {
			getClients().add( client );
			Collections.sort( getClients(), ClientStateMsg.MsgNameComparator );
			pos = Collections.binarySearch(
				getClients(), client, ClientStateMsg.MsgNameComparator );

			fireTableRowsInserted( pos, pos );
		}
		
    }


	public Color getSelectedFGColor(int row, int col) {
		return getCellFGColor(row, col).darker();
	}

	public Color getCellBGColor(int row, int col) {
		return Color.WHITE;
	}

    public Color getCellFGColor(int row, int col) {

        if( getClients() != null
            	&& row >= 0 && row < getRowCount()
            	&& col >= 0 && col <= getColumnCount()) {

            if ( getRowAt(row).getStatus() == MsgUtils.Statuses.Down) {
                return CELL_COLORS[3];
            }
            else if ( getRowAt(row).getStatus() == MsgUtils.Statuses.Questionable ) {
                return CELL_COLORS[2];
            }
            else if ( getRowAt(row).getStatus() == MsgUtils.Statuses.Idle ) {
                return CELL_COLORS[1];
            }
            else if ( getRowAt(row).getStatus() == MsgUtils.Statuses.Healthy ) {
                return CELL_COLORS[0];
            }
        }

        return Color.BLACK;
    }

    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    public String getColumnName(int index) {
        return COLUMN_NAMES[index];
    }
    /**
     * All clients
     */
    private Vector<ClientStateMsg> getClients() {
        if (clients == null)
            clients = new Vector<ClientStateMsg>(16);

        return clients;
    }

    /**
     * Returns the value of a row in the form of a ClientStateMsg object.
     */
    public synchronized ClientStateMsg getRowAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= getRowCount())
            return null;
        else
            return getClients().get( rowIndex );
    }

    /**
     * Returns the number of rows
     */
    public int getRowCount() {
        return getClients().size();
    }

    /**
     * getValueAt method comment.
     */
    public synchronized Object getValueAt(int row, int col) {
        if (row < getRowCount() && row >= 0) {
            ClientStateMsg client = getRowAt(row);
            switch (col) {
                case ClientTableModel.NAME :
                    {
                        return client.getName();
                    }

                case ClientTableModel.STATE :
                    {
						MsgUtils.updateState( client );
                        return client.getStatus();
                    }

                case ClientTableModel.CURRENT_PLAY :
                    {
                    	//show the current theme playing
                        if( client.getCurrentPlay() == null )
							return "";
                        else
                        	return client.getCurrentPlay() + " (" + client.getCurrentPlaySlot() + ")";
                    }

				case ClientTableModel.TIME :
					{
						//show the date we care about
						if( client.isIdle() )
							return "";
						else
							return FormatUtils.timeDate( client.getMsgDate() );
					}

                default :
                    return null;
            }
        }
        else
            return null;
    }
    
    public String getToolTip( int row, int col ){
		if (row < getRowCount() && row >= 0) {
			return getValueAt(row, col).toString();
			//return "Running for " + Time.getDescription( getRowAt(row).getUptime() );
		}
    	else
    		return "";
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void removeRow(int indx) {
        if (indx >= 0) {
        	getClients().remove( indx );
            fireTableRowsDeleted( indx, indx );
        }

    }

}
