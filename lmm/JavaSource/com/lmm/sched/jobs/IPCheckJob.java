package com.lmm.sched.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import com.lmm.sched.data.IPUpdater;

/**
 * Cron help:
 * 
 *    http://www.opensymphony.com/quartz/tutorial.html#cronTriggers
 */
public class IPCheckJob implements Job
{
	private IPUpdater ipUpdate = null;
	public static final String LMM_IP_UPDATER = "lmm_currentIP";

	public IPCheckJob()
	{
		super();
	}

    public void execute(JobExecutionContext context)
    {
		JobDataMap map = context.getJobDetail().getJobDataMap();
		IPUpdater ipUpdate = (IPUpdater)map.get(LMM_IP_UPDATER);
		
		//check our IP and update it if need be
		if( ipUpdate != null ) {
			ipUpdate.execute();
		}
		
    	
    }

}
