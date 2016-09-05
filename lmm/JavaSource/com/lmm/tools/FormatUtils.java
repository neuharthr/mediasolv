package com.lmm.tools;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;


public class FormatUtils {

	private static SimpleDateFormat STD_DATE_FORMATTER =
			new SimpleDateFormat("MM/dd/yy HH:mm:ss");

	private static SimpleDateFormat TIME_DATE_FORMATTER =
			new SimpleDateFormat("HH:mm MM/dd/yy");

	private static SimpleDateFormat DATE_ONLY_FORMATTER =
		new SimpleDateFormat("MMddyyyy");

	private static SimpleDateFormat TIME_ONLY_FORMATTER =
		new SimpleDateFormat("HHmmss");

	private static SimpleDateFormat TIME_FORMATTER =
			new SimpleDateFormat("HH:mm");

	private static SimpleDateFormat ABRV_DATE_FORMATTER =
		new SimpleDateFormat("MM/dd/yyyy 'at' HH:mm:ss");

	private static SimpleDateFormat FULL_DATE_FORMATTER =
		new SimpleDateFormat("MMMM dd, yyyy");

	private static SimpleDateFormat TIME_FULL_FORMATTER =
			new SimpleDateFormat("HH:mm:ss");

	private static DecimalFormat DEC_PLACE_FORMATTER =
			new DecimalFormat("#,###");

	private static DecimalFormat DBL_PLACE_FORMATTER =
			new DecimalFormat("#,###.00");

	public static final GregorianCalendar GC = new GregorianCalendar();
	public static final GregorianCalendar NOW = new GregorianCalendar();


	public static String decFormat( double val ) {
		return DBL_PLACE_FORMATTER.format( val );
	}

	public static String decFormat( long val ) {
		return DEC_PLACE_FORMATTER.format( val );
	}

	public static String stdDate( Date d ) {
		if( d == null )
			return "";
		else
			return STD_DATE_FORMATTER.format( d );
	}

	public static String abvrDate( Date d ) {
		if( d == null )
			return "";
		else
			return ABRV_DATE_FORMATTER.format( d );
	}

	public static String fullDate( Date d ) {
		if( d == null )
			return "";
		else
			return FULL_DATE_FORMATTER.format( d );
	}

	public static String fullTime( Date d ) {
		if( d == null )
			return "";
		else
			return TIME_FULL_FORMATTER.format( d );
	}

	public static String onlyDate( Date d ) {
		if( d == null )
			return "";
		else
			return DATE_ONLY_FORMATTER.format( d );
	}

	public static String onlyTime( Date d ) {
		if( d == null )
			return "";
		else
			return TIME_ONLY_FORMATTER.format( d );
	}

	/**
	 * Shows only the time format if the given data is today, else it shows
	 * the time & date if the given date is any day before today.
	 * 
	 * @param d
	 * @return
	 */
	public static String timeDate( Date d ) {
		if( d == null )
			return "";
		else {
			GC.setTimeInMillis( d.getTime() );
			NOW.setTimeInMillis( System.currentTimeMillis() );
			if( GC.get(GregorianCalendar.DAY_OF_YEAR) == NOW.get(GregorianCalendar.DAY_OF_YEAR)
					&& GC.get(GregorianCalendar.YEAR) == NOW.get(GregorianCalendar.YEAR) )
				return TIME_FORMATTER.format( d );
			else
				return TIME_DATE_FORMATTER.format( d );
		}

	}
}
