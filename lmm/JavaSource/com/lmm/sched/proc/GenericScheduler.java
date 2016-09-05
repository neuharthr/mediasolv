package com.lmm.sched.proc;

import java.text.ParseException;
import java.util.Date;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;

import com.lmm.tools.LMMLogger;


public class GenericScheduler {

	private Scheduler qScheduler = null;

	public static final String LMM_JOB_GROUP = "LMM_JOBS";
	public static final String LMM_JOB_GROUP_NON_REPEAT = "LMM_JOBS_NO_REPEAT";


	public GenericScheduler() {
		super();
		init();
	}
	
	private void init() {
		try {
			SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
			setQScheduler( schedFact.getScheduler() );
			getQScheduler().start();
		}
		catch( Exception ex )
		{
			LMMLogger.error( "Unable to start", ex );
		}

	}

	public void addCron( String name, String expr, Class clazz, String mapId, Object mapObj )
	{
		CronTrigger ct = null;
		JobDetail jd = null;
		try {
			ct = new CronTrigger(
				name,
				LMM_JOB_GROUP,
				"cronJob" + name,
				LMM_JOB_GROUP,
				expr);

			jd = new JobDetail(
				"cronJob" + name, LMM_JOB_GROUP, clazz );
			
			if( mapId != null )
				jd.getJobDataMap().put( mapId, mapObj );
			
		} catch( ParseException pe ) {
			LMMLogger.error( "Unable to parse CRON expression: " + expr, pe );
		}

		try {
			getQScheduler().scheduleJob( jd, ct );			
		}
		catch( SchedulerException se ) {
			LMMLogger.error( "Unable to schedule Job with CronTrigger", se );
		}

	}

	public void addSimple( String name, Date startTime,
		Date endTime, int repeatCnt, long repeatInterval, 
		Class clazz, String mapId, Object mapObj )
	{
		SimpleTrigger st = null;
		JobDetail jd = null;
		
		st = new SimpleTrigger(
			"simpJob" + name,
			(repeatInterval <= 0L ? LMM_JOB_GROUP_NON_REPEAT : LMM_JOB_GROUP),
			startTime,
			endTime,
			repeatCnt,
			repeatInterval);

		jd = new JobDetail(
			"simpJob" + name, LMM_JOB_GROUP, clazz );
		
		if( mapId != null )
			jd.getJobDataMap().put( mapId, mapObj );
		

		try {
			getQScheduler().scheduleJob( jd, st );			
		}
		catch( SchedulerException se ) {
			LMMLogger.error( "Unable to schedule Job with SimpleTrigger", se );
		}

	}

    /**
     * @return
     */
	protected Scheduler getQScheduler() {
        return qScheduler;
    }

	public void runJobNow(String jobName) {
		try {
			jobName = "simpJob" + jobName;
			getQScheduler().triggerJob(jobName, LMM_JOB_GROUP);
		}
		catch( SchedulerException se ) {
			LMMLogger.error( "Unable to run the Job (" + jobName + ", " + LMM_JOB_GROUP + ") now", se );
		}
	}
	

    /**
     * @param scheduler
     */
    protected void setQScheduler(Scheduler scheduler) {
        qScheduler = scheduler;
    }

}
