package com.lmm.pop;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lmm.db.DBCommon;


public class POPMetricManager {

	//stores all of todays metrics
	private Map<String, POPDailyMetric> dailyMetrics =
			new HashMap<String, POPDailyMetric>(32);

	private static POPMetricManager instance;
	
	//number of metric updates for a given day before we save the data
	public static final int BATCH_COUNT = 5;

	

	private POPMetricManager() {
		super();
	}
	
	public static POPMetricManager getInstance() {
		if( instance == null )
			instance = new POPMetricManager();
		
		return instance;
	}

	/**
	 * Gets the proper metric for today by its name. This metric is also written
	 * to the DB based on the number of total plays it has done.
	 * 
	 * @param themeName
	 * @return
	 */
	public synchronized POPDailyMetric getTodaysMetric( String themeName ) {

		Date currDate = new Date();
		POPDailyMetric popDaily = dailyMetrics.get( themeName );

		//if we do not have this pop in memory or we have progressed to another day then
		// create a new popDaily instance
		if(  popDaily == null || !popDaily.isValidMillis(System.currentTimeMillis()) ) {
			
			//if we are here because of a new day, lets write the last metrics to the DB
			if( popDaily != null )
				DBCommon.getDB().popDailyMetric_Update( popDaily );
				
			popDaily = new POPDailyMetric(themeName, currDate);

			List<POPDailyMetric> l = DBCommon.getDB().popDailyMetric_Retrieve(
					popDaily.getDate(), popDaily.getThemeName() );
			if( l.size() > 0 )
				popDaily = l.get(0);

			dailyMetrics.put( themeName, popDaily );
		}

		if( ((popDaily.getTotalPlays()+1) % BATCH_COUNT) == 0 ) {
			//write to DB
			DBCommon.getDB().popDailyMetric_Update( popDaily );
		}

		return popDaily;
	}

}
