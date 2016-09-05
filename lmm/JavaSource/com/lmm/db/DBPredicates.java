package com.lmm.db;

import java.util.Date;
import java.util.GregorianCalendar;

import com.db4o.query.Predicate;
import com.lmm.pop.POPDailyMetric;
import com.lmm.server.lite.LitePlayer;

/**
 * Contains all the DB predicates for each object.
 * 
 */
public class DBPredicates {

	/**
	 * Both end & themeName can be null.
	 * 
	 */
	static Predicate<POPDailyMetric> getPOPPredicate(
			final Date start, Date end, final String themeName ) {

		if( end != null ) {
        		final GregorianCalendar gc = new GregorianCalendar();
        		gc.setTime( end );
        		gc.set( GregorianCalendar.HOUR, 23 ); gc.set( GregorianCalendar.MINUTE, 59 );
        		gc.set( GregorianCalendar.SECOND, 29 ); gc.set( GregorianCalendar.MILLISECOND, 999999 );
        		end = new Date( gc.getTime().getTime() );
		}
   
		final Date calcEnd = end;
		Predicate<POPDailyMetric> pred = new Predicate<POPDailyMetric>() {
		   public boolean match(POPDailyMetric popMetric) {

			   if( themeName == null )
				   return popMetric.getDate().after(start)
				   		&& popMetric.getDate().before(calcEnd);
			   else
				   return popMetric.getDate().after(start)
			   			&& popMetric.getDate().before(calcEnd)
			   			&& popMetric.getThemeName().equals(themeName);
		   }
		};

		return pred;
	}

	static Predicate<LitePlayer> getLitePlayerPred( final String uuid ) {
		Predicate<LitePlayer> pred = new Predicate<LitePlayer>() {
			   public boolean match(LitePlayer lPlayer) {
				   if( uuid == null )
					   return true;
				   else
					   return lPlayer.getUuid().equals(uuid);
			   }
			};
			
		return pred;
	}

}