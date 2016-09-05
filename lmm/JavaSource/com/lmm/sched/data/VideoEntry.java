package com.lmm.sched.data;

import com.lmm.sched.proc.LMMUtils;


public class VideoEntry extends LMMEntry
{
	private String url = null;
	

	public VideoEntry( String entryID, String fileName, String url,
			Integer[] playOder, Integer playDuration,
			String timeWindow, String dayWindow, Integer channel )
	{
		super();
		setEntryID( entryID );
		setFileName( fileName );
		setUrl( url );
		setPlayOrder( playOder );
		setPlayDuration( playDuration );
		setTimeWindow( timeWindow );
		setDayWindow( dayWindow );
		setChannel( channel );
	}

	/**
	 * @return
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * @param string
	 */
	public void setUrl(String string)
	{
		url = string;
	}

//	xml.2 = lmm_logo.xml
//	play_order.2 = 4
//	play_duration.2 = 10
//	web_url.2 = -1
//	time_window.2 = -1
//	day_window.2 = -1
//	channel.2 = 0
	public String getStringEntry() {		
		String cr = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer(256);

		sb.append( "xml." + getEntryID() + " = " + getFileName() + cr );
		sb.append( "play_order." + getEntryID() + " = " 
			+ LMMUtils.arrayAsString(getPlayOrder(), ",") + cr);

		sb.append( "play_duration." + getEntryID() + " = " + new Integer(getPlayDuration().intValue() / 1000) + cr);
		sb.append( "web_url." + getEntryID() + " = " + LMMUtils.parseNullProp(getUrl()) + cr);
		sb.append( "time_window." + getEntryID() + " = " + LMMUtils.parseNullProp(getTimeWindow()) + cr);
		sb.append( "day_window." + getEntryID() + " = " + LMMUtils.parseNullProp(getDayWindow()) + cr );
		sb.append( "channel." + getEntryID() + " = " + getChannel() + cr + cr);
		
		return sb.toString();
	}
}
