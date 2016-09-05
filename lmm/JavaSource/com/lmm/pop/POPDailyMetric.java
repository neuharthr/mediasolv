package com.lmm.pop;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;
import com.lmm.server.lite.OwnableData;

/**
 * Metrics gathered per theme for proof of performance on a given day.
 *
 */
public class POPDailyMetric implements Comparable<POPDailyMetric> {

	private Date date;	//primary key
	private String themeName;
	private long totalSeconds = 0L;
	private int totalPlays = 0;
	private Vector<Long> playTimeMillis = new Vector<Long>(32);

	private OwnableData owner = new OwnableData();
	
	//non metric data
	private long minMillis;
	private long maxMillis;


	POPDailyMetric( String themeName, Date theDay ) {
		super();
		if( theDay == null )
			throw new IllegalArgumentException("A valid day must be give");

		if( themeName == null )
			throw new IllegalArgumentException("A valid theme name must be give");

		this.themeName = themeName;
		
		final GregorianCalendar gc = new GregorianCalendar();
		gc.setTime( theDay );
		gc.set( GregorianCalendar.HOUR, 0 ); gc.set( GregorianCalendar.MINUTE, 0 );
		gc.set( GregorianCalendar.SECOND, 0 ); gc.set( GregorianCalendar.MILLISECOND, 0 );
		
		//lets round down to the current day
		this.date = new Date( gc.getTimeInMillis() );
	}

	public int compareTo(POPDailyMetric o) {
		if( getDate() == null ) {
			return -1;
		} else {
			return getDate().compareTo( ((POPDailyMetric)o).getDate() );
		}
	}

	public Date getDate() {
		return date;
	}

	protected boolean isValidMillis( long millis ) {
		if( minMillis == 0 || maxMillis == 0 ) {
			final GregorianCalendar gc = new GregorianCalendar();
			gc.setTime( getDate() );
			gc.set( GregorianCalendar.HOUR, 0 ); gc.set( GregorianCalendar.MINUTE, 0 );
			gc.set( GregorianCalendar.SECOND, 0 ); gc.set( GregorianCalendar.MILLISECOND, 0 );
			minMillis = gc.getTimeInMillis();

			gc.set( GregorianCalendar.HOUR, 23 ); gc.set( GregorianCalendar.MINUTE, 59 );
			gc.set( GregorianCalendar.SECOND, 29 ); gc.set( GregorianCalendar.MILLISECOND, 999999 );
			maxMillis = gc.getTimeInMillis();
		}

		return millis >= minMillis && millis <= maxMillis;
		
	}
	
	public void addPlayTime( long millis ) {
		if( !isValidMillis(millis) )
			throw new IllegalArgumentException("The given millis does not fall within the set day of: " + getDate() );
	
		playTimeMillis.add( millis );
	}
	
    public Long[] getPlayTimes() {
    	if( playTimeMillis == null )
    		return new Long[0];
    	else
    		return (Long[])playTimeMillis.toArray(new Long[playTimeMillis.size()]);
    }
	
	public void addSeconds( int secs ) {
		totalSeconds += secs;
	}
	
	public void incrementTotalPlays() {
		totalPlays++;
	}

	public String getThemeName() {
		return themeName;
	}

	public int getTotalPlays() {
		return totalPlays;
	}

	public long getTotalSeconds() {
		return totalSeconds;
	}

	public OwnableData getOwner() {
		return owner;
	}

	public void setOwner(OwnableData owner) {
		this.owner = owner;
	}
}
