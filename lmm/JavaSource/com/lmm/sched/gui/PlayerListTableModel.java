package com.lmm.sched.gui;
import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import com.lmm.gui.DisplayableModel;
import com.lmm.sched.data.LMMEntry;
import com.lmm.sched.data.VideoEntry;
import com.lmm.sched.proc.LMMUtils;

public class PlayerListTableModel extends AbstractTableModel implements DisplayableModel {

    //(VideoEntry)entry value
    private Vector<VideoEntry> playList = null;

    //The columns and their column index	
    public static final int NAME = 0;
    public static final int PLAY_ORDER = 1;
    public static final int PLAY_DURATION = 2;
	public static final int DAY_WINDOW = 3;
	public static final int TIME_WINDOW = 4;
    public static final int WEB_URL = 5;
    public static final int CHANNEL = 6;
    
    //The column names based on their column index
    public static final String[] COLUMN_NAMES ={ 
    	"Name", "Order", "Duration", "Day Window",
    	"Time Window", "Web", "Chann."
    };

    //The color schemes
    public static final Color[] CELL_COLORS = {
        //Video entry
		Color.BLACK,
		//Web entry
		Color.GRAY
	};

//	xml.2 = lmm_logo.xml
//	play_order.2 = 4
//	play_duration.2 = 10
//	web_url.2 = -1
//	time_window.2 = -1
//	day_window.2 = -1
//
//	xml.3 = lmm_web_traffic.xml
//	play_order.3 = 3
//	play_duration.3 = 10
//	web_url.3 = http://www.dot.state.mn.us/tmc/trafficinfo/map/refreshmap.html
//	time_window.3 = 00:00-00:17
//	day_window.3 = -1

    /**
     * PlayerListTableModel constructor comment.
     */
    public PlayerListTableModel() {
        super();
    }

    /**
     * Removes all data from the table
     */
    public void clear() {
        getPlayList().clear();
        fireTableDataChanged();
    }

	public Color getSelectedFGColor(int row, int col) {
		return getCellFGColor(row, col).darker();
	}

	public Color getCellBGColor(int row, int col) {
		return Color.WHITE;
	}

    public Color getCellFGColor(int row, int col) {
        return CELL_COLORS[0];
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
    private Vector<VideoEntry> getPlayList() {
        if (playList == null)
			playList = new Vector<VideoEntry>(16);

        return playList;
    }

    /**
     * Returns the value of a row in the form of a VideoEntry object.
     */
    public synchronized VideoEntry getRowAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= getRowCount())
            return null;
        else
            return (VideoEntry)getPlayList().get( rowIndex );
    }

    /**
     * Returns the number of rows
     */
    public int getRowCount() {
        return getPlayList().size();
    }

    /**
     * getValueAt method comment.
     */
    public synchronized Object getValueAt(int row, int col) {
        if (row < getRowCount() && row >= 0) {
            VideoEntry entry = getRowAt(row);
            switch (col) {
                case PlayerListTableModel.NAME :
                    {
                        return entry.getFileNameNoExt();
                    }

                case PlayerListTableModel.PLAY_ORDER :
                    {
						return LMMUtils.arrayAsString( entry.getPlayOrder(), "," );
                    }

                case PlayerListTableModel.PLAY_DURATION :
                    {
                       	return entry.getPlayDuration().intValue() / 1000 + "";
                    }

				case PlayerListTableModel.WEB_URL :
					{
						if( entry.getUrl() == null || entry.getUrl().length() <= 0
							|| LMMUtils.INVALID_STRING_VALUE.equals(entry.getUrl()) )
							return "";
						else
							return "X";
					}
				case PlayerListTableModel.DAY_WINDOW :
					{
						return entry.getDayWindow();
					}
				case PlayerListTableModel.TIME_WINDOW :
					{
						return entry.getTimeWindow();
					}
				case PlayerListTableModel.CHANNEL :
					{
						return entry.getChannel().toString();
					}

                default :
                    return "";
            }
        }
        else
            return null;
    }
    
    public Set<Integer> getPlayOrders( int excludeRowNum ) {
    	HashSet<Integer> allOrders = new HashSet<Integer>(16);

    	for( int i = 0; i < getPlayList().size(); i++ ){
    		if( i == excludeRowNum )
    			continue;
    		
    		allOrders.addAll( Arrays.asList(getPlayList().get(i).getPlayOrder()) );
    	}
    	
    	return allOrders;
    }

    //ignore this rule for now else where
    public int getMaxPlayOrder() {
//    	int maxValue = 0;
//    	
//    	for( int i = 0; i < getPlayList().size(); i++ ){    		
//    		List<Integer> all =  Arrays.asList(getPlayList().get(i).getPlayOrder());
//    		
//    		for( Integer val : all ) {
//    			if( val > maxValue )
//    				maxValue = val;
//    		}
//    			
//    	}    	
//   		return maxValue + 1;
    	return Integer.MAX_VALUE;
    }
    
    public String getToolTip( int row, int col ){
		if (row < getRowCount() && row >= 0)    	
			return (String)getValueAt(row, col);
    	else
    		return "";
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

	public void addRow( VideoEntry entry ) {
		getPlayList().add( entry );		
		Collections.sort( getPlayList(), LMMEntry.OrderComparator );

		fireTableDataChanged();
	}

	public void updateRow( int row, VideoEntry entry ) {
		getPlayList().set( row, entry );
		Collections.sort( getPlayList(), LMMEntry.OrderComparator );
		
		fireTableDataChanged();
	}

    public void removeRow(int indx) {

        if (indx >= 0) {
            getPlayList().remove( indx );
            fireTableRowsDeleted( indx, indx );
        }

    }

}
