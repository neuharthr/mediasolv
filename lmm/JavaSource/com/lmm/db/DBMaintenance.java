package com.lmm.db;

import java.util.Date;

import com.lmm.sched.proc.LMMUtils;

/**
 * Handles all DB maintenance tasks.
 * 
 */
public class DBMaintenance {
	
	public static final long POPDAILY_RETENTION_DAYS = 90 * 86400000; //90 days
	
	DBMaintenance() {
		super();
	}
    
	/**
	 * Removal of old data is done here to control the size of our DB.
	 *
	 */
	public void prune() {
		
		if( LMMUtils.isPOPEnabled() ) {
			Date popDate = new Date( System.currentTimeMillis() - POPDAILY_RETENTION_DAYS );
			DBCommon.getDB().delete(
					DBPredicates.getPOPPredicate(popDate, null, null));
		}

	}


}