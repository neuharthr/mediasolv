package com.lmm.sched.proc;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

import com.lmm.client.LMMClient;
import com.lmm.client.LMMClientHandler;
import com.lmm.db.DBCommon;
import com.lmm.sched.data.IPUpdater;
import com.lmm.sched.jobs.AutoUpdateJob;
import com.lmm.sched.jobs.ClientMsgJob;
import com.lmm.sched.jobs.DBMaintenanceJob;
import com.lmm.sched.jobs.IPCheckJob;
import com.lmm.sched.jobs.ProcessExecJob;
import com.lmm.sched.jobs.VideoJob;
import com.lmm.tools.LMMFlags;
import com.lmm.tools.LMMLogger;


public class LMMScheduler extends GenericScheduler implements TriggerListener
{
	private LMMClient client = null;
	private PlayOrder pOrder = null;
	private boolean startupPhase = true;	//used to signify the app is just starting up


	public static final long CLIENT_UPDATE = 60000L; //60 seconds
	public static final long AUTOUPDATE_CHECK = 86400000L; //24 hours
	public static final long DB_MAINTENANCE_CHECK = 90000000L; //26 hours


	public static final String JOB_HEARTBEAT = "_HeartBeat";

	public static void main( String[] args )
	{
		LMMLogger.info( "Starting MediaSOLV Scheduler v. " + LMMUtils.VERSION );
		System.setProperty( DBCommon.DB_FILENAME_KEY, "db_sched.yap" );

		if( new AutoUpdateJob().doUpdate() <= 0 ) {
			LMMScheduler ts = new LMMScheduler();
			ts.startClient();			
			ts.startJobs();			
		}
		else {
			//we only get here if this app was started manually
			LMMLogger.info("Updates were found and have been applied successfully, please restart the application");
		}

	}
	
	public LMMScheduler() {
		super();

		try {
			getQScheduler().addGlobalTriggerListener( this );
		}
		catch( SchedulerException se ) {
			LMMLogger.error( "Unable to add GLOBAL listener to scheduler", se );
		}

	}

	/**
	 * This method will EXIT the app if an update is needed, so be careful
	 * when calling.
	 */
	public void restartSchedulerUpdate() {
		try {			
			if( new AutoUpdateJob().doUpdate(client) <= 0 ) {			
				String[] jobs = getQScheduler().getJobNames( GenericScheduler.LMM_JOB_GROUP );			
				for( int i = 0; i < jobs.length; i++ )
					getQScheduler().deleteJob( jobs[i], GenericScheduler.LMM_JOB_GROUP );
		
				restartClientIfChanged();
				startJobs();
			}
			else {
				LMMLogger.info("Updates were found and have been applied successfully, please restart the application");
			}

		}
		catch( SchedulerException se ) {
			LMMLogger.error( "Unable to restart scheduler", se );
		}
	}
	
	
	public void startJobs() {

		pOrder = buildPlayOrder();

		// ---- plays all videos
		if( !LMMFlags.isPlayerSuppressed(LMMUtils.getAppFlags()) )
			queueVideoJobs();


		// ---- start one-time events
		if( startupPhase )
			queueOneTimeJobs();

		// ---- start reoccuring events
		queueReoccuringJobs();		

		// ---- establishes all client scheduled events
		queueClientJobs();

		
		//the app is not starting up anymore once all jobs have been scheduled
		startupPhase = false;
	}

	private void startClient() {
		//do not bother with any connectivity if we are not set up for access
		if( LMMUtils.getServerHost() == null )
			return;

		client = new LMMClient(
				LMMUtils.getServerHost(),
				LMMUtils.getServerPort(),
				LMMUtils.getComputerName() );

		LMMClientHandler msgHandler = new LMMClientHandler( client, this );
		client.setMessageListener( msgHandler );

		client.connect();		
	}

	private void restartClientIfChanged() {
		//do not bother with any connectivity if we are not set up for access
		if( LMMUtils.getServerHost() == null )
			return;

		//try to stop, then start the client if our connection properties have changed
		if( client != null &&
			( !LMMUtils.getServerHost().equals(client.getHost())
				|| LMMUtils.getServerPort() != client.getPort()
				|| !LMMUtils.getComputerName().equals(client.getName())) ) {
			
			try {
				client.disconnect();
				client = null;
			}
			catch( IOException ioe ) {
				LMMLogger.error("Unable to stop the client connection", ioe);
			}
			
			startClient();
		}

	}

	private void queueClientJobs() {		
		// ------  Client/Server jobs below  ------------------------
		//do not bother with any connectivity if we are not set up for access
		if( client == null )
			return;

		HashMap clientMap = new HashMap();
		clientMap.put( ClientMsgJob.LMM_CLIENT, client );
		clientMap.put( VideoJob.PLAYORDER_MAP, pOrder );

		//in 20 seconds, start sending heartbeat messages to the server
		addSimple(
			JOB_HEARTBEAT,
			new Date(System.currentTimeMillis() + 20000L),
			null,
			SimpleTrigger.REPEAT_INDEFINITELY, CLIENT_UPDATE, //repeat interval
			ClientMsgJob.class,
			ClientMsgJob.CLIENT_MAP,
			clientMap );		
	}
	
	private void queueVideoJobs() {

		if( pOrder == null )
			return;

		if( pOrder.getPlayMap().size() > 0 ) {
			addSimple(
				"_PlayOrder",
				new Date(),
				null,
				SimpleTrigger.REPEAT_INDEFINITELY,
				pOrder.getCurrTotalDuation(),
				VideoJob.class,
				VideoJob.PLAYORDER_MAP,
				pOrder );
		}

	}
	
	private PlayOrder buildPlayOrder() {

		PlayOrder pOrder = new PlayOrder();
		
		//add all cron Video jobs here
		for( int i = 0; i < LMMUtils.getVideoEntries().length; i++ ) {

			if( LMMUtils.getVideoEntries()[i].getPlayDuration() != null ) {
				for( int j = 0; j < LMMUtils.getVideoEntries()[i].getPlayOrder().length; j++ )
					pOrder.getPlayMap().put(
						LMMUtils.getVideoEntries()[i].getPlayOrder()[j],
						LMMUtils.getVideoEntries()[i] );
			}

		}

		return pOrder;
	}

	/**
	 * Starts jobs that only fire one time during the aplication execution.
	 *
	 */
	private void queueOneTimeJobs() {
		
		//send the notifcation of our startup once, 30 seconds from now
		addSimple(
			"_NotifStart",
			new Date(System.currentTimeMillis() + 30000L),
			null,
			0, 0, //repeat interval
			ProcessExecJob.class,
			ProcessExecJob.EXEC_TYPE,
			ProcessExecJob.TYPE_MAIL );	
	}

	/**
	 * Starts jobs that fire more than one time during the aplication execution.
	 *
	 */
	private void queueReoccuringJobs() {
		
		//add our FTP/IP task to the schedule
		if( LMMUtils.getIpDiscoveryUrl() != null && LMMUtils.getIpCheckCronExpr() != null ) {
			addCron(
				"_IPUpdater",
				LMMUtils.getIpCheckCronExpr(),
				IPCheckJob.class,
				IPCheckJob.LMM_IP_UPDATER,
				new IPUpdater() );
		}
			
		//add the kill app cron job if present
		if( LMMUtils.getKillCmd() != null && LMMUtils.getKillCronExpr() != null ) {
			addCron(
				"_AppKill",
				LMMUtils.getKillCronExpr(),
				ProcessExecJob.class,
				ProcessExecJob.EXEC_FILENAME,
				LMMUtils.getAppsDir() + LMMUtils.getKillCmd() );
		}

		//in AUTOUPDATE_CHECK millis, start checking for jar updates every AUTOUPDATE_CHECK millis
		if( LMMUtils.isAutoUpdate() )
			addSimple(
				"_AutoUpdate",
				new Date(System.currentTimeMillis() + AUTOUPDATE_CHECK),
				null,
				SimpleTrigger.REPEAT_INDEFINITELY, AUTOUPDATE_CHECK, //repeat interval
				AutoUpdateJob.class,
				AutoUpdateJob.UPDATE_CLIENT,
				client );		
	
		//add a re-occuring DB maintenance task
		addSimple(
			"_DBMaintenance",
			new Date(System.currentTimeMillis() + DB_MAINTENANCE_CHECK),
			null,
			SimpleTrigger.REPEAT_INDEFINITELY, DB_MAINTENANCE_CHECK, //repeat interval
			DBMaintenanceJob.class,
			null,
			null );
	}
	
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
		return false; 
	}

	public String getName() {
		return "trigListener";
	}

	public void triggerComplete(Trigger trigger, JobExecutionContext context, int triggerInstructionCode) {
		JobDataMap map = context.getJobDetail().getJobDataMap();

		if( trigger instanceof SimpleTrigger )
			handleSimpleTrigger( map, (SimpleTrigger)trigger );

	}

	public void triggerMisfired(Trigger trigger) {}
	
	
	public void triggerFired(Trigger trigger, JobExecutionContext context) {

	}


	private void handleSimpleTrigger( JobDataMap map, SimpleTrigger trigger )
	{
		PlayOrder pOrder = (PlayOrder)map.get(VideoJob.PLAYORDER_MAP);
		if( pOrder == null ) return;

		//reschulde our trigger if the duration changed due to time
		// window changes
		try {

			long currDur = pOrder.getCurrTotalDuation();

			if( currDur != trigger.getRepeatInterval() ) {
				
				trigger.setRepeatInterval( pOrder.getCurrTotalDuation() );
				getQScheduler().rescheduleJob(
					trigger.getName(), trigger.getGroup(),
					trigger );
			}

		}
		catch( SchedulerException se ) {
			LMMLogger.error( "Unable to reschedule Job as SimpleTrigger", se );
		}

	}

}
