package com.lmm.sched.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import com.lmm.client.LMMClient;
import com.lmm.sched.proc.LMMUtils;
import com.lmm.tools.LMMLogger;
import com.lmm.tools.LMMUpdater;

/**
 * Does a check for any updates that may be needed.
 */
public class AutoUpdateJob implements Job
{
	//LMMClient instance
	public static final String UPDATE_CLIENT = "lmm_update_client";

	public AutoUpdateJob() {
		super();
	}

    public void execute(JobExecutionContext context) {
		JobDataMap map = context.getJobDetail().getJobDataMap();
		
    	doUpdate( (LMMClient)map.get(UPDATE_CLIENT) );
    }

    /**
     * The given client may be null.
     * 
     * @param lmmClient
     * @return
     */
	public int doUpdate( final LMMClient lmmClient ) {
		
		try {
			if( LMMUtils.isAutoUpdate() ) {
				LMMUpdater updater = new LMMUpdater();
				if( lmmClient != null )
					updater.setLMMClient( lmmClient );

				return updater.executeUpdates();
			}
		}
		catch( Exception e ) {
			LMMLogger.error( "Unable to perform Auto Update check", e );
		}

		return 0;
	}

	/**
	 * Use this method if the caller does not care to be notified during and upgrade event.
	 * This method is typically used for GUI applications.
	 * 
	 * @return
	 */
	public int doUpdate() {
		return doUpdate( null );
	}
}
