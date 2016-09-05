package com.lmm.tools;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *
 */
public class Time {

	public static final long SECOND = 1000;
	public static final long MINUTE = SECOND * 60;
	public static final long HOUR = MINUTE * 60;
	public static final long DAY = HOUR * 24;
	public static final long WEEK = DAY * 7;

	public static Date getDate(Calendar cal) {
		Calendar adjustedCal = new GregorianCalendar();
		adjustedCal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		adjustedCal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
		adjustedCal.set(Calendar.DATE, cal.get(Calendar.DATE));
		adjustedCal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
		adjustedCal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
		adjustedCal.set(Calendar.SECOND, cal.get(Calendar.SECOND));
		adjustedCal.set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND));

		return adjustedCal.getTime();
	}

	public static Date getDate(TimeZone tz) {
		Calendar cal = new GregorianCalendar(tz);

		return getDate(cal);
	}

	public static Date getDate(Date date, TimeZone tz) {
		Calendar cal = new GregorianCalendar(tz);
		cal.setTime(date);

		return getDate(cal);
	}

	public static String getDescription(long milliseconds) {
		String s = "";

		long x = 0;

		x = milliseconds / WEEK;
		if( x > 0 ) {
			s += x + " Week, ";
			milliseconds -= x * WEEK;
		}
		
		x = milliseconds / DAY;
		if( x > 0 ) {
			s += x + " Day, ";
			milliseconds -= x * DAY;
		}
		
		x = milliseconds / HOUR;
		if( x > 0 ) {
			s += x + " Hr, ";
			milliseconds -= x * HOUR;
		}

		x = milliseconds / MINUTE;
		if( x > 0 ) {
			s += x + " Min, ";
			milliseconds -= x * MINUTE;
		}

		x = milliseconds / SECOND;
		s += x + " Sec";
		milliseconds -= x * SECOND;

		return s;
	}

}
