package com.lmm.sched.data;

import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;


public abstract class LMMEntry
{
	private String entryID = null;  //unique ID for each entry
    private String fileName = null;
	private Integer[] playOrder = null;
	private Integer playDuration = null;  //millis
	private String timeWindow = null; //format:  6:19-21:55
	
	//M=Monday T=Tuesday W=Wednesday R=Thursday F=Friday S=Saturday U=Sunday
	private String dayWindow = null; //format:  MTWRFSU
	
	private Integer channel = null; //possible values are 0-3

	public static transient Comparator OrderComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			if( o1 instanceof LMMEntry && o2 instanceof LMMEntry ) {
				LMMEntry ve1 = (LMMEntry)o1;
				LMMEntry ve2 = (LMMEntry)o2;
				
				if( ve1.getPlayOrder() == null || ve1.getPlayOrder().length <= 0 )
					return 1;
				if( ve2.getPlayOrder() == null || ve2.getPlayOrder().length <= 0 )
					return 1;

				return ve1.getPlayOrder()[0].compareTo( ve2.getPlayOrder()[0] );
			}
			return 0;
		}
	};

    /**
         * @return
         */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Remove extension and any file separators from the file name.
     * @return
     */
	public String getFileNameNoExt()
	{
		if( getFileName() == null || getFileName().lastIndexOf(".") <= -1 )
			return getFileName();
		else {
			String a = getFileName();
			int end = a.lastIndexOf(".") >= 0 ? a.lastIndexOf(".") : a.length();
			int st = (a.lastIndexOf("/") >= 0 ? a.lastIndexOf("/")+1 : 0);
			st = (a.lastIndexOf("\\") >= st ? a.lastIndexOf("\\")+1 : st);
			
			return a.substring( st, end );
		}
	}

    /**
         * @param string
         */
    public void setFileName(String string)
    {
        fileName = string;
    }

    /**
     * @return
     */
    public Integer getPlayDuration()
    {
        return playDuration;
    }

    /**
     * @return
     */
    public Integer[] getPlayOrder()
    {
        return playOrder;
    }

    /**
     * @param integer
     */
    public void setPlayDuration(Integer integer)
    {
        playDuration = integer;
    }

    /**
     * @param integer
     */
    public void setPlayOrder(Integer[] integer)
    {
        playOrder = integer;
    }

	/**
	 * @return
	 */
	public String getTimeWindow()
	{
		return timeWindow;
	}

	/**
	 * @return
	 */
	public String getDayWindow()
	{
		return dayWindow;
	}

	/**
	 * @param string
	 */
	protected void setTimeWindow(String string)
	{
		timeWindow = string;
	}
	
	/**
	 * @param string
	 */
	protected void setDayWindow(String string)
	{
		dayWindow = string;
	}

	
	protected boolean isMillisInWindow( long millis )
	{
		//no timewindow means it runs all the time!
		if( getTimeWindow() == null ) return true;

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis( millis );
		int currMins = gc.get( GregorianCalendar.HOUR_OF_DAY ) * 60 +
						gc.get( GregorianCalendar.MINUTE );

		// TimeWindow is in the format:    09:10-22:35
		//string[0]->startTime, string[1]->endTime
		String[] times = getTimeWindow().split("-");
				
		int startMins = Integer.parseInt(times[0].split(":")[0]) * 60 +
						Integer.parseInt(times[0].split(":")[1]);
		int endMins = Integer.parseInt(times[1].split(":")[0]) * 60 +
						Integer.parseInt(times[1].split(":")[1]);
		
		return currMins >= startMins && currMins < endMins;
	}

	protected boolean isMillisInDayWindow( long millis )
	{
		//no dayWindow means it runs all the time!
		if( getDayWindow() == null ) return true;

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis( millis );
		int currDay = gc.get( GregorianCalendar.DAY_OF_WEEK );
		
		switch( currDay ) {
			case GregorianCalendar.SUNDAY:
			return getDayWindow().toUpperCase().indexOf("U") >= 0;

			case GregorianCalendar.MONDAY:
			return getDayWindow().toUpperCase().indexOf("M") >= 0;

			case GregorianCalendar.TUESDAY:
			return getDayWindow().toUpperCase().indexOf("T") >= 0;

			case GregorianCalendar.WEDNESDAY:
			return getDayWindow().toUpperCase().indexOf("W") >= 0;

			case GregorianCalendar.THURSDAY:
			return getDayWindow().toUpperCase().indexOf("R") >= 0;

			case GregorianCalendar.FRIDAY:
			return getDayWindow().toUpperCase().indexOf("F") >= 0;

			case GregorianCalendar.SATURDAY:
			return getDayWindow().toUpperCase().indexOf("S") >= 0;
		}


		return false;
	}

	public boolean isDateInWindows( Date date )
	{
		return isMillisInDayWindow(date.getTime())
			&& isMillisInWindow(date.getTime());
	}
    /**
     * @return
     */
    public String getEntryID() {
        return entryID;
    }

    /**
     * @param string
     */
    public void setEntryID(String string) {
        entryID = string;
    }

    /**
     * @return
     */
    public Integer getChannel() {
        return channel;
    }

    /**
     * @param integer
     */
    public void setChannel(Integer integer) {
        channel = integer;
    }

}