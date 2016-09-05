/**
 * ===========================================
 * JFreeReport : a free Java reporting library
 * ===========================================
 *
 * Project Info:  http://reporting.pentaho.org/
 *
 * (C) Copyright 2006-2007, by Pentaho Corporation and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ------------
 * CountryDataTableModel.java
 * ------------
 * (C) Copyright 2006-2007, by Pentaho Corporation.
 */

package com.lmm.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.lmm.db.DBCommon;
import com.lmm.pop.POPDailyMetric;
import com.lmm.tools.FormatUtils;
import com.lmm.tools.Time;

/**
 * A sample data source for the JFreeReport Demo Application.
 * 
 * @author David Gilbert
 */
public class POPDailyMetricTableModel extends AbstractTableModel {

	public static final String[] COL_NAMES = {
		"Date",
		"Theme",
		"Total Plays",
		"Avg. Play",
		"Play Duration"
	};
	
	public static final Class[] COL_CLASS = {
		String.class,
		String.class,
		Integer.class,
		String.class,
		String.class
	};
	
	/**
	 * Storage for the data.
	 */
	private List<POPDailyMetric> data;
	private String playerName;
	private Date startDate;
	private Date endDate;

	/**
	 * Default constructor - builds the sample data source using incomplete (and
	 * possibly inaccurate) data for countries of the world.
	 */
	public POPDailyMetricTableModel( final Date start, final Date end) {
		
		startDate = start;
		endDate = end;
		
		ArrayList<POPDailyMetric> tempData =
			new ArrayList<POPDailyMetric>(DBCommon.getDB().popDailyMetric_Retrieve( start, end, null ) );

		Collections.sort( tempData );
		data = tempData;
	}

	/**
	 * Returns the number of rows in the table model.
	 * 
	 * @return the row count.
	 */
	public int getRowCount() {
		return data.size();
	}

	/**
	 * Returns the number of columns in the table model.
	 * 
	 * @return the column count.
	 */
	public int getColumnCount() {
		return COL_NAMES.length;
	}

	/**
	 * Returns the class of the data in the specified column.
	 * 
	 * @param column
	 *            the column (zero-based index).
	 * @return the column class.
	 */
	public Class getColumnClass(final int column) {
		return COL_CLASS[column];
	}

	/**
	 * Returns the name of the specified column.
	 * 
	 * @param column
	 *            the column (zero-based index).
	 * @return the column name.
	 */
	public String getColumnName(final int column) {
		return COL_NAMES[column];
	}

	/**
	 * Returns the data value at the specified row and column.
	 * 
	 * @param row
	 *            the row index (zero based).
	 * @param column
	 *            the column index (zero based).
	 * @return the value.
	 */
	public Object getValueAt(final int row, final int column) {
		POPDailyMetric popData = data.get(row);
		
		switch(column) {
			case 0:
				return FormatUtils.fullDate( popData.getDate() );

			case 1:
				return popData.getThemeName();
			
			case 2:
				return popData.getTotalPlays(); 

			case 3:
				return FormatUtils.decFormat(
					(popData.getTotalSeconds() / popData.getTotalPlays()) ) + " Sec";
			
			case 4:
				return Time.getDescription( popData.getTotalSeconds() * 1000 );				

		}
		
		return "";
	}

}
